package com.depressiontherapygame.Users.Consult.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class ModelPost {
    //user same name as we given while uploading post
    @Exclude private String pId, pTitle, pDescr, pLikes, pComments, pTime, uid, uEmail, uDp, uLastName;

    public ModelPost() {
    }

    @Keep
    public ModelPost(String pId, String pTitle, String pDescr, String pLikes, String pComments, String pTime, String uid, String uEmail, String uDp, String uLastName) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pDescr = pDescr;
        this.pLikes = pLikes;
        this.pComments = pComments;
        this.pTime = pTime;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uLastName = uLastName;
    }

    @Keep
    public String getpId() {
        return pId;
    }

    @Keep
    public void setpId(String pId) {
        this.pId = pId;
    }

    @Keep
    public String getpTitle() {
        return pTitle;
    }

    @Keep
    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    @Keep
    public String getpDescr() {
        return pDescr;
    }

    @Keep
    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    @Keep
    public String getpLikes() {
        return pLikes;
    }

    @Keep
    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    @Keep
    public String getpComments() {
        return pComments;
    }

    @Keep
    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    @Keep
    public String getpTime() {
        return pTime;
    }

    @Keep
    public void setpTime(String pTime) {
        this.pTime = pTime;
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
