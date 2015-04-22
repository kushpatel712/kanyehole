package com.dropout.kanyehole;

public class ObstacleGenerator {
    private static final String colors[] = { "Red", "Green", "Blue", "White", "Black" };

    public void generate(ArcView drawer,int mScrWidth, int mScrHeight){
        for(int i=0; i < 5; i++) {
            System.out.println("Object: "+ i);
            Obstacle obstacle = (Obstacle)ObstacleFactory.getObstacle(getRandomColor(),mScrWidth, mScrHeight);
            drawer.registerObject(obstacle);
        }
    }
    private static String getRandomColor() {
        return colors[(int)(Math.random()*colors.length)];
    }
    private static int getRandomX() {
        return (int)(Math.random()*100 );
    }
    private static int getRandomY() {
        return (int)(Math.random()*100);
    }
}