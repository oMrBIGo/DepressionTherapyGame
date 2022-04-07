package com.depressiontherapygame.Users.History;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.History.Adapter.AdapterHL;
import com.depressiontherapygame.Users.History.Model.ModelHL;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.Setting.SettingActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class HistoryLoginActivity extends AppCompatActivity {

    //views
    RecyclerView recyclerView;
    String myUid;
    List<ModelHL> HlList;
    AdapterHL adapterHl;
    ImageView buttonBack;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_login);

        init_screen();

        recyclerView = findViewById(R.id.hisRecyclerview);

        checkUserStatus();

        loadHisLogin();

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryLoginActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                buttonBack.setEnabled(false);
            }
        });
    }

    private void loadHisLogin() {
        /* Get user email and uid from auth */
        //layout(Linear) for recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init comments list
        HlList = new ArrayList<ModelHL>();

        //path of the post, to get it's comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน").child(myUid).child("ประวัติการเข้าใช้งาน");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HlList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    ModelHL modelHl = ds.getValue(ModelHL.class);

                    HlList.add(modelHl);
                    //pass myUid and postId as parameter of constructor of Comment Adapter


                    //setup adapter
                    adapterHl = new AdapterHL(HistoryLoginActivity.this, HlList);
                    //set adapter
                    recyclerView.setAdapter(adapterHl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!= null) {
            //user is signed in
            myUid = user.getUid();
        } else {
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
            Intent intent = new Intent(HistoryLoginActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}