package com.depressiontherapygame.Users.QuizDepression.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.QuizDepression.Model.RecycModel;

import java.util.List;

public class RecycAdapter extends RecyclerView.Adapter<RecycAdapter.MyViewHolder> {

    private List<RecycModel> mList;
    private OnReCycListener mOnReCycListener;

    public RecycAdapter(List<RecycModel> mList, OnReCycListener onReCycListener) {
        this.mList = mList;
        this.mOnReCycListener = onReCycListener;
    }

    @NonNull
    @Override
    public RecycAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_item, parent, false);
        return new MyViewHolder(view, mOnReCycListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycAdapter.MyViewHolder holder, int position) {
        RecycModel image01 = mList.get(position);
        holder.image.setImageResource(image01.getImage());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView image;
        OnReCycListener onReCycListener;

        public MyViewHolder(@NonNull View itemView, OnReCycListener onReCycListener) {
            super(itemView);

            image = itemView.findViewById(R.id.bannerIv);
            this.onReCycListener = onReCycListener;
            image.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            onReCycListener.onReCycClick(getAdapterPosition());
        }
    }

    public interface OnReCycListener {
        void onReCycClick (int position);
    }
}
