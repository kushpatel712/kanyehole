package com.dropout.kanyehole;

/**
 * Created by Kush on 4/2/2015.
 */
import java.util.HashMap;

public class ArrowFactory {
    private static final HashMap<String, Drawable> arrowMap = new HashMap<String, Drawable>();

    public static Drawable getArrow(String color, int mScrWidth, int mScrHeight) {
        Arrow arrow = (Arrow)arrowMap.get(color);
        int angle = (int)(Math.random()*4);
        angle *= 90;
        arrow = new Arrow(angle, mScrWidth, mScrHeight);
        arrowMap.put(color, arrow);
//        if(obstacle == null) {
//            obstacle = new Obstacle(angle, mScrWidth, mScrHeight);
//            obstacleMap.put(color, obstacle);
//           // System.out.println("Creating obstacle of color : " + color);
//        }
        return arrow;
    }
}
