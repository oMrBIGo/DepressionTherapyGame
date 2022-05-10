package com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.GameTetris.Model.LeaderModel;
import com.depressiontherapygame.Users.GameTetris.adapters.LeaderAdapter;
import com.depressiontherapygame.Users.History.HistoryDepActivity;
import com.depressiontherapygame.Users.History.HistoryLoginActivity;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
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

public class LeaderboardActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    ImageView icon_profile;
    RecyclerView recyclerView;
    LeaderAdapter leaderAdapter;
    List<LeaderModel> leaderModelList;
    private TextView textViewWelcome, textViewLevel;
    private ProgressBar progressBar;
    ImageView buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        final Animation animation = AnimationUtils.loadAnimation(LeaderboardActivity.this, R.anim.button_bounce_home);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeaderboardActivity.this, MainActivityGame.class));
                finish();
                buttonBack.startAnimation(animation);
                buttonBack.setEnabled(false);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLevel = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        icon_profile = findViewById(R.id.icon_profile);

        showUserProfile(firebaseUser);

        init_screen();

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        leaderModelList = new ArrayList<>();

        recyclerView.setAdapter(leaderAdapter);

        loadLeaderBoard();
    }

    private void loadLeaderBoard() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("TopScore");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                leaderModelList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    LeaderModel model = ds.getValue(LeaderModel.class);

                    leaderModelList.add(model);

                    int newIndex = 0;
                    leaderModelList.add(newIndex,model);
                    leaderAdapter.notifyItemInserted(newIndex);
                    recyclerView.smoothScrollToPosition(newIndex);

                    leaderAdapter = new LeaderAdapter(LeaderboardActivity.this, leaderModelList);
                    leaderAdapter.notifyDataSetChanged();
                    recyclerView.setAdapter(leaderAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                    String lastname = "" + snapshot.child("lastname").getValue();
                    String level = "" + snapshot.child("level").getValue();
                    String image = ""+snapshot.child("image").getValue();

                    textViewWelcome.setText(lastname);
                    textViewLevel.setText("ปัจจุบัน "+level);

                    //set image, using Picasso
                    Picasso.get().load(image).resize(100,130).into(icon_profile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LeaderboardActivity.this, "มีอะไรบางอย่างผิดปกติ!",
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
    int backPressed = 0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(LeaderboardActivity.this, MainActivityGame.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}