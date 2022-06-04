package com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.depressiontherapygame.Users.GameTetris.base.AppBaseActivity;
import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.GameTetris.utils.Consts;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.Setting.SettingActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageButton;
import pl.droidsonroids.gif.GifImageView;

public class MainActivityGame extends AppBaseActivity {

    @BindView(R.id.tv_select_level)
    AppCompatTextView tvSelectLevel;
    @BindView(R.id.tv_play)
    AppCompatTextView tvPlay;
    @BindView(R.id.iv_setting)
    AppCompatImageView ivSetting;
    @BindView(R.id.iv_sound)
    AppCompatImageView ivSound;

    private ImageButton buttonBack;
    private ImageView imageView;
    private TextView textViewWelcome, textViewLevel;
    private ProgressBar progressBar;
    private String lastname, email;

    private FirebaseAuth authProfile;

    private static final String TAG = "BANNER_AD_TAG";

    //declare AdView (Banner Ad)
    private AdView adView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        ButterKnife.bind(this);

        final Animation animation = AnimationUtils.loadAnimation(MainActivityGame.this, R.anim.button_bounce_home);

        if (mNetworkUtils.isConnected()) {
        } else {
        }
        init_screen();
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityGame.this, HomeActivity.class));
                finish();
                buttonBack.setEnabled(false);
                buttonBack.startAnimation(animation);
            }
        });

        GifImageView Leaderboard = (GifImageView) findViewById(R.id.Leaderboard);
        Leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivityGame.this, LeaderboardActivity.class));
                finish();
                Leaderboard.setEnabled(false);
                Leaderboard.startAnimation(animation);
            }
        });

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLevel = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        if (firebaseUser == null) {
            Toast.makeText(MainActivityGame.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        /* AdMobs */
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Log.d(TAG, "onInitializationComplete: ");
            }
        });

        //Set your test devices. Check your logcat output for the hashed device ID to
        //get test ads a physical device. e.g.
        MobileAds.setRequestConfiguration(
                new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("", "")).build()
        );

        //init banner ad
        adView = findViewById(R.id.adView);
        //ad request
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //setUp ad listener
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdClicked() {
                super.onAdClicked();
                Log.d(TAG, "onAdClicked: ");
            }

            @Override
            public void onAdClosed() {
                super.onAdClosed();
                Log.d(TAG, "onAdClosed: ");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                Log.e(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());
            }

            @Override
            public void onAdImpression() {
                super.onAdImpression();
                Log.d(TAG, "onAdImpression: ");
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Log.d(TAG, "onAdLoaded: ");
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
                Log.d(TAG, "onAdOpened: ");
            }
        });
    }

    @Override
    protected void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (adView != null) {
            adView.resume();
        }
        super.onResume();
        boolean isMusic = mPrefUtils.getBoolean(Consts.SharedPrefs.IS_MUSIC, true);
        //Set Music icon
        if (isMusic) {
            ivSound.setImageDrawable(getResources().getDrawable(R.drawable.volume));
        } else {
            ivSound.setImageDrawable(getResources().getDrawable(R.drawable.volume1));
        }
    }

    @OnClick({R.id.tv_select_level, R.id.tv_play, R.id.tv_exit,R.id.iv_setting ,R.id.iv_sound})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select_level:
                startActivity(new Intent(MainActivityGame.this, LevelActivity.class));
                break;
            case R.id.tv_play:
                startActivity(new Intent(MainActivityGame.this, GameActivity.class));
                break;
            case R.id.tv_exit:

                startActivity(new Intent(MainActivityGame.this, HomeActivity.class));
                finish();
                break;
            case R.id.iv_setting:
                startActivity(new Intent(MainActivityGame.this, SettingActivityGame.class));
                break;
            case R.id.iv_sound:
                if (!mPrefUtils.getBoolean(Consts.SharedPrefs.IS_MUSIC, true)) {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_MUSIC, true);
                    if (getApplicationContext() != null)
                        ((MusicPlayerActivity) MainActivityGame.this).doBindService();
                    ivSound.setImageDrawable(getResources().getDrawable(R.drawable.volume));

                } else {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_MUSIC, false);
                    if (getApplicationContext() != null)
                        ((MusicPlayerActivity) MainActivityGame.this).doUnbindService();
                    ivSound.setImageDrawable(getResources().getDrawable(R.drawable.volume1));
                }
                break;
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting USer Reference from Database for "Register Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUserShow modelUserShow = snapshot.getValue(ModelUserShow.class);
                if (modelUserShow != null) {
                    String lastname = "" + snapshot.child("lastname").getValue();
                    String level = "" + snapshot.child("level").getValue();
                    String image = ""+snapshot.child("image").getValue();

                    textViewWelcome.setText(lastname);
                    textViewLevel.setText("ปัจจุบัน "+level);

                    //set image, using Picasso
                    Picasso.get().load(image).into(imageView);
                    if (level.toString().equals("เลเวล1")) {
                        ImageView levelUp = (ImageView) findViewById(R.id.level);
                        levelUp.setVisibility(View.GONE);
                    }

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivityGame.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

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


    int backPressed = 0;

    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(MainActivityGame.this, HomeActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }

    /**
     * Show Exit Dialog
     */
    private void showExitDialog() {

        Dialog mExitDialog = new Dialog(MainActivityGame.this);

        if (mExitDialog.getWindow() != null) {
            mExitDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = mExitDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            mExitDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mExitDialog.setContentView(R.layout.dialog_exit);
            mExitDialog.setCancelable(false);
            mExitDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout llExit = mExitDialog.findViewById(R.id.ll_exit);
            LinearLayout llNo = mExitDialog.findViewById(R.id.ll_no);

            llNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mExitDialog.isShowing())
                        mExitDialog.dismiss();
                }
            });

            llExit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mExitDialog.isShowing())
                        mExitDialog.dismiss();

                    finish();
                }
            });

            mExitDialog.show();
        }
    }

    /**
     * Show level up dialog
     */
    private void showLevelUpDialog() {

        Dialog mLevelUpDialog = new Dialog(MainActivityGame.this);

        if (mLevelUpDialog.getWindow() != null) {
            mLevelUpDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            Window window = mLevelUpDialog.getWindow();
            WindowManager.LayoutParams wlp = window.getAttributes();
            mLevelUpDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            wlp.gravity = Gravity.CENTER;
            window.setAttributes(wlp);
            mLevelUpDialog.setContentView(R.layout.dialog_level_completed);
            mLevelUpDialog.setCancelable(false);
            mLevelUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            LinearLayout llRestart = mLevelUpDialog.findViewById(R.id.ll_restart);
            AppCompatTextView tvLevel = mLevelUpDialog.findViewById(R.id.tv_level);
            AppCompatTextView tvLevelNext = mLevelUpDialog.findViewById(R.id.tv_next_level);
            tvLevel.setText("level 1");
            llRestart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mLevelUpDialog.isShowing())
                        mLevelUpDialog.dismiss();

                    //  onReplayClick();
                }
            });

            mLevelUpDialog.show();
        }
    }
}
