package com.depressiontherapygame.Users;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.depressiontherapygame.R;

public class MyServiceMusic extends Service {

    private MediaPlayer mMediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.main_sound);
        mMediaPlayer.setLooping(true);
    }

    public void onStart(Intent intent, int startId) {
        mMediaPlayer.start();
    }

    public void onDestroy() {
        mMediaPlayer.stop();
    }


}

