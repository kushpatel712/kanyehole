package com.dropout.kanyehole;

import java.util.ArrayList;

public class ArrowGenerator {
    private ArrayList<Integer> avail = new ArrayList<Integer>();
    public void generate(ObjectView drawer,int mScrWidth, int mScrHeight){
        avail.add(0);
        avail.add(90);
        avail.add(180);
        avail.add(270);
        int num = 1;
        if (Math.random()*3 < 1){
            num = 2;
        }
        for(int i=0; i < num; i++) {
            int index = (int)(Math.random()*(4-i));
            int angle = avail.remove(index);
            Arrow arrow = new Arrow(angle, mScrWidth, mScrHeight);
            drawer.registerDrawable(arrow);
        }
    }
}