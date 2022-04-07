package com.depressiontherapygame.Users.GameTetris.db.tebles;


import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created on 11/11/2021.
 */
@Entity
public class GameLevel {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "level")
    private int level;

    @ColumnInfo(name = "score")
    private int score;

    @Nullable
    @ColumnInfo(name = "status")
    private String status;

    public long getId() {
        return id;
    }

    public void  setId(long id) {
        this.id = id;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Nullable
    public String getStatus() {
        return status;
    }

    public void setStatus(@Nullable String status) {
        this.status = status;
    }
}




