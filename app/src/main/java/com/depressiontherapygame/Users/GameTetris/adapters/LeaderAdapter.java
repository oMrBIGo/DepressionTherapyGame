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
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderAdapter extends FirebaseRecyclerAdapter<LeaderModel, LeaderAdapter.viewHolder> {

    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    public LeaderAdapter(@NonNull FirebaseRecyclerOptions<LeaderModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull viewHolder holder, int position, @NonNull LeaderModel model) {
        String lastname = model.getLastname();
        String level = model.getLevel();
        Picasso.get().load(model.getImage()).into(holder.icon_profile);
        int score = model.getScore();
        holder.setData(lastname, level, score);

    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard_layout, parent, false);
        return new viewHolder(view);
    }

    public class viewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvLevel, tvScore;
        CircleImageView icon_profile;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvName);
            tvLevel = itemView.findViewById(R.id.tvLevel);
            tvScore = itemView.findViewById(R.id.tvScore);
            icon_profile = itemView.findViewById(R.id.icon_profile);
        }

        public void setData(String lastname, String level, int score) {
            tvName.setText(lastname);
            tvLevel.setText(level);
            tvScore.setText(String.valueOf(score));
        }
    }

}
