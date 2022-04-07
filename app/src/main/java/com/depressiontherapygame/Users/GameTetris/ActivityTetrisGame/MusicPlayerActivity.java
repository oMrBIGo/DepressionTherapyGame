package com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.depressiontherapygame.Users.GameTetris.utils.PrefUtils;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.GameTetris.services.MusicPlayerService;
import com.depressiontherapygame.Users.GameTetris.utils.Consts;

import timber.log.Timber;

/**
 * This activity is act as base activity for the application. it will bind the @MusicPlayerService
 */
public class MusicPlayerActivity extends AppCompatActivity {

    public MusicPlayerService mService;
    public PrefUtils mPrefUtils;
    public SoundPool spSound;
    public int imove;
    boolean serviceBound = false;
    //Binding this Client to the AudioPlayer Service
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            MusicPlayerService.LocalBinder binder = (MusicPlayerService.LocalBinder) service;
            mService = binder.getService();
            serviceBound = true;

            Intent intent = new Intent(MusicPlayerActivity.this, MusicPlayerService.class);

            boolean playMusic = mPrefUtils.getBoolean(Consts.SharedPrefs.IS_MUSIC, true);

            if (playMusic) {
                intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
                mService.sendCommand(intent);
            } else {
                intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
                mService.sendCommand(intent);
            }

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

            Timber.e("Service Bound");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            startService(new Intent(MusicPlayerService.ACTION_STOP_MUSIC));
            serviceBound = false;
        }
    };

    public void soundMove() {
        spSound.play(imove, 1.0f, 1.0f, 1, 0, 1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        doBindService();
    }

    @Override
    protected void onStop() {
        doUnbindService();
        super.onStop();
    }

    public void doBindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);

        // start playing music if the user specified so in the settings screen
        if (!serviceBound) {
            boolean playMusic = mPrefUtils.getBoolean(Consts.SharedPrefs.IS_MUSIC, true);
            if (playMusic) {
                intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
            } else {
                intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
            }

            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            serviceBound = true;
        } else {
            boolean playMusic = mPrefUtils.getBoolean(Consts.SharedPrefs.IS_MUSIC, true);
            if (playMusic) {
                intent.setAction(MusicPlayerService.ACTION_START_MUSIC);
                mService.sendCommand(intent);
            } else {
                intent.setAction(MusicPlayerService.ACTION_STOP_MUSIC);
                mService.sendCommand(intent);

            }
        }
    }

    public void doUnbindService() {
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPrefUtils = new PrefUtils(this);
        spSound = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        imove = spSound.load(this, R.raw.smashing, 1);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("ServiceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        serviceBound = savedInstanceState.getBoolean("ServiceState");
    }

    @Override
    protected void onDestroy() {
        if (serviceBound) {
            unbindService(serviceConnection);
            //service is active
            mService.stopSelf();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
