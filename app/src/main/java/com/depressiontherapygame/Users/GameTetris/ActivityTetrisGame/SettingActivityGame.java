package com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;

import com.depressiontherapygame.Users.GameTetris.base.AppBaseActivity;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.GameTetris.utils.Consts;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingActivityGame extends AppBaseActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.iv_back_toolbar)
    AppCompatImageView ivBackToolbar;
    @BindView(R.id.tv_title_toolbar)
    AppCompatTextView tvTitleToolbar;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;
    @BindView(R.id.sc_sound)
    SwitchCompat scSound;
    @BindView(R.id.sc_music)
    SwitchCompat scMusic;
    @BindView(R.id.sc_vibrate)
    SwitchCompat scVibrate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_game);
        ButterKnife.bind(this);
        initToolbar();
    }

    private void initToolbar() {
        tvTitleToolbar.setText(R.string.setting);

        boolean isSound = mPrefUtils.getBoolean(Consts.SharedPrefs.IS_SOUND, true);
        boolean isMusic = mPrefUtils.getBoolean(Consts.SharedPrefs.IS_MUSIC, true);
        boolean isVibration = mPrefUtils.getBoolean(Consts.SharedPrefs.IS_VIBRATOR, true);

        //Set Sound Switch
        if (isMusic) {
            scSound.setChecked(true);
        } else {
            scSound.setChecked(false);
        }

        //Set Music Switch
        if (isSound) {
            scMusic.setChecked(true);
        } else {
            scMusic.setChecked(false);
        }

        //Set Vibration Switch
        if (isVibration) {
            scVibrate.setChecked(true);
        } else {
            scVibrate.setChecked(false);
        }

        scSound.setOnCheckedChangeListener(this);
        scMusic.setOnCheckedChangeListener(this);
        scVibrate.setOnCheckedChangeListener(this);
    }

    @OnClick({R.id.iv_back_toolbar, R.id.sc_sound, R.id.sc_music, R.id.sc_vibrate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back_toolbar:
                onBackPressed();
                break;
            case R.id.sc_sound:
                break;
            case R.id.sc_music:
                break;
            case R.id.sc_vibrate:
                break;
        }
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.sc_sound:
                if (isChecked) {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_MUSIC, true);
                    if (SettingActivityGame.this != null)
                        ((MusicPlayerActivity) SettingActivityGame.this).doBindService();
                } else {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_MUSIC, false);
                    if (SettingActivityGame.this != null)
                        ((MusicPlayerActivity) SettingActivityGame.this).doUnbindService();
                }
                break;
            case R.id.sc_music:
                if (isChecked) {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_SOUND, true);
                } else {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_SOUND, false);
                }
                break;

            case R.id.sc_vibrate:
                if (isChecked) {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_VIBRATOR, true);
                } else {
                    mPrefUtils.setBoolean(Consts.SharedPrefs.IS_VIBRATOR, false);
                }
                break;
        }
    }
}
