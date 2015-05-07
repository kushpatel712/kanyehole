package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.Display;
import android.view.View;
import 	android.graphics.RectF;

import java.util.ArrayList;


public class ObjectView extends View {

    private float mX;
    private float mY;
    private float obX;
    private float obY;
    private final int mR;
    public Context c;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private double angle;
    private int xx;
    private int yy;
   // Bitmap b= BitmapFactory.decodeResource(getResources(), R.drawable.kanyeheadpng);
    Paint p =new Paint();
    int length=getResources().getDisplayMetrics().widthPixels;
    int height=getResources().getDisplayMetrics().heightPixels;

    Arc arc=Arc.getInstance();
    private ArrayList<Drawable> drawList = new ArrayList<Drawable>();
    private ArrayList<Drawable> obsList = new ArrayList<Drawable>();
  //  public RectF hi;

    public ArrayList<Drawable> getDrawList(){
       return drawList;
    }
    public ArrayList<Drawable> getObsList(){
        return obsList;
    }
    public void setPosition(float x,float y){
        this.mX = x;
        this.mY = y;
    }

    public void setObjectPosition(float x,float y){
        this.obX=x;
        this.obX =y;
    }

    public void setAngle(double angle){
        this.angle = angle;
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
        this.mX = x;
        this.mY = y;
        this.mR = r; //radius
        this.angle=angle;
        mPaint.setStrokeWidth(50);
       // hi=new RectF(xx-xx/5,yy+xx/5,xx+xx/5,yy-xx/5);
    }
    public void registerObstacle(Drawable obj){
        obsList.add(obj);
    }
    public void registerDrawable(Drawable obj){
        drawList.add(obj);
    }
    public Context getCont(){return this.c;}
}
