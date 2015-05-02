package com.dropout.kanyehole;

import android.graphics.Bitmap;

public class ObstacleGenerator {
    private static final String colors[] = { "Red", "Green", "Blue", "Purple", "Black" };

    public void generate(ObjectView drawer,int mScrWidth, int mScrHeight, Bitmap b, Bitmap red, Bitmap cyan){
        for(int i=0; i < 2; i++) {
            //System.out.println("Object: "+ i);
            Obstacle obstacle = (Obstacle)ObstacleFactory.getObstacle(getRandomColor(),mScrWidth, mScrHeight, b, red, cyan);
            if (drawer.getObsSize() < 10) {
                drawer.registerObstacle(obstacle);
            }
        }
    }
    private static String getRandomColor() {
        return colors[(int)(Math.random()*colors.length)];
    }
    private static int getRandomX() { return (int)(Math.random()*100 ); }
    private static int getRandomY() {
        return (int)(Math.random()*100);
    }
}