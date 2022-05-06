package com.depressiontherapygame.Users.QuizDepression.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class RecycModel {

    @Exclude
    private int image;

    @Keep
    public RecycModel() {
    }

    @Keep
    public RecycModel(int image) {
        this.image = image;
    }

    @Keep
    public int getImage() {
        return image;
    }

    @Keep
    public void setImage(int image) {
        this.image = image;
    }
}
