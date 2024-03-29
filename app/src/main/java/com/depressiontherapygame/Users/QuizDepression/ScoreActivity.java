package com.depressiontherapygame.Users.QuizDepression;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.QuizDepression.Adapter.RecycAdapter;
import com.depressiontherapygame.Users.Consult.DashboardActivity;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.QuizDepression.Model.RecycModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScoreActivity extends AppCompatActivity implements RecycAdapter.OnReCycListener {

    private ImageView imageView;
    private TextView textViewWelcome;
    private TextView textViewLv;
    private TextView firstdep;
    private TextView firstsco;
    private Button ButtonPrintf;
    private ProgressBar progressBar;
    private String lastname, email, firstDepression;
    private int firstscore;
    Dialog dialog;
    private List<RecycModel> mList = new ArrayList<>();
    private RecycAdapter mAdapter;

    private String stringFilePath = Environment.getExternalStorageDirectory().getPath() + "/Download/Pdf.pdf";
    private File file = new File(stringFilePath);

    SharedPref sharedPref;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_score);

        init_screen();

        /* dialog show */
        dialog = new Dialog(this);

        openDepForm();

        FirebaseAuth authProfile = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = authProfile.getCurrentUser();

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        mAdapter = new RecycAdapter(mList, this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        prepareMovieData();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        /* animation Button */
        final Animation animation = AnimationUtils.loadAnimation(ScoreActivity.this, R.anim.button_bounce_home);

        Button ButtonForm = (Button) findViewById(R.id.ButtonForm);
        ButtonForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://forms.gle/YootYZTYnt96xQ3z5");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });


        TextView score = findViewById(R.id.score);
        TextView result = findViewById(R.id.form_score);
        TextView resultfirst = findViewById(R.id.beforeform_score);
        firstdep = findViewById(R.id.firstform_score);
        firstsco = findViewById(R.id.firstscore);
        CardView cardViewFirstDep = findViewById(R.id.cardViewFirstDep);
        String score_str = getIntent().getStringExtra("SCORE");
        String message = getIntent().getStringExtra("RESULT");
        String messageFirst = getIntent().getStringExtra("RESULTFirst");

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        if (messageFirst.toString().equals("ระดับความซึมเศร้าของคุณลดลง")) {
            resultfirst.setTextColor(Color.GREEN);
            cardViewFirstDep.setCardBackgroundColor(Color.parseColor("#006400"));
        } else if (messageFirst.toString().equals("ระดับความซึมเศร้าของคุณเท่าเดิม")) {
            resultfirst.setTextColor(Color.CYAN);
            cardViewFirstDep.setCardBackgroundColor(Color.parseColor("#1C4B75"));
        } else if (messageFirst.toString().equals("ระดับความซึมเศร้าของคุณแย่ลง")) {
            resultfirst.setTextColor(Color.parseColor("#FF786F"));
            cardViewFirstDep.setCardBackgroundColor(Color.parseColor("#8E0A00"));
        }

        score.setText(score_str);
        result.setText(message);
        resultfirst.setText(messageFirst);

        ButtonPrintf = findViewById(R.id.ButtonPrintf);
        ButtonPrintf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userID = firebaseUser.getUid();
                //Extracting USer Reference from Database for "Register Users"
                DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
                referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ModelUserShow modelUserShow = snapshot.getValue(ModelUserShow.class);
                        if (modelUserShow != null) {
                            firstDepression = modelUserShow.getFirstdepression();
                            String lastname = "" + snapshot.child("lastname").getValue();
                            String level = "" + snapshot.child("level").getValue();
                            String email = "" + snapshot.child("email").getValue();
                            String firstDepression = "" + snapshot.child("firstdepression").getValue();
                            String firstscore = "" + snapshot.child("firstscore").getValue();

                            textViewWelcome.setText(lastname);
                            textViewLv.setText("ปัจจุบัน " + level);
                            firstdep.setText(firstDepression);
                            firstsco.setText(firstscore + " คะแนน");

                            //PDF

                            PdfDocument pdfDocument = new PdfDocument();
                            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(450, 600, 1).create();
                            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

                            Paint paint = new Paint();

                            String stringPDF = "==========[แอปพลิเคชันเกมบำบัดภาวะซึมเศร้าในวัยรุ่น]==========";
                            String depressionN = "แบบประเมินภาวะซึมเศร้า (9Q)";
                            String header = "ผลการทำแบบประเมินภาวะซึมเศร้าในวัยรุ่น";
                            String FirstMessage = "ผลประเมินระดับภาวะซึมเศร้าครั้งแรกเท่ากับ";
                            String NewMessage = "ผลประเมินระดับภาวะซึมเศร้าครั้งล่าสุดเท่ากับ";
                            String timeStamp = String.valueOf(System.currentTimeMillis()); //เวลาทำแบบประเมิน

                            Calendar calendar = Calendar.getInstance(Locale.getDefault());
                            calendar.setTimeInMillis(Long.parseLong(timeStamp));
                            String pTime = DateFormat.format("dd/MM/yyyy HH:mm:ss", calendar).toString();

                            int x = 10, y = 25, y1 = 75, y2 = 125, y3 = 150, y4 = 200, y5 = 225, y6 = 250, y7 = 275,
                                    y8 = 300, y9 = 325, y10 = 350, y11 = 375,y12 = 400, y13 = 425, y14 = 450, y15 = 475,
                                    y16 = 500, y17 = 550,y18 = 575;

                            page.getCanvas().drawText(stringPDF, x, y, paint);

                            page.getCanvas().drawText("ชื่อแบบประเมิน : "+depressionN, x, y1, paint);

                            page.getCanvas().drawText("ชื่อผู้ใช้งาน : "+lastname, x, y2, paint);

                            page.getCanvas().drawText("อีเมล : "+email, x, y3, paint);

                            page.getCanvas().drawText(header, x, y5, paint);

                            page.getCanvas().drawText(FirstMessage, x, y7, paint);

                            page.getCanvas().drawText("คะแนน : "+firstscore , x, y8, paint);

                            page.getCanvas().drawText("ภาวะซึมเศร้า : "+firstDepression, x, y9, paint);

                            page.getCanvas().drawText(NewMessage, x, y11, paint);

                            page.getCanvas().drawText("คะแนน : "+score_str , x, y12, paint);

                            page.getCanvas().drawText("ภาวะซึมเศร้า : "+message, x, y13, paint);


                            page.getCanvas().drawText("ระดับความซึมเศร้าของท่านหลังจากการเล่นเกม", x, y15, paint);

                            page.getCanvas().drawText(messageFirst, x, y16, paint);

                            page.getCanvas().drawText("เวลาการทำแบบประเมิน : " + pTime, x, y17, paint);

                            page.getCanvas().drawText("==========================================================", x, y18, paint);

                            pdfDocument.finishPage(page);

                            try {
                                pdfDocument.writeTo(new FileOutputStream(file));

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            pdfDocument.close();




                        }



                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(ScoreActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                                Toast.LENGTH_LONG).show();
                    }
                });

            }
        });

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLv = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, QuizMainActivity.class);
                startActivity(intent);
                buttonBack.startAnimation(animation);
                finish();
                buttonBack.setEnabled(false);
            }
        });

        LinearLayout BtnClickDep = (LinearLayout) findViewById(R.id.BtnClickDep);
        BtnClickDep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, QuizMainActivity.class);
                startActivity(intent);
                imageView.startAnimation(animation);
                finish();
                BtnClickDep.setEnabled(false);
            }
        });

        LinearLayout BtnClick1323 = (LinearLayout) findViewById(R.id.BtnClick1323);
        BtnClick1323.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:1323"));
                startActivity(callIntent);
                imageView.startAnimation(animation);
            }
        });

        LinearLayout BtnClickCon = (LinearLayout) findViewById(R.id.BtnClickCon);
        BtnClickCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScoreActivity.this, DashboardActivity.class);
                startActivity(intent);
                imageView.startAnimation(animation);
                finish();
                BtnClickCon.setEnabled(false);
            }
        });

        if (firebaseUser == null) {
            Toast.makeText(ScoreActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }


    private void prepareMovieData() {
        RecycModel reCyc = new RecycModel(R.drawable.depq01);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq02);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq03);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq04);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq05);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq06);
        mList.add(reCyc);
        reCyc = new RecycModel(R.drawable.depq07);
        mList.add(reCyc);
        mAdapter.notifyDataSetChanged();
    }

    private void showUserProfile(final FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();

        //Extracting USer Reference from Database for "Register Users"
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ModelUserShow modelUserShow = snapshot.getValue(ModelUserShow.class);
                if (modelUserShow != null) {
                    firstDepression = modelUserShow.getFirstdepression();
                    String lastname = "" + snapshot.child("lastname").getValue();
                    String level = "" + snapshot.child("level").getValue();
                    String firstDepression = "" + snapshot.child("firstdepression").getValue();
                    String firstscore = "" + snapshot.child("firstscore").getValue();

                    textViewWelcome.setText(lastname);
                    textViewLv.setText("ปัจจุบัน " + level);
                    firstdep.setText(firstDepression);
                    firstsco.setText(firstscore + " คะแนน");

                    String image = "" + snapshot.child("image").getValue();

                    //set image, using Picasso
                    Picasso.get().load(image).resize(130, 130).into(imageView);

                    if (level.toString().equals("เลเวล1")) {
                        ImageView levelUp = (ImageView) findViewById(R.id.level);
                        levelUp.setVisibility(View.GONE);
                    }

                }
                progressBar.setVisibility(View.GONE);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScoreActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onReCycClick(int position) {
        mList.get(position);

        if (position == 0) {
            openDepDialog01();
        } else if (position == 1) {
            openDepDialog02();
        } else if (position == 2) {
            openDepDialog03();
        } else if (position == 3) {
            openDepDialog04();
        } else if (position == 4) {
            openDepDialog05();
        } else if (position == 5) {
            openDepDialog06();
        } else if (position == 6) {
            openDepDialog07();
        }

    }

    /* open Dialog Show Dep01 */
    private void openDepForm() {
        /* set dialog [depq01_dialog.xml] */
        dialog.setContentView(R.layout.dep_form_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button confirm = dialog.findViewById(R.id.confirm);

        /* button click reject preliminary agreement */
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://forms.gle/YootYZTYnt96xQ3z5");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        /* dialog show */
        dialog.show();
    }


    /* open Dialog Show Dep01 */
    private void openDepDialog01() {
        /* set dialog [depq01_dialog.xml] */
        dialog.setContentView(R.layout.depq01_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep02 */
    private void openDepDialog02() {
        /* set dialog [depq02_dialog.xml] */
        dialog.setContentView(R.layout.depq02_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep03 */
    private void openDepDialog03() {
        /* set dialog [depq03_dialog.xml] */
        dialog.setContentView(R.layout.depq03_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep04 */
    private void openDepDialog04() {
        /* set dialog [depq04_dialog.xml] */
        dialog.setContentView(R.layout.depq04_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep05 */
    private void openDepDialog05() {
        /* set dialog [depq05_dialog.xml] */
        dialog.setContentView(R.layout.depq05_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep06 */
    private void openDepDialog06() {
        /* set dialog [depq06_dialog.xml] */
        dialog.setContentView(R.layout.depq06_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
    }

    /* open Dialog Show Dep07 */
    private void openDepDialog07() {
        /* set dialog [depq07_dialog.xml] */
        dialog.setContentView(R.layout.depq07_dialog);
        /* sey dialog background Transparent */
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        /* init view button in dialog */
        Button cancelBtn = dialog.findViewById(R.id.cancel);

        /* button click reject preliminary agreement */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /* dialog show */
        dialog.show();
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

            Intent intent = new Intent(ScoreActivity.this, QuizMainActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }
}