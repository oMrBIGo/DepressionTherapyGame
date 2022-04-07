package com.depressiontherapygame.Users.Consult.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class ModelComment {

    @Exclude private String cId, comment, timestamp, uid, uEmail, uDp, uLastName;

    public ModelComment() {
    }

    @Keep
    public ModelComment(String cId, String comment, String timestamp, String uid, String uEmail, String uDp, String uLastName) {
        this.cId = cId;
        this.comment = comment;
        this.timestamp = timestamp;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uLastName = uLastName;
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
    public String getComment() {
        return comment;
    }

    @Keep
    public void setComment(String comment) {
        this.comment = comment;
    }

    @Keep
    public String getTimestamp() {
        return timestamp;
    }

    @Keep
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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
    public String getuEmail() {
        return uEmail;
    }

    @Keep
    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    @Keep
    public String getuDp() {
        return uDp;
    }

    @Keep
    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    @Keep
    public String getuLastName() {
        return uLastName;
    }

    @Keep
    public void setuLastName(String uLastName) {
        this.uLastName = uLastName;
    }
}