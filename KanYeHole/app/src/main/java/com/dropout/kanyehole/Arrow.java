package com.dropout.kanyehole;

/**
 * This Class is for the Arrows that move down the screen for DDR.
 * They can have different angles representing the different directions <- ->
 */

public class Arrow implements Drawable {
    public boolean outside = false;
    public boolean miss = true;
    private android.graphics.PointF position, speed;
    private double angle = 0;
    private double id = 0;
    public boolean added = false;
    private int mScrWidth, mScrHeight;

    //Constructor for the Arrow, startAngle defines which arrow direction
    public Arrow(int startAngle, int mScrWidth, int mScrHeight) {
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        position.x = mScrWidth / 2;
        position.y = mScrHeight / 2;
        angle = startAngle;

        //Set position based on the angle
        if (angle == 0) {
            position.x = Buttons.up_X;
        } else if (angle == 90) {
            position.x = Buttons.right_X;
        } else if (angle == 180) {
            position.x = Buttons.down_X;
        } else if (angle == 270) {
            position.x = Buttons.left_X;
        }

        this.setSpeed(0, mScrHeight / 300);
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }

    //Updates the position for the game loop
    public boolean updatePosition() {
        //Move the arrow down the screen based on speed
        position.y = Math.abs((position.y + speed.y) % mScrHeight);

        //Adds the arrow to the Button lists for checking
        if ((!this.added) && (position.y >= mScrHeight * 3 / 4)) {
            addArrow();
            this.added = true;
        }

        //Removes the arrow from the button lists if its at the bottom of the screen
        if (position.y > mScrHeight * 5.95 / 6) {
            outside = true;
            this.remove();
            if (miss) {
                return true; // This return is to show that the arrow was never pressed
            }
        }
        return false;
    }

    public float getXPosition() {
        return position.x;
    }

    public float getYPosition() {
        return position.y;
    }
    public double getAngle() {return angle;}
    public void setSpeed(float xSpeed, float ySpeed) {
        speed.x = xSpeed;
        speed.y = ySpeed;
    }

    //Removes the arrow from its respective button list
    public void remove() {
        try {
            if (angle == 0) {
                Buttons.upArrows.remove(this);
            } else if (angle == 90) {
                Buttons.rightArrows.remove(this);
            } else if (angle == 180) {
                Buttons.downArrows.remove(this);
            } else if (angle == 270) {
                Buttons.leftArrows.remove(this);
            }
        } catch (NullPointerException n) {
        }
    }

    //Adds the arrow to its respective button list
    public void addArrow() {
        try {
            if (angle == 0) {
                Buttons.upArrows.add(this);

            } else if (angle == 90) {
                Buttons.rightArrows.add(this);

            } else if (angle == 180) {
                Buttons.downArrows.add(this);
            } else if (angle == 270) {
                Buttons.leftArrows.add(this);


            }
        } catch (NullPointerException n) {

        }


    }

}