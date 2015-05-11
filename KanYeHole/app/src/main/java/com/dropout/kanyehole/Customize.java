package com.dropout.kanyehole;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class is for the Customize Page (AKA The page that lets you choose Kanye head)
 */
public class Customize extends Activity {

    public static List<Integer> imageHolder = new ArrayList<Integer>();
    public static ArrayList<String> kanyeNames = new ArrayList<String>();
    public static int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Field[] heads = com.dropout.kanyehole.R.drawable.class.getFields();
        for (Field f : heads) {
            try {
                //finds images with "kanyeheadmap" to pick the right ones
                if (f.getName().contains("kanyeheadmap")) {
                    int resID = getResources().getIdentifier(f.getName(), "drawable", getPackageName());
                    if (f.getName().equals("kanyeheadmap")) { //Makes the standard 0 pic just kanyeheadmap
                        imageHolder.add(0, resID);
                        kanyeNames.add(0, f.getName());
                    } else {
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
        Bitmap croppedBmp = Bitmap.createBitmap(icon, 0, 0, icon.getWidth() / 3, icon.getHeight());
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

    public void toggle(View view) {

    }

    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }

    //Button for going to previous image
    public void shiftImageBack(View view) {
        index = Math.max((index - 1), 0);
        final ImageButton kanyeHead = (ImageButton) findViewById(R.id.headimage);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                imageHolder.get(index));
        Bitmap croppedBmp = Bitmap.createBitmap(icon, 0, 0, icon.getWidth() / 3, icon.getHeight());
        kanyeHead.setImageBitmap(croppedBmp);

    }

    //Button for going to next image
    public void shiftImageForward(View view) {
        if (index < imageHolder.size() - 1)
            index = (index + 1);
        final ImageButton kanyeHead = (ImageButton) findViewById(R.id.headimage);
        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                imageHolder.get(index));
        Bitmap croppedBmp = Bitmap.createBitmap(icon, 0, 0, icon.getWidth() / 3, icon.getHeight());
        kanyeHead.setImageBitmap(croppedBmp);

    }

    //Button for starting the game
    public void sendPlay(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
