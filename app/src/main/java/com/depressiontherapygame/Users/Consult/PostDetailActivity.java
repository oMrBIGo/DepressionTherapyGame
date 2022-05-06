package com.depressiontherapygame.Users.Consult;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.depressiontherapygame.Users.Consult.Adapter.AdapterComments;
import com.depressiontherapygame.Users.Consult.Model.ModelComment;
import com.depressiontherapygame.Users.LoginRegister.Model.ModelUserShow;
import com.depressiontherapygame.Users.MainActivity;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.NightMode.SharedPref;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PostDetailActivity extends AppCompatActivity {

    //to get detail of user and post
    String hisUid, myUid, myEmail, myLastName, myDp,
            postId, pLikes, hisDp, hisLastName;

    boolean mProcessComment = false;
    boolean mProcessLike = false;
    //views
    ImageView uPictureIv;
    TextView uLastNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv;
    ImageButton moreBtn;
    Button likeBtn;
    LinearLayout profileLayout;
    RecyclerView recyclerView;

    List<ModelComment> commentList;
    AdapterComments adapterComments;

    //progress bar
    ProgressBar progressBar;

    //add comments views
    EditText commentEt;
    ImageButton sendBtn;
    ImageView cAvatarIv;

    FirebaseAuth firebaseAuth;

    ImageView profileIv;
    TextView lastnameTv, levelTv;

    SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.darkTheme);
        } else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        //get id of post using intent
        Intent intent = getIntent();
        postId = intent.getStringExtra("postId");

        init_screen();
        firebaseAuth = FirebaseAuth.getInstance();
        profileIv = findViewById(R.id.icon_profile);
        lastnameTv = findViewById(R.id.lastname_home);
        levelTv = findViewById(R.id.lv_home);

        //init views
        uPictureIv = findViewById(R.id.uPictureIv);
        uLastNameTv = findViewById(R.id.uLastNameTv);
        pTimeTv = findViewById(R.id.pTimeTv);
        pTitleTv = findViewById(R.id.pTitleTv);
        pDescriptionTv = findViewById(R.id.pDescriptionTv);
        pLikesTv = findViewById(R.id.pLikesTv);
        pCommentsTv = findViewById(R.id.pCommentsTv);
        moreBtn = findViewById(R.id.moreBtn);
        likeBtn = findViewById(R.id.likeBtn);
        profileLayout = findViewById(R.id.profileLayout);
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        commentEt = findViewById(R.id.commentEt);
        sendBtn = findViewById(R.id.sendBtn);
        cAvatarIv = findViewById(R.id.cAvatarIv);

        final Animation animation = AnimationUtils.loadAnimation(PostDetailActivity.this, R.anim.button_bounce_home);

        ImageButton ButtonBack = (ImageButton) findViewById(R.id.buttonBack);
        ButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostDetailActivity.this, DashboardActivity.class));
                finish();
                ButtonBack.setEnabled(false);
                ButtonBack.startAnimation(animation);
            }
        });

        showUserProfile();

        loadPostInfo();

        checkUserStatus();

        loadUserInfo();

        setLikes();

        loadComments();

        //send comment button click
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment();
            }
        });

        //like button click handle
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likePost();
            }
        });

        //more button click handle
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreOptions();
            }
        });

    }

    private void loadComments() {
        //layout(Linear) for recyclerview
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        //set layout to recyclerview
        recyclerView.setLayoutManager(layoutManager);

        //init comments list
        commentList = new ArrayList<>();

        //path of the post, to get it's comments
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("คำปรึกษา").child(postId).child("คอมเม้นต์");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ModelComment modelComment = ds.getValue(ModelComment.class);

                    commentList.add(modelComment);

                    //pass myUid and postId as parameter of constructor of Comment Adapter


                    //setup adapter
                    adapterComments = new AdapterComments(getApplicationContext(), commentList, myUid, postId);
                    //set adapter
                    recyclerView.setAdapter(adapterComments);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void showMoreOptions() {
        //creating popup menu currently having option Delete, we will add more options later
        PopupMenu popupMenu = new PopupMenu(this, moreBtn, Gravity.END);

        if (hisUid.equals(myUid)) {
            //add items in menu
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "แก้ไขปรึกษา");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "ลบคำปรึกษา");
        }

        //item click listener
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == 0) {
                    //Edit is clicked
                    //start AddPostActivity with key "editPost" and the id of the post clicked
                    Intent intent = new Intent(PostDetailActivity.this, AddPostActivity.class);
                    intent.putExtra("key", "editPost");
                    intent.putExtra("editPostId", postId);
                    startActivity(intent);
                    finish();
                }
                else if (id == 1){
                    //delete is clicked
                    beginDelete();
                }

                return false;
            }
        });
        //show menu
        popupMenu.show();
    }

    private void beginDelete() {

        Query fquery = FirebaseDatabase.getInstance().getReference("คำปรึกษา").orderByChild("pId").equalTo(postId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue(); // remove values from firebase where pid matches
                }
                //deleted
                Toast.makeText(PostDetailActivity.this, "ลบคำปรึกษาเรียบร้อยแล้ว", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(PostDetailActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);

            }
        });
    }

    private void setLikes() {
        //when the details of post is loading, also check if current user has liked it or not
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");

        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).hasChild(myUid)){
                    //user has liked this post
                    /*To indicate that the post is liked by this(SignedIn) user
                    Change drawable left icon of like button
                    Change text of like button from "Like" to "Liked"*/
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_liked, 0,0,0);
                    likeBtn.setText("ให้กำลังใจแล้ว");
                }
                else {
                    //user has not liked this post
                    /*To indicate that the post is not liked by this(SignedIn) user
                    Change drawable left icon of like button
                    Change text of like button from "Liked" to "Like"*/
                    likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0,0,0);
                    likeBtn.setText("ให้กำลังใจ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void likePost() {
        //get total number of likes for the post, whose like button clicked
        //if currently signed in user has not liked it before
        //increase value by 1, otherwise decrease value by 1
        mProcessLike = true;
        //get id of the post clicked
        final DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        final DatabaseReference postsRef = FirebaseDatabase.getInstance().getReference().child("คำปรึกษา");
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (mProcessLike){
                    if (dataSnapshot.child(postId).hasChild(myUid)){
                        //already liked, so remove like
                        postsRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)-1));
                        likesRef.child(postId).child(myUid).removeValue();
                        mProcessLike = false;
                    }
                    else {
                        // not liked, like it
                        postsRef.child(postId).child("pLikes").setValue(""+(Integer.parseInt(pLikes)+1));
                        likesRef.child(postId).child(myUid).setValue("Liked"); //set any value
                        mProcessLike = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void postComment() {
        progressBar.setVisibility(View.VISIBLE);

        //get data from comment edit text
        String comment = commentEt.getText().toString().trim();
        //validate
        if (TextUtils.isEmpty(comment)){
            //no value is entered
            Toast.makeText(this, "กรุณาคอมเม้นต์...", Toast.LENGTH_SHORT).show();
            return;
        }

        String timeStamp = String.valueOf(System.currentTimeMillis());
        //each post will have a child "คอมเม้นต์" tha will contain comments of that post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("คำปรึกษา").child(postId).child("คอมเม้นต์");

        HashMap<String, Object> hashMap = new HashMap<>();
        //put info in hashmap
        hashMap.put("cId", timeStamp);
        hashMap.put("comment", comment);
        hashMap.put("timestamp", timeStamp);
        hashMap.put("uid", myUid);
        hashMap.put("uEmail", myEmail);
        hashMap.put("uDp", myDp);
        hashMap.put("uLastName", myLastName+"[ผู้ใช้งาน]");

        //put this data in db
        ref.child(timeStamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //added
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(PostDetailActivity.this, "คอมเม้นต์แล้ว...", Toast.LENGTH_SHORT).show();
                        commentEt.setText("");
                        updateCommentCount();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed, not added
                       progressBar.setVisibility(View.GONE);
                        Toast.makeText(PostDetailActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void updateCommentCount() {
        //whenever user adds comment increase the comment count as we did for like count
        mProcessComment = true;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("คำปรึกษา").child(postId);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mProcessComment) {
                    String comments = ""+snapshot.child("pComments").getValue();
                    int newCommentVal = Integer.parseInt(comments) + 1;
                    ref.child("pComments").setValue(""+newCommentVal);
                    mProcessComment = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadUserInfo() {
        //get user info
        Query myRef = FirebaseDatabase.getInstance().getReference("ผู้ใช้งาน");
        myRef.orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()) {
                    myLastName = ""+ds.child("lastname").getValue();
                    myDp = ""+ds.child("image").getValue();

                    //set data
                    try {
                        //fire image is received then set
                        Picasso.get().load(myDp).placeholder(R.drawable.profile_image).resize(100,130).into(cAvatarIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.man).resize(100,130).into(cAvatarIv);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadPostInfo() {
        //get post using the id of the post
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("คำปรึกษา");
        Query query = ref.orderByChild("pId").equalTo(postId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //keep checking the posts until get the required post
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                    String pTitle = ""+ds.child("pTitle").getValue();
                    String pDescr = ""+ds.child("pDescr").getValue();
                    pLikes = ""+ds.child("pLikes").getValue();
                    String pTimeStamp = ""+ds.child("pTime").getValue();
                    hisDp = ""+ds.child("uDp").getValue();
                    hisUid = ""+ds.child("uid").getValue();
                    String uEmail = ""+ds.child("uEmail").getValue();
                    hisLastName = ""+ds.child("uLastName").getValue();
                    String commentCount = ""+ds.child("pComments").getValue();

                    //convert timestamp to dd/mm/yyyy hh:mm am/pm
                    Calendar calendar = Calendar.getInstance(Locale.getDefault());
                    calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
                    String pTime = DateFormat.format("dd/MM/yyyy HH:mm:ss", calendar).toString();

                    //set data
                    pTitleTv.setText(pTitle);
                    pDescriptionTv.setText(pDescr);
                    pLikesTv.setText(pLikes + " กำลังใจ");
                    pTimeTv.setText("โพสต์เมื่อ " + pTime);
                    pCommentsTv.setText(commentCount +" คอมเม้นต์");

                    uLastNameTv.setText(hisLastName);

                    //set user image in comment part
                    try {
                        Picasso.get().load(hisDp).placeholder(R.drawable.profile_image).resize(100,130).into(uPictureIv);
                    } catch (Exception e) {
                        Picasso.get().load(R.drawable.man).resize(100,130).into(uPictureIv);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


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
                    String level = "" + snapshot.child("level").getValue();
                    String image = ""+snapshot.child("image").getValue();

                    lastnameTv.setText(lastname);
                    levelTv.setText("ปัจจุบัน "+level);

                    //set image, using Picasso
                    Picasso.get().load(image).resize(100,130).into(profileIv);

                    if (level.toString().equals("เลเวล1")) {
                        ImageView levelUp = (ImageView) findViewById(R.id.level);
                        levelUp.setVisibility(View.GONE);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostDetailActivity.this, "มีอะไรบางอย่างผิดปกติ!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void checkUserStatus() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user!= null) {
            //user is signed in
            myEmail = user.getEmail();
            myUid = user.getUid();
        } else {
            //user not signed in, go to main Activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
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
            Intent intent = new Intent(PostDetailActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
        }
    }

}