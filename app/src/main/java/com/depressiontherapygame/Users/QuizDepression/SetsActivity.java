package com.depressiontherapygame.Users.QuizDepression;

import static com.depressiontherapygame.Users.QuizDepression.QuizMainActivity.selected_cat_index;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.QuizDepression.Adapter.SetsAdapter;
import com.depressiontherapygame.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
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
import java.util.Arrays;
import java.util.List;

public class SetsActivity extends AppCompatActivity {

    private GridView sets_grid;
    private FirebaseFirestore firestore;

    public static List<String> setsIDs = new ArrayList<>();

    SharedPref sharedPref;
    private ImageView imageView;
    private TextView textViewWelcome, textViewLv;
    private ProgressBar progressBar;
    private String lastname, email;
    private FirebaseAuth authProfile;

    private static final String TAG = "BANNER_AD_TAG";

    //declare AdView (Banner Ad)
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sets);

        init_screen();

        sets_grid = findViewById(R.id.sets_gridview);

        firestore = FirebaseFirestore.getInstance();

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLv = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        final Animation animation = AnimationUtils.loadAnimation(SetsActivity.this, R.anim.button_bounce_home);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetsActivity.this, CategoryActivity.class);
                startActivity(intent);
                finish();
                buttonBack.setEnabled(false);
                buttonBack.startAnimation(animation);
            }
        });

        if (firebaseUser == null) {
            Toast.makeText(SetsActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

        loadSets();


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
                Toast.makeText(SetsActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadSets() {

        setsIDs.clear();

        firestore.collection("QUIZ").document(QuizMainActivity.catList.get(selected_cat_index).getId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                long noOfSets = (long) documentSnapshot.get("SETS");

                for (int i = 1; i <= noOfSets; i++) {
                    setsIDs.add(documentSnapshot.getString("SET" + String.valueOf(i) + "_ID"));
                }

                SetsAdapter adapter = new SetsAdapter(setsIDs.size());
                sets_grid.setAdapter(adapter);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SetsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

            Intent intent = new Intent(SetsActivity.this, CategoryActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}