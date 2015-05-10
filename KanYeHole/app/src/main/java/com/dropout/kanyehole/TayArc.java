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
public class TayArc implements Drawable{
    private static TayArc instance = new TayArc(0,0);
    android.graphics.PointF position, speed;
    double angle = 90;
    int mScrWidth, mScrHeight;
    double radius;
    public static void deleteInstance(){
        instance = null;
    }
    private TayArc(int mScrWidth, int mScrHeight){
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        position.x = mScrWidth/2;
        position.y = mScrHeight/4;
        speed.x = 0;
        speed.y = 0;
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }

    public static TayArc getInstance()
    {
        if(instance == null)
            instance = new TayArc(0,0);
        return instance;
    }
    public void setPosition(float x, float y){
        position.x = x;
        position.y = y;
    }
    public synchronized boolean updatePosition(){
        angle += speed.x;
        int yyyy = mScrHeight/4+mScrHeight/12;
        int xxxx = mScrWidth/2;
        radius=xxxx*.7;

        position.x=(float)(xxxx + radius * Math.cos(((angle+.5f)*2*Math.PI/360)));
        position.y=((float)(yyyy+ radius * Math.sin(((angle+.5f)*2*Math.PI/360))));
        return false;
    }
    public void setScrDims(int height, int width){
        this.mScrHeight = height;
        this.mScrWidth = width;
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
