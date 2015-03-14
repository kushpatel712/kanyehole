package com.dropout.kanyehole;

/**
 * Created by Kush on 3/14/2015.
 */
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;//
//
//
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorEventListener;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends ActionBarActivity {


    ArcView mBallView2 = null;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr = null;
    TimerTask mTsk = null;
    int scores = 0;
    int mScrWidth, mScrHeight;
    android.graphics.PointF mBallPos, mBallSpd;
    static double angle = 0;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //hide title bar
        getWindow().setFlags(0xFFFFFFFF,
                LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        setContentView(R.layout.game_layout);
        //create pointer to main screen
       // final FrameLayout mainView = (android.widget.FrameLayout) findViewById(R.id.rgame);

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        mScrWidth = display.getWidth();
        mScrHeight = display.getHeight();
        mBallPos = new android.graphics.PointF();
        mBallSpd = new android.graphics.PointF();

        //create variables for ball position and speed
        mBallPos.x = mScrWidth / 2;
        mBallPos.y = mScrHeight / 2;
        mBallSpd.x = 0;
        mBallSpd.y = 0;

        mBallView2 = new ArcView(this, mScrWidth / 2, mBallPos.y, 50, 0, angle, mScrWidth / 2, mScrHeight / 2);
        final FrameLayout mainView = (android.widget.FrameLayout) findViewById(R.id.rgame);

        mainView.addView(mBallView2); //add ball to main screen
        mBallView2.invalidate(); //call onDraw in BallView
        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                new SensorEventListener() {
                    @Override
                    public void onSensorChanged(SensorEvent event) {
                        //set ball speed based on phone tilt (ignore Z axis)
                        mBallSpd.x = -event.values[0];
                        mBallSpd.y = event.values[1];
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
    }

    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        //create timer to move ball to new position
        mTmr = new Timer();
        mTsk = new TimerTask() {
            public void run() {

                mBallPos.x += mBallSpd.x;
                mBallPos.y += mBallSpd.y;
                angle += mBallSpd.x;
                float x = (float) (mScrWidth / 2 + 50 * Math.sin(angle));
                float y = (float) (mScrHeight / 2 + 50 * Math.cos(angle));
                //if ball goes off screen, reposition to opposite side of screen
                if (mBallPos.x > mScrWidth) mBallPos.x = 0;
                if (mBallPos.y > mScrHeight) mBallPos.y = 0;
                if (mBallPos.x < 0) mBallPos.x = mScrWidth;
                if (mBallPos.y < 0) mBallPos.y = mScrHeight;
                //update ball class instance
                mBallView2.mX = x;
                mBallView2.mY = y;
                int count = 0;
                if (((x / mBallPos.x) > .95 && (x / mBallPos.x) < 1.05) &&
                        ((y / mBallPos.y) > .95 && (y / mBallPos.y) < 1.05)) {
                    mBallPos.x = mScrWidth;
                    mBallPos.y = mScrHeight;

                    //TextView solveText2 = (TextView) findViewById(R.id.score);
                    scores++;
                    //solveText2.setText(Integer.toString(scores));
                } else {
                    //TextView solveText2 = (TextView) findViewById(R.id.score);
                    //if(count==0){
                    //solveText2.setText("dfgdf");
                    //count++;
                    //}
                    //System.out.println("no");
                }

                //redraw ball. Must run in background thread to prevent thread lock.
                RedrawHandler.post(new Runnable() {
                    public void run() {
                        mBallView2.invalidate();
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
}

