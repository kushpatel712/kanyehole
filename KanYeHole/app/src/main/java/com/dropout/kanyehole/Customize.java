package com.dropout.kanyehole;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;


public class Customize extends Activity {

    public static List<Integer> imageHolder = new ArrayList<Integer>();
    public static ArrayList<String> kanyeNames = new ArrayList<String>();
    public static int index=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Field[] heads = com.dropout.kanyehole.R.drawable.class.getFields();
        for (Field f : heads) {
            //System.out.println(f.getName());
            try {
                if(f.getName().contains("kanyeheadmap")){
                    int resID = getResources().getIdentifier(f.getName() , "drawable", getPackageName());
                    if(f.getName().equals("kanyeheadmap")){
                        imageHolder.add(0,resID);
                        kanyeNames.add(0,f.getName());
                    }
                    else {
                        imageHolder.add(resID);
                        kanyeNames.add(f.getName());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setContentView(R.layout.activity_customize);
        final ImageButton kanyeHead = (ImageButton) findViewById(R.id.headimage);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                imageHolder.get(index));
        Bitmap croppedBmp = Bitmap.createBitmap(icon, 0, 0, icon.getWidth()/3, icon.getHeight());
        kanyeHead.setImageBitmap(croppedBmp);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void toggle(View view){

    }
    public void onBackPressed(){
        // super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }
    public void shiftImageBack(View view){
        index=Math.max((index-1),0);
        final ImageButton kanyeHead = (ImageButton) findViewById(R.id.headimage);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                imageHolder.get(index));
        Bitmap croppedBmp = Bitmap.createBitmap(icon, 0, 0, icon.getWidth()/3, icon.getHeight());
        kanyeHead.setImageBitmap(croppedBmp);
        //kanyeHead.setImageResource(imageHolder.get(index));

    }
    public void shiftImageForward(View view){
        if (index < imageHolder.size()-1)
            index=(index+1);
        final ImageButton kanyeHead = (ImageButton) findViewById(R.id.headimage);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                imageHolder.get(index));
        Bitmap croppedBmp = Bitmap.createBitmap(icon, 0, 0, icon.getWidth()/3, icon.getHeight());
        kanyeHead.setImageBitmap(croppedBmp);

    }
    public void sendPlay(View view){
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
        //finish();
    }
}