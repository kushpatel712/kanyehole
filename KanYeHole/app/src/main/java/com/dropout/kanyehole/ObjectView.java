package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import java.util.ArrayList;


public class ObjectView extends View {

    public Context c;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private ArrayList<Drawable> drawList = new ArrayList<Drawable>();
    private ArrayList<Drawable> obsList = new ArrayList<Drawable>();

    public ArrayList<Drawable> getDrawList(){
       return drawList;
    }
    public ArrayList<Drawable> getObsList(){
        return obsList;
    }

    public int getObsSize() {
        return obsList.size();
    }
   //construct new ball object
    public ObjectView(Context context, float x, float y, int r, int color, double angle) {
        super(context);
        c=context;
        //color hex is [transparency][red][green][blue]
        if (color==0)
        mPaint.setColor(0xFF00FF00); //not transparent. color is green
        else
        	mPaint.setColor(0XFFFF0000);
        mPaint.setColor(Color.BLACK);
        mPaint.setStrokeWidth(50);
       // hi=new RectF(xx-xx/5,yy+xx/5,xx+xx/5,yy-xx/5);
    }
    public void registerObstacle(Drawable obj){
        obsList.add(obj);
    }
    public void registerDrawable(Drawable obj){
        drawList.add(obj);
    }
}
