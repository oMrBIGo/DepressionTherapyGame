package com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivityGame extends AppBaseActivity {

    @BindView(R.id.tv_select_level)
    AppCompatTextView tvSelectLevel;
    @BindView(R.id.tv_play)
    AppCompatTextView tvPlay;
    @BindView(R.id.iv_setting)
    AppCompatImageView ivSetting;
    @BindView(R.id.iv_sound)
    AppCompatImageView ivSound;

    private ImageView imageView;
    private TextView textViewWelcome, textViewEmail;
    private ProgressBar progressBar;
    private String lastname, email;

    private FirebaseAuth authProfile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        ButterKnife.bind(this);

        if (mNetworkUtils.isConnected()) {
        } else {
        }
        init_screen();
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewEmail = findViewById(R.id.email_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        if (firebaseUser == null) {
            Toast.makeText(MainActivityGame.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    @Override
    protected void onResume() {
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
                    String email = "" + snapshot.child("email").getValue();

                    textViewWelcome.setText(lastname);
                    textViewEmail.setText(email);

                    String image = ""+snapshot.child("image").getValue();

                    //set image, using Picasso
                    Picasso.get().load(image).into(imageView);

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
      /*  if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            if (mNetworkUtils.isConnected()) {
                mInterstitialAd.show();
            }
        }*/

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
