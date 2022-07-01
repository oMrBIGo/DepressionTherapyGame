package com.depressiontherapygame.Users;

/**
 * Created on 18-10-21.
 * Update on 26-04-22.
 * Developer by Nathit Panrod
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.Consult.DashboardActivity;
import com.depressiontherapygame.Users.FirstTimeDep.FirstMainActivity;
import com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame.MainActivityGame;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.LoginRegister.UserProfileActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.QuizDepression.QuizMainActivity;
import com.depressiontherapygame.Users.Setting.SettingActivity;
import com.depressiontherapygame.Users.ViewPager.ViewPagerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout sliderDotsPanel;
    private int dotsCount;
    private ImageView[] dots;
    private ImageView imageView;
    private TextView textViewWelcome, textViewLv;
    private ProgressBar progressBar;
    private Button btnQuizDep, Tetris;
    SharedPref sharedPref;
    ImageView depressionBtn1, depressionBtn2, depressionBtn3, depressionBtn4, depressionBtn5, depressionBtn6, depressionBtn7, depressionBtn8;
    ImageView settingBtn;
    Dialog dialog;
    Button DialogTetris;
    private long backPressedTime;

    private static final String TAG = "BANNER_AD_TAG";

    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* NightMode [SharedPref.java] */
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        init_screen();

        dialog = new Dialog(this);

        settingBtn = findViewById(R.id.settingBtn);

        final Animation animation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_bounce_home);

        viewPager = (ViewPager) findViewById(R.id.viewPager);

        sliderDotsPanel = (LinearLayout) findViewById(R.id.SliderDots);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        btnQuizDep = findViewById(R.id.btnQuizDep);
        Tetris = findViewById(R.id.Button_play_Tetris);
        viewPager.setAdapter(viewPagerAdapter);

        TextView Tetris_Description = (TextView) findViewById(R.id.Tetris_Description);
        Tetris_Description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTetrisDescriptionDialog();
                Tetris_Description.startAnimation(animation);
            }
        });

        Tetris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, MainActivityGame.class));
                finish();
                Tetris.setEnabled(false);
            }
        });

        /* Button Click: Show dialog Tetris */
        DialogTetris = findViewById(R.id.details_tetris_btn);
        DialogTetris.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTetrisDialog();
            }
        });

        /* Button Click: next to [DashboardActivity.java] */
        Button btnConDep = (Button) findViewById(R.id.btnConDep);
        btnConDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, DashboardActivity.class));
                finish();
                btnConDep.setEnabled(false);
            }
        });

        imageView = findViewById(R.id.icon_profile);

        depressionBtn1 = findViewById(R.id.depressionBtn1);
        depressionBtn2 = findViewById(R.id.depressionBtn2);
        depressionBtn3 = findViewById(R.id.depressionBtn3);
        depressionBtn4 = findViewById(R.id.depressionBtn4);
        depressionBtn5 = findViewById(R.id.depressionBtn5);
        depressionBtn6 = findViewById(R.id.depressionBtn6);
        depressionBtn7 = findViewById(R.id.depressionBtn7);
        depressionBtn8 = findViewById(R.id.depressionBtn8);
        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLv = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(HomeActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }

        settingBtn = (ImageView) findViewById(R.id.settingBtn);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                settingBtn.startAnimation(animation);
                settingBtn.setEnabled(false);
            }
        });

        depressionBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog01();
                depressionBtn1.startAnimation(animation);
            }
        });

        depressionBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog02();
                depressionBtn2.startAnimation(animation);
            }
        });

        depressionBtn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog03();
                depressionBtn3.startAnimation(animation);
            }
        });

        depressionBtn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog04();
                depressionBtn4.startAnimation(animation);
            }
        });

        depressionBtn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog05();
                depressionBtn5.startAnimation(animation);
            }
        });

        depressionBtn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog06();
                depressionBtn6.startAnimation(animation);
            }
        });

        depressionBtn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog07();
                depressionBtn7.startAnimation(animation);
            }
        });

        depressionBtn8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDepDialog08();
                depressionBtn8.startAnimation(animation);
            }
        });

        dotsCount = viewPagerAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 0);
            sliderDotsPanel.addView(dots[i], params);
        }
        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.nonactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new MyTimerTask(), 2000, 3000);

    }

    public class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            HomeActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() == 0) {
                        viewPager.setCurrentItem(1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });

        }
    }

    private void openDepDialog01 () {
        dialog.setContentView(R.layout.depression01_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void openDepDialog02 () {
        dialog.setContentView(R.layout.depression02_layout_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /* open Dialog Show Dep03 */
    private void openDepDialog03 () {
        /* set dialog [dep03_layout_dialog.xml] */
        dialog.setContentView(R.layout.depression03_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep04 */
    private void openDepDialog04 () {
        /* set dialog [dep04_layout_dialog.xml] */
        dialog.setContentView(R.layout.depression04_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep05 */
    private void openDepDialog05 () {
        /* set dialog [dep05_layout_dialog.xml] */
        dialog.setContentView(R.layout.depression05_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep06 */
    private void openDepDialog06 () {
        /* set dialog [dep06_layout_dialog.xml] */
        dialog.setContentView(R.layout.depression06_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep07 */
    private void openDepDialog07 () {
        /* set dialog [dep07_layout_dialog.xml] */
        dialog.setContentView(R.layout.depression07_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep08 */
    private void openDepDialog08 () {
        /* set dialog [dep08_layout_dialog.xml] */
        dialog.setContentView(R.layout.depression08_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Detail_tetris */
    private void openTetrisDialog() {
        /* set dialog [tetris_layout_dialog.xml] */
        dialog.setContentView(R.layout.hwtopytetris_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Description_tetris */
    private void openTetrisDescriptionDialog() {
        /* set dialog [tetris_layout_dialog.xml] */
        dialog.setContentView(R.layout.tetris_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* show user profile header */
    private void showUserProfile(FirebaseUser firebaseUser) {
        final Animation animation = AnimationUtils.loadAnimation(HomeActivity.this, R.anim.button_bounce_home);
        String userID = firebaseUser.getUid();
        /* Extracting USer Reference from Database for "Register Users" */
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUserShow modelUserShow = snapshot.getValue(ModelUserShow.class);

                if (modelUserShow != null) {

                    String lastname = "" + snapshot.child("lastname").getValue();
                    String email = "" + snapshot.child("email").getValue();
                    String level = "" + snapshot.child("level").getValue();
                    String image = "" + snapshot.child("image").getValue();
                    String firstdepression = "" + snapshot.child("firstdepression").getValue();
                    textViewWelcome.setText(lastname);
                    textViewLv.setText("ปัจจุบัน " + level);

                    if (firstdepression.toString().equals("ยังไม่ได้ทำแบบประเมิน")) {
                        btnQuizDep.setBackgroundDrawable(ContextCompat.getDrawable(HomeActivity.this,R.drawable.button_state_list_animator));
                        Tetris.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(HomeActivity.this, "กรุณาทำแบบประเมินครั้งแรกก่อนเล่นเกมเททริส", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(HomeActivity.this, "กรุณาทำแบบประเมินครั้งแรกก่อนเล่นเกมเททริส", Toast.LENGTH_SHORT).show();
                        Tetris.setText("กรุณาทำแบบประเมินครั้งแรก");
                        DialogTetris.setVisibility(View.GONE);
                        Tetris.setBackgroundDrawable(ContextCompat.getDrawable(HomeActivity.this,R.drawable.button_state_list_animator_gray));
                        btnQuizDep.setText(R.string.firstdepression);
                        /* Button Click: next to [SplashQuizActivity.java] */
                        btnQuizDep.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this, FirstMainActivity.class);
                                startActivity(intent);
                                finish();
                                btnQuizDep.startAnimation(animation);
                                btnQuizDep.setEnabled(false);
                            }
                        });
                    } else if (level.toString().equals("เลเวล12")) {

                        btnQuizDep.setBackgroundDrawable(ContextCompat.getDrawable(HomeActivity.this,R.drawable.button_state_list_animator));
                        btnQuizDep.setText(R.string.level_show_before);
                        /* Button Click: next to [SplashQuizActivity.java] */
                        Tetris.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this, MainActivityGame.class);
                                startActivity(intent);
                                finish();
                                Tetris.setEnabled(false);
                            }
                        });
                        btnQuizDep.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this, QuizMainActivity.class);
                                startActivity(intent);
                                finish();
                                btnQuizDep.setEnabled(false);
                            }
                        });
                    } else {
                        btnQuizDep.setBackgroundDrawable(ContextCompat.getDrawable(HomeActivity.this,R.drawable.button_state_list_animator_gray));
                        btnQuizDep.setText(R.string.level_12);
                        /* Button Click: next to [MainActivityGame.java] */
                        Tetris.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(HomeActivity.this, MainActivityGame.class);
                                startActivity(intent);
                                finish();
                                Tetris.setEnabled(false);
                            }
                        });
                        btnQuizDep.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(HomeActivity.this, "กรุณาปลดล็อคเลเวล 12 เกมเททริส คุณถึงสามารถทำแบบประเมินได้", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(HomeActivity.this, "กรุณาปลดล็อคเลเวล 12 เกมเททริส คุณถึงสามารถทำแบบประเมินได้", Toast.LENGTH_SHORT).show();
                    }

                    if (level.toString().equals("เลเวล1")) {
                        ImageView levelUp = (ImageView) findViewById(R.id.level);
                        levelUp.setVisibility(View.GONE);
                    }

                    //set image, using Picasso
                    Picasso.get().load(image).resize(100,100).into(imageView);

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeActivity.this, "มีอะไรบางอย่างผิดปกติ!",
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

    /* onBackPressed */
    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            finish();
        } else {
            Toast.makeText(this, "ทำอีกครั้งเพื่อออกจากแอปพลิเคชัน", Toast.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }
}