package com.dropout.kanyehole;

/**
 * This Class is the Taylor Head that moves with the accelerometer.
 * There is only one of them since it's basically the player's character.
 */
public class TayArc implements Drawable {
    private static TayArc instance = new TayArc(0, 0);
    private android.graphics.PointF position, speed;
    private double angle = 90;
    private int mScrWidth, mScrHeight;
    private double radius;

    //constructor, params are for scaling
    private TayArc(int mScrWidth, int mScrHeight) {
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        position.x = mScrWidth / 2;
        position.y = mScrHeight / 4;
        speed.x = 0;
        speed.y = 0;
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }

    //destructor
    public static void deleteInstance() {
        instance = null;
    }

    //Singleton pattern
    public static TayArc getInstance() {
        if (instance == null)
            instance = new TayArc(0, 0);
        return instance;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    //update the position for the game thread
    public synchronized boolean updatePosition() {
        angle += speed.x;
        int yyyy = mScrHeight / 4 + mScrHeight / 12;
        int xxxx = mScrWidth / 2;
        radius = xxxx * .7;

        //update position based on angle and radius
        position.x = (float) (xxxx + radius * Math.cos(((angle + .5f) * 2 * Math.PI / 360)));
        position.y = ((float) (yyyy + radius * Math.sin(((angle + .5f) * 2 * Math.PI / 360))));
        return false; //unused
    }

    //getters and setters
    public void setScrDims(int height, int width) {
        this.mScrHeight = height;
        this.mScrWidth = width;
    }

    public float getXPosition() {
        return position.x;
    }

    public float getYPosition() {
        return position.y;
    }

    public void setSpeed(float xSpeed, float ySpeed) {
        speed.x = xSpeed;
        speed.y = ySpeed;
    }
}
