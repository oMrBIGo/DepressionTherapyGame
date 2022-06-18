package com.depressiontherapygame.Users.Setting;

/**
 * Created on 22-10-21.
 * Update on 02-02-22.
 * Developer by Nathit Panrod
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.Consult.DashboardActivity;
import com.depressiontherapygame.Users.History.HistoryDepActivity;
import com.depressiontherapygame.Users.History.HistoryLoginActivity;
import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.MainActivity;
import com.depressiontherapygame.Users.SplashActivity;
import com.depressiontherapygame.Users.Update.ChangePasswordActivity;
import com.depressiontherapygame.Users.Update.DeleteProfileActivity;
import com.depressiontherapygame.Users.Update.UpdateEmailActivity;
import com.depressiontherapygame.Users.LoginRegister.UserProfileActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;

import pl.droidsonroids.gif.GifImageView;

public class SettingActivity extends AppCompatActivity {

    /* View */
    private static final String FILE_NAME = "EmailSave";
    SharedPref sharedPref;
    private ProgressBar progressBar;
    private String lastname, email;
    private TextView textViewWelcome;
    private TextView textViewEmail;
    private TextView dep, firstdep;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    ImageView profileIv;
    LinearLayout ver_box;
    ImageButton back;
    Dialog dialog;
    TextView emailV_Tv;
    GifImageView gifImageView;
    Switch mySwitch;
    private RelativeLayout btnvef;

    private static final String TAG = "BANNER_AD_TAG";

    //declare AdView (Banner Ad)
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* NightMode [SharedPref.java] */
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btnvef = findViewById(R.id.menu_vef);
        /* dialog show */
        dialog = new Dialog(this);

        gifImageView = findViewById(R.id.gif);

        /* Save Email */
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        String textEmail = sharedPreferences.getString("textEmail", "ไม่พบข้อมูล");

        final Animation animation = AnimationUtils.loadAnimation(SettingActivity.this, R.anim.button_bounce_home);

        profileIv = findViewById(R.id.icon_profile);
        profileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
                profileIv.startAnimation(animation);
                profileIv.setEnabled(false);
            }
        });

        init_screen();

        /* Initialize Firebase */
        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        /* init view */
        textViewWelcome = findViewById(R.id.textView_show_id);
        textViewEmail = findViewById(R.id.textView_show_welcome);
        dep = findViewById(R.id.depression);
        dep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dep.setMaxLines(3);
            }
        });
        firstdep = findViewById(R.id.firstdepression);
        firstdep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstdep.setMaxLines(3);
            }
        });
        progressBar = findViewById(R.id.progressBar);

        /* Check user sign in and check email verified*/
        if (firebaseUser == null) {
            Toast.makeText(SettingActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        emailV_Tv = findViewById(R.id.emailV_Tv);
        ver_box = findViewById(R.id.verifity_box);

        checkIfEmailVerified(firebaseUser);

        /* Button Click: next to [HomeActivity.java] */
        back = findViewById(R.id.buttonBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                back.startAnimation(animation);
                back.setEnabled(false);
            }
        });

        /* Button Click: next to [HomeActivity.java] */
        btnvef.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser.sendEmailVerification();
                showAlertDialog();

            }
        });

        /* Button Click: next to [HomeActivity.java] */
        RelativeLayout btnhome = (RelativeLayout) findViewById(R.id.menu_home);
        btnhome.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                btnhome.setEnabled(false);
            }
        });

        /* Button Click: next to [UserProfileActivity.java] */
        RelativeLayout btnProfile = (RelativeLayout) findViewById(R.id.menu_profile);
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
                btnProfile.setEnabled(false);

            }
        });

        /* Button Click: next to [HistoryLoginActivity.java] */
        RelativeLayout hisUserBtn = (RelativeLayout) findViewById(R.id.menu_his_user);
        hisUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HistoryLoginActivity.class);
                startActivity(intent);
                finish();
                hisUserBtn.setEnabled(false);

            }
        });

        /* Button Click: next to [HistoryLoginActivity.java] */
        RelativeLayout hisDepBtn = (RelativeLayout) findViewById(R.id.menu_his_depression);
        hisDepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, HistoryDepActivity.class);
                startActivity(intent);
                finish();
                hisDepBtn.setEnabled(false);

            }
        });

        /* Button Click: next to [UpdateEmailActivity.java] */
        RelativeLayout btnUpdateEmail = (RelativeLayout) findViewById(R.id.menu_update_email);
        btnUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
                btnUpdateEmail.setEnabled(false);
            }
        });

        /* Button Click: next to [ChangePasswordActivity.java] */
        RelativeLayout btnChangePassword = (RelativeLayout) findViewById(R.id.menu_change_password);
        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, ChangePasswordActivity.class);
                startActivity(intent);
                finish();
                btnChangePassword.setEnabled(false);
            }
        });

        RelativeLayout btnFeedBack = (RelativeLayout) findViewById(R.id.menu_feedback);
        btnFeedBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }

            }
        });

        /* Button Click: next to [DeleteProfileActivity.java] */
        RelativeLayout btnDeleteUser = (RelativeLayout) findViewById(R.id.menu_delete_profile);
        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingActivity.this, DeleteProfileActivity.class);
                startActivity(intent);
                finish();
                btnDeleteUser.setEnabled(false);
            }
        });

        /* Button Click: signOut */
        Button buttonLogout = (Button) findViewById(R.id.menu_logout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAlertDialogLogout();

            }
        });

        /* Switch NightMode share to [SharedPref.java] */
        mySwitch = (Switch) findViewById(R.id.myswitch);
        if (sharedPref.loadNightModeState() == true) {
            mySwitch.setChecked(true);
        }
        /* Switch Click Change true[Night] and false[Day] and restartApp */
        mySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPref.setNightModeState(true);
                    restartApp();
                    mySwitch.setEnabled(false);
                } else {
                    sharedPref.setNightModeState(false);
                    restartApp();
                    mySwitch.setEnabled(false);
                }
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
    protected void onResume() {
        if (adView != null) {
            adView.resume();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }


    /* check Email Verification */
    private void showAlertDialog() {
        /* Setup the Alert Builder */
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("อีเมลยังไม่ได้รับการยืนยัน กรุณาตรวจสอบ");
        builder.setMessage("เมื่อคุณกด ยืนยันอีเมล ทางระบบจะส่ง email ไปให้คุณยืนยัน \n เมื่อคุณยืนยันแล้วกรุณาออกจากระบบและเข้าสู่ระบบใหม่อีกครั้ง.");

        /* Open Email Apps if User clicks/taps Continue button */
        builder.setPositiveButton("ยืนยันอีเมล", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window and not within our app
                startActivity(intent);
            }
        });
        /* Create the AlertDialog */
        AlertDialog alertDialog = builder.create();
        /* Show the AlertDialog */
        alertDialog.show();
    }

    //Users coming to UserProfileActivity after successful registration
    private void showAlertDialogLogout() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("ออกจากระบบ");
        builder.setMessage("คุณต้องการออกจากระบบใช่หรือไม่?");

        //Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("ออกจากระบบ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                authProfile.signOut();
                Intent intent = new Intent(SettingActivity.this, SplashActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(SettingActivity.this, "คุณได้ออกจากระบบแล้ว", Toast.LENGTH_SHORT).show();
            }
        });
        //Return to User Profile Activity if USer presses Cancel Button
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        //Create the AlertDialog
        final AlertDialog alertDialog = builder.create();

        //Show the AlertDialog
        alertDialog.show();
    }

    /* restartApp on Click Switch Change */
    public void restartApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                //Create an intent that will start the main activity.
                Intent mainIntent = new Intent(SettingActivity.this, SettingActivity.class);
                SettingActivity.this.startActivity(mainIntent);

                //Finish splash activity so user cant go back to it.
                SettingActivity.this.finish();
                mySwitch.setEnabled(true);

                //Apply splash exit (fade out) and main entry (fade in) animation transitions.
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }, 1);
    }

    /* showUserProfile Firebase Realtime [ID] show lastname, email and depression  */
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
                    String depression = "" + snapshot.child("depression").getValue();
                    String firstdepression = "" + snapshot.child("firstdepression").getValue();
                    String image = ""+snapshot.child("image").getValue();

                    textViewEmail.setText(email);
                    textViewWelcome.setText("ชื่อผู้ใช้: " + lastname);
                    dep.setText(depression);
                    firstdep.setText(firstdepression);

                    //set image, using Picasso
                    Picasso.get().load(image).resize(100,130).into(profileIv);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SettingActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /* checkIfEmailVerified */
    @SuppressLint("ResourceAsColor")
    private void checkIfEmailVerified (FirebaseUser firebaseUser){
        if (!firebaseUser.isEmailVerified()) {
            ver_box.setBackgroundResource(R.drawable.notverifity_box);
            gifImageView.setImageResource(R.drawable.warn_gif);
            emailV_Tv.setText("not verified");
            emailV_Tv.setTextSize(10);
            emailV_Tv.setTextColor(getResources().getColor(R.color.white));
            btnvef.setVisibility(View.VISIBLE);;

        }
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

    /* onBackPressed */
    int backPressed = 0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(SettingActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}
