package com.dropout.kanyehole;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class EndActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CharSequence score = getIntent().getCharSequenceExtra("Score");
//        Intent intent = new Intent("finish_activity");
//        sendBroadcast(intent);
        setContentView(R.layout.end_layout);
        TextView scoreText = (TextView) findViewById(R.id.score);
        scoreText.setText("Score: "+score);
        StringBuilder sb= new StringBuilder();
        Leaderboard.savePreferences("kanye", score+" ");
        Leaderboard.scores.add((String) score+"");
        boolean color = true;
    }

    public void onBackPressed(){
        // super.onBackPressed();
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }
    public void sendMenu(View view){
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
