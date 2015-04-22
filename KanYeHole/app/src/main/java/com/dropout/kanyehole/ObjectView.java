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
    Bitmap b= BitmapFactory.decodeResource(getResources(), R.drawable.kanyeheadpng);
    Paint p =new Paint();
    int length=getResources().getDisplayMetrics().widthPixels;
    int height=getResources().getDisplayMetrics().heightPixels;

    Arc arc=Arc.getInstance();
    private ArrayList<Drawable> drawList = new ArrayList<Drawable>();
  //  public RectF hi;

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
    public void registerObject(Drawable obj){
        drawList.add(obj);
    }
    //called by invalidate()	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //System.out.println("length of drawables"+drawList.size());
//        for (Drawable obj: drawList){
//            obj.draw(canvas,getResources().getDisplayMetrics().heightPixels,getResources().getDisplayMetrics().widthPixels);
//        }
        for (int i = 0; i < drawList.size();i++){
           // System.out.println("Object: "+ i);
            Drawable obj = drawList.get(i);
            if (obj.getClass().equals(Obstacle.class)){
                Obstacle obs = (Obstacle)obj;
               // System.out.println(obs.outside);
                if (obs.outside){
                    drawList.remove(i);
                    i--;
                    continue;
                }
            }
            obj.draw(canvas,getResources().getDisplayMetrics().heightPixels,getResources().getDisplayMetrics().widthPixels);
        }
        float newX, newY;
        double angle=arc.getAngle();
        if(angle>180){
            newY=10;
        }
        else
        {
            newY=-10;
        }
        newX = (float) (length / 2 + 70 * Math.sin(angle));
        newY += (float) (height / 4 + 70 * Math.cos(angle));

        //canvas.drawBitmap(b, (newX+10), (newY+10), p);

//        mPaint.setColor(Color.GREEN);
//        int yyyy = getResources().getDisplayMetrics().heightPixels/4;
//        int xxxx = getResources().getDisplayMetrics().widthPixels/2;
//        RectF hi=new RectF(xxxx-xxxx/5,yyyy-xxxx/5,yyyy+xxxx/5,yyyy-+xxxx/5);
//        mPaint.setStrokeWidth(10);
//        float angs= (float)angle;
//        angs+=0.5f;
//       canvas.drawArc(new RectF(xxxx-(xxxx*2/5),yyyy-(xxxx*2/5),xxxx+(xxxx*2/5),yyyy+(xxxx*2/5)), (angs-30)%360, 60, false, mPaint);
//       mPaint.setColor(Color.RED);
//       //canvas.drawCircle(obX,obY,20,mPaint);
//        canvas.drawCircle(xxxx/2,yyyy/2,20,mPaint);

      // canvas.drawCircle(x,y,r,mPaint);
    }
    public Context getCont(){return this.c;}
}
