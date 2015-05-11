package com.dropout.kanyehole;

/**
 * This Class is for the generating the obstacles with specificly random angles
 */
public class ObstacleGenerator {

    //create the obstacles
    public void generate(ObjectView drawer, int mScrWidth, int mScrHeight, boolean taymode) {
        int limit = 3;
        int maxWaves = 3;
        if (taymode){
            maxWaves = 4;
        }
        int angle = (int) (Math.random() * 360); //initial angle
        for (int i = 0; i < limit; i++) {

            //create the obstacle
            Obstacle obstacle = new Obstacle(angle, mScrWidth, mScrHeight, taymode);
            if (drawer.getObsSize() < limit*maxWaves) {
                drawer.registerObstacle(obstacle);
                angle = (angle + 90 / limit) % 360; //new angle is close to prev so that the obstacles take up a sector of the movement circle
            }
        }
    }
}