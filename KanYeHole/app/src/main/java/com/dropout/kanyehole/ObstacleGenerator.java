package com.dropout.kanyehole;


public class ObstacleGenerator {
    public void generate(ObjectView drawer,int mScrWidth, int mScrHeight, boolean taymode){
        int limit = 3;
        int angle = (int)(Math.random()*360);
        for(int i=0; i < limit; i++) {

            Obstacle obstacle = new Obstacle(angle, mScrWidth, mScrHeight, taymode);
            if (drawer.getObsSize() < 20) {
                drawer.registerObstacle(obstacle);
                angle = (angle+90/limit)%360;
            }
        }
    }
}