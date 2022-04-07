package com.depressiontherapygame.Users.GameTetris;

import timber.log.Timber;

public class Application extends android.app.Application {
    private static Application sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());

        sInstance = this;
    }


    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static Application getInstance() {
        return sInstance;
    }
}
