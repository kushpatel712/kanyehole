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
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.*;
//
//
import android.os.Handler;
import android.view.WindowManager.LayoutParams;
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

public class TayActivity extends Activity {
    public FrameLayout mainView;
    public static TayArc tayArc = null;
    public ObjectView arcView = null;
    public boolean notPaused=true;
    public Timer mTmr = null;
    public TimerTask mTsk = null;

    private int hits=0;
    int mScrWidth, mScrHeight;
    private MediaPlayer music;
    private int musicpos=0;
    public ArrayList<Drawable> drawList;
    public ArrayList<Drawable> obsList;
    public AtomicInteger lives = new AtomicInteger(1);
    public AtomicInteger score = new AtomicInteger(0);
    public AtomicInteger multiplier = new AtomicInteger(1);
    public AtomicInteger hittype = new AtomicInteger(-1);
    public int hittimer = -1;
    public int ticks = 1;
    private boolean endswap = false;
    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(0xFFFFFFFF,
                LayoutParams.FLAG_FULLSCREEN | LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        int oldscore = intent.getIntExtra("Score",0);
        int oldmult = intent.getIntExtra("Mult",0);
        Intent endintent = new Intent("finish_activity");
        sendBroadcast(intent);
        score.getAndSet(oldscore);
        multiplier.getAndSet(oldmult);

        setContentView(R.layout.tay_layout);
        MyApplication.setContext(this.getBaseContext());
        GLSurfaceView glSurfaceView =
                (GLSurfaceView)findViewById(R.id.glsurfaceview);
        // Set the Renderer for drawing on the GLSurfaceView
        // Create an OpenGL ES 2.0 context.
        glSurfaceView.setEGLContextClientVersion(2);
        //setZOrderOnTop(true);
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);

        glSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        // Set the Renderer for drawing on the GLSurfaceView
        SurfaceRenderer mRenderer = new SurfaceRenderer(this, mScrHeight, mScrWidth, true, null, this);
        glSurfaceView.setRenderer(mRenderer);
        // Render the view only when there is a change in the drawing data
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        //create pointer to main screen
        // final FrameLayout mainView = (android.widget.FrameLayout) findViewById(R.id.rgame);
        music = MediaPlayer.create(MyApplication.getAppContext(),R.raw.ybm);
        music.start();

        //get screen dimensions
        Display display = getWindowManager().getDefaultDisplay();
        mScrWidth = display.getWidth();
        mScrHeight = display.getHeight();
        tayArc = TayArc.getInstance();
        tayArc.setScrDims(mScrHeight, mScrWidth);
        tayArc.setPosition(mScrWidth, mScrHeight);

        arcView = new ObjectView(this, mScrWidth / 2, tayArc.getYPosition(), 50, 0, 0);
        drawList = arcView.getDrawList();
        obsList = arcView.getObsList();

//        BroadcastReceiver broadcast_reciever = new BroadcastReceiver() {
//
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                String action = intent.getAction();
//                if (action.equals("finish_activity")) {
//                    unregisterReceiver(this);
//                    finish();
//                }
//            }
//        };
//        registerReceiver(broadcast_reciever, new IntentFilter("finish_activity"));

