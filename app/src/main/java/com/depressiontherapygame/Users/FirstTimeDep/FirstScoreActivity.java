package com.depressiontherapygame.Users.FirstTimeDep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.QuizDepression.Adapter.RecycAdapter;
import com.depressiontherapygame.Users.QuizDepression.Model.RecycModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FirstScoreActivity extends AppCompatActivity implements RecycAdapter.OnReCycListener {

    private TextView score;
    private ImageView imageView;
    private TextView textViewWelcome, textViewLv, result, resultfirst;
    private ProgressBar progressBar;
    private String lastname, email;
    private FirebaseAuth authProfile;
    Dialog dialog;
    private List<RecycModel> mList = new ArrayList<>();
    private RecycAdapter mAdapter;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_score);
        init_screen();

        /* dialog show */
        dialog = new Dialog(this);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new RecycAdapter(mList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        prepareMovieData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);


        /* animation Button */
        final Animation animation = AnimationUtils.loadAnimation(FirstScoreActivity.this, R.anim.button_bounce_home);

        score = findViewById(R.id.score);
        result = findViewById(R.id.form_score);
        resultfirst = findViewById(R.id.firstform_score);
        String score_str = getIntent().getStringExtra("SCORE");
        String message = getIntent().getStringExtra("RESULT");
        score.setText(score_str);
        result.setText(message);


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLv = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        Button button_home = (Button) findViewById(R.id.button_home);
        button_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FirstScoreActivity.this, HomeActivity.class));
                finish();
                button_home.setEnabled(false);
            }
        });

        ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstScoreActivity.this, HomeActivity.class);
                startActivity(intent);
                buttonBack.startAnimation(animation);
                finish();
                buttonBack.setEnabled(false);
            }
        });

        if (firebaseUser == null) {
            Toast.makeText(FirstScoreActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void prepareMovieData() {
        RecycModel reCyc = new RecycModel(R.drawable.depq01);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq02);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq03);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq04);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq05);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq06);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq07);
        mList.add(reCyc);
        mAdapter.notifyDataSetChanged();
    }


    private void showUserProfile(final FirebaseUser firebaseUser) {
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
                    textViewLv.setText("ปัจจุบัน "+level);

                    //set image, using Picasso
                    Picasso.get().load(image).resize(130,130).into(imageView);

                    if (level.toString().equals("เลเวล1")) {
                        ImageView levelUp = (ImageView) findViewById(R.id.level);
                        levelUp.setVisibility(View.GONE);
                    }
                }
                progressBar.setVisibility(View.GONE);
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FirstScoreActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onReCycClick(int position) {
        mList.get(position);

        if (position == 0) {
            openDepDialog01();
        } else if (position == 1) {
            openDepDialog02();
        } else if (position == 2) {
            openDepDialog03();
        } else if (position == 3) {
            openDepDialog04();
        } else if (position == 4) {
            openDepDialog05();
        } else if (position == 5) {
            openDepDialog06();
        } else if (position == 6) {
            openDepDialog07();
        }

    }

    /* open Dialog Show Dep01 */
    private void openDepDialog01 () {
        /* set dialog [depq01_dialog.xml] */
        dialog.setContentView(R.layout.depq01_dialog);
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

    /* open Dialog Show Dep02 */
    private void openDepDialog02 () {
        /* set dialog [depq02_dialog.xml] */
        dialog.setContentView(R.layout.depq02_dialog);
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

    /* open Dialog Show Dep03 */
    private void openDepDialog03 () {
        /* set dialog [depq03_dialog.xml] */
        dialog.setContentView(R.layout.depq03_dialog);
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
        /* set dialog [depq04_dialog.xml] */
        dialog.setContentView(R.layout.depq04_dialog);
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
        /* set dialog [depq05_dialog.xml] */
        dialog.setContentView(R.layout.depq05_dialog);
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
        /* set dialog [depq06_dialog.xml] */
        dialog.setContentView(R.layout.depq06_dialog);
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
        /* set dialog [depq07_dialog.xml] */
        dialog.setContentView(R.layout.depq07_dialog);
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

            Intent intent = new Intent(FirstScoreActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}