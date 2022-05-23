package com.depressiontherapygame.Users.LoginRegister;

/**
 * Created on 22-10-21.
 * Update on 30-01-22.
 * Developer by Nathit Panrod
 */

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.HomeActivity;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.Setting.SettingActivity;
import com.depressiontherapygame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    /* View */
    private TextView textViewWelcome;
    private TextView textViewLastname;
    private TextView textViewEmail;
    private TextView textViewAge;
    private TextView textViewPhone;
    private TextView textViewWelcomeH, textViewLvH;
    private ProgressBar progressBar, progressBar1;
    private ImageView imageView, icProfile;
    private FirebaseAuth authProfile;
    private Button Update;
    SharedPref sharedPref;
    Button buttonEditProfile, buttonUploadProfile;
    private ImageButton buttonBack;
    Dialog dialog;
    TextView valueText;
    private EditText editTextUpdateName, editTextUpdatePhone, editTExtUpdateAge;
    private String textLastname, textAge, textPhone;

    private CircleImageView Upload;
    private Uri imageUri = null;
    private CardView cardView;

    private static final String TAG = "PROFILE_EDIT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* NightMode [SharedPref.java] */
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        /* dialog show */
        dialog = new Dialog(this);

        /* init Screen */
        init_screen();
        /* init view */
        textViewWelcome = findViewById(R.id.textView_show_welcome);
        textViewLastname = findViewById(R.id.textView_show_lastname);
        textViewEmail = findViewById(R.id.textView_show_email);
        textViewAge = findViewById(R.id.textView_show_age);
        textViewPhone = findViewById(R.id.textView_show_phone);
        progressBar = findViewById(R.id.progressBar);
        TextView textProfile = findViewById(R.id.Upload1_Profile);
        textViewWelcomeH = findViewById(R.id.lastname_home); // show header
        textViewLvH = findViewById(R.id.lv_home); // show header
        icProfile = findViewById(R.id.icon_profile); // show header
        icProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(UserProfileActivity.this, "คุณอยู่หน้าโปรไฟล์แล้ว", Toast.LENGTH_SHORT).show();
            }
        });

        /* Button Click: next to [UploadActivity.java] */
        textProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUploadProfileDialog();
            }
        });

        /* Button Click: next to [UploadActivity.java] */
        imageView = findViewById(R.id.imageView_Profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUploadProfileDialog();
            }
        });

        final Animation animation = AnimationUtils.loadAnimation(UserProfileActivity.this, R.anim.button_bounce_home);

        /* Button Click: next to [SettingActivity.java] */
        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, SettingActivity.class);
                startActivity(intent);
                finish();
                buttonBack.startAnimation(animation);
                buttonBack.setEnabled(false);
            }
        });

        /* Button Click: next to [HomeActivity.java] */
        buttonUploadProfile = findViewById(R.id.button_home);
        buttonUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
                buttonUploadProfile.setEnabled(false);
            }
        });

        /* Button Click: next to [UpdateProfileActivity.java] */
        buttonEditProfile = findViewById(R.id.button_editProfile);
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUpdateProfileDialog();
            }
        });

        /* Initialize FirebaseAuth */
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        /* show user header bar */
        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    /* show User Profile */
    private void showUserProfile(final FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        /* Extracting USer Reference from Database for "Register Users" */
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUserShow modelUserShow = snapshot.getValue(ModelUserShow.class);
                if (modelUserShow != null) {
                    String lastname = "" + snapshot.child("lastname").getValue();
                    String email = "" + snapshot.child("email").getValue();
                    String level = "" + snapshot.child("level").getValue();
                    String age = "" + snapshot.child("age").getValue();
                    String phone = "" + snapshot.child("phone").getValue();
                    String image = "" + snapshot.child("image").getValue();

                    textViewWelcome.setText(lastname);
                    textViewLastname.setText(lastname);
                    textViewEmail.setText(email);
                    textViewAge.setText(age+ " ปี");
                    textViewPhone.setText(phone);
                    textViewWelcomeH.setText(lastname);
                    textViewLvH.setText("ปัจจุบัน "+level);

                    /* set image, using Picasso */
                    Picasso.get().load(image).into(imageView);
                    Picasso.get().load(image).into(icProfile);

                    if (level.toString().equals("เลเวล1")) {
                        ImageView levelUp = (ImageView) findViewById(R.id.level);
                        levelUp.setVisibility(View.GONE);
                        }

                } else {
                    Toast.makeText(UserProfileActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                            Toast.LENGTH_LONG).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /* open Dialog Show UploadProfile */
    private void openUploadProfileDialog() {
        /* animation Button */
        final Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_bounce);
        /* set dialog [upload_layout_dialog.xml] */
        dialog.setContentView(R.layout.upload_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        /* firebase */
        authProfile = FirebaseAuth.getInstance();
        loadUserInfo();

        //init view
        cardView = dialog.findViewById(R.id.cardViewBtn);
        progressBar1 = dialog.findViewById(R.id.progressBar_splash);
        progressBar1.setVisibility(View.GONE);
        valueText = (TextView) dialog.findViewById(R.id.valueText);
        valueText.setVisibility(View.GONE);

        //Button Click: upload Profile
        Upload = dialog.findViewById(R.id.imageView_Profile);
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowImageAttachMenu();
            }
        });

        //Button Click update profile
        Update = dialog.findViewById(R.id.update1);
        Update.setEnabled(false);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar1.setVisibility(View.VISIBLE);
                valueText.setVisibility(View.VISIBLE);
                validateData();
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

    private void loadUserInfo() {
        Log.d(TAG, "loadUserInfo: กำลังโหลดข้อมูลผู้ใช้ของผู้ใช้ " + authProfile.getUid());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        reference.child(authProfile.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //get all info user here from snapshot
                        String image = "" + snapshot.child("image").getValue();

                        //set image, using Picasso
                        Picasso.get()
                                .load(image)
                                .placeholder(R.drawable.profile_image)
                                .into(Upload);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void validateData() {
        if (imageUri == null) {
            //need to update without image
            updateProfile("");
        } else {
            //need to update with image
            uploadImage();
        }

    }

    private void uploadImage() {
        Log.d(TAG, "UploadImage: กำลังอัปโหลด...");
        Toast.makeText(UserProfileActivity.this, "กำลังอัพเดตรูปโปรไฟล์ กรุณารอสักครู่...", Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        //image path, use uid to replace previous
        String filePathAndName = "image/" + authProfile.getUid();

        //storage reference
        StorageReference reference = FirebaseStorage.getInstance().getReference(filePathAndName);
        reference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.d(TAG, "onSuccess: อัพโหลดรูปโปรไฟล์");
                        Log.d(TAG, "onSuccess: รับ url ของภาพที่อัพโหลด");
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uploadedImageUrl = "" + uriTask.getResult();
                        Log.d(TAG, "onSucces: URL รูปภาพที่อัปโหลด:" + uploadedImageUrl);
                        progressBar1.setProgress(0);
                        valueText.setText("อัปโหลดรูปโปรไฟล์เสร็จสิ้น 100 %");

                        updateProfile(uploadedImageUrl);

                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double progress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressBar1.setProgress((int) progress);
                Update.setEnabled(false);
                cardView.setBackgroundTintList(ContextCompat.getColorStateList(
                        UserProfileActivity.this, R.color.teal_200));
                Update.setText("กำลังอัปโหลด...");
                valueText.setText("กำลังอัปโหลดรูปโปรไฟล์ กรุณารอสักครู่... " + progress + " %");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ไม่สามารถอัปโหลดภาพได้เนื่องจาก " + e.getMessage());
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(UserProfileActivity.this, "ไม่สามารถอัปโหลดภาพได้", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateProfile(String imageUri) {
        Log.d(TAG, "updateProfile: กำลังอัปเดตโปรไฟล์ผู้ใช้");

        //setup data to update in db
        HashMap<String, Object> hashMap = new HashMap<>();
        if (imageUri != null) {
            hashMap.put("image", "" + imageUri);
        }

        //update data to db
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        databaseReference.child(authProfile.getUid())
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG, "onSuccess: อัปเดตโปรไฟล์แล้ว...");
                        Toast.makeText(UserProfileActivity.this, "อัพเดตรูปโปรไฟล์เรียบร้อย", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        restartApp();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: ไม่สามารถอัปเดตรูปโปรไฟล์ลงฐานข้อมูลได้ เนื่องจาก" + e.getMessage());
                        Toast.makeText(UserProfileActivity.this, "ไม่สามารถอัปเดตรูปโปรไฟล์ลงฐานข้อมูลได้", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void ShowImageAttachMenu() {
        //init/setup popup Menu
        PopupMenu popupMenu = new PopupMenu(this, Upload);
        popupMenu.getMenu().add(Menu.NONE, 0, 0, "กล้องถ่ายรูป");
        popupMenu.getMenu().add(Menu.NONE, 1, 1, "แกลอรี่");
        popupMenu.show();


        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //get id of item clicked
                int which = item.getItemId();
                if (which == 0) {
                    //camera clicked
                    pickImageCamera();

                } else if (which == 1) {
                    //gallery clicked
                    pickImageGallery();
                }
                return false;
            }
        });
    }

    private void pickImageCamera() {
        //intent to pick image from camera
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Pick"); //image title
        values.put(MediaStore.Images.Media.DESCRIPTION, "Sample Image Description");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraActivityResultLauncher.launch(intent);

    }

    private void pickImageGallery() {
        //intent to pick image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryActivityResultLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> cameraActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to result of camera intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: เลือกจากกล้อง " + imageUri);
                        Intent data = result.getData(); //no need here as in camera case we already have image in ImageUri variable
                        Upload.setImageURI(imageUri);
                        Update.setEnabled(true);
                        cardView.setBackgroundTintList(ContextCompat.getColorStateList(
                                UserProfileActivity.this, R.color.dark_green));
                        Update.setText("ตั้งเป็นรูปโปรไฟล์");

                    } else {
                        Toast.makeText(UserProfileActivity.this, "ยกเลิก", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //used to result of gallery intent
                    //get uri of image
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, "onActivityResult: " + imageUri);
                        Intent data = result.getData();
                        imageUri = data.getData();
                        Log.d(TAG, "onActivityResult: เลือกจากแกลเลอรี่" + imageUri);
                        Upload.setImageURI(imageUri);
                        Update.setEnabled(true);
                        cardView.setBackgroundTintList(ContextCompat.getColorStateList(
                                UserProfileActivity.this, R.color.dark_green));
                    } else {
                        Toast.makeText(UserProfileActivity.this, "ยกเลิก", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    /* open Dialog Show Update Profile */
    private void openUpdateProfileDialog() {
        /* set dialog [edit_layout_dialog.xml] */
        dialog.setContentView(R.layout.edit_layout_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        authProfile = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = authProfile.getCurrentUser();

        TextInputLayout text_input_lastname = dialog.findViewById(R.id.text_input_lastname);
        TextInputLayout text_input_phone = dialog.findViewById(R.id.text_input_phone);

        text_input_lastname.setHintEnabled(false);
        text_input_phone.setHintEnabled(false);


        editTextUpdateName = dialog.findViewById(R.id.lastname);
        editTextUpdatePhone = dialog.findViewById(R.id.phone);
        String userID = firebaseUser.getUid();
        /* Extracting USer Reference from Database for "Register Users" */
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUserShow modelUserShow = snapshot.getValue(ModelUserShow.class);
                if (modelUserShow != null) {
                    String lastname = "" + snapshot.child("lastname").getValue();
                    String phone = "" + snapshot.child("phone").getValue();
                    String age = "" + snapshot.child("age").getValue();

                    editTextUpdateName.setText(lastname);
                    editTextUpdatePhone.setText(phone);

                } else {
                    Toast.makeText(UserProfileActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                            Toast.LENGTH_LONG).show();
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });


        Button buttonUpdateProfile = dialog.findViewById(R.id.ButtonUpdateProfile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });

        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);
        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        //Show Profile Data
        showUserProfile(firebaseUser);

        /* dialog show */
        dialog.show();
    }

    private void updateProfile(final FirebaseUser firebaseUser) {
        //Obtain the data entered by user
        textLastname = editTextUpdateName.getText().toString().trim();
        textPhone = editTextUpdatePhone.getText().toString().trim();
        String checkPhone = "^" + ".{10}" + "$";

        if (TextUtils.isEmpty(textLastname)) {
            Toast.makeText(UserProfileActivity.this, "กรุณากรอกชื่อ-นามสกุล", Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("กรุณาระบุชื่อเต็ม");
            editTextUpdateName.requestFocus();
        } else if (TextUtils.isEmpty(textPhone)) {
            Toast.makeText(UserProfileActivity.this, "กรุณาใส่หมายเลขโทรศัพท์มือถือของคุณอีกครั้ง", Toast.LENGTH_LONG).show();
            editTextUpdatePhone.setError("กรุณากรอกเบอร์โทรศัพท์ให้ถูกต้อง");
            editTextUpdatePhone.requestFocus();
        } else if (!textPhone.matches(checkPhone)) {
            Toast.makeText(UserProfileActivity.this, "กรุณาใส่หมายเลขโทรศัพท์มือถือของคุณอีกครั้ง", Toast.LENGTH_LONG).show();
            editTextUpdatePhone.setError("เบอร์โทรศัพท์ควรมี 10 หลัก");
            editTextUpdatePhone.requestFocus();

        } else {

            HashMap<String, Object> result = new HashMap<>();
            result.put("lastname", textLastname);
            result.put("phone", textPhone);
            //Enter User Data ino the Firebase Realtime Database. Set up dependencies
            //Extract USer reference from Database for "ผู้ใช้งาน"
            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);
            referenceProfile.child(userID).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            //Setting new display Name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(textLastname).build();
                            firebaseUser.updateProfile(profileUpdates);

                            Toast.makeText(UserProfileActivity.this, "อัพเดตข้อมูลโปรไฟล์เรียบร้อย", Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            restartApp();
                            progressBar.setVisibility(View.GONE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UserProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        }
    }

    /* restartApp on Click Switch Change */
    public void restartApp() {
        Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
        startActivity(i);
        finish();
    }

    /* init_screen */
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
            Intent intent = new Intent(UserProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}