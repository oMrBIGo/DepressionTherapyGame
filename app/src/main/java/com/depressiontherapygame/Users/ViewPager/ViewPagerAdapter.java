package com.depressiontherapygame.Users.ViewPager;

/**
 * Created on 20-10-21.
 * Update on 01-02-22.
 * Developer by Nathit Panrod
 */

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.depressiontherapygame.R;

public class ViewPagerAdapter extends PagerAdapter {

    /* View */
    private Context context;
    private LayoutInflater layoutInflater;
    /* Integer View Input image */
    private Integer[] images = {R.drawable.depression_01, R.drawable.depression_02};

    public ViewPagerAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        /* Init View */
        /* animation Button */
        final Animation animation = AnimationUtils.loadAnimation(context, R.anim.button_bounce_home);
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("PrivateApi")
            @Override
            public void onClick(View view) {

                if (position == 0) {
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String shareBody = "https://play.google.com/store/apps/details?id=com.depressiontherapygame";
                    myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(myIntent, "แอปพลิเคชันเกมบำบัดภาวะโรคซึมเศร้า"));

                } else if (position == 1) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:1323"));
                    context.startActivity(callIntent);
                    imageView.startAnimation(animation);
                }

            }
        });

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);
    }
}
