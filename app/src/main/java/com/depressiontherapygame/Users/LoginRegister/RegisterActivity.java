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

import com.depressiontherapygame.Users.FirstUserProfileActivity;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    /* View */
    private TextInputEditText lastname, email, password, phone;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textShDN;
    FirebaseAuth authProfile;
    SharedPref sharedPref;
    DatabaseReference databaseUsers;
    String uid;
    Dialog dialog;
    private static final String TAG = "RegisterActivity";
    ImageButton backBtn;
    Button ButtonReg;
    int score, firstscore;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* NightMode [SharedPref.java] */
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init_screen();

        /* dialog show preliminary agreement */
        dialog = new Dialog(this);

        /* Initialize FirebaseAuth */
        authProfile = FirebaseAuth.getInstance();

        /* Change ImageView NightMode [turn:off-on] */
        imageView = (ImageView) findViewById(R.id.Logo);
        if (sharedPref.loadNightModeState() == true) {
            imageView.setImageResource(R.drawable.logo_black);
        }

        /* animation Button */
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce);

        /* Checkbox show preliminary agreement [accept and reject] */
        final CheckBox mCheckBoxRemember = (CheckBox) findViewById(R.id.agreement);

        /* Toast Show Text */
        Toast.makeText(RegisterActivity.this, "ยินดีต้อนรับเข้าสู่หน้าลงทะเบียน", Toast.LENGTH_LONG).show();

        /* init view */
        progressBar = findViewById(R.id.progressBar);
        lastname = findViewById(R.id.lastname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);

        databaseUsers = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        /* Button Click: next to [LoginActivity.java] */
        TextView nextReg = (TextView) findViewById(R.id.nextLogin);
        nextReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                /* animation next activity Fade */
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
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                /* animation next activity Fade */
                finish();
                backBtn.setEnabled(false);
            }
        });

        /* Button Click: Sign up and get user information at firebase */
        ButtonReg = findViewById(R.id.ButtonReg);
        ButtonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /* Input String lastname, email, password [Pass 6+], phoneNumber */
                String textLastname = lastname.getText().toString().trim();
                String textEmail = email.getText().toString().trim();
                String textPassword = password.getText().toString().trim();
                String textPhone = phone.getText().toString().trim();
                String checkPassword = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,20}$";

                if (mCheckBoxRemember.isChecked()) {
                    if (TextUtils.isEmpty(textLastname)) {
                        lastname.setError("กรุณาระบุชื่อเต็ม");
                        lastname.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        email.setError("กรุณากรอกอีเมลให้ถูกต้อง");
                        email.requestFocus();
                    } else if (textPhone.length() != 10) {
                        phone.setError("เบอร์โทรศัพท์ควรมี 10 หลัก");
                        phone.requestFocus();
                    } else if (TextUtils.isEmpty(textPassword)) {
                        Toast.makeText(RegisterActivity.this, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_LONG).show();
                    } else if (!textPassword.matches(checkPassword)) {
                        password.setError("กรุณากรอกรหัสผ่านที่ประกอบไปด้วย อักษรพิมพ์เล็ก อักษรพิมพ์ใหญ่ ตัวเลขและอักขระพิเศษ รวมกันอย่างน้อย 8 ตัวขึ้นไป เช่น Abc1234#");
                        password.requestFocus();
                    } else {
                        registerUser(textLastname, textEmail, textPhone, textPassword);
                    }
                } else {
                    if (TextUtils.isEmpty(textLastname)) {
                        lastname.setError("กรุณาระบุชื่อเต็ม");
                        lastname.requestFocus();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                        email.setError("กรุณากรอกอีเมลให้ถูกต้อง");
                        email.requestFocus();
                    } else if (textPhone.length() != 10) {
                        phone.setError("เบอร์โทรศัพท์ควรมี 10 หลัก");
                        phone.requestFocus();
                    } else if (TextUtils.isEmpty(textPassword)) {
                        Toast.makeText(RegisterActivity.this, "กรุณากรอกรหัสผ่าน", Toast.LENGTH_LONG).show();
                    } else if (!textPassword.matches(checkPassword)) {
                        password.setError("กรุณากรอกรหัสผ่านที่ประกอบไปด้วย อักษรพิมพ์เล็ก อักษรพิมพ์ใหญ่ ตัวเลขและอักขระพิเศษ รวมกันอย่างน้อย 8 ตัวขึ้นไป เช่น Abc1234#");
                        password.requestFocus();
                    } else {
                        Toast.makeText(RegisterActivity.this, "กรุณาอ่านข้อตกลงเบื้องต้นอย่างละเอียดก่อนจะลงทะเบียน", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        /* Button Click: click to show [Dialog] preliminary agreement */
        Button buttonCheck = (Button) findViewById(R.id.btnCheck);
        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openConditionDialog();
            }
        });
    }

    /* open Dialog Show preliminary agreement */
    private void openConditionDialog() {
        /* set dialog [condition_layout_dialog.xml] */
        dialog.setContentView(R.layout.condition_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button confirmBtn = dialog.findViewById(R.id.confirm);
        Button cancelBtn = dialog.findViewById(R.id.cancel);
        /* checkbox */
        final CheckBox mCheckBoxRemember = (CheckBox) findViewById(R.id.agreement);

        /* button click accept preliminary agreement */
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mCheckBoxRemember.setChecked(true);
            }
        });

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                mCheckBoxRemember.setChecked(false);
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* Register user using the credentials given */
    private void registerUser(final String textLastname, final String textEmail, final String textPhone, final String textPassword) {
        progressBar.setVisibility(View.VISIBLE);
        /* Create User Profile */
        authProfile.createUserWithEmailAndPassword(textEmail, textPassword)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            /* Sign in success, dismiss dialog and start register activity */
                            FirebaseUser user = authProfile.getCurrentUser();
                            /* Get user email and uid from auth */
                            uid = user.getUid();
                            String textEmail = user.getEmail();
                            score = 0;
                            firstscore = 0;
                            /* When user is registered store user info oin firebase realtime database */
                            /* using HashMap */
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("uid", uid);
                            hashMap.put("email", textEmail);
                            hashMap.put("lastname", textLastname); //will add later (e.g. edit profile)
                            hashMap.put("phone", textPhone); //will add later (e.g. edit profile)
                            hashMap.put("image", "ยังไม่ได้อัปโหลดรูปโปรไฟล์"); //will add later (e.g. edit profile)
                            hashMap.put("score", score);
                            hashMap.put("depression", "ยังไม่ได้ทำแบบประเมิน");
                            hashMap.put("firstscore", firstscore);
                            hashMap.put("firstdepression", "ยังไม่ได้ทำแบบประเมิน");
                            hashMap.put("BeforeDepression", "ยังไม่ได้ทำแบบประเมิน");
                            hashMap.put("level", "เลเวล1");
                            /* FirebaseDatabase */
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            /* path to store user data named "ผู้ใช้งาน" */
                            DatabaseReference reference = database.getReference("ผู้ใช้งาน");
                            /* put data with in hashmap in database and send Email Verification */
                            reference.child(uid).setValue(hashMap);
                            Toast.makeText(RegisterActivity.this, "สมัครสมาชิกเรียบร้อยแล้ว",
                                    Toast.LENGTH_SHORT).show();
                            /* FirebaseAuth user signOut */
                            Intent intent = new Intent(RegisterActivity.this, FirstUserProfileActivity.class);
                            /* animation next activity Fade */
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);
                            ButtonReg.setEnabled(false);
                            IpAddressTimeHis();
                            finish();
                        } else {

                            try {
                                throw task.getException();
                            } catch (FirebaseAuthWeakPasswordException e) {
                                password.setError("รหัสผ่านของคุณอ่อนแอเกินไป");
                                password.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                email.setError("อีเมลของคุณไม่ถูกต้องหรือมีการใช้งานอยู่แล้ว");
                                email.requestFocus();
                            } catch (FirebaseAuthUserCollisionException e) {
                                email.setError("ผู้ใช้ได้ลงทะเบียนกับอีเมลนี้แล้ว กรุณาลงทะเบียนด้วยอีเมลอื่น");
                                email.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                            ButtonReg.setEnabled(true);
                            //Hide ProgressBar whether user creation is successful or failed
                            progressBar.setVisibility(View.GONE);
                        }
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
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }
}

