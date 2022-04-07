package com.depressiontherapygame.Users.Consult;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.Consult.Fragment.MyConsultFragment;
import com.depressiontherapygame.Users.Consult.Fragment.ShowConsultFragment;
import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DashboardActivity extends AppCompatActivity {

    //view firebase
    FirebaseAuth firebaseAuth;

    ImageView profileIv;
    TextView lastnameTv, emailTv;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        init_screen();

        //init
        firebaseAuth = FirebaseAuth.getInstance();

        //init toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");

        profileIv = findViewById(R.id.profileIv);
        lastnameTv = findViewById(R.id.lastnameTv);
        emailTv = findViewById(R.id.emailTv);

        //init views
        BottomNavigationView navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(selectedListener);

        //Show Consult fragment transaction (default)
        ShowConsultFragment showConsultFragment = new ShowConsultFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content, showConsultFragment, "");
        fragmentTransaction.commit();

        //firebase auth instance
        firebaseAuth = FirebaseAuth.getInstance();

        showUserProfile();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener selectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    //handle item clicks
                    switch (item.getItemId()) {
                        case R.id.nav_show_consult:
                            //Show Consult fragment transaction
                            ShowConsultFragment showConsultFragment = new ShowConsultFragment();
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.content, showConsultFragment, "");
                            fragmentTransaction.commit();
                            return true;
                        case R.id.nav_my_consult:
                            //My Consult fragment transaction
                            MyConsultFragment myConsultFragment = new MyConsultFragment();
                            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction2.replace(R.id.content, myConsultFragment, "");
                            fragmentTransaction2.commit();
                            return true;
                    }

                    return false;
                }
            };

    private void showUserProfile() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
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
                    String image = ""+snapshot.child("image").getValue();

                    lastnameTv.setText(lastname);
                    emailTv.setText(email);

                    //set image, using Picasso
                    Picasso.get().load(image).resize(100,130).into(profileIv);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(DashboardActivity.this, "มีอะไรบางอย่างผิดปกติ!",
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
            Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //get item id
        int id = item.getItemId();
        if (id == R.id.nav_back) {
            Intent intent = new Intent(DashboardActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        if (id == R.id.nav_add_consult) {
            Intent intent = new Intent(DashboardActivity.this, AddPostActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }

        return super.onOptionsItemSelected(item);
    }

}
