package com.dropout.kanyehole;

/**
 * This Class is for the Main Game Activity
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Rect;
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
import android.view.ViewTreeObserver;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class GameActivity extends Activity {
    private FrameLayout mainView;
    public Arc kanyeArc = null;
    private ObjectView arcView = null;
    private boolean notPaused = true;
    private Timer mTmr = null;
    private TimerTask mTsk = null;
    public ArrayList<Drawable> drawList;
    public ArrayList<Drawable> obsList;
    private AtomicInteger lives = new AtomicInteger(3);
    private AtomicInteger score = new AtomicInteger(0);
    private AtomicInteger multiplier = new AtomicInteger(1);
    public AtomicInteger hittype = new AtomicInteger(-1);
    private int hittimer = -1;
    private int ticks = 1;
    private boolean endswap = false;
    private int mScrWidth, mScrHeight;
    private SurfaceRenderer mRenderer;
    private GLSurfaceView glSurfaceView;
    private MediaPlayer music;
    private int musicpos = 0;
    private SensorEventListener listen;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(0xFFFFFFFF,
                LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        /* Much of the initialization for OPENGL surfaceview and renderer and parameters for drawing, as well as the
        way for drawing using vertices, indexes and uv matrices taken/modified from a set of GLES2.0
         Tutorials that can be found (part 1-6) at:

        http://androidblog.reindustries.com/a-real-open-gl-es-2-0-2d-tutorial-part-1/

        */
        glSurfaceView =
                (GLSurfaceView) findViewById(R.id.glsurfaceview);
        // Set the Renderer for drawing on the GLSurfaceView
        // Create an OpenGL ES 2.0 context.
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mRenderer = new SurfaceRenderer(this, mScrHeight, mScrWidth, false, this, null);
        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        //Setup the music for the game
        music = MediaPlayer.create(this, R.raw.flclean);
        music.start();

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        mScrWidth = display.getWidth();
        mScrHeight = display.getHeight();

        //Create the arc
        kanyeArc = Arc.getInstance();
        kanyeArc.setScrDims(mScrHeight, mScrWidth);
        kanyeArc.setPosition(mScrWidth, mScrHeight);

        //Setup the objectview and grab the draw lists
        arcView = new ObjectView(this, mScrWidth / 2, kanyeArc.getYPosition(), 50, 0, 0);
        drawList = arcView.getDrawList();
        obsList = arcView.getObsList();
        mainView = (android.widget.FrameLayout) findViewById(R.id.rgame);

        //Clear any existing arrow arrays for the arrow buttons since they are static
        clearArrowArrays();
        //initialize the arrow button coordinates
        initializeButtonCoordinates();

        mainView.addView(arcView); //add the view to screen
        //keep initializing the button coordinates based on viewtree
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {

                initializeButtonCoordinates();

            }
        });

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

        //Add the listener for the accelerometer for the arc
        listen = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //set ball speed based on phone tilt (ignore Z axis)
                kanyeArc.setSpeed(event.values[0] / 6, event.values[1] / 6);
                //timer event will redraw ball
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            } //ignore this event
        };
        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(listen
                ,
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
        mTmr = new Timer();
        long time = System.currentTimeMillis();

        //add the game updating to the timer schedule
        mTsk = new TimerTask() {
            public void run() {
                if (notPaused) {

                    //update arc position
                    kanyeArc.updatePosition();

                    //update arrow positions
                    for (int i = 0; i < drawList.size(); i++) {
                        Drawable d = drawList.get(i);
                        boolean missed = d.updatePosition();

                        if (d.getClass().equals(Arrow.class)) {
                            Arrow a = (Arrow) d;
                            //if the arrow wasnt touched, reset multiplier and set miss
                            if (missed) {
                                multiplier.getAndSet(1);
                                hittype.getAndSet(0);

                            }
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

                        //if you die with enough score, start taylor mode
                        if (!endswap && currentscore > 5000 && currentlives <= 0) {
                            endswap = true;
                            tayGame(currentscore, currentmultiplier);
                        }

                        //if you die with not enough score, go to the end screen
                        if (!endswap && currentscore <= 5000 && currentlives <= 0) {
                            endswap = true;
                            endGame(currentscore);

                        }

                        //display score, multiplier
                        TextView scoreText = (TextView) findViewById(R.id.score);
                        scoreText.setText("Score: " + currentscore);
                        TextView multText = (TextView) findViewById(R.id.multiplier);
                        multText.setText("Combo: " + currentmultiplier);
                    }
                });

                //send obstacles twice as fast as arrows
                if (ticks % 83 == 4) {
                    ObstacleGenerator obsGen = new ObstacleGenerator();
                    obsGen.generate(arcView, mScrWidth, mScrHeight, false);
                }

                //send arrows based on bpm (not dynamic yet)
                if (ticks % 166 == 0) {
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

    //Go to taylor game and send score/multiplier
    public void tayGame(int curscore, int curmult) {
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Intent intent = new Intent(this, TayActivity.class);
        intent.putExtra("Score", curscore);
        intent.putExtra("Mult", curmult);
        startActivity(intent);
        finish();
    }

    //Go to the end screen with score
    public void endGame(int curscore) {
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("Score", "" + curscore);
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() {
        //clean up arc
        Arc.deleteInstance();
        super.onDestroy();
    }


    public void onBackPressed() {
        //pause the music w/place
        music.pause();
        musicpos = music.getCurrentPosition();
        super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }

    //Iniitalize button variables for statics
    public void initializeButtonCoordinates() {
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.arrowup);
        final ImageButton left = (ImageButton) mainView.findViewById(R.id.left);
        final ImageButton down = (ImageButton) mainView.findViewById(R.id.down);
        final ImageButton up = (ImageButton) mainView.findViewById(R.id.up);
        final ImageButton right = (ImageButton) mainView.findViewById(R.id.right);
        left.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Buttons.standard_Y = (int) left.getY();
        Buttons.left_X = (int) left.getX();
        Buttons.down_X = (int) down.getX() + down.getWidth() - b.getWidth();
        Buttons.up_X = (int) up.getX();
        Buttons.right_X = (int) right.getX();
        Buttons.arrow_width = left.getWidth();
        Buttons.arrow_height = left.getHeight();
        Buttons.leftRect = new Rect(Buttons.left_X - 5, Buttons.standard_Y - 5, Buttons.left_X + b.getWidth() + 5, Buttons.standard_Y + b.getHeight() + 5);
        Buttons.upRect = new Rect(Buttons.up_X - 5, Buttons.standard_Y - 5, Buttons.up_X + b.getWidth() + 5, Buttons.standard_Y + b.getHeight() + 5);
        Buttons.downRect = new Rect(Buttons.down_X - 5, Buttons.standard_Y - 5, Buttons.down_X + b.getWidth() + 5, Buttons.standard_Y + b.getHeight() + 5);
        Buttons.rightRect = new Rect(Buttons.right_X - 5, Buttons.standard_Y - 5, Buttons.right_X + b.getWidth() + 5, Buttons.standard_Y + b.getHeight() + 5);
    }

    //clear old arrays for buttons
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
