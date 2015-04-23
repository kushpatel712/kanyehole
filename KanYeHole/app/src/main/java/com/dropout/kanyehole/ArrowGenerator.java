package com.dropout.kanyehole;

public class ArrowGenerator {
    private static final String colors[] = { "Red", "Green", "Blue", "Purple", "Black" };

    public void generate(ObjectView drawer,int mScrWidth, int mScrHeight){
        for(int i=0; i < 1; i++) {
            //System.out.println("Object: "+ i);
            Arrow arrow = (Arrow)ArrowFactory.getArrow(getRandomColor(), mScrWidth, mScrHeight);
            drawer.registerDrawable(arrow);
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