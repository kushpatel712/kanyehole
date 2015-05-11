package com.dropout.kanyehole;

import java.util.ArrayList;

/**
 * This Class generates the arrows and picks the directions
 */

public class ArrowGenerator {

    //Used to make sure arrows aren't duplicated in one generate
    private ArrayList<Integer> avail = new ArrayList<Integer>();

    //Creates the arrows
    public void generate(ObjectView drawer, int mScrWidth, int mScrHeight) {
        avail.add(0);
        avail.add(90);
        avail.add(180);
        avail.add(270);
        int num = 1;

        //Randomly send 2 arrows instead of one
        if (Math.random() * 3 < 1) {
            num = 2;
        }

        //Create the arrows
        for (int i = 0; i < num; i++) {

            //Only use available directions
            int index = (int) (Math.random() * (4 - i));
            int angle = avail.remove(index);

            //Create the arrow and add it to the drawlist
            Arrow arrow = new Arrow(angle, mScrWidth, mScrHeight);
            drawer.registerDrawable(arrow);
        }
    }
}