package com.depressiontherapygame.Users.GameTetris.ActivityTetrisGame;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.depressiontherapygame.Users.GameTetris.db.databasehelper.DatabaseHelper;
import com.depressiontherapygame.Users.GameTetris.db.tebles.GameLevel;
import com.depressiontherapygame.Users.GameTetris.listeners.ItemClickListener;
import com.depressiontherapygame.R;
import com.depressiontherapygame.Users.GameTetris.adapters.LevelAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LevelActivity extends AppCompatActivity implements ItemClickListener<GameLevel> {
    @BindView(R.id.rv_level)
    RecyclerView rvLevel;
    @BindView(R.id.iv_back_toolbar)
    AppCompatImageView ivBackToolbar;
    @BindView(R.id.tv_title_toolbar)
    AppCompatTextView tvTitleToolbar;
    @BindView(R.id.toolbar)
    RelativeLayout toolbar;

    private List<GameLevel> levelModelList;
    private LevelAdapter mLevelAdapter;
    private Observer<List<GameLevel>> gameLevelListObserver;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        ButterKnife.bind(this);
        initToolbar();

        databaseHelper = new DatabaseHelper(this);

        levelModelList = new ArrayList<>();
        mLevelAdapter = new LevelAdapter(levelModelList, this, this);
        rvLevel.setLayoutManager(new GridLayoutManager(this, 4));
        rvLevel.setAdapter(mLevelAdapter);

        gameLevelListObserver = new Observer<List<GameLevel>>() {
            @Override
            public void onChanged(@Nullable List<GameLevel> gameLevels) {
                if (gameLevels != null && !gameLevels.isEmpty()) {
                    levelModelList.clear();
                    levelModelList.addAll(gameLevels);
                    mLevelAdapter.notifyDataSetChanged();

                    Log.d("mPuzzle Size ==>> %s", String.valueOf(levelModelList.size()));
                }
            }
        };
        databaseHelper.getAllLevel().observe(this, gameLevelListObserver);
    }

    private void initToolbar() {
        tvTitleToolbar.setText(R.string.select_level);
    }

    @Override
    public void onItemClicked(int position, GameLevel model) {
        Intent intent = new Intent(LevelActivity.this,GameActivity.class);
        intent.putExtra("speed_preference", model.getLevel());
        startActivity(intent);
    }

    @OnClick({R.id.iv_back_toolbar, R.id.tv_title_toolbar})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back_toolbar:
                onBackPressed();
                break;
            case R.id.tv_title_toolbar:
                break;
        }
    }
}
