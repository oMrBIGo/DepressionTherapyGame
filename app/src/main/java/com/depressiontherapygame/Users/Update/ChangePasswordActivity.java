package com.depressiontherapygame.Users.Update;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.FirstTimeDep.FirstScoreActivity;
import com.depressiontherapygame.Users.History.HistoryLoginActivity;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.LoginRegister.UserProfileActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.Setting.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

public class ChangePasswordActivity extends AppCompatActivity {

    FirebaseAuth authProfile;
    private EditText editTextPasswordCurrent, editTextPasswordNew, editTextPasswordConfirmNew;
    private TextView textViewAuthenticated;
    private Button buttonChangePassword, buttonReAuthenticate;
    private CardView buttonChangePasswordCardView;
    private ProgressBar progressBar;
    private String userPasswordCurrent;
    SharedPref sharedPref;

    ImageView icon_profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        init_screen();

        editTextPasswordNew = findViewById(R.id.editText_change_password_new);
        editTextPasswordCurrent = findViewById(R.id.editText_change_password_current);
        editTextPasswordConfirmNew = findViewById(R.id.editText_change_password_new_confirm);
        textViewAuthenticated = findViewById(R.id.textView_change_password_authenticated);
        progressBar = findViewById(R.id.progressBar);
        buttonReAuthenticate = findViewById(R.id.Button_change_password_authenticate_user);
        buttonChangePassword = findViewById(R.id.ButtonChangePassword);
        buttonChangePasswordCardView = findViewById(R.id.ButtonChangePasswordCardView);

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        icon_profile = findViewById(R.id.icon_profile);

        showUserProfile(firebaseUser);

        final Animation animation = AnimationUtils.loadAnimation(ChangePasswordActivity.this, R.anim.button_bounce_home);

        ImageButton buttonBack = (ImageButton) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChangePasswordActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                buttonBack.setEnabled(false);
                buttonBack.startAnimation(animation);
            }
        });

        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่พบรายละเอียดข้อมูลของผู้ใช้",
                    Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }

    }

    //ReAuthenticate USer before changing password
    private void reAuthenticateUser(final FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPasswordCurrent = editTextPasswordCurrent.getText().toString().trim();
                String checkPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

                if (TextUtils.isEmpty(userPasswordCurrent)) {
                    Toast.makeText(ChangePasswordActivity.this, "ต้องใช้รหัสผ่าน", Toast.LENGTH_SHORT).show();
                    editTextPasswordCurrent.setError("กรุณาใส่รหัสผ่านปัจจุบันของคุณเพื่อรับรองความถูกต้อง");
                    editTextPasswordCurrent.requestFocus();
                } else if (!userPasswordCurrent.matches(checkPassword)) {
                    editTextPasswordCurrent.setError("กรุณากรอกรหัสผ่านที่ประกอบไปด้วย อักษรพิมพ์เล็ก อักษรพิมพ์ใหญ่ ตัวเลขและอักขระพิเศษ รวมกันอย่างน้อย 8 ตัวขึ้นไป เช่น Abc1234#");
                    editTextPasswordCurrent.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate User new
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPasswordCurrent);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @SuppressLint("SetTextI18n")
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //Disable editText for Current Password / Enable EditText for New Password and Confirm New Password
                                editTextPasswordCurrent.setEnabled(false);
                                editTextPasswordNew.setEnabled(true);
                                editTextPasswordConfirmNew.setEnabled(true);

                                //Enable Change Password Button. Disable Authenticate Button
                                buttonReAuthenticate.setEnabled(false);
                                buttonChangePassword.setEnabled(true);

                                //Set TextView to show User is authenticated / verified
                                textViewAuthenticated.setText("คุณได้รับการตรวจสอบ/ยืนยันแล้ว" +
                                        "คุณสามารถเปลี่ยนรหัสผ่านได้ทันที!");
                                Toast.makeText(ChangePasswordActivity.this, "รหัสผ่านได้รับการยืนยันแล้ว"
                                        + "เปลี่ยนรหัสผ่านใหม่", Toast.LENGTH_SHORT).show();

                                //Update color of Change password Button
                                buttonChangePasswordCardView.setCardBackgroundColor(ContextCompat.getColorStateList(
                                        ChangePasswordActivity.this, R.color.dark_green));

                                buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changePassword(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, "รหัสผ่านไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    private void changePassword(FirebaseUser firebaseUser) {

        String userPasswordNew = editTextPasswordNew.getText().toString();
        String userPasswordConfirmNew = editTextPasswordConfirmNew.getText().toString();
        String checkPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

        if (TextUtils.isEmpty(userPasswordNew)) {
            Toast.makeText(ChangePasswordActivity.this, "ต้องใช้รหัสผ่านใหม่", Toast.LENGTH_SHORT).show();
            editTextPasswordNew.setError("กรุณาใส่รหัสผ่านใหม่ของคุณ");
            editTextPasswordNew.requestFocus();
        } else if (!userPasswordNew.matches(checkPassword)) {
            editTextPasswordNew.setError("กรุณากรอกรหัสผ่านที่ประกอบไปด้วย อักษรพิมพ์เล็ก อักษรพิมพ์ใหญ่ ตัวเลขและอักขระพิเศษ รวมกันอย่างน้อย 8 ตัวขึ้นไป เช่น Abc1234#");
            editTextPasswordNew.requestFocus();
        }else if (TextUtils.isEmpty(userPasswordConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "กรุณายืนยันรหัสผ่านใหม่ของคุณ", Toast.LENGTH_SHORT).show();
            editTextPasswordConfirmNew.setError("กรุณากรอกรหัสผ่านใหม่อีกครั้ง");
            editTextPasswordConfirmNew.requestFocus();
        }else if (!userPasswordConfirmNew.matches(checkPassword)) {
            editTextPasswordConfirmNew.setError("รหัสผ่านไม่ตรงกัน");
            editTextPasswordConfirmNew.requestFocus();
        } else if (!userPasswordNew.matches(userPasswordConfirmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "รหัสผ่านไม่ตรงกัน", Toast.LENGTH_SHORT).show();
            editTextPasswordConfirmNew.setError("กรุณาใส่รหัสผ่านเดิมอีกครั้ง");
            editTextPasswordConfirmNew.requestFocus();
        } else if (!userPasswordCurrent.matches(userPasswordCurrent)) {
            Toast.makeText(ChangePasswordActivity.this, "รหัสผ่านใหม่ไม่สามารถเหมือนกับรหัสผ่านเก่าได้", Toast.LENGTH_SHORT).show();
            editTextPasswordNew.setError("กรุณาใส่รหัสผ่านใหม่");
            editTextPasswordNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPasswordNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, "รหัสผ่านถูกเปลี่ยน", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        try {
                            throw task.getException();
                        } catch (Exception e) {
                            Toast.makeText(ChangePasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                }
            });
        }

    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

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
                Toast.makeText(ChangePasswordActivity.this, "มีอะไรบางอย่างผิดปกติ!",
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

            Intent intent = new Intent(ChangePasswordActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}