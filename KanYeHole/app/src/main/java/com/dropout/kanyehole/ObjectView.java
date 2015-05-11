package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;

/**
 * This Class is for the the lists of drawables
 */
public class ObjectView extends View {
    //holds drawables (arrows)
    private ArrayList<Drawable> drawList = new ArrayList<Drawable>();

    //holds obstacles
    private ArrayList<Drawable> obsList = new ArrayList<Drawable>();

    //construct new ball object
    public ObjectView(Context context, float x, float y, int r, int color, double angle) {
        super(context);
    }

    public ArrayList<Drawable> getDrawList() {
        return drawList;
    }

    public ArrayList<Drawable> getObsList() {
        return obsList;
    }

    public int getObsSize() {
        return obsList.size();
    }

    public void registerObstacle(Drawable obj) {
        obsList.add(obj);
    }

    public void registerDrawable(Drawable obj) {
        drawList.add(obj);
    }
}
