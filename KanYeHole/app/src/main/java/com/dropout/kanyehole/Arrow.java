package com.dropout.kanyehole;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Display;
import android.widget.Button;

/**
 * Created by Kush on 4/1/2015
 */
public class Arrow implements Drawable{
    /*
    * TODO: Make me a Flyweight! :D
    * */
    android.graphics.PointF position, speed;
    double angle = 0;
    int count=0;
    boolean added=false;
    int mScrWidth, mScrHeight;
    public boolean outside = false;
    public boolean touch = false;
    public boolean perfect = false;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Context context=MyApplication.getAppContext();
    Bitmap b = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowup);
    Bitmap perf = BitmapFactory.decodeResource(context.getResources(), R.drawable.perfect);
    public void draw(Canvas canvas, int height, int width){
        paint.setColor(Color.GREEN);

        updatePosition();
        int yyyy = (int)position.y;
        int xxxx = (int)position.x;
        //System.out.println("x"+xxxx+" y"+yyyy);
        paint.setColor(Color.RED);
        if(touch){
            paint.setColor(Color.BLUE);
        }
        Bitmap up= BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowup);
        Bitmap down= BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowdown);
        Bitmap left= BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowleft);
        Bitmap right= BitmapFactory.decodeResource(context.getResources(), R.drawable.arrowright);

        if (angle == 0){
            b = up;
        }
        else if (angle == 90){
            b = right;
        }
        else if (angle == 180){
            b = down;
        }
        else if (angle == 270){
            b = left;
        }
        float X=(float)(xxxx);
        float Y=(float)(yyyy);
        canvas.drawBitmap(b,X,Y,paint);
        if (perfect){
            //System.out.println("Perf");
            canvas.drawBitmap(perf,xxxx,Y+perf.getHeight()/2,paint);
        }
    }
    public Arrow(int startAngle, int mScrWidth, int mScrHeight){
        position = new android.graphics.PointF();
        speed = new android.graphics.PointF();
        //position.x = mScrWidth/2+(float)Math.random()*(mScrWidth/2);
        // position.y = mScrHeight/4+(float)Math.random()*(mScrHeight/4);
        position.x=mScrWidth/2;
        position.y=mScrHeight/4;
        //speed.x = (float) (Math.sin(startAngle));
       // speed.y = (float)(Math.cos(startAngle));
        angle=startAngle;
        if (angle == 0){
            position.x=Buttons.up_X;
            position.y=mScrHeight/2;
        }
        else if (angle == 90){
            position.x=Buttons.right_X;
            position.y=mScrHeight/2;
        }
        else if (angle == 180){
            position.x=Buttons.down_X;
            position.y=mScrHeight/2;
        }
        else if (angle == 270){
            position.x=Buttons.left_X;
            position.y=mScrHeight/2;
        }
        this.setSpeed(0,5);
        this.mScrWidth = mScrWidth;
        this.mScrHeight = mScrHeight;
    }

    public void setXPosition(float x){
        position.x = x;
    }
    public void setYPosition(float y){
        position.y = y;
    }
    public void setPosition(float x, float y){
        position.x = x;
        position.y = y;
    }
    public void updatePosition(){
        // setSpeed(speed.x,speed.y);
        //position.x = Math.abs((position.x +speed.x)%mScrWidth);
        position.y =Math.abs((position.y+ speed.y)%mScrHeight);
        //double distance = Math.sqrt(Math.pow(position.x-mScrWidth/2,2)+Math.pow(position.y-mScrHeight/4,2));
        //System.out.println("dist"+distance+" rad"+(mScrWidth/4+21));
        if (position.y == Buttons.standard_Y){
            perfect = true;

        }
        if((!this.added)&&(position.y>=mScrHeight*3/4)){

            addArrow();
            this.added=true;
            System.out.println("added");
        }
        if (position.y > mScrHeight*5.75/6){
            outside = true;
            perfect = false;
            this.remove();

        }
    }
    public float getXPosition(){
        return position.x;
    }
    public float getYPosition(){
        return position.y;
    }
    public void setSpeed(float xSpeed, float ySpeed){
        speed.x = xSpeed;
        speed.y = ySpeed;
    }


    public int bitWidth(){
        return  b.getWidth();
    }
    public int bitHeight(){
        return  b.getHeight();
    }
    public float getObjectXPosition(){
        return this.position.x;
    }
    public float getObjectYPosition(){
        return this.position.y;
    }
    public Rect getArrowRectangle(){
        Rect rectangle=new Rect((int) this.getObjectXPosition(),(int) this.getObjectYPosition(),(int)this.getObjectXPosition()+b.getWidth(),(int)this.getObjectYPosition()+b.getHeight());
        return rectangle;
    }

    public void remove(){
        try {
            if (angle == 0) {
                Buttons.upArrows.remove(this);
                this.b.recycle();
                this.perf.recycle();
            } else if (angle == 90) {
                Buttons.rightArrows.remove(this);
                this.b.recycle();
                this.perf.recycle();
            } else if (angle == 180) {
                Buttons.downArrows.remove(this);
                this.b.recycle();
                this.perf.recycle();
            } else if (angle == 270) {
                Buttons.leftArrows.remove(this);
                this.b.recycle();
                this.perf.recycle();

            }
        }
        catch(NullPointerException n){

        }


    }
    public void addArrow(){
        try {
            if (angle == 0) {
                Buttons.upArrows.add(this);

            } else if (angle == 90) {
                Buttons.rightArrows.add(this);

            } else if (angle == 180) {
                Buttons.downArrows.add(this);
            } else if (angle == 270) {
                Buttons.leftArrows.add(this);


            }
        }
        catch(NullPointerException n){

        }


    }

}
