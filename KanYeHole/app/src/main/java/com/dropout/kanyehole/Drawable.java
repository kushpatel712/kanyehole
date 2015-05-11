package com.dropout.kanyehole;

/**
 * This Interface is for the drawn objects that get updated
 * (Arrow, Arc, Obstacle)
 */
public interface Drawable {
    public float getXPosition();

    public float getYPosition();

    public boolean updatePosition();
}
