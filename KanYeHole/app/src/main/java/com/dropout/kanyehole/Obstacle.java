package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Display;
import android.widget.TextView;

/**
 * Created by Kush on 4/1/2015
 */
public class Obstacle implements Drawable{
    /*
    * TODO: Make me a Flyweight! :D
    * */
    android.graphics.PointF position, speed;
    private double distfromcenter = 0;
    double angle = 0;
    int count=0;
    int mScrWidth, mScrHeight;
    public boolean outside = false;
    public boolean touch = false;
    private boolean taylormode = false;
    public boolean flash = false;
    public Obstacle(int startAngle, int mScrWidth, int mScrHeight, boolean taymode){
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        taylormode = taymode;
        position.x=mScrWidth/2;
        position.y=mScrHeight/4+mScrHeight/12;
        speed.x = (float) (Math.cos(startAngle*2*Math.PI/360));
        speed.y = (float)(Math.sin(startAngle*2*Math.PI/360));
        speed.x*=2;
        speed.y*=2;
        angle=startAngle;
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }
    public void setPosition(float x, float y){
        position.x = x;
        position.y = y;
    }
    private static boolean collided(float x1,float y1, float r1, float x2,float y2, float r2)
    {
        float a,dx, dy;
        a = (r1+r2) * (r1+r2);
        dx = (float) (x1 - x2);
        dy = (float) (y1 - y2);

        if (a > (dx*dx) + (dy*dy))
        {
            return true;
        }
        return false;
    }
    public synchronized boolean updatePosition(){
        position.x = Math.abs((position.x +speed.x)%mScrWidth);
        position.y =Math.abs((position.y+ speed.y)%mScrHeight);
        double distance = Math.sqrt(Math.pow(position.x-mScrWidth/2,2)+Math.pow(position.y-mScrHeight/4-mScrHeight/12,2));
        distfromcenter = distance;
        if (distance > mScrWidth/2*.8+21){
            outside = true;

        }
        if (taylormode) {
            TayArc arc = TayArc.getInstance();
            if (collided(arc.getXPosition(), arc.getYPosition(), Buttons.obsSize / 2, position.x, position.y, Buttons.obsSize / 2)) {
                touch = true;
            }
        } else {
            Arc arc = Arc.getInstance();
            if (collided(arc.getXPosition(), arc.getYPosition(), Buttons.obsSize / 2, position.x, position.y, Buttons.obsSize / 2)) {
                touch = true;
            }
        }
        if(touch&&count==0){
            count++;
            return true;
        }
        return false;
    }
    public double getDist(){
        return  distfromcenter;
    }
    public float getXPosition(){
        return position.x;
    }
    public float getYPosition(){
        return position.y;
    }
    public void setSpeed(float xSpeed, float ySpeed){
        speed.x = xSpeed+ (float) (50 * Math.sin(angle*2*Math.PI/360));
        speed.y = ySpeed+ (float)(50 * Math.cos(angle*2*Math.PI/360));
    }
}
