package com.depressiontherapygame.Users.GameTetris.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class LeaderModel {

    @Exclude private String image;
    @Exclude private String lastname;
    @Exclude private String level;
    @Exclude private int score;
    @Exclude private String uid;

    @Keep
    public LeaderModel() {
    }
    @Keep
    public LeaderModel(String image, String lastname, String level, int score, String uid) {
        this.image = image;
        this.lastname = lastname;
        this.level = level;
        this.score = score;
        this.uid = uid;
    }
    @Keep
    public String getImage() {
        return image;
    }
    @Keep
    public void setImage(String image) {
        this.image = image;
    }
    @Keep
    public String getLastname() {
        return lastname;
    }
    @Keep
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    @Keep
    public String getLevel() {
        return level;
    }
    @Keep
    public void setLevel(String level) {
        this.level = level;
    }
    @Keep
    public int getScore() {
        return score;
    }
    @Keep
    public void setScore(int score) {
        this.score = score;
    }
    @Keep
    public String getUid() {
        return uid;
    }
    @Keep
    public void setUid(String uid) {
        this.uid = uid;
    }
}