        mainView = (android.widget.FrameLayout) findViewById(R.id.rtay);
       // initializeButtonCoordinates();
        clearArrowArrays();
        mainView.addView(arcView); //add ball to main screen
//        mainView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//
//            public void onGlobalLayout() {
//
//                initializeButtonCoordinates()  ;
//
//            }
//        });

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

//    //listener for menu button on phone
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add("Exit"); //only one menu item
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    //listener for menu item clicked
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        if (item.getTitle() == "Exit") //user clicked Exit
//            finish(); //will call onPause
//        return super.onOptionsItemSelected(item);
//    }

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
        //music.release();
    }

    @Override
    public void onResume() //app moved to foreground (also occurs at app startup)
    {
        music.seekTo(musicpos);
        music.start();
        //create timer to move ball to new position
        mTmr = new Timer();
        long time=System.currentTimeMillis();
        mTsk = new TimerTask() {
            public void run(){
                if (notPaused) {

                    tayArc.updatePosition();
                    //System.out.println(kanyeArc.getXPosition()+" "+kanyeArc.getYPosition());

                    for (int i = 0; i < drawList.size();i++){
                        Drawable d = drawList.get(i);
                        if(d.updatePosition()){
                            multiplier.getAndSet(1);
                            hittype.getAndSet(0);
                        }
                        if (d.getClass().equals(Arrow.class)){
                            Arrow a = (Arrow) d;
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
                //final boolean updatelives = liveschange;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // if (updatelives){
                        TextView livesText = (TextView) findViewById(R.id.lives);
                        livesText.setText("Lives: "+currentlives);
                        if (!endswap && currentlives <= 0) {
                            //    finish();
                            endGame(currentscore);

                        }
                        // }
                        TextView scoreText = (TextView) findViewById(R.id.score);
                        scoreText.setText("Score: "+currentscore);
                        TextView multText = (TextView) findViewById(R.id.multiplier);
                        multText.setText("Combo: "+currentmultiplier);
                    }
                });

                if (ticks %60 == 4){
                    ObstacleGenerator obsGen = new ObstacleGenerator();
                    obsGen.generate(arcView, mScrWidth, mScrHeight, true);
                }
                if (ticks %115 == 0){
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

    public void endGame(int curscore){
        Intent intent = new Intent(this, EndActivity.class);
        CharSequence curscoreseq = ""+curscore;
        intent.putExtra("Score",curscoreseq);
        startActivity(intent);
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onDestroy() //main thread stopped
    {
        tayArc.deleteInstance();
        super.onDestroy();
        //System.runFinalizersOnExit(true); //wait for threads to exit before clearing app
        //android.os.Process.killProcess(android.os.Process.myPid());  //remove app from memory
    }

    //listener for config change.
    //This is called when user tilts phone enough to trigger landscape view
    //we want our app to stay in portrait view, so bypass event
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
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
        System.out.println("fdg: "+up.getLeft()+" Width/height:"+mScrWidth+"/"+mScrHeight);
        left.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        System.out.print("Size: "+b.getWidth());
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
        System.out.println("Left:"+Buttons.left_X+" "+"Down:"+Buttons.down_X+" "+"Up:"+Buttons.up_X+" "+"Right:"+Buttons.right_X+" ");
        System.out.println("Actual Left:"+left.getX()+" "+"Down:"+down.getX()+" "+"Up:"+up.getX()+" "+"Right:"+right.getLeft()+" ");
        System.out.println("end "+drawList.size()+" ");

    }
    public void clearArrowArrays(){
        Buttons.rightArrows.clear();
        Buttons.leftArrows.clear();
        Buttons.upArrows.clear();
        Buttons.downArrows.clear();
    }

    public void upPressed(View view){
        if (Buttons.upArrows.size()==0){
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            hittimer = 0;
            return;
        }
        Arrow arrow = Buttons.upArrows.get(0);
        float ypos = arrow.getYPosition();
        ypos -= Buttons.arrow_height+60;
        System.out.println("Up: "+ypos +" Image: "+Buttons.standard_Y + " ArrowHeight: " + Buttons.arrow_height);
        if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/4){
            System.out.println("perfect");
            multiplier.getAndAdd(2);
            hittype.getAndSet(2);
            arrow.miss = false;
            Buttons.upArrows.remove(0);

        }
        else if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/1.1) {
            System.out.println("good");
            multiplier.getAndAdd(1);
            hittype.getAndSet(1);
            arrow.miss = false;
            Buttons.upArrows.remove(0);
        }
        else {
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            System.out.println("miss");
        }
        hittimer = 0;
    }
    public void rightPressed(View view){
        if (Buttons.rightArrows.size()==0){
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            hittimer = 0;
            return;
        }
        Arrow arrow = Buttons.rightArrows.get(0);
        float ypos = arrow.getYPosition();
        ypos -= Buttons.arrow_height+60;
        System.out.println("Right: "+ypos +" Image: "+Buttons.standard_Y+ " ArrowHeight: " + Buttons.arrow_height);
        if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/4){
            System.out.println("perfect");
            arrow.miss = false;
            multiplier.getAndAdd(2);
            hittype.getAndSet(2);
            Buttons.rightArrows.remove(0);

        }
        else if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/1.1) {
            System.out.println("good");
            arrow.miss = false;
            multiplier.getAndAdd(1);
            hittype.getAndSet(1);
            Buttons.rightArrows.remove(0);
        }
        else {
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            System.out.println("miss");
        }
        hittimer = 0;
    }
    public void downPressed(View view){
        if (Buttons.downArrows.size()==0){
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            hittimer = 0;
            return;
        }
        Arrow arrow = Buttons.downArrows.get(0);
        float ypos = arrow.getYPosition();
        ypos -= Buttons.arrow_height+60;
        System.out.println("Down: "+ypos +" Image: "+Buttons.standard_Y+ " ArrowHeight: " + Buttons.arrow_height);
        if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/4){
            System.out.println("perfect");
            arrow.miss = false;
            multiplier.getAndAdd(2);
            hittype.getAndSet(2);
            Buttons.downArrows.remove(0);

        }
        else if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/1.1) {
            System.out.println("good");
            arrow.miss = false;
            multiplier.getAndAdd(1);
            hittype.getAndSet(1);
            Buttons.downArrows.remove(0);
        }
        else {
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            System.out.println("miss");
        }
        hittimer = 0;
    }
    public void leftPressed(View view){
        if (Buttons.leftArrows.size()==0){
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            hittimer = 0;
            return;
        }
        Arrow arrow = Buttons.leftArrows.get(0);
        float ypos = arrow.getYPosition();
        ypos -= Buttons.arrow_height+60;
        System.out.println("Left: "+ypos +" Image: "+Buttons.standard_Y+ " ArrowHeight: " + Buttons.arrow_height);
        if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/4){
            System.out.println("perfect");
            arrow.miss = false;
            multiplier.getAndAdd(2);
            hittype.getAndSet(2);
            Buttons.leftArrows.remove(0);
        }
        else if(Math.abs(Buttons.standard_Y - ypos) <= Buttons.arrow_height/1.1) {
            System.out.println("good");
            arrow.miss = false;
            multiplier.getAndAdd(1);
            hittype.getAndSet(1);
            Buttons.leftArrows.remove(0);
        }
        else {
            multiplier.getAndSet(1);
            hittype.getAndSet(0);
            System.out.println("miss");
        }
        hittimer = 0;
    }
}
