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
import android.os.Handler;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.SensorEventListener;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends ActionBarActivity {

    private Arc kanyeArc = null;
    private ArcView arcView = null;
    Handler RedrawHandler = new Handler(); //so redraw occurs in main thread
    Timer mTmr = null;
    TimerTask mTsk = null;
    int scores = 0;
    int mScrWidth, mScrHeight;



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
        kanyeArc = Arc.getInstance();
        kanyeArc.position.set(mScrWidth,mScrHeight);


        //create variables for ball position and speed


        arcView = new ArcView(this, mScrWidth / 2, kanyeArc.getYPosition(), 50, 0, 0);
        final FrameLayout mainView = (android.widget.FrameLayout) findViewById(R.id.rgame);

        mainView.addView(arcView); //add ball to main screen
        arcView.invalidate(); //call onDraw in BallView
        //listener for accelerometer, use anonymous class for simplicity
        ((SensorManager) getSystemService(Context.SENSOR_SERVICE)).registerListener(
                        new SensorEventListener() {
                            @Override
                            public void onSensorChanged(SensorEvent event) {
                                //set ball speed based on phone tilt (ignore Z axis)
                                kanyeArc.setSpeed(event.values[0],event.values[1]);
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
                kanyeArc.updatePosition(arcView);
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
}

