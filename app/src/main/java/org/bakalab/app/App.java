package org.bakalab.app;

import android.app.Application;
import android.content.Context;

public class App extends Application {
    // FUCK android and its context, memory leak and we dont fing care
    private volatile static Context context;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }

}
