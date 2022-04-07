package com.depressiontherapygame.Users.FirstTimeDep.Adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.FirstTimeDep.FirstQuestionActivity;

public class FirstSetsAdapter extends BaseAdapter {

    private int numOfSets;

    public FirstSetsAdapter(int numOfSets) {
        this.numOfSets = numOfSets;
    }

    @Override
    public int getCount() {
        return numOfSets;
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
    public View getView(final int position, View convertView, final ViewGroup parent) {


        View view;

        if(convertView == null)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item_layout,parent,false);
        }
        else
        {
            view = convertView;
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(parent.getContext(), FirstQuestionActivity.class);
                intent.putExtra("SETNO", position);
                parent.getContext().startActivity(intent);
                view.setEnabled(false);
            }
        });

        ((TextView) view.findViewById(R.id.setNo_tv)).setText(String.valueOf(position+1));


        return view;
    }
}
