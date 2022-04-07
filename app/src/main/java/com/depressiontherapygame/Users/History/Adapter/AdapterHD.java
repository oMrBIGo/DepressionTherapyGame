package com.depressiontherapygame.Users.History.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.History.Model.ModelHD;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterHD extends RecyclerView.Adapter<AdapterHD.MyHolder> {

    Context context;
    List<ModelHD> HdList;

    public AdapterHD(Context context, List<ModelHD> HlList) {
        this.context = context;
        this.HdList = HlList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_his_depression, parent, false);

        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get the data
        final String cid = HdList.get(i).getcId();
        String depression = HdList.get(i).getDepression();
        int score = HdList.get(i).getScore();
        String logintime = HdList.get(i).getLogintime();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(logintime));
        String pTime = DateFormat.format("dd/MM/yyyy HH:mm:ss", calendar).toString();

        //set the data
        myHolder.score.setText("ผลคะแนนแบบประเมิน: " + score);
        myHolder.depression.setText("ผลการประเมิน: " + depression);
        myHolder.logintime.setText("เวลาทำแบบประเมิน: " + pTime + " น.");

    }

    @Override
    public int getItemCount() {
        return HdList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        //declare views from row_comments.xml
        TextView score, depression, logintime;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            score = itemView.findViewById(R.id.ScoreTv);
            depression = itemView.findViewById(R.id.DepTv);
            logintime = itemView.findViewById(R.id.TimeTv);

        }
    }
}
