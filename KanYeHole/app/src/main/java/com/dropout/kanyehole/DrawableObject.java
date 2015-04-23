package com.dropout.kanyehole;

/**
 * Created by Kush on 4/1/2015
 */
public abstract class DrawableObject implements Drawable{
    /*
    * TODO: Make me a Flyweight! :D
    * */
    android.graphics.PointF position, speed;
    double angle = 0;
    int mScrWidth, mScrHeight;
    public DrawableObject(int startAngle, int mScrWidth, int mScrHeight){
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        position.x = mScrWidth/2;
        position.y = mScrHeight/4;
        speed.x = 10*50 * (float) (Math.sin(startAngle));
        speed.y = 10*50 * (float)(Math.cos(startAngle));
        angle=startAngle;
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
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
        // setSpeed(speed.x,speed.y);
        position.x = Math.abs((position.x +speed.x)%mScrWidth);
        position.y =Math.abs((position.y+ speed.y)%mScrHeight);
        v.setObjectPosition(position.x, position.y);
        //System.out.println(position.x+" "+position.y);
    }
    public float getXPosition(){
        return position.x;
    }
    public float getYPosition(){
        return position.y;
    }
    public void setSpeed(float xSpeed, float ySpeed){
        speed.x = xSpeed+(float) (50 * Math.sin(angle));
        speed.y = ySpeed+ (float)(50 * Math.cos(angle));
    }



    public float getObjectXPosition(){
        return this.position.x;
    }
    public float getObjectYPosition(){
        return this.position.y;
    }



}
