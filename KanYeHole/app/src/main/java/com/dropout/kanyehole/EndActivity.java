package com.dropout.kanyehole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * This Class is for the End Screen
 */
public class EndActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Grab the old score from the game
        CharSequence score = getIntent().getCharSequenceExtra("Score");
        setContentView(R.layout.end_layout);
        TextView scoreText = (TextView) findViewById(R.id.score);
        scoreText.setText("Score: " + score);

        //Add score to local leaderboard
        Leaderboard.savePreferences("kanye", score + " ");
    }

    //Go to menu
    public void onBackPressed() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);

    }

    //Go to menu
    public void sendMenu(View view) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }
}
