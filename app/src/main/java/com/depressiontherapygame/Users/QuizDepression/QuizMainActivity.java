package com.depressiontherapygame.Users.QuizDepression;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.QuizDepression.Model.CategoryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22-11-21.
 * <p>
 * [** Update : 25/11/2021 **]
 * [** DEVELOPER BY NATHIT PANROD  **]
 */

public class QuizMainActivity extends AppCompatActivity {

    SharedPref sharedPref;
    private FirebaseAuth authProfile;
    Button btnQuizStart;
    public static List<CategoryModel> catList = new ArrayList<>();
    public static int selected_cat_index = 0;
    private FirebaseFirestore firestore;


    private ImageView imageView;
    private TextView textViewWelcome;
    private TextView textViewLv;
    private ProgressBar progressBar;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_main);


        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = authProfile.getCurrentUser();

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLv = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        showUserProfile(firebaseUser);

        /* dialog show */
        dialog = new Dialog(this);

        init_screen();



        ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizMainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                buttonBack.setEnabled(false);

            }
        });

        firestore = FirebaseFirestore.getInstance();

        btnQuizStart = (Button) findViewById(R.id.btnQuizStart);
        btnQuizStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
                btnQuizStart.setEnabled(false);

            }
        });
        Button btnQuizExit = (Button) findViewById(R.id.btnQuizExit);
        btnQuizExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizMainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                btnQuizExit.setEnabled(false);

            }
        });

        Button btnDep = (Button) findViewById(R.id.btnDep);
        btnDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTetrisDialog();
            }
        });


    }

    private void loadData() {
        catList.clear();

        firestore.collection("QUIZ").document("Categories")
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        long count = (long) doc.get("COUNT");

                        for (int i = 1; i <= count; i++) {
                            String catName = doc.getString("CAT" + String.valueOf(i) + "_NAME");
                            String catID = doc.getString("CAT" + String.valueOf(i) + "_ID");

                            catList.add(new CategoryModel(catID, catName));
                        }

                        Intent intent = new Intent(QuizMainActivity.this, CategoryActivity.class);
                        startActivity(intent);
                        QuizMainActivity.this.finish();


                    } else {

                        Toast.makeText(QuizMainActivity.this, "ไม่พบข้อมูล", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(QuizMainActivity.this, HomeActivity.class);
                        startActivity(intent);
                        QuizMainActivity.this.finish();
                    }

                } else {

                    Toast.makeText(QuizMainActivity.this, "อินเทอร์เน็ตมีปัญหากรุณาตรวจสอบ", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /* open Dialog Show Detail_tetris */
    private void openTetrisDialog() {
        /* set dialog [tetris_layout_dialog.xml] */
        dialog.setContentView(R.layout.dep_dialog);
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
                    String image = "" + snapshot.child("image").getValue();

                    textViewWelcome.setText(lastname);
                    textViewLv.setText("ปัจจุบัน "+level);

                    //set image, using Picasso
                    Picasso.get().load(image).resize(130, 130).into(imageView);

                    if (level.toString().equals("เลเวล1")) {
                        ImageView levelUp = (ImageView) findViewById(R.id.level);
                        levelUp.setVisibility(View.GONE);
                    }

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(QuizMainActivity.this, "มีอะไรบางอย่างผิดปกติ!",
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

            Intent intent = new Intent(QuizMainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}