package com.dropout.kanyehole;

/**
 * Created by Kush on 4/1/2015.
 */
public class Arc {
    private static Arc instance = null;
    android.graphics.PointF position, speed;
    double angle = 0;
    int mScrWidth, mScrHeight;
    private Arc(int mScrWidth, int mScrHeight ){
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        position.x = mScrWidth/2;
        position.y = mScrHeight/4;
        speed.x = 0;
        speed.y = 0;
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }

    public static Arc getInstance()
    {
        if(instance == null)
            instance = new Arc(0,0);
        return instance;
    }

    public void setXPosition(float x){
        position.x = x;
    }
    public void setYPosition(float y){
        position.y = y;
    }
    public void setPosition(float x, float y){
        position.x = x;
        position.y = y;
    }
    public void updatePosition(ArcView v){
        position.x += speed.x;
        position.y += speed.y;
        angle += speed.x;
        float newX = (float) (mScrWidth / 2 + 50 * Math.sin(angle));
        float newY = (float) (mScrHeight / 4 + 50 * Math.cos(angle));
        //if ball goes off screen, reposition to opposite side of screen
        //update ball class instance
        v.setPosition(newX,newY);
        v.setAngle(angle);
        int count = 0;
//        if (((newX / position.x) > .95 && (newX / position.x) < 1.05) &&
//                ((newY / position.y) > .95 && (newY / position.y) < 1.05)) {
//            position.x = mScrWidth;
//            position.y = mScrHeight;
//
//            //TextView solveText2 = (TextView) findViewById(R.id.score);
//            scores++;
//            //solveText2.setText(Integer.toString(scores));
//        } else {
//            //TextView solveText2 = (TextView) findViewById(R.id.score);
//            //if(count==0){
//            //solveText2.setText("dfgdf");
//            //count++;
//            //}
//            //System.out.println("no");
//        }

        //redraw ball. Must run in background thread to prevent thread lock.
//        RedrawHandler.post(new Runnable() {
//            public void run() {
//                mBallView2.invalidate();
//            }
//        });
    }
    public float getXPosition(){
        return position.x;
    }
    public float getYPosition(){
        return position.y;
    }
    public void setSpeed(float xSpeed, float ySpeed){
        speed.x = xSpeed;
        speed.y = ySpeed;
    }

}
