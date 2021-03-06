package com.dropout.kanyehole;

import android.graphics.Rect;

import java.util.ArrayList;

/**
 * This Class holds some global variables for the arrows and the arrow buttons
 */
public class Buttons {

    static public int standard_Y;
    static public int up_X;
    static public int down_X;
    static public int right_X;
    static public int left_X;
    static public int arrow_width;
    static public int arrow_height;
    static public ArrayList<Arrow> upArrows = new ArrayList<Arrow>();
    static public ArrayList<Arrow> leftArrows = new ArrayList<Arrow>();
    static public ArrayList<Arrow> rightArrows = new ArrayList<Arrow>();
    static public ArrayList<Arrow> downArrows = new ArrayList<Arrow>();
    static public Rect upRect;
    static public Rect leftRect;
    static public Rect rightRect;
    static public Rect downRect;
    static public float obsSize;

}