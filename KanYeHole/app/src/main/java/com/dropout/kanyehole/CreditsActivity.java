package com.dropout.kanyehole;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.MediaController;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;


public class CreditsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);


    }


    @Override
     public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credits, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.dancingditto);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        VideoView videoHolder = (VideoView)findViewById(R.id.videoView);
        videoHolder.setMediaController(new MediaController(this));
        videoHolder.setVideoURI(video);
        videoHolder.requestFocus();
        System.out.println("Attempting to play video");
        videoHolder.start();
        System.out.println("Playing video");
        final ScrollView scroll = (ScrollView)findViewById(R.id.scrollView);
        final TextView songs= (TextView) findViewById(R.id.songs);
        final TextView credit= (TextView) findViewById(R.id.credits);
        StringBuilder credits = new StringBuilder();
        credits.append("Songs:\n\n");
        credits.append("Flashing Lights by Kanye West\n");
        credits.append("You Belong With Me by Taylor Swift\n");
        credits.append("Power by Kanye West\n\n");
        credits.append("Images taken from:\n\n");
        credits.append("http://kanyeheads.tumblr.com\n");
        credits.append("http://www.polyvore.com/\n");
        credits.append("http://soulalcatraz.deviantart.com/\n");
        credits.append("http://www.mirror.co.uk/\n");
        credits.append("http://nicklewis.ca/\n");
        credits.append("http://imgkid.com/\n");
        credits.append("http://galleryhip.com/\n");
        credits.append("Images edited with Python and Gimp\n\n");
        credits.append("Credits Video:\n\n");
        credits.append("Ditto dancing 'Conga'\n");
        credits.append("URL: https://www.youtube.com/watch?v=ODKTITUPusM\n");
        credits.append("Song: Conga by Gloria Estefan\n\n\n");
        credits.append("KanYe-Hole\n");
        credits.append("Developed by: Nathan Nguyen, Kush Patel, and Hanpei Zhang\n");

        songs.setText(credits.toString());
        ScrollingMovementMethod smm= new ScrollingMovementMethod();
        credit.setMovementMethod(new ScrollingMovementMethod());

        Animation translatebu= AnimationUtils.loadAnimation(this, R.anim.animationfile);
        Animation credittop= AnimationUtils.loadAnimation(this, R.anim.creditanim);


        credit.startAnimation(credittop);
        songs.startAnimation(translatebu);
       /* scroll.post(new Runnable()
        {
            public void run()
            {
                scroll.smoothScrollTo(0, credit.getBottom());
            }
        });*/
        /*while (scroll.canScrollVertically(1)) {
            System.out.println("Scrolling");
            scroll.smoothScrollBy(0, 1);
        }*/

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        //moveTaskToBack(true);
    }
}
