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

import com.depressiontherapygame.Users.History.Model.ModelHL;
import com.depressiontherapygame.R;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterHL extends RecyclerView.Adapter<AdapterHL.MyHolder> {

    Context context;
    List<ModelHL> HlList;

    public AdapterHL(Context context, List<ModelHL> HlList) {
        this.context = context;
        this.HlList = HlList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_his_login, parent, false);

        return new MyHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        //get the data
        final String cid = HlList.get(i).getcId();
        String ipaddress = HlList.get(i).getIpaddress();
        String logintime = HlList.get(i).getLogintime();


        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(logintime));
        String pTime = DateFormat.format("dd/MM/yyyy HH:mm:ss", calendar).toString();

        //set the data
        myHolder.ipaddress.setText("IP Address: " + ipaddress);
        myHolder.logintime.setText("เวลาเข้าสู่ระบบ: " + pTime + " น.");

    }

    @Override
    public int getItemCount() {
        return HlList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        //declare views from row_comments.xml
        TextView ipaddress, logintime;

        public MyHolder(@NonNull View itemView) {
            super(itemView);
            ipaddress = itemView.findViewById(R.id.IPAddressTv);
            logintime = itemView.findViewById(R.id.TimeTv);
        }
    }
}
