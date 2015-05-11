package com.dropout.kanyehole;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * This Class is for the Starting menu
 */

public class MenuActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return true;
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

    //Go to game
    public void sendPlay(View view) {
        Intent intent = new Intent(this, Customize.class);
        startActivity(intent);
        finish();
    }

    //Go to Credits
    public void sendCredits(View view) {
        Intent intent = new Intent(this, CreditsActivity.class);
        startActivity(intent);
    }

    //Go to leaderboard
    public void sendLeader(View view) {
        Intent intent = new Intent(this, Leaderboard.class);
        startActivity(intent);
    }
}