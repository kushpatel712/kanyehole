package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Created by Kush on 4/1/2015.
 */
public class Arc implements Drawable{
    /*
    * TODO: Make me a Flyweight! :D
    * */
   // public class Obstacles
    //{
    //}
    private static Arc instance = new Arc(0,0);
    android.graphics.PointF position, speed;
    double angle = 0;
    int mScrWidth, mScrHeight;
    Context context=MyApplication.getAppContext();
   // Bitmap b= BitmapFactory.decodeResource(context.getResources(), R.drawable.smallhead);
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public void draw(Canvas canvas, int height, int width){
        paint.setColor(Color.GREEN);
        int yyyy = height/4;
        int xxxx = width/2;
        RectF hi=new RectF(xxxx-xxxx/5,yyyy-xxxx/5,yyyy+xxxx/5,yyyy-+xxxx/5);
        paint.setStrokeWidth(10);
        float angs= (float)angle;
        angs+=0.5f;
        canvas.drawArc(new RectF(xxxx-(xxxx*2/5),yyyy-(xxxx*2/5),xxxx+(xxxx*2/5),yyyy+(xxxx*2/5)), (angs-30)%360, 60, false, paint);
       // canvas.drawBitmap(b,position.x,position.y,paint);
    }
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
    }
    public float getXPosition(){
        return position.x;
    }
    public double getAngle(){return angle;}
    public float getYPosition(){
        return position.y;
    }
    public float getXSpeed(){
        return speed.x;
    }
    public float getYSpeed(){
        return speed.y;
    }
    public void setSpeed(float xSpeed, float ySpeed){
        speed.x = xSpeed;
        speed.y = ySpeed;
    }

}
