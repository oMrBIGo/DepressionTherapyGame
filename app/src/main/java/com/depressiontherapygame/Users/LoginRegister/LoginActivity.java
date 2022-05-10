package com.depressiontherapygame.Users.LoginRegister;

/**
 * Created on 19-10-21.
 * Update on 30-01-22.
 * Developer by Nathit Panrod
 */

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.Users.MainActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    /* View */
    private static final String FILE_EMAIL = "EmailSave";
    private TextInputEditText edittextEmail, edittextPassword;
    private ProgressBar progressBar;
    ImageButton backBtn;
    private Button LoginBtn, forgotPass;
    private ImageView imageView;
    DatabaseReference databaseUsers;
    SharedPref sharedPref;
    private static final String TAG = "LoginActivity";
    String textLastname, textPhone, depression, firstdepression, BeforeDepression, level, TimeNow;
    int score, firstscore;
    Dialog dialog;
    private Button ButtonPasswordResetEmail;
    private TextInputEditText editTextPasswordResetEmail;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* NightMode [SharedPref.java] */
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        init_screen();

        /* dialog show */
        dialog = new Dialog(this);

        /* init view */
        imageView = (ImageView) findViewById(R.id.Logo);
        edittextEmail = findViewById(R.id.email);
        edittextPassword = findViewById(R.id.password);
        progressBar = findViewById(R.id.progressBar);

        /* Change ImageView NightMode [turn:off-on] */
        if (sharedPref.loadNightModeState() == true) {
            imageView.setImageResource(R.drawable.logo_black);
        }

        Date d = new Date();
        CharSequence s = android.text.format.DateFormat.format("MM-dd-yy hh-mm-ss", d.getTime());
        TimeNow = String.valueOf(s);

        /* Checkbox save email and password [true and false] */
        final CheckBox mCheckBoxRemember = (CheckBox) findViewById(R.id.remember_Me);

        /* remember Me Save Email or Password User */
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        String textEmail = sharedPreferences.getString("textEmail", "");
        String textPassword = sharedPreferences.getString("textPassword", "");
        if (sharedPreferences.contains("checked") && sharedPreferences.getBoolean("checked", false) == true) {
            mCheckBoxRemember.setChecked(true);
        } else {
            mCheckBoxRemember.setChecked(false);
        }

        /* Initialize Firebase Auth */
        authProfile = FirebaseAuth.getInstance();
        databaseUsers = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");

        /* setText email and password */
        edittextEmail.setText(textEmail);
        edittextPassword.setText(textPassword);



        /* Button Click: next to [RegisterActivity.java] */
        TextView nextReg = (TextView) findViewById(R.id.nextRegister);
        nextReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                nextReg.setEnabled(false);
            }
        });

        /* Button Click: back to [MainActivity.java] */
        backBtn = findViewById(R.id.buttonBack);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtn.startAnimation(animation);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                backBtn.setEnabled(false);
            }
        });

        /* Button Click: Sign in to [HomeActivity.java] */
        LoginBtn = findViewById(R.id.ButtonLogin);
        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                /* Input String email, password [Pass 6+] */
                String textEmail = edittextEmail.getText().toString().trim();
                String textPassword = edittextPassword.getText().toString().trim();
                String checkPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

                if (mCheckBoxRemember.isChecked()) {
                    editor.putBoolean("checked", true);
                    editor.apply();
                    StoreDataUsingSharedPref(textEmail, textPassword);

                    if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        edittextEmail.setError("กรุณากรอกอีเมลให้ถูกต้อง");
                        edittextEmail.requestFocus();
                    } else if (TextUtils.isEmpty(textPassword)) {
                        edittextPassword.setError("จำเป็นต้องมีรหัสผ่าน");
                        edittextPassword.requestFocus();
                    } else if (!textPassword.matches(checkPassword)) {
                        edittextPassword.setError("กรุณากรอกรหัสผ่านที่ประกอบไปด้วย อักษรพิมพ์เล็ก อักษรพิมพ์ใหญ่ ตัวเลขและอักขระพิเศษ รวมกันอย่างน้อย 8 ตัวขึ้นไป เช่น Abc1234#");
                        edittextPassword.requestFocus();
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        loginUser(textLastname, textEmail, textPhone, textPassword, score, depression, firstscore, firstdepression, BeforeDepression, level);
                    }
                } else {
                    editor.putBoolean("checked", false);
                    editor.apply();
                    if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        edittextEmail.setError("กรุณากรอกอีเมลให้ถูกต้อง");
                        edittextEmail.requestFocus();
                    } else if (TextUtils.isEmpty(textPassword)) {
                        edittextPassword.setError("จำเป็นต้องมีรหัสผ่าน");
                        edittextPassword.requestFocus();
                    } else if (!textPassword.matches(checkPassword)) {
                        edittextPassword.setError("กรุณากรอกรหัสผ่านที่ประกอบไปด้วย อักษรพิมพ์เล็ก อักษรพิมพ์ใหญ่ ตัวเลขและอักขระพิเศษ รวมกันอย่างน้อย 8 ตัวขึ้นไป เช่น Abc1234#");
                        edittextPassword.requestFocus();
                    } else {
                        getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit().clear().commit();
                        progressBar.setVisibility(View.VISIBLE);
                        loginUser(textLastname, textEmail, textPhone, textPassword, score, depression, firstscore, firstdepression, BeforeDepression, level);
                    }
                }
            }
        });

        /* Button Click : forgot Password */
        forgotPass = findViewById(R.id.forgotPassword);
        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openForgotDialog();
            }
        });
    }

    /* Remember Me: save email and password on SharedPreferences */
    private void StoreDataUsingSharedPref(String textEmail, String textPassword) {
        SharedPreferences.Editor editor = getSharedPreferences(FILE_EMAIL, MODE_PRIVATE).edit();
        editor.putString("textEmail", textEmail);
        editor.putString("textPassword", textPassword);
        editor.apply();
    }

    /* login user using the credentials given */
    private void loginUser(final String textLastname, final String textEmail, final String textPhone, final String textPassword, int score, final String depression, int firstscore, final String firstdepression, final String BeforeDepression, final String level) {
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        authProfile.signInWithEmailAndPassword(textEmail, textPassword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    /* Sign in success, update UI with the signed-in user's information */
                    FirebaseUser user = authProfile.getCurrentUser();
                    /* if user is signing in first time then get and show user */
                    if (task.getResult().getAdditionalUserInfo().isNewUser()) {
                        /* Get user email and uid from auth */
                        String textEmail = user.getEmail();
                        String uid = user.getUid();
                        /* When user is registered store user info oin firebase realtime database too */
                        /* using HashMap */
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("uid", uid);
                        hashMap.put("email", textEmail);
                        hashMap.put("lastname", textLastname); /* will add later (e.g. edit profile) */
                        hashMap.put("phone", textPhone); /* will add later (e.g. edit profile) */
                        hashMap.put("image", ""); /* will add later (e.g. edit profile) */
                        hashMap.put("score", score);
                        hashMap.put("depression", depression);
                        hashMap.put("firstscore", firstscore);
                        hashMap.put("firstdepression", firstdepression);
                        hashMap.put("BeforeDepression", BeforeDepression);
                        hashMap.put("level", level);

                        /* Initialize FirebaseDatabase */
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        /* path to store user data named "ผู้ใช้งาน" */
                        DatabaseReference reference = database.getReference("ผู้ใช้งาน");
                        /* put data with in hashmap in database */
                        reference.child(uid).setValue(hashMap);
                    }
                    if (user.isEmailVerified()) {
                        LoginBtn.startAnimation(animation);
                        Toast.makeText(LoginActivity.this, "เข้าสู่ระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                        IpAddressTimeHis();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                        LoginBtn.setEnabled(false);
                    } else {
                        LoginBtn.startAnimation(animation);
                        Toast.makeText(LoginActivity.this, "เข้าสู่ระบบเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                        IpAddressTimeHis();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                        LoginBtn.setEnabled(false);
                    }
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        edittextEmail.setError("ไม่มีชื่อผู้ใช้อยู่ในระบบ กรุณาลงทะเบียนใหม่อีกครั้ง");
                        edittextEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        edittextPassword.setError("มีข้อมูลบางอย่างไม่ถูกต้อง กรุณาตรวจสอบและลองใหม่อีกครั้ง");
                        edittextPassword.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    LoginBtn.setEnabled(true);
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void IpAddressTimeHis() {
        FirebaseUser user = authProfile.getCurrentUser();
        String uid = user.getUid();
        String ipaddress = getLocalIpAddress();
        String logintime = String.valueOf(System.currentTimeMillis());
        String id = databaseUsers.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("ipaddress", ipaddress);
        hashMap.put("logintime", logintime);


        //put this data in db
        databaseUsers.child(uid).child("ประวัติการเข้าใช้งาน").child(id).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //added
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed, not added
                    }
                });

    }

    /* Check if User is already logged in. In such case, straightaway take the User to the User */
    @Override
    protected void onStart() {
        super.onStart();
        if (authProfile.getCurrentUser() != null) {
            Toast.makeText(LoginActivity.this, "เข้าสู่ระบบแล้ว!", Toast.LENGTH_SHORT).show();
            //Start the HomeActivity
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            IpAddressTimeHis();
            finish();
        } else {
            Toast.makeText(LoginActivity.this, "คุณสามารถเข้าสู่ระบบได้ทันที!", Toast.LENGTH_SHORT).show();
        }
    }

    /* open Dialog Show UploadProfile */
    private void openForgotDialog() {
        /* animation Button */
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        /* set dialog [upload_layout_dialog.xml] */
        dialog.setContentView(R.layout.forgot_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //Button Click update profile
        editTextPasswordResetEmail = dialog.findViewById(R.id.editText_password_reset_email);
        ButtonPasswordResetEmail = dialog.findViewById(R.id.ButtonPasswordReset);

        ButtonPasswordResetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextPasswordResetEmail.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(LoginActivity.this, "กรุณากรอกอีเมลที่ลงทะเบียนไว้", Toast.LENGTH_SHORT).show();
                    editTextPasswordResetEmail.setError("จำเป็นต้องใช้อีเมล");
                    editTextPasswordResetEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, "กรุณากรอกอีเมลที่ถูกต้อง", Toast.LENGTH_SHORT).show();
                    editTextPasswordResetEmail.setError("ต้องใช้อีเมลที่ถูกต้อง");
                    editTextPasswordResetEmail.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);
        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cancelBtn.startAnimation(animation);
            }
        });
        /* dialog show */
        dialog.show();
    }

    private void resetPassword(String email) {
        authProfile = FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "โปรดตรวจสอบกล่องข้อความของคุณ เพื่อรีเซ็ตรหัสผ่าน",
                            Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window and not within our app
                    startActivity(intent);
                    dialog.dismiss();

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextPasswordResetEmail.setError("ไม่มีข้อมูลการลงทะเบียนของอีเมลนี้ กรุณาลงทะเบียนใหม่อีกครั้ง");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("TAG", "***** IP=" + ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("TAG", ex.toString());
        }
        return null;
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
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }
}