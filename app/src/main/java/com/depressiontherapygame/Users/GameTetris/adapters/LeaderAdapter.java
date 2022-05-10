package com.depressiontherapygame.Users.GameTetris.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.GameTetris.Model.LeaderModel;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.MyHolder> {

    Context context;
    List<LeaderModel> modelList;
    private DatabaseReference LeaderRef;

    public LeaderAdapter(Context context, List<LeaderModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String StName = modelList.get(position).getLastname();
        String StLevel = modelList.get(position).getLevel();
        int StScore = modelList.get(position).getScore();
        String StImage = modelList.get(position).getImage();

        holder.tvName.setText("ชื่อผู้ใช้: "+StName);
        holder.tvLevel.setText("เลเวล: "+StLevel);
        holder.tvScore.setText("คะแนน: "+StScore);

        try {
            Picasso.get().load(StImage).placeholder(R.drawable.profile_image).into(holder.icon_profile);
        } catch (Exception e) {

        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvLevel, tvScore;
        CircleImageView icon_profile;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvScore = itemView.findViewById(R.id.tvScore);
            icon_profile = itemView.findViewById(R.id.icon_profile);

        }
    }
}
