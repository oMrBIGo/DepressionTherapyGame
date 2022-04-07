package com.depressiontherapygame.Users.LoginRegister.Model;

public class ModelUserShow {
    public String phone;
    public String lastname;
    public String email;
    public String depression;
    public String firstdepression;
    public String level;
    public int firstscore;

    public ModelUserShow() {
    }

    public ModelUserShow(String phone, String lastname, String email, String depression, String firstdepression, String level, int firstscore) {
        this.phone = phone;
        this.lastname = lastname;
        this.email = email;
        this.depression = depression;
        this.firstdepression = firstdepression;
        this.level = level;
        this.firstscore = firstscore;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepression() {
        return depression;
    }

    public void setDepression(String depression) {
        this.depression = depression;
    }

    public String getFirstdepression() {
        return firstdepression;
    }

    public void setFirstdepression(String firstdepression) {
        this.firstdepression = firstdepression;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public int getFirstscore() {
        return firstscore;
    }

    public void setFirstscore(int firstscore) {
        this.firstscore = firstscore;
    }
}
