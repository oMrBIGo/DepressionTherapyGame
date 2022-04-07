package com.depressiontherapygame.Users.Update;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.MainActivity;
import com.depressiontherapygame.Users.LoginRegister.UserProfileActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;
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

import java.io.File;

public class DeleteProfileActivity extends AppCompatActivity {

    String myuid;
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private TextInputEditText editTextUserPassword;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar;
    private String userPassword;
    private Button buttonReAuthenticate, buttonDeleteUser;
    private static final String TAG = "DeleteProfileActivity";
    private CardView buttonDeleteCardView;
    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);

        init_screen();

        myuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar = findViewById(R.id.progressBar);
        editTextUserPassword = findViewById(R.id.editText_Delete_User);
        textViewAuthenticated = findViewById(R.id.textView_Delete_User_Authenticated);
        buttonDeleteUser = findViewById(R.id.ButtonDeleteUser);
        buttonReAuthenticate = findViewById(R.id.Button_delete_user_authenticate);
        buttonDeleteCardView = findViewById(R.id.ButtonDeleteCardView);

        //Disable Delete USer Button until USer is authenticated
        buttonDeleteUser.setEnabled(false);

        authProfile = FirebaseAuth.getInstance();
        firebaseUser = authProfile.getCurrentUser();

        ImageView imageView = findViewById(R.id.buttonBack);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DeleteProfileActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                imageView.setEnabled(false);
            }
        });

        if (firebaseUser.equals("")) {
            Toast.makeText(DeleteProfileActivity.this, "มีอะไรบางอย่างผิดปกติ" +
                    "ไม่พบบัญชีผู้ใช้ในขณะนี้", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
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
                userPassword = editTextUserPassword.getText().toString();

                if (TextUtils.isEmpty(userPassword)) {
                    Toast.makeText(DeleteProfileActivity.this, "ต้องใช้รหัสผ่าน", Toast.LENGTH_SHORT).show();
                    editTextUserPassword.setError("กรุณาใส่รหัสผ่านปัจจุบันของคุณเพื่อรับรองความถูกต้อง");
                    editTextUserPassword.requestFocus();
                } else {

                    progressBar.setVisibility(View.VISIBLE);

                    //ReAuthenticate User new
                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPassword);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                //Disable editText for Password.
                                editTextUserPassword.setEnabled(false);

                                //Enable Delete User Button. Disable Authenticate Button
                                buttonReAuthenticate.setEnabled(false);
                                buttonDeleteUser.setEnabled(true);

                                //Set TextView to show User is authenticated / verified
                                textViewAuthenticated.setText("คุณได้รับการตรวจสอบแล้ว" +
                                        "คุณสามารถยกเลิกบัญชีผู้ใช้ของคุณได้ทันที โปรดระวัง คุณจะไม่สามารถกู้คืนบัญชีของคุณได้อีก");
                                Toast.makeText(DeleteProfileActivity.this, "รหัสผ่านได้รับการยืนยันแล้ว"
                                        , Toast.LENGTH_SHORT).show();

                                //Update color of Change password Button
                                buttonDeleteCardView.setBackgroundTintList(ContextCompat.getColorStateList(
                                        DeleteProfileActivity.this, R.color.dark_green));

                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(DeleteProfileActivity.this, "กรุณากรอกรหัสผ่านให้ถูกต้อง", Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    //Users coming to UserProfileActivity after successful registration
    private void showAlertDialog() {
        //Setup the Alert Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("ยกเลิกบัญชีผู้ใช้");
        builder.setMessage("คุณต้องการยกเลิกโปรไฟล์และข้อมูลที่เกี่ยวข้องหรือไม่? คุณจะไม่สามารถกู้คืนข้อมูลกลับมาได้อีก!");

        //Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // deleteUSer(firebaseUser);
                deleteUSerData();
            }
        });
        //Return to User Profile Activity if USer presses Cancel Button
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Create the AlertDialog
        final AlertDialog alertDialog = builder.create();

        //Change the button color of Continue
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });

        //Show the AlertDialog
        alertDialog.show();
    }

    private void deleteUSer(FirebaseUser firebaseUser) {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    authProfile.signOut();
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUSerData() {
       //Delete Data from Realtime Database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "onSuccess: User Data Deleted");
                // deleteUser Auth
                deleteUSer(firebaseUser);
                deleteCache(getApplicationContext());
                Toast.makeText(DeleteProfileActivity.this, "บัญชีผู้ใช้ถูกยกเลิกเรียบร้อยแล้ว!",
                        Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(DeleteProfileActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: " +e.getMessage());
                Toast.makeText(DeleteProfileActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) { e.printStackTrace();}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
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

    int backPressed = 0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {

            Intent intent = new Intent(DeleteProfileActivity.this, SettingActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}