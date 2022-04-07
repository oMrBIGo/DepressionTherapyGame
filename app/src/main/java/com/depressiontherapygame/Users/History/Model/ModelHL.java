package com.depressiontherapygame.Users.History.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;


public class ModelHL {

    @Exclude String cId, ipaddress, logintime;

    public ModelHL() {
    }
    @Keep
    public ModelHL(String cId, String ipaddress, String logintime) {
        this.cId = cId;
        this.ipaddress = ipaddress;
        this.logintime = logintime;
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
    public String getIpaddress() {
        return ipaddress;
    }

    @Keep
    public void setIpaddress(String ipaddress) {
        this.ipaddress = ipaddress;
    }

    @Keep
    public String getLogintime() {
        return logintime;
    }

    @Keep
    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }
}
