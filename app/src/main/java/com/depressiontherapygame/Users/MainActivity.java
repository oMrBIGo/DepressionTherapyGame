package com.depressiontherapygame.Users;

/**
 * Created on 18-10-21.
 * Update on 27-01-22.
 * Developer by Nathit Panrod
 */

import android.content.Intent;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.depressiontherapygame.Users.LoginRegister.LoginActivity;
import com.depressiontherapygame.Users.LoginRegister.RegisterActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;

public class MainActivity extends AppCompatActivity {

    /* View */
    private long backPressedTime;
    private ImageView imageView;
    SharedPref sharedPref;
    Button register, login;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        /* NightMode [SharedPref.java] */
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* [Enable]init_screen */
        init_screen();

        /* Init VIew */
        imageView = (ImageView) findViewById(R.id.Logo);
        register = (Button) findViewById(R.id.ButtonReg);
        login = (Button) findViewById(R.id.ButtonLogin);

        /* Change ImageView NightMode [turn:off-on] */
        if (sharedPref.loadNightModeState() == true) {
            imageView.setImageResource(R.drawable.logo_black);
            }

        /* Button Animation */
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        /* Button Login */
        login = findViewById(R.id.ButtonLogin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the animation
                login.startAnimation(animation);
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                login.setEnabled(false);
            }
        });

        /* Button Register */
        register = findViewById(R.id.ButtonReg);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start the animation
                register.startAnimation(animation);
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                register.setEnabled(false);
            }
        });

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
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            System.exit(0);
        } else {
            Toast.makeText(this, "ทำอีกครั้งเพื่อออกจากแอปพลิเคชัน", Toast.LENGTH_SHORT).show();

        }
        backPressedTime = System.currentTimeMillis();
    }
}