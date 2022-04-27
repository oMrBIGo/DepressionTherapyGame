package com.depressiontherapygame.Users.FirstTimeDep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.FirstTimeDep.Adapter.FirstSetsAdapter;
import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class FirstSetsActivity extends AppCompatActivity {

    private GridView sets_grid;
    private FirebaseFirestore firestore;
    public static List<String> setsIDs = new ArrayList<>();
    SharedPref sharedPref;
    private ImageView imageView;
    private TextView textViewWelcome, textViewLv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_sets);
        init_screen();

        sets_grid = findViewById(R.id.sets_gridview);

        firestore = FirebaseFirestore.getInstance();

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLv = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        if (firebaseUser == null) {
            Toast.makeText(FirstSetsActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        loadSets();

        final Animation animation = AnimationUtils.loadAnimation(FirstSetsActivity.this, R.anim.button_bounce_home);

        ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FirstSetsActivity.this, HomeActivity.class);
                startActivity(intent);
                buttonBack.startAnimation(animation);
                finish();
                buttonBack.setEnabled(false);
            }
        });


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
                    textViewLv.setText("ปัจจุบัน "+ level);

                    //set image, using Picasso
                    Picasso.get().load(image).resize(130,130).into(imageView);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(FirstSetsActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadSets() {

        setsIDs.clear();

        firestore.collection("QUIZ").document(FirstMainActivity.catList.get(FirstMainActivity.selected_cat_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                long noOfSets = (long) documentSnapshot.get("SETS");

                for (int i = 1; i <= noOfSets; i++) {
                    setsIDs.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID"));
                }

                FirstSetsAdapter adapter = new FirstSetsAdapter(setsIDs.size());
                sets_grid.setAdapter(adapter);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FirstSetsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

            Intent intent = new Intent(FirstSetsActivity.this, FirstCategoryActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}