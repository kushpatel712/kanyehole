package com.dropout.kanyehole;

import android.app.Application;
import android.content.Context;

/**
 * This Class is for global context for bitmaps
 */

public class MyApplication extends Application {

    private static Context context;

    public static Context getAppContext() {
        return MyApplication.context;
    }

    public static void setContext(Context c) {
        context = c;
    }

    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
    }
}
