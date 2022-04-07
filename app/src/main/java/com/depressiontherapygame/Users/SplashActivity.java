package com.depressiontherapygame.Users;

/**
 * Created on 18-10-21.
 * Update on 27-01-22.
 * Developer by Nathit Panrod
 */

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;

public class SplashActivity extends AppCompatActivity {

    /* View */
    ProgressBar progressBar;
    private ImageView imageView;
    SharedPref sharedPref;
    TextView valueText;
    int value;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* NightMode [SharedPref.java] */
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        /* init View */
        imageView = findViewById(R.id.Logo);
        progressBar = findViewById(R.id.progressBar_splash);
        valueText = (TextView) findViewById(R.id.valueText);
        /* Animation Show */
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(200);
        animation.setStartOffset(50);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        valueText.startAnimation(animation);

        /* Change ImageView NightMode [turn:off-on] */
        if (sharedPref.loadNightModeState() == true) {
            imageView.setImageResource(R.drawable.logo_black);
        }

        /* [Enable]init_screen */
        init_screen();

        /* [Enable]Start ProgressBar */
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                startProgress();
            }
        });
        thread.start();

    }

    /* Start Progress Bar */
    public void startProgress() {
        for (value = 0; value <= 99; value = value + 1) {
            try {
                Thread.sleep(15);
                progressBar.setProgress(value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    valueText.setText(String.valueOf("รอสักครู่ " + value + "%"));
                }
            });
        }
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    /* Init Screen */
    private void init_screen() {
        final int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);
        final View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    decorView.setSystemUiVisibility(flags);
                }
            }
        });
    }

    /* onBackPressed */
    int backPressed = 0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}