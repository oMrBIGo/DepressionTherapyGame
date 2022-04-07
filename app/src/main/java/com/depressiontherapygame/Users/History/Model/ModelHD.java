package com.depressiontherapygame.Users.History.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class ModelHD {

    @Exclude private String cId, depression, logintime, uid;
    @Exclude private int score;

    public ModelHD() {
    }

    @Keep
    public ModelHD(String cId, String depression, String logintime, String uid, int score) {
        this.cId = cId;
        this.depression = depression;
        this.logintime = logintime;
        this.uid = uid;
        this.score = score;
    }

    @Keep
    public String getcId() {
        return cId;
    }

    @Keep
    public void setcId(String cId) {
        this.cId = cId;
    }

    @Keep
    public String getDepression() {
        return depression;
    }

    @Keep
    public void setDepression(String depression) {
        this.depression = depression;
    }

    @Keep
    public String getLogintime() {
        return logintime;
    }

    @Keep
    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    @Keep
    public String getUid() {
        return uid;
    }

    @Keep
    public void setUid(String uid) {
        this.uid = uid;
    }

    @Keep
    public int getScore() {
        return score;
    }

    @Keep
    public void setScore(int score) {
        this.score = score;
    }
}
