package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.widget.TextView;

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
    float headX;
    float headY;
    int mScrWidth, mScrHeight;
    double radius;
    Context context=MyApplication.getAppContext();
    Bitmap b= BitmapFactory.decodeResource(context.getResources(), R.drawable.smallhead);
    Bitmap logo= BitmapFactory.decodeResource(context.getResources(), R.drawable.gamelogo);
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public void draw(Canvas canvas, int height, int width){
        int yyyy = height/4;
        int xxxx = width/2;
        radius=xxxx*.7;
        RectF hi=new RectF(xxxx-xxxx/5,yyyy-xxxx/5,yyyy+xxxx/5,yyyy-+xxxx/5);
        paint.setStrokeWidth(10);
        float angs= (float)angle;
        angs+=0.5f;
        //canvas.drawArc(new RectF(xxxx-(xxxx*2/5),yyyy-(xxxx*2/5),xxxx+(xxxx*2/5),yyyy+(xxxx*2/5)), (angs-30)%360, 60, false, paint);
        //System.out.println("angle:"+((angs-30)%360));
        //System.out.println("cos:"+(double) (Math.cos(((angs-30)%360))));
        //System.out.println(radius);
       // canvas.drawBitmap(b,,((float)(mScrHeight / 2 + 50 * Math.sin(angle))));
        //float headX = (float)(xxxx);
        //System.out.println(headX);
        headX=(float)(xxxx-this.bitWidth()/2 + radius * Math.cos((angs*2*Math.PI/360)));
        //float nX=float)(xxxx + 100 * Math.cos((angs)%360));
        //float headX=-(float) (Math.cos(angs)*xxxx*.6);
        //System.out.println("x:" + (xxxx-(xxxx*2/5))+ " headx:"+headX + " angle:" +angle +" sin:"+Math.sin(angle)+" arcang:"+((angs-30)%360));
        headY=((float)(yyyy-this.bitHeight()/2 + radius * Math.sin((angs*2*Math.PI/360))));
        //float headY=-(float) (Math.sin(angs)*yyyy*.6);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.MAGENTA);
        canvas.drawCircle((float)xxxx,(float)yyyy,(float)radius,paint);
        canvas.drawBitmap(b,headX,headY,paint);
        canvas.drawBitmap(logo,(float)xxxx-logo.getWidth()/2,(float)yyyy-logo.getHeight()/2,paint);
        //canvas.drawRect(new Rect(getheadX()+1,getheadY()+1,getheadX()+bitWidth()*29/30-2,getheadY()+bitHeight()*2/3-2),paint);
        paint.setColor(Color.GRAY);
        //canvas.drawRect(new Rect(getheadX()+1+bitWidth()/10,getheadY()+bitHeight()*2/3-2,getheadX()+bitWidth()*22/30-2,getheadY()+bitHeight()*29/30),paint);


        //System.out.println("Drawing head at x crd: "+ headX);
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
    public void updatePosition(ObjectView v){
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

    public int getheadX(){
        return (int)headX;
    }
    public int getheadY(){
        return (int)headY;
    }
    public int bitWidth(){
        return  b.getWidth();
    }
    public int bitHeight(){
        return  b.getHeight();
    }
}
