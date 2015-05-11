package com.dropout.kanyehole;

/**
 * This Class is for the Main Game Activity but Taylor mode
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class TayActivity extends Activity {
    public static TayArc tayArc = null;
    private FrameLayout mainView;
    private ObjectView arcView = null;
    private boolean notPaused = true;
    private Timer mTmr = null;
    private TimerTask mTsk = null;
    public ArrayList<Drawable> drawList;
    public ArrayList<Drawable> obsList;
    private AtomicInteger lives = new AtomicInteger(1);
    private AtomicInteger score = new AtomicInteger(0);
    private AtomicInteger multiplier = new AtomicInteger(1);
    public AtomicInteger hittype = new AtomicInteger(-1);
    private int hittimer = -1;
    private int ticks = 1;
    private int mScrWidth, mScrHeight;
    private int hits = 0;
    private MediaPlayer music;
    private int musicpos = 0;
    private boolean endswap = false;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(0xFFFFFFFF,
                LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        //get the old score and multiplier from intent
        int oldscore = intent.getIntExtra("Score", 0);
        int oldmult = intent.getIntExtra("Mult", 0);
        score.getAndSet(oldscore);
        multiplier.getAndSet(oldmult);

        setContentView(R.layout.tay_layout);
        MyApplication.setContext(this.getBaseContext());

        /* Much of the initialization for OPENGL renderer and paramters for drawing, as well as the
         way for drawing using vertices, indexes and uv matrixes taken/modified from a set of GLES2.0
        Tutorials that can be found (part 1-6) at:

        http://androidblog.reindustries.com/a-real-open-gl-es-2-0-2d-tutorial-part-1/

        */
        GLSurfaceView glSurfaceView =
                (GLSurfaceView) findViewById(R.id.glsurfaceview);
        // Set the Renderer for drawing on the GLSurfaceView
        // Create an OpenGL ES 2.0 context.
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        SurfaceRenderer mRenderer = new SurfaceRenderer(this, mScrHeight, mScrWidth, true, null, this);
        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        //Setup music
        music = MediaPlayer.create(MyApplication.getAppContext(), R.raw.ybm);
        music.start();

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        mScrWidth = display.getWidth();
        mScrHeight = display.getHeight();

        //Create the taylor arc
        tayArc = TayArc.getInstance();
        tayArc.setScrDims(mScrHeight, mScrWidth);
        tayArc.setPosition(mScrWidth, mScrHeight);

        //Create view and grab draw lists
        arcView = new ObjectView(this, mScrWidth / 2, tayArc.getYPosition(), 50, 0, 0);
        drawList = arcView.getDrawList();
        obsList = arcView.getObsList();
        mainView = (android.widget.FrameLayout) findViewById(R.id.rtay);

        //clear old arrow button arrays
        clearArrowArrays();

        mainView.addView(arcView); //add ball to main screen

        //////Setup the touch listeners for the image buttons (had to do it programmatically to make it onTouch instead of onRelease

        ImageButton up = (ImageButton) findViewById(R.id.up);
        View.OnTouchListener upListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    upPressed(v);
                }
                return false;
            }
        };
        ImageButton down = (ImageButton) findViewById(R.id.down);
        View.OnTouchListener downListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downPressed(v);
                }
                return false;
            }
        };
        ImageButton left = (ImageButton) findViewById(R.id.left);
        View.OnTouchListener leftListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftPressed(v);
                }
                return false;
            }
        };
        ImageButton right = (ImageButton) findViewById(R.id.right);
        View.OnTouchListener rightListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightPressed(v);
                }
                return false;
            }
        };
        up.setOnTouchListener(upListener);
        down.setOnTouchListener(downListener);
        left.setOnTouchListener(leftListener);
        right.setOnTouchListener(rightListener);

        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        //set ball speed based on phone tilt (ignore Z axis)
                        tayArc.setSpeed(event.values[0] / 6, event.values[1] / 6);
                        //timer event will redraw ball
                    }

                    @Override
                    public void onAccuracyChanged(Sensor sensor, int accuracy) {
                    } //ignore this event
                },
                ((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                        .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);

    }

    //For state flow see http://developer.android.com/reference/android/app/Activity.html
    @Override
    public void onPause() //app moved to background, stop background threads
    {
        //pause music w/position
        music.pause();
        musicpos = music.getCurrentPosition();
        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;
        super.onPause();
    }

    //Holds the main game timer loop
    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        //resume music at position
        music.seekTo(musicpos);
        music.start();
        //create timer to move ball to new position
        mTmr = new Timer();
        long time = System.currentTimeMillis();

        //add the game updating to the timer schedule
        mTsk = new TimerTask() {
            public void run() {
                if (notPaused) {

                    //update arc position
                    tayArc.updatePosition();

                    //update arrow positions
                    for (int i = 0; i < drawList.size(); i++) {
                        Drawable d = drawList.get(i);

                        //if the arrow wasnt touched, reset multiplier and set miss
                        if (d.updatePosition()) {
                            multiplier.getAndSet(1);
                            hittype.getAndSet(0);
                        }
                        if (d.getClass().equals(Arrow.class)) {
                            Arrow a = (Arrow) d;
                            //remove outside arrows
                            if (a.outside) {
                                drawList.remove(i);
                                i--;
                            }
                        }
                    }

                    //update obstacle positions
                    for (int i = 0; i < obsList.size(); i++) {
                        Drawable d = obsList.get(i);

                        //if there was a new collision, lose 1 life
                        if (d.updatePosition()) {
                            lives.getAndDecrement();
                        }
                        Obstacle o = (Obstacle) d;
                        //remove outside obstacles
                        if (o.outside) {
                            obsList.remove(d);
                            i--;
                        }
                    }
                }

                //update the score
                final int currentscore = score.addAndGet(multiplier.get());
                final int currentlives = lives.get();
                final int currentmultiplier = multiplier.get();

                //set the recent hittype(miss,great,perfect) and put a timer on it to stop displaying
                if (hittimer != -1) {
                    hittimer++;
                }
                if (hittimer > 40) {
                    hittimer = -1;
                    hittype.getAndSet(-1);
                }

                //Run on the ui so that we can update the textviews for score, lives, multiplier
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //display lives
                        TextView livesText = (TextView) findViewById(R.id.lives);
                        livesText.setText("Lives: " + currentlives);

                        //if you die go to end screen
                        if (!endswap && currentlives <= 0) {
                            endswap = true;
                            endGame(currentscore);
                        }

                        //display score and multiplier
                        TextView scoreText = (TextView) findViewById(R.id.score);
                        scoreText.setText("Score: " + currentscore);
                        TextView multText = (TextView) findViewById(R.id.multiplier);
                        multText.setText("Combo: " + currentmultiplier);
                    }
                });

                //send obstacles twice as often the arrows
                if (ticks % 60 == 4) {
                    ObstacleGenerator obsGen = new ObstacleGenerator();
                    obsGen.generate(arcView, mScrWidth, mScrHeight, true);
                }

                //send the arrows based on bpm (not dynamic yet)
                if (ticks % 115 == 0) {
                    ArrowGenerator arrGen = new ArrowGenerator();
                    arrGen.generate(arcView, mScrWidth, mScrHeight);
                }
                ticks++;
                if (ticks > 1000) {
                    ticks = 1;
                }
            }
        }; // TimerTask

        mTmr.schedule(mTsk, 10, 10); //start timer
        super.onResume();
    } // onResume

    //Go to end screen with score
    public void endGame(int curscore) {
        Intent intent = new Intent(this, EndActivity.class);
        CharSequence curscoreseq = "" + curscore;
        intent.putExtra("Score", curscoreseq);
        startActivity(intent);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() //main thread stopped
    {
        //clean up tayarc
        tayArc.deleteInstance();
        super.onDestroy();
    }

    public void onBackPressed() {
        //pause the music w/position
        music.pause();
        musicpos = music.getCurrentPosition();
        super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }

    //Clear old arrow button arrays
    public void clearArrowArrays() {
        Buttons.rightArrows.clear();
        Buttons.leftArrows.clear();
        Buttons.upArrows.clear();
        Buttons.downArrows.clear();
    }


    //Do the processing for when a button is pressed
    public void arrowPressed(ArrayList<Arrow> arrows) {
        //If there are no arrows displayed, count a miss
        if (arrows.size() == 0) {
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            hittimer = 0;
            return;
        }

        //Use lowest arrow
        Arrow arrow = arrows.get(0);
        float ypos = arrow.getYPosition();
        //System.out.println("Arrow:"+ypos+" Y:"+Buttons.standard_Y);
        ypos -= mScrHeight / 11.7; // Calibrated for most phones, but may need to later add a calibration activity for edge cases
        //System.out.println("ypos:"+ypos+" Y:"+Buttons.standard_Y);

        //Do hit detection based on offset y position
        if (Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height / 4) { //perfect
            arrow.miss = false;
            multiplier.getAndAdd(2);
            hittype.getAndSet(2);
            arrows.remove(0);
        } else if (Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height) { //great
            arrow.miss = false;
            multiplier.getAndAdd(1);
            hittype.getAndSet(1);
            arrows.remove(0);
        } else { //miss
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
        }
        hittimer = 0;
    }

    ////Methods to use for touch listeners
    public void upPressed(View view) {
        arrowPressed(Buttons.upArrows);
    }

    public void rightPressed(View view) {
        arrowPressed(Buttons.rightArrows);
    }

    public void downPressed(View view) {
        arrowPressed(Buttons.downArrows);
    }

    public void leftPressed(View view) {
        arrowPressed(Buttons.leftArrows);
    }

}
