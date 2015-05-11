package com.dropout.kanyehole;

/**
 * This Class is for the Obstacles objects
 * They start at the center and spread out, collide with arc = lose a life
 */
public class Obstacle implements Drawable {
    public boolean outside = false;
    public boolean touch = false;
    public boolean flash = false;
    private android.graphics.PointF position, speed;
    private double angle = 0;
    private int count = 0;
    private int mScrWidth, mScrHeight;
    private double distfromcenter = 0;
    private boolean taylormode = false;

    //Constructor, taymode makes it the taylor version for the specific arc
    public Obstacle(int startAngle, int mScrWidth, int mScrHeight, boolean taymode) {
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        taylormode = taymode;
        position.x = mScrWidth / 2; //center of movement circle
        position.y = mScrHeight / 4 + mScrHeight / 12; //center of movement circle
        //speed based on screen size and angle
        speed.x = (float) (mScrWidth / 540.0) * (float) (Math.cos(startAngle * 2 * Math.PI / 360));
        speed.y = (float) (mScrWidth / 540.0) * (float) (Math.sin(startAngle * 2 * Math.PI / 360));
        angle = startAngle;
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }

    //Check for collision between 2 circles (Obstacle and arc)
    private static boolean collided(float x1, float y1, float r1, float x2, float y2, float r2) {
        float a, dx, dy;
        a = (r1 + r2) * (r1 + r2);
        dx = (float) (x1 - x2);
        dy = (float) (y1 - y2);

        if (a > (dx * dx) + (dy * dy)) {
            return true;
        }
        return false;
    }

    public void setPosition(float x, float y) {
        position.x = x;
        position.y = y;
    }

    //Update the position for game loop
    public synchronized boolean updatePosition() {
        //Move position based on position and speed
        position.x = Math.abs((position.x + speed.x) % mScrWidth);
        position.y = Math.abs((position.y + speed.y) % mScrHeight);

        //Distance for checking if the obstacle is outside of the circle and for image scaling
        double distance = Math.sqrt(Math.pow(position.x - mScrWidth / 2, 2) + Math.pow(position.y - mScrHeight / 4 - mScrHeight / 12, 2));
        distfromcenter = distance;

        //if it's outside the circle by a decent amount, set it to be removed
        if (distance > mScrWidth / 2 * .8 + 21) {
            outside = true;
        }

        //Check collision with appropriate arc
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

        //If it has collided for the first time, return true
        if (touch && count == 0) {
            count++;
            return true;
        }
        return false;
    }

    //more Getters and setters
    public double getDist() {
        return distfromcenter;
    }

    public float getXPosition() {
        return position.x;
    }

    public float getYPosition() {
        return position.y;
    }

    public void setSpeed(float xSpeed, float ySpeed) {
        speed.x = xSpeed + (float) (50 * Math.sin(angle * 2 * Math.PI / 360));
        speed.y = ySpeed + (float) (50 * Math.cos(angle * 2 * Math.PI / 360));
    }
}
