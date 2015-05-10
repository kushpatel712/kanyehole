package com.dropout.kanyehole;

/**
 * Created by Kush on 3/14/2015.
 */
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.Image;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
//
//
import android.os.Handler;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorEventListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GameActivity extends Activity {
    public FrameLayout mainView;
    public Arc kanyeArc = null;
    public ObjectView arcView = null;
    public boolean notPaused=true;
    public Timer mTmr = null;
    public TimerTask mTsk = null;
    private SurfaceRenderer mRenderer;
    private GLSurfaceView glSurfaceView;
    private int hits=0;
    int mScrWidth, mScrHeight;
    private MediaPlayer music;
    private int musicpos=0;
    private SensorEventListener listen;
    public ArrayList<Drawable> drawList;
    public ArrayList<Drawable> obsList;
    public AtomicInteger lives = new AtomicInteger(3);
    public AtomicInteger score = new AtomicInteger(0);
    public AtomicInteger multiplier = new AtomicInteger(1);
    public AtomicInteger hittype = new AtomicInteger(-1);
    public int hittimer = -1;
    public int ticks = 1;
    public boolean tayswap = false;
    public boolean endswap = false;
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(0xFFFFFFFF,
                LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_layout);

        MyApplication.setContext(this.getBaseContext());
        glSurfaceView =
                (GLSurfaceView)findViewById(R.id.glsurfaceview);
        // Set the Renderer for drawing on the GLSurfaceView
        // Create an OpenGL ES 2.0 context.
        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mRenderer = new SurfaceRenderer(this, mScrHeight, mScrWidth, false, this, null);
        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        music = MediaPlayer.create(MyApplication.getAppContext(),R.raw.flclean);
        music.start();

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        mScrWidth = display.getWidth();
        mScrHeight = display.getHeight();
        kanyeArc = Arc.getInstance();
        kanyeArc.setScrDims(mScrHeight, mScrWidth);
        kanyeArc.setPosition(mScrWidth, mScrHeight);

        arcView = new ObjectView(this, mScrWidth / 2, kanyeArc.getYPosition(), 50, 0, 0);
        drawList = arcView.getDrawList();
        obsList = arcView.getObsList();

        mainView = (android.widget.FrameLayout) findViewById(R.id.rgame);
        clearArrowArrays();
        initializeButtonCoordinates();
        mainView.addView(arcView); //add ball to main screen
        mainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {

                initializeButtonCoordinates()  ;

            }
        });
        ImageButton up=(ImageButton)findViewById(R.id.up);
        View.OnTouchListener upListener=new View.OnTouchListener(){
            public boolean onTouch(    View v,    MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    upPressed(v);
                }
                return false;
            }
        }
                ;
        ImageButton down=(ImageButton)findViewById(R.id.down);
        View.OnTouchListener downListener=new View.OnTouchListener(){
            public boolean onTouch(    View v,    MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    downPressed(v);
                }
                return false;
            }
        }
                ;
        ImageButton left=(ImageButton)findViewById(R.id.left);
        View.OnTouchListener leftListener=new View.OnTouchListener(){
            public boolean onTouch(    View v,    MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    leftPressed(v);
                }
                return false;
            }
        }
                ;
        ImageButton right=(ImageButton)findViewById(R.id.right);
        View.OnTouchListener rightListener=new View.OnTouchListener(){
            public boolean onTouch(    View v,    MotionEvent event){
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    rightPressed(v);
                }
                return false;
            }
        }
                ;
        up.setOnTouchListener(upListener);
        down.setOnTouchListener(downListener);
        left.setOnTouchListener(leftListener);
        right.setOnTouchListener(rightListener);

        listen = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                //set ball speed based on phone tilt (ignore Z axis)
                kanyeArc.setSpeed(event.values[0]/6,event.values[1]/6);
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
        music.pause();
        musicpos = music.getCurrentPosition();
        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;
        super.onPause();
    }

    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        music.seekTo(musicpos);
        music.start();
        mTmr = new Timer();
        long time=System.currentTimeMillis();
        mTsk = new TimerTask() {
            public void run(){
                if (notPaused) {

                    kanyeArc.updatePosition();

                    for (int i = 0; i < drawList.size();i++){
                        Drawable d = drawList.get(i);
                        boolean missed = d.updatePosition();

                        if (d.getClass().equals(Arrow.class)){
                            Arrow a = (Arrow) d;
                            if(missed){
                                multiplier.getAndSet(1);
                                hittype.getAndSet(0);

                            }
                            if (a.outside){
                                drawList.remove(i);
                                i--;
                            }
                        }
                    }
                    for (int i = 0; i < obsList.size();i++){
                        Drawable d = obsList.get(i);
                        if (d.updatePosition()){
                            lives.getAndDecrement();
                        }
                        Obstacle o = (Obstacle) d;
                        if (o.outside){
                            obsList.remove(d);
                            i--;
                        }
                    }
                }
                final int currentscore=score.addAndGet(multiplier.get());
                final int currentlives=lives.get();
                final int currentmultiplier=multiplier.get();

                if (hittimer != -1){
                    hittimer++;
                }
                if (hittimer > 40){
                    hittimer = -1;
                    hittype.getAndSet(-1);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                            TextView livesText = (TextView) findViewById(R.id.lives);
                            livesText.setText("Lives: "+currentlives);
                        if (!tayswap &&currentscore > 5000 && currentlives <= 0){
                            tayswap = !tayswap;
                            tayGame(currentscore, currentmultiplier);
                        }
                        if (!endswap &&currentscore <= 5000&&currentlives <= 0) {
                            endswap = !endswap;
                            endGame(currentscore);

                        }
                        TextView scoreText = (TextView) findViewById(R.id.score);
                        scoreText.setText("Score: "+currentscore);
                        TextView multText = (TextView) findViewById(R.id.multiplier);
                        multText.setText("Combo: "+currentmultiplier);
                    }
                });

                if (ticks %83 == 4){
                    ObstacleGenerator obsGen = new ObstacleGenerator();
                    obsGen.generate(arcView, mScrWidth, mScrHeight, false);
                }
                if (ticks %166 == 0){
                    ArrowGenerator arrGen = new ArrowGenerator();
                    arrGen.generate(arcView, mScrWidth, mScrHeight);
                }
                ticks++;
                if (ticks > 1000){
                    ticks = 1;
                }
            }
        }; // TimerTask

        mTmr.schedule(mTsk, 10, 10); //start timer
        super.onResume();
    } // onResume

    public void tayGame(int curscore, int curmult){
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Intent intent = new Intent(this, TayActivity.class);
        intent.putExtra("Score",curscore);
        intent.putExtra("Mult",curmult);
        startActivity(intent);
        finish();
    }
    public void endGame(int curscore){
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Intent intent = new Intent(this, EndActivity.class);
        intent.putExtra("Score",""+curscore);
        startActivity(intent);
        finish();
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() {
        Arc.deleteInstance();
        super.onDestroy();
    }


    public void onBackPressed(){
        music.pause();
        musicpos = music.getCurrentPosition();
        super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }

    public void pause(View view){
        hits++;
        if(hits%2==1) {
            notPaused = false;
            super.onPause();
        }
        else notPaused=true;


    }
    public void initializeButtonCoordinates(){
        Bitmap b = BitmapFactory.decodeResource(MyApplication.getAppContext().getResources(), R.drawable.arrowup);
        final ImageButton left = (ImageButton) mainView.findViewById(R.id.left);
        final ImageButton down = (ImageButton) mainView.findViewById(R.id.down);
        final ImageButton up = (ImageButton) mainView.findViewById(R.id.up);
        final ImageButton right = (ImageButton) mainView.findViewById(R.id.right);
        left.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        Buttons.standard_Y= (int)left.getY();
        Buttons.left_X= (int)left.getX();
        Buttons.down_X= (int)down.getX()+down.getWidth()-b.getWidth();
        Buttons.up_X= (int)up.getX();
        Buttons.right_X= (int)right.getX();
        Buttons.arrow_width=left.getWidth();
        Buttons.arrow_height=left.getHeight();
        Buttons.leftRect=new Rect(Buttons.left_X-5,Buttons.standard_Y-5,Buttons.left_X+b.getWidth()+5,Buttons.standard_Y+b.getHeight()+5);
        Buttons.upRect=new Rect(Buttons.up_X-5,Buttons.standard_Y-5,Buttons.up_X+b.getWidth()+5,Buttons.standard_Y+b.getHeight()+5);
        Buttons.downRect=new Rect(Buttons.down_X-5,Buttons.standard_Y-5,Buttons.down_X+b.getWidth()+5,Buttons.standard_Y+b.getHeight()+5);
        Buttons.rightRect=new Rect(Buttons.right_X-5,Buttons.standard_Y-5,Buttons.right_X+b.getWidth()+5,Buttons.standard_Y+b.getHeight()+5);
    }
    public void clearArrowArrays(){
        Buttons.rightArrows.clear();
        Buttons.leftArrows.clear();
        Buttons.upArrows.clear();
        Buttons.downArrows.clear();
    }
    public void arrowPressed(ArrayList<Arrow> arrows){
        if (arrows.size()==0){
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            hittimer = 0;
            return;
        }
        Arrow arrow = arrows.get(0);
        float ypos = arrow.getYPosition();
        ypos -= Buttons.arrow_height-40;
        if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/4){

            arrow.miss = false;
            multiplier.getAndAdd(2);
            hittype.getAndSet(2);
            arrows.remove(0);
            System.out.println("perfect"+ arrow.id);

        }
        else if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/1.1) {

            arrow.miss = false;
            multiplier.getAndAdd(1);
            hittype.getAndSet(1);
            arrows.remove(0);
            System.out.println("good"+ arrow.id);
        }
        else {
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            System.out.println("miss"+ arrow.id);
        }
        hittimer = 0;
    }
    public void upPressed(View view){
        arrowPressed(Buttons.upArrows);
    }
    public void rightPressed(View view){
        arrowPressed(Buttons.rightArrows);
    }
    public void

    downPressed(View view){
        arrowPressed(Buttons.downArrows);
    }
    public void leftPressed(View view){
        arrowPressed(Buttons.leftArrows);
    }
}
