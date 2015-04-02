package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.Display;
import android.view.View;
import 	android.graphics.RectF;


public class ArcView extends View {

    private float mX;
    private float mY;
    private final int mR;
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private double angle;
    private int xx;
    private int yy;
  //  public RectF hi;

    public void setPosition(float x,float y){
        this.mX = x;
        this.mY = y;
    }
    public void setAngle(double angle){
        this.angle = angle;
    }
    //construct new ball object
    public ArcView(Context context, float x, float y, int r,int color, double angle) {
        super(context);
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

    //called by invalidate()	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      //  canvas.drawArc(new RectF(10, 10, 200, 150), 0, 90, false, mPaint);
        mPaint.setColor(Color.GREEN);
        int yyyy = getResources().getDisplayMetrics().heightPixels/4;
        int xxxx = getResources().getDisplayMetrics().widthPixels/2;
        RectF hi=new RectF(xxxx-xxxx/5,yyyy-xxxx/5,yyyy+xxxx/5,yyyy-+xxxx/5);

        mPaint.setStrokeWidth(20);
        float angs= (float)angle;
        angs+=0.5f;
        //--canvas.drawText("SDF4334SDS", 300, 300, mPaint);
        //--canvas.drawText(Integer.toString(xxxx-(xxxx/5)), 300, 800, mPaint);
        //--canvas.drawText(Integer.toString(yyyy-(yyyy/5)), 100, 800, mPaint);
       //-- canvas.drawText("SDFsdfSDFSDS", xxxx, yyyy, mPaint);
        int bottome=yyyy+120;
        //canvas.drawArc(new RectF(xxxx-(xxxx/5),yyyy-(xxxx/5),xxxx+(xxxx/5),yyyy+(xxxx/5)), 90, angs, true, mPaint);
        //canvas.drawArc(new RectF(480,bottome,720,781), 90, angs, true, mPaint);
        //canvas.drawArc(new RectF(480,781+240,720,781), 90, 100, true, mPaint);
       
       canvas.drawArc(new RectF(xxxx-(xxxx/5),yyyy-(xxxx/5),xxxx+(xxxx/5),yyyy+(xxxx/5)), (angs-30)%360, 60, false, mPaint);
        //canvas.drawArc(hi, 90, angs, true, mPaint);

        //mPaint.setColor(Color.RED);
        //--canvas.drawRect(hi,mPaint);
        // Path androidPath = new Path(); 
       // androidPath.arcTo(new RectF(xx-xx/5,yy+xx/5,xx+xx/5,yy-xx/5), 90, angs);
        //--canvas.drawArc(new RectF(10, 10, 200, 150), 0, 90, false, mPaint);

       // canvas.drawPath(androidPath, mPaint);
    } 
}
