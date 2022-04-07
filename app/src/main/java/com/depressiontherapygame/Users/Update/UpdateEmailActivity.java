package com.depressiontherapygame.Users.Update;

import static com.android.volley.VolleyLog.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.MainActivity;

import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.Setting.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextInputEditText textViewAuthenticated;
    private String userOldEmail, userNewEmail, userPassword;
    private Button buttonUpdateEmail;
    private CardView buttonUpdateEmailCv;
    private TextInputEditText editTextNewEmail, editTextPassword;
    SharedPref sharedPref;
    private TextView tvAuthenticated, tvAuthenticatedTt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        init_screen();

        ImageView imageView = findViewById(R.id.buttonBack);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateEmailActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                imageView.setEnabled(false);
            }
        });

        progressBar = findViewById(R.id.progressBar);
        editTextPassword = findViewById(R.id.editText_update_password_v);
        editTextNewEmail = findViewById(R.id.editText_update_email_new);
        textViewAuthenticated = findViewById(R.id.textView_update_email_old);
        buttonUpdateEmail = findViewById(R.id.ButtonUpdateEmail);
        textViewAuthenticated.setOnTouchListener(otl);
        buttonUpdateEmailCv = findViewById(R.id.buttonUpdateEmailCardView);
        tvAuthenticated = findViewById(R.id.textView_update_email_new_header);
        tvAuthenticatedTt = findViewById(R.id.textView_update_email_authenticated_a);

        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        //set old email ID on TextView
        userOldEmail = firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")) {
            Toast.makeText(UpdateEmailActivity.this, "อะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้", Toast.LENGTH_LONG).show();
        } else {

            reAuthenticate(firebaseUser);
        }
    }


    private View.OnTouchListener otl = new View.OnTouchListener() {
        public boolean onTouch(View v, MotionEvent event) {
            return true; // the listener has consumed the event
        }
    };

    //ReAuthenticate/Verify Suer before updating email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser = findViewById(R.id.Button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Obtain password for authentication
                userPassword = editTextPassword.getText().toString().trim();

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(UpdateEmailActivity.this, "ต้องใช้รหัสผ่านเพื่อดำเนินการต่อ",
                            Toast.LENGTH_LONG).show();
                    editTextPassword.setError("กรุณาใส่รหัสผ่านสำหรับการตรวจสอบสิทธิ์");
                    editTextPassword.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail, userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //set TextView to show that user is authenticated
                                tvAuthenticated.setText("คุณได้รับการตรวจสอบแล้ว");
                                tvAuthenticatedTt.setText("คุณสามารถอัปเดตอีเมลได้เลย");
                                Toast.makeText(UpdateEmailActivity.this, "รหัสผ่านได้รับการยืนยัน" +
                                        "คุณสามารถเปลี่ยนแปลงอีเมลได้เลย", Toast.LENGTH_LONG).show();

                                //Disable EditText for password, button to verify user and enable EditText for new Email authenticated
                                editTextNewEmail.setEnabled(true);
                                editTextPassword.setEnabled(false);
                                buttonVerifyUser.setEnabled(false);
                                buttonUpdateEmail.setEnabled(true);

                                //Change color of Update Email button
                                buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this,
                                        R.color.dark_green));

                                buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        userNewEmail = editTextNewEmail.getText().toString().trim();
                                        if (TextUtils.isEmpty(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "จำเป็นต้องใช้อีเมลที่ต้องการเปลี่ยนใหม่", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("กรุณากรอกอีเมลที่คุณต้องการเปลี่ยนใหม่");
                                            editTextNewEmail.requestFocus();
                                        } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()) {
                                            Toast.makeText(UpdateEmailActivity.this, "กรุณากรอกอีเมลที่ถูกต้อง", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("กรุณาระบุอีเมล");
                                            editTextNewEmail.requestFocus();
                                        } else if (userOldEmail.matches(userNewEmail)) {
                                            Toast.makeText(UpdateEmailActivity.this, "อีเมลใหม่ต้องไม่เหมือนกับอีเมลเก่า", Toast.LENGTH_LONG).show();
                                            editTextNewEmail.setError("กรุณากรอกอีเมล์ใหม่");
                                            editTextNewEmail.requestFocus();
                                        } else {
                                            progressBar.setVisibility(View.VISIBLE);
                                            UpdateValueEmail();
                                        }
                                    }
                                });

                                //Update color of Change password Button
                                buttonUpdateEmailCv.setCardBackgroundColor(ContextCompat.getColorStateList(
                                        UpdateEmailActivity.this, R.color.dark_green));
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(UpdateEmailActivity.this, "กรุณากรอกรหัสผ่านให้ถูกต้อง", Toast.LENGTH_SHORT).show();
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void updateEmail(final FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()) {

                    //Verify Email
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "อัปเดตอีเมลแล้ว โปรดยืนยันอีเมลใหม่ของคุณ", Toast.LENGTH_SHORT).show();
                    Toast.makeText(UpdateEmailActivity.this, "กรุณาเข้าสู่ระบบใหม่อีกครั้งเพื่อยืนยันอีเมล", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(UpdateEmailActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void UpdateValueEmail() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("email", userNewEmail);

        //Delete Data from Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        databaseReference.child(firebaseUser.getUid()).updateChildren(result).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: User Data Deleted");
                // deleteUser Auth
                // deletePhoto();
                updateEmail(firebaseUser);
                authProfile.signOut();
                Toast.makeText(UpdateEmailActivity.this, "อัพเดตข้อมูลอีเมลเรียบร้อย หากคุณต้องการกลับมาใช้อีเมลเดิม กรุณาตรวจเช็คข้อความในอีเมลของคุณ", Toast.LENGTH_LONG).show();
                //Stop user for returning to UpdateEmailActivity on pressing back button and close activity

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " +e.getMessage());
                Toast.makeText(UpdateEmailActivity.this, "ไม่สามารถแก้ไขข้อมูลอีเมลได้ กรุณาลองใหม่อีกครั้ง", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(UpdateEmailActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}