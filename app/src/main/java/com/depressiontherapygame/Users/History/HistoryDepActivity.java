package com.depressiontherapygame.Users.History;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.depressiontherapygame.Users.History.Adapter.AdapterHD;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.History.Model.ModelHD;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.LoginRegister.UserProfileActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.Setting.SettingActivity;
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

public class HistoryDepActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String myUid;
    List<ModelHD> HdList;
    SharedPref sharedPref;
    AdapterHD adapterHd;
    private FirebaseAuth authProfile;

    FirebaseAuth firebaseAuth;
    ImageView icon_profile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_dep);

        init_screen();

        authProfile = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = authProfile.getCurrentUser();
        recyclerView = findViewById(R.id.hisRecyclerviewDep);
        LinearLayoutManager layoutManager = new LinearLayoutManager(HistoryDepActivity.this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);


        showUserProfile(firebaseUser);

        icon_profile = findViewById(R.id.icon_profile);

        //Progressbar
        progressBar = findViewById(R.id.progressBar);

        icon_profile = findViewById(R.id.icon_profile);
        icon_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryDepActivity.this, UserProfileActivity.class));
                finish();
            }
        });

        loadHisDep();

        ImageView buttonBack = (ImageView) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryDepActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                buttonBack.setEnabled(false);
            }
        });

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

                    String image = ""+snapshot.child("image").getValue();

                    //set image, using Picasso
                    Picasso.get().load(image).resize(100,130).into(icon_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryDepActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadHisDep() {
        //init comments list
        HdList = new ArrayList<ModelHD>();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        String uid = firebaseUser.getUid();
        //path of the post, to get it's comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        ref.child(uid).child("ประวัติการทำแบบประเมิน").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HdList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelHD modelHd = ds.getValue(ModelHD.class);

                    HdList.add(modelHd);


                    //setup adapter
                    adapterHd = new AdapterHD(HistoryDepActivity.this, HdList);
                    adapterHd.notifyDataSetChanged();
                    //set adapter
                    recyclerView.setAdapter(adapterHd);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
    int backPressed = 0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(HistoryDepActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}