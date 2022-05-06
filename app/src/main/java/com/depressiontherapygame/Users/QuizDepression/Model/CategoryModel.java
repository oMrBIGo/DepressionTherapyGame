package com.depressiontherapygame.Users.QuizDepression.Model;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class CategoryModel {


    @Exclude private String id;
    @Exclude private String name;

    @Keep
    public CategoryModel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    @Keep
    public String getId() {
        return id;
    }

    @Keep
    public void setId(String id) {
        this.id = id;
    }

    @Keep
    public String getName() {
        return name;
    }

    @Keep
    public void setName(String name) {
        this.name = name;
    }
}
