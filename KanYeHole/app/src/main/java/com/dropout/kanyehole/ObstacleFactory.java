package com.dropout.kanyehole;

/**
 * Created by Kush on 4/2/2015.
 */
import java.util.HashMap;

public class ObstacleFactory {
    private static final HashMap<String, Drawable> obstacleMap = new HashMap<String, Drawable>();

    public static Drawable getObstacle(String color, int mScrWidth, int mScrHeight) {
        Obstacle obstacle = (Obstacle)obstacleMap.get(color);
        int angle = (int)(Math.random()*360);
        obstacle = new Obstacle(angle, mScrWidth, mScrHeight);
        obstacleMap.put(color, obstacle);
//        if(obstacle == null) {
//            obstacle = new Obstacle(angle, mScrWidth, mScrHeight);
//            obstacleMap.put(color, obstacle);
//           // System.out.println("Creating obstacle of color : " + color);
//        }
        return obstacle;
    }
}
