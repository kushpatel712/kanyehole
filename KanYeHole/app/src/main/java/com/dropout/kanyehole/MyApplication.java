package com.dropout.kanyehole;

import android.app.Application;
import android.content.Context;

/**
 * Created by Kush on 4/20/2015.
 */
public class MyApplication extends Application {

    private static Context context;

    public void onCreate(){
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
    public static void setContext(Context c){
        context=c;
    }
}
