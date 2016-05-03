package com.rashata.jjamie.messenger;

import android.app.Application;

import com.rashata.jjamie.messenger.manager.Contextor;

/**
 * Created by JJamie on 4/19/16 AD.
 */
public class MessengerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //Initialize thing(s) here
        Contextor.getInstance().init(getApplicationContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
