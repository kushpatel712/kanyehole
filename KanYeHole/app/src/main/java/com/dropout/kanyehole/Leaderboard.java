package com.dropout.kanyehole;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;


public class Leaderboard extends Activity {
    int score = 0;
    String highScore = null;
    public static ArrayList<Integer> scores =new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
       // View rootView = findViewById(android.R.id.content);

      /* rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            public void onGlobalLayout() {*/

                loadSavedPreferences("kanye");

           // }
       // });




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

    public void generateLeaderboard(){

        TextView  one = (TextView) findViewById(R.id.one);
        TextView  two = (TextView) findViewById(R.id.two);
        TextView  three = (TextView) findViewById(R.id.three);
        TextView  four = (TextView) findViewById(R.id.four);
        TextView  five = (TextView) findViewById(R.id.five);
        TextView  song = (TextView) findViewById(R.id.song);
        System.out.println(scores.get(0));

        System.out.println(scores.toString());
        one.setText("1: "+scores.get((0)));
        two.setText("2: "+scores.get((1)));
        three.setText("3: "+scores.get((2)));
        four.setText("4: "+scores.get((3)));
        five.setText("5: "+scores.get((4)));
        StringBuilder sb=new StringBuilder();

        if(scores.size()>5) {
            for (int i = 0; i < 5; i++) {

                sb.append(scores.get(i) + " ");

            }
            System.out.println(sb.toString());
            truncatePreferences("kanye", sb.toString());
        }




    }
    public void loadSavedPreferences(String song) {

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);

        highScore = sharedPreferences.getString(song, "0");
        String[] parse=highScore.split("\\s+");
        scores.clear();
        for(int i=0;i<parse.length;i++){
           scores.add(Integer.parseInt(parse[i]));
        }
        Collections.sort(scores);
        Collections.reverse(scores);
        while(scores.size()<5){
            scores.add(0);
        }

        System.out.println("Generating");
        generateLeaderboard();

    }

    public static void savePreferences(String song, String value) {

       /* StringBuilder sb= new StringBuilder();
        for(int i=0;i<scores.size();i++){
            sb.append(scores.get(i));
            sb.append(" ");
        }*/

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getAppContext());
        String current = sharedPreferences.getString(song, "0");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(song, current+value);

        editor.commit();

    }

    public static void truncatePreferences(String song, String truncatedValues){

        SharedPreferences truncatePreferences = PreferenceManager
                .getDefaultSharedPreferences(MyApplication.getAppContext());
        String current = truncatePreferences.getString(song, "0");
        SharedPreferences.Editor editor = truncatePreferences.edit();
        editor.putString(song, truncatedValues);

        editor.commit();

    }
}
