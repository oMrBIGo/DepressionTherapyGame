package com.depressiontherapygame.Users.QuizDepression.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class ModelScore {


    @Exclude
    int score;

    @Keep
    public ModelScore(int score) {
        this.score = score;
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
