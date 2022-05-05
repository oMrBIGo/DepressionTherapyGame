package com.depressiontherapygame.Users.QuizDepression;

import static com.depressiontherapygame.Users.QuizDepression.QuizMainActivity.catList;
import static com.depressiontherapygame.Users.QuizDepression.QuizMainActivity.selected_cat_index;
import static com.depressiontherapygame.Users.QuizDepression.SetsActivity.setsIDs;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.ArrayMap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.depressiontherapygame.Users.QuizDepression.Quest.QuizQuest;
import com.depressiontherapygame.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView textViewWelcome, textViewLv;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private List<QuizQuest> quizQuestsList;
    private TextView quizQuest, qCount;
    private Button Option1, Option2, Option3, Option4;
    private int firstscore;
    private int score;
    String depression, BeforeDepression;
    private int QuizNumber;
    private int setNo;
    private FirebaseFirestore firestore;


    int total = 0;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        init_screen();

        quizQuest = findViewById(R.id.QuizQuest);
        qCount = findViewById(R.id.QuizNumber);
        Option1 = findViewById(R.id.option1);
        Option2 = findViewById(R.id.option2);
        Option3 = findViewById(R.id.option3);
        Option4 = findViewById(R.id.option4);

        Option1.setOnClickListener(this);
        Option2.setOnClickListener(this);
        Option3.setOnClickListener(this);
        Option4.setOnClickListener(this);

        quizQuestsList = new ArrayList<>();

        authProfile = FirebaseAuth.getInstance();
        final FirebaseUser firebaseUser = authProfile.getCurrentUser();

        setNo = getIntent().getIntExtra("SETNO", 1);
        firestore = FirebaseFirestore.getInstance();

        getQuizQuestList();

        score = 0;
        depression = "";
        BeforeDepression = "";

        textViewWelcome = findViewById(R.id.lastname_home);
        textViewLv = findViewById(R.id.lv_home);
        progressBar = findViewById(R.id.progressBar);
        imageView = findViewById(R.id.icon_profile);

        final Animation animation = AnimationUtils.loadAnimation(QuestionActivity.this, R.anim.button_bounce_home);

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionActivity.this, SetsActivity.class);
                startActivity(intent);
                finish();
                buttonBack.setEnabled(false);
                buttonBack.startAnimation(animation);

            }
        });


        if (firebaseUser == null) {
            Toast.makeText(QuestionActivity.this, "มีอะไรบางอย่างผิดปกติ! ไม่มีรายละเอียดของผู้ใช้ในขณะนี้",
                    Toast.LENGTH_LONG).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
    }

    private void getQuizQuestList() {

        quizQuestsList.clear();

        firestore.collection("QUIZ").document(catList.get(selected_cat_index).getId())
                .collection(setsIDs.get(setNo)).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        Map<String, QueryDocumentSnapshot> docList = new ArrayMap<>();

                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            docList.put(doc.getId(), doc);
                        }

                        QueryDocumentSnapshot quesListDoc = docList.get("QUESTIONS_LIST");

                        String count = quesListDoc.getString("COUNT");


                        for (int i = 0; i < Integer.valueOf(count); i++) {
                            String quesID = quesListDoc.getString("Q" + String.valueOf(i + 1) + "_ID");

                            QueryDocumentSnapshot quesDoc = docList.get(quesID);

                            quizQuestsList.add(new QuizQuest(
                                    quesDoc.getString("QUESTION"),
                                    quesDoc.getString("A"),
                                    quesDoc.getString("B"),
                                    quesDoc.getString("C"),
                                    quesDoc.getString("D"),

                                    Integer.valueOf(quesDoc.getString("ANSWER1")),
                                    Integer.valueOf(quesDoc.getString("ANSWER2")),
                                    Integer.valueOf(quesDoc.getString("ANSWER3")),
                                    Integer.valueOf(quesDoc.getString("ANSWER4")),

                                    Integer.valueOf(quesDoc.getString("CURRENT1ANS")),
                                    Integer.valueOf(quesDoc.getString("CURRENT2ANS")),
                                    Integer.valueOf(quesDoc.getString("CURRENT3ANS")),
                                    Integer.valueOf(quesDoc.getString("CURRENT4ANS"))
                            ));

                        }
                        setQuestion();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(QuestionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setQuestion() {

        quizQuest.setText(quizQuestsList.get(0).getQuestion());
        Option1.setText(quizQuestsList.get(0).getOptionA());
        Option2.setText(quizQuestsList.get(0).getOptionB());
        Option3.setText(quizQuestsList.get(0).getOptionC());
        Option4.setText(quizQuestsList.get(0).getOptionD());

        qCount.setText("คำถามข้อที่: " + String.valueOf(1) + "/" + String.valueOf(quizQuestsList.size()));

        QuizNumber = 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {

        int selectedOption = 0;

        switch (v.getId()) {
            case R.id.option1:
                selectedOption = 1;
                break;

            case R.id.option2:
                selectedOption = 2;
                break;

            case R.id.option3:
                selectedOption = 3;
                break;

            case R.id.option4:
                selectedOption = 4;
                break;

            default:
        }

        checkAnswer(selectedOption, v);

    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void checkAnswer(int selectedOption, View view) {

        if (selectedOption == quizQuestsList.get(0).getAns1Str()) {
            //Right Answer
            ((Button) view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score = quizQuestsList.get(0).getCorrectAns1();
            Option1.setEnabled(false);
        } else if (selectedOption == quizQuestsList.get(0).getAns2Str()) {
            //Right Answer
            ((Button) view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score = quizQuestsList.get(0).getCorrectAns2();
            Option2.setEnabled(false);
        } else if (selectedOption == quizQuestsList.get(0).getAns3Str()) {
            //Right Answer
            ((Button) view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score = quizQuestsList.get(0).getCorrectAns3();
            Option3.setEnabled(false);
        } else if (selectedOption == quizQuestsList.get(0).getAns4Str()) {
            //Right Answer
            ((Button) view).setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            score = quizQuestsList.get(0).getCorrectAns4();
            Option4.setEnabled(false);
        }

        total = total + score;

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                changeQuestion();
                Option1.setEnabled(true);
                Option2.setEnabled(true);
                Option3.setEnabled(true);
                Option4.setEnabled(true);
            }
        }, 200);

    }

    private void changeQuestion() {

        if (QuizNumber < quizQuestsList.size() - 1) {
            {

                QuizNumber++;
                playAnim(quizQuest, 0, 0);
                playAnim(Option1, 0, 1);
                playAnim(Option2, 0, 2);
                playAnim(Option3, 0, 3);
                playAnim(Option4, 0, 4);

                qCount.setText("คำถามข้อที่: " + String.valueOf(QuizNumber + 1) + "/" + String.valueOf(quizQuestsList.size()));

            }
        } else {
            final FirebaseUser firebaseUser = authProfile.getCurrentUser();
            String depression = "";
            if (total < 7) {
                depression = "ไม่มีอาการของโรคซึมเศร้าหรือมีอาการของโรคซึมเศร้าระดับน้อยมาก";
            } else if (total < 13) {
                depression = "มีอาการของโรคซึมเศร้า ระดับน้อย";
            } else if (total < 18) {
                depression = "มีอาการของโรคซึมเศร้า ระดับปานกลาง";
            } else {
                depression = "มีอาการของโรคซึมเศร้า ระดับรุนแรง";
            }

            if (total < firstscore) {
                BeforeDepression = "ระดับความซึมเศร้าของคุณลดลง";
            } else if (total == firstscore) {
                BeforeDepression = "ระดับความซึมเศร้าของคุณเท่าเดิม";
            } else if (total > firstscore) {
                BeforeDepression = "ระดับความซึมเศร้าของคุณไม่ดีขึ้นเลย";
            } else {
                BeforeDepression = "ไม่สามารถคำนวณได้";
            }

            // Go to total Activity
            HashMap<String, Object> result = new HashMap<>();
            result.put("score", total);
            result.put("depression", depression);
            result.put("BeforeDepression", BeforeDepression);

            FirebaseUser user = authProfile.getCurrentUser();
            String uid = user.getUid();
            String timeStamp = String.valueOf(System.currentTimeMillis());

            //each post will have a child "คอมเม้นต์" tha will contain comments of that post
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน").child(uid).child("ประวัติการทำแบบประเมิน");

            HashMap<String, Object> hashMap = new HashMap<>();
            //put info in hashmap
            hashMap.put("cId", timeStamp);
            hashMap.put("score", total);
            hashMap.put("depression", depression);
            hashMap.put("logintime", timeStamp);
            hashMap.put("uid", uid);

            //put this data in db
            ref.child(timeStamp).setValue(hashMap)
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

            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
            String userID = firebaseUser.getUid();
            String finalDepression = depression;
            String firstDepression = BeforeDepression;
            referenceProfile.child(userID).updateChildren(result)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Intent intent = new Intent(QuestionActivity.this, ScoreActivity.class);
                            intent.putExtra("SCORE", String.valueOf(total) + " คะแนน");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            intent.putExtra("RESULT", finalDepression);
                            intent.putExtra("RESULTFirst", firstDepression);
                            startActivity(intent);
                            finish();
                            //QuizActivity.this.finish();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });


        }
    }


    private void playAnim(final View view, final int value, final int viewNumber) {

        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(150)
                .setStartDelay(25).setInterpolator(new DecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (value == 0) {

                            switch (viewNumber) {

                                case 0:
                                    ((TextView) view).setText(quizQuestsList.get(QuizNumber).getQuestion());
                                    break;
                                case 1:
                                    ((Button) view).setText(quizQuestsList.get(QuizNumber).getOptionA());
                                    break;
                                case 2:
                                    ((Button) view).setText(quizQuestsList.get(QuizNumber).getOptionB());
                                    break;
                                case 3:
                                    ((Button) view).setText(quizQuestsList.get(QuizNumber).getOptionC());
                                    break;
                                case 4:
                                    ((Button) view).setText(quizQuestsList.get(QuizNumber).getOptionD());
                                    break;
                            }

                            if (viewNumber != 0)

                                ((Button) view).setBackgroundTintList(ColorStateList.valueOf(0));

                            playAnim(view, 1, viewNumber);

                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
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
                    String lastname = "" + snapshot.child("lastname").getValue();
                    String level = "" + snapshot.child("level").getValue();
                    firstscore = (int) +snapshot.child("firstscore").getValue(Integer.class);
                    textViewWelcome.setText(lastname);
                    textViewLv.setText("ปัจจุบัน "+level);

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
                Toast.makeText(QuestionActivity.this, "มีอะไรบางอย่างผิดปกติ!",
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

            Intent intent = new Intent(QuestionActivity.this, SetsActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();

        }
    }


}