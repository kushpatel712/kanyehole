package com.dropout.kanyehole;

/**
 * Created by Kush on 3/14/2015.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;

import java.util.Timer;
import java.util.TimerTask;

//
//


public class TayActivity extends Activity {

    private Arc kanyeArc = null;
    private Object mPauseLock=new Object();
    private Obstacle circle=null;
    private ObjectView arcView = null;
    public boolean notPaused=true;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr = null;
    TimerTask mTsk = null;
    int scores = 0;
    int hits=0;
    int mScrWidth, mScrHeight;
    public int score = 0;
    public int lives = 3;
    MediaPlayer music;
    private Bitmap b;
    private Bitmap bred;
    private Bitmap bcyan;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        getWindow().setFlags(0xFFFFFFFF,
                LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.tay_layout);
        MyApplication.setContext(this.getBaseContext());
        //create pointer to main screen
       // final FrameLayout mainView = (android.widget.FrameLayout) findViewById(R.id.rgame);
        music = MediaPlayer.create(MyApplication.getAppContext(),R.raw.ybm);
        music.start();
        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        mScrWidth = display.getWidth();
        mScrHeight = display.getHeight();
        kanyeArc = Arc.getInstance();
        kanyeArc.setPosition(mScrWidth, mScrHeight);
        Bitmap pic= BitmapFactory.decodeResource(getResources(), R.drawable.tswift);
        Bitmap logo= BitmapFactory.decodeResource(getResources(), R.drawable.taylogo);
        kanyeArc.setPicture(pic);
        kanyeArc.setLogo(logo);
        kanyeArc.setArcColor(Color.CYAN);
        b=BitmapFactory.decodeResource(getResources(), R.drawable.smallhead);
        bred= BitmapFactory.decodeResource(getResources(), R.drawable.smallheadred);
        bcyan= BitmapFactory.decodeResource(getResources(), R.drawable.smallheadcyan);
        //circle= new Obstacle(10,mScrWidth,mScrHeight);


        //create variables for ball position and speed


        arcView = new ObjectView(this, mScrWidth / 2, kanyeArc.getYPosition(), 50, 0, 0);
        arcView.registerDrawable(kanyeArc);

        //// NEED TO USE ACTUAL IMAGE BUTTONS
        Arrow rbutton = new Arrow(90,mScrWidth, mScrHeight);
        rbutton.setSpeed(0,0);
        Arrow lbutton = new Arrow(270,mScrWidth, mScrHeight);
        lbutton.setSpeed(0,0);
        Arrow ubutton = new Arrow(0,mScrWidth, mScrHeight);
        ubutton.setSpeed(0,0);
        Arrow dbutton = new Arrow(180,mScrWidth, mScrHeight);
        dbutton.setSpeed(0,0);
        System.out.println("Screen xy: " + mScrWidth + " " + mScrHeight);
        rbutton.setXPosition((float) (mScrWidth * 3.0 / 4));
        System.out.println("rightbutton xy: " + (mScrWidth * 3.0 / 4) + " " + (mScrHeight * 4.75 / 6));
        rbutton.setYPosition((float) (mScrHeight * 4.75 / 6));
        lbutton.setXPosition((float) (mScrWidth * 0.0 / 4));
        System.out.println("leftbutton x: " + (mScrWidth * 0.0 / 4));
        lbutton.setYPosition((float) (mScrHeight * 4.75 / 6));
        ubutton.setXPosition((float)(mScrWidth*2.0/4));
        System.out.println("upbutton x: "+(mScrWidth*2.0/4));
        ubutton.setYPosition((float)(mScrHeight* 4.75/6));
        dbutton.setXPosition((float)(mScrWidth*1.0/4));
        System.out.println("downbutton x: "+(mScrWidth*1.0/4));
        dbutton.setYPosition((float)(mScrHeight* 4.75/6));
        arcView.registerDrawable(rbutton);
        arcView.registerDrawable(lbutton);
        arcView.registerDrawable(ubutton);
        arcView.registerDrawable(dbutton);

        ////

        //ObstacleGenerator obsGen = new ObstacleGenerator();
        //obsGen.generate(arcView,mScrWidth,mScrHeight);
        final FrameLayout mainView = (FrameLayout) findViewById(R.id.rgame);

        mainView.addView(arcView); //add ball to main screen
        arcView.invalidate(); //call onDraw in BallView
        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                        new SensorEventListener() {
                            @Override
                            public void onSensorChanged(SensorEvent event) {
                                //set ball speed based on phone tilt (ignore Z axis)
                                kanyeArc.setSpeed(event.values[0]/3,event.values[1]/3);
                                //timer event will redraw ball
                            }

                            @Override
                            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                            } //ignore this event
                        },
                        ((SensorManager) getSystemService(Context.SENSOR_SERVICE))
                                .getSensorList(Sensor.TYPE_ACCELEROMETER).get(0), SensorManager.SENSOR_DELAY_NORMAL);



    }

    //listener for menu button on phone
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Exit"); //only one menu item
        return super.onCreateOptionsMenu(menu);
    }

    //listener for menu item clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        if (item.getTitle() == "Exit") //user clicked Exit
            finish(); //will call onPause
        return super.onOptionsItemSelected(item);
    }

    //For state flow see http://developer.android.com/reference/android/app/Activity.html
    @Override
    public void onPause() //app moved to background, stop background threads
    {
        mTmr.cancel(); //kill\release timer (our only background thread)
        mTmr = null;
        mTsk = null;
        super.onPause();
        music.release();
    }


    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        //create timer to move ball to new position
        mTmr = new Timer();
        long time=System.currentTimeMillis();
        mTsk = new TimerTask() {
            public void run() {
                if (notPaused)
                    kanyeArc.updatePosition(arcView);
                if (System.currentTimeMillis()%200==199){
                    ObstacleGenerator obsGen = new ObstacleGenerator();
                    obsGen.generate(arcView, mScrWidth, mScrHeight, b, bred, bcyan);
                }
                if (System.currentTimeMillis()%200==99){
                    ArrowGenerator arrGen = new ArrowGenerator();
                    arrGen.generate(arcView, mScrWidth, mScrHeight);
                }
                    //circle.updatePosition(arcView);
                    RedrawHandler.post(new Runnable() {
                        public void run() {
                            arcView.invalidate();
                        }
                    });

            }
        }; // TimerTask

        mTmr.schedule(mTsk, 10, 10); //start timer
        super.onResume();
    } // onResume

    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() //main thread stopped
    {
        super.onDestroy();
        System.runFinalizersOnExit(true); //wait for threads to exit before clearing app
        android.os.Process.killProcess(android.os.Process.myPid());  //remove app from memory
    }

    //listener for config change.
    //This is called when user tilts phone enough to trigger landscape view
    //we want our app to stay in portrait view, so bypass event
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    public void onBackPressed(){
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
}

