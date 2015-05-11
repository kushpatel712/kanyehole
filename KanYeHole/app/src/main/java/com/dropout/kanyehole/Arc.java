package com.dropout.kanyehole;

/**
 * This Class is the Head that moves with the accelerometer.
 * There is only one of them since it's basically the player's character.
 */
public class Arc implements Drawable {
    private static Arc instance = new Arc(0, 0);
    private android.graphics.PointF position, speed;
    private double angle = 90;
    private int mScrWidth, mScrHeight;
    private double radius;

    //constructor, params are for scaling
    private Arc(int mScrWidth, int mScrHeight) {
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        position.x = mScrWidth / 2;
        position.y = mScrHeight / 4;
        speed.x = 0;
        speed.y = 0;
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }

    //Delete the head since its static
    public static void deleteInstance() {
        instance = null;
    }

    //singleton pattern
    public static Arc getInstance() {
        if (instance == null)
            instance = new Arc(0, 0);
        return instance;
    }


    //Update the position of the head, this is used in the game loop
    public synchronized boolean updatePosition() {
        angle += speed.x;
        int yyyy = mScrHeight / 4 + mScrHeight / 12; //y center of the movement circle
        int xxxx = mScrWidth / 2; //x center of the movement circle
        radius = xxxx * .7; //radius of circle

        //updates the x and y positions based on the angle
        position.x = (float) (xxxx + radius * Math.cos(((angle + .5f) * 2 * Math.PI / 360)));
        position.y = ((float) (yyyy + radius * Math.sin(((angle + .5f) * 2 * Math.PI / 360))));
        return false; //the boolean returns false because this class doesn't use the boolean return
    }

    //Set the dimensions of the screen
    public void setScrDims(int height, int width) {
        this.mScrHeight = height;
        this.mScrWidth = width;
    }

    //Getters and Setters that are self-explanatory

    public float getXPosition() {
        return position.x;
    }

    public double getAngle() {
        return angle;
    }

    public float getYPosition() {
        return position.y;
    }

    public float getXSpeed() {
        return speed.x;
    }

    public float getYSpeed() {
        return speed.y;
    }

    public void setSpeed(float xSpeed, float ySpeed) {
        speed.x = xSpeed;
        speed.y = ySpeed;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }
}
