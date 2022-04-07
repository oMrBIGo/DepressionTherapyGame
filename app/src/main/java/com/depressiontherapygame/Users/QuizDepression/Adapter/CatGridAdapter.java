package com.depressiontherapygame.Users.QuizDepression.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.depressiontherapygame.Users.QuizDepression.Model.CategoryModel;
import com.depressiontherapygame.Users.QuizDepression.QuizMainActivity;
import com.depressiontherapygame.Users.QuizDepression.SetsActivity;
import com.depressiontherapygame.R;

import java.util.List;

public class CatGridAdapter extends BaseAdapter {

    private List<CategoryModel> catList;

    public CatGridAdapter(List<CategoryModel> catList) {
        this.catList = catList;
    }


    @Override
    public int getCount() {
        return catList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;

        if(convertView == null)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cat_item_layout,parent,false);
        }
        else
        {
            view = convertView;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QuizMainActivity.selected_cat_index = position;
                Intent intent = new Intent(parent.getContext(), SetsActivity.class);
                parent.getContext().startActivity(intent);
                view.setEnabled(false);

            }
        });

        ((TextView) view.findViewById(R.id.setName)).setText(catList.get(position).getName());


        return view;
    }

}
