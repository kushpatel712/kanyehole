package com.dropout.kanyehole;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This Class is for the Leaderboard page
 */

public class Leaderboard extends Activity {
    private static ArrayList<Integer> scores = new ArrayList<Integer>();
    private String highScore = null;

    //Save the scores to local preferences
    public static void savePreferences(String song, String value) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getAppContext());
        String current = sharedPreferences.getString(song, "0");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(song, current + value);
        editor.commit();
    }

    //Truncate the local preference list
    public static void truncatePreferences(String song, String truncatedValues) {
        SharedPreferences truncatePreferences = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = truncatePreferences.edit();
        editor.putString(song, truncatedValues);
        editor.commit();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        loadSavedPreferences("kanye");
        System.out.println("Leaderboard generated");
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

    //Create the top 5 leaderboard using scores list
    public void generateLeaderboard() {
        TextView one = (TextView) findViewById(R.id.one);
        TextView two = (TextView) findViewById(R.id.two);
        TextView three = (TextView) findViewById(R.id.three);
        TextView four = (TextView) findViewById(R.id.four);
        TextView five = (TextView) findViewById(R.id.five);
        System.out.println(scores.get(0));

        System.out.println(scores.toString());

        //Set the textviews to the scores
        one.setText("1: " + scores.get((0)));
        two.setText("2: " + scores.get((1)));
        three.setText("3: " + scores.get((2)));
        four.setText("4: " + scores.get((3)));
        five.setText("5: " + scores.get((4)));
        StringBuilder sb = new StringBuilder();

        if (scores.size() > 5) {
            for (int i = 0; i < 5; i++) {
                sb.append(scores.get(i) + " ");

            }
            System.out.println(sb.toString());
            truncatePreferences("kanye", sb.toString());
        }


    }

    //Load scoreslist from the local preferences
    public void loadSavedPreferences(String song) {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        highScore = sharedPreferences.getString(song, "0");
        String[] parse = highScore.split("\\s+");
        scores.clear();
        for (int i = 0; i < parse.length; i++) {
            scores.add(Integer.parseInt(parse[i]));
        }
        Collections.sort(scores);
        Collections.reverse(scores);
        while (scores.size() < 5) {
            scores.add(0);
        }
        System.out.println("Generating");
        generateLeaderboard();

    }
}
