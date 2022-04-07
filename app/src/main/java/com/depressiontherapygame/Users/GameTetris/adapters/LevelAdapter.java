package com.depressiontherapygame.Users.GameTetris.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.depressiontherapygame.Users.GameTetris.db.tebles.GameLevel;
import com.depressiontherapygame.Users.GameTetris.listeners.ItemClickListener;
import com.depressiontherapygame.Users.GameTetris.utils.Consts;
import com.depressiontherapygame.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.MyViewHolder> {

    private Context mContext;
    private List<GameLevel> mLevelModelList;
    private ItemClickListener<GameLevel> mOnItemClickListener;

    public LevelAdapter(List<GameLevel> levelModelList, Context context, ItemClickListener<GameLevel> onItemClickListener) {
        this.mLevelModelList = levelModelList;
        this.mContext = context;
        this.mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.row_level, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GameLevel levelModel = mLevelModelList.get(position);

        holder.tvLevel.setText(String.valueOf(levelModel.getLevel()));
        holder.tvScore.setText(String.valueOf(levelModel.getScore()));
        if (levelModel.getStatus().equals(Consts.OtherConstant.LEVEL_COMPLETE)) {
            holder.llLevelMain.setBackground(mContext.getResources().getDrawable(R.drawable.levelcompleted));
            holder.tvLevel.setPadding(0, 0, 0, 0);
            holder.tvScore.setVisibility(View.VISIBLE);
            holder.tvScoreLabel.setVisibility(View.VISIBLE);

        } else if (levelModel.getStatus().equals(Consts.OtherConstant.LEVEL_NOT_COMPLETE)) {
            holder.llLevelMain.setBackground(mContext.getResources().getDrawable(R.drawable.levelnotcompleted));
            holder.tvLevel.setPadding(0, 0, 0, 0);
            holder.tvScore.setVisibility(View.VISIBLE);
            holder.tvScoreLabel.setVisibility(View.VISIBLE);

        } else if (levelModel.getStatus().equals(Consts.OtherConstant.LEVEL_LOCK)) {
            holder.llLevelMain.setBackground(mContext.getResources().getDrawable(R.drawable.levelpending));
            holder.tvLevel.setPadding(0, 0, 0, 0);
            holder.tvScore.setVisibility(View.INVISIBLE);
            holder.tvScoreLabel.setVisibility(View.INVISIBLE);

        }

        holder.llLevelMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!levelModel.getStatus().equals(Consts.OtherConstant.LEVEL_LOCK)) {
                    mOnItemClickListener.onItemClicked(position, levelModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLevelModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_level)
        AppCompatTextView tvLevel;
        @BindView(R.id.ll_level_main)
        LinearLayout llLevelMain;
        @BindView(R.id.tv_score)
        AppCompatTextView tvScore;
        @BindView(R.id.tv_score_label)
        AppCompatTextView tvScoreLabel;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
