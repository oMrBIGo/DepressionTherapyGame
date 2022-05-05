package com.depressiontherapygame.Users.QuizDepression.Quest;

import androidx.annotation.Keep;

import com.google.firebase.database.Exclude;

public class QuizQuest {

    @Exclude String question;
    @Exclude String optionA;
    @Exclude String optionB;
    @Exclude String optionC;
    @Exclude String optionD;
    @Exclude int ans1Str;
    @Exclude int ans2Str;
    @Exclude int ans3Str;
    @Exclude int ans4Str;
    @Exclude int correctAns1;
    @Exclude int correctAns2;
    @Exclude int correctAns3;
    @Exclude int correctAns4;

    @Keep
    public QuizQuest(String question, String optionA, String optionB, String optionC, String optionD, int correctAns1, int correctAns2, int correctAns3, int correctAns4, int ans1Str, int ans2Str, int ans3Str, int ans4Str) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.ans1Str = ans1Str;
        this.ans2Str = ans2Str;
        this.ans3Str = ans3Str;
        this.ans4Str = ans4Str;
        this.correctAns1 = correctAns1;
        this.correctAns2 = correctAns2;
        this.correctAns3 = correctAns3;
        this.correctAns4 = correctAns4;
    }

    @Keep
    public String getQuestion() {
        return question;
    }

    @Keep
    public void setQuestion(String question) {
        this.question = question;
    }

    @Keep
    public String getOptionA() {
        return optionA;
    }

    @Keep
    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    @Keep
    public String getOptionB() {
        return optionB;
    }

    @Keep
    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    @Keep
    public String getOptionC() {
        return optionC;
    }

    @Keep
    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    @Keep
    public String getOptionD() {
        return optionD;
    }

    @Keep
    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    @Keep
    public int getAns1Str() {
        return ans1Str;
    }

    @Keep
    public void setAns1Str(int ans1Str) {
        this.ans1Str = ans1Str;
    }

    @Keep
    public int getAns2Str() {
        return ans2Str;
    }

    @Keep
    public void setAns2Str(int ans2Str) {
        this.ans2Str = ans2Str;
    }

    @Keep
    public int getAns3Str() {
        return ans3Str;
    }

    @Keep
    public void setAns3Str(int ans3Str) {
        this.ans3Str = ans3Str;
    }

    @Keep
    public int getAns4Str() {
        return ans4Str;
    }

    @Keep
    public void setAns4Str(int ans4Str) {
        this.ans4Str = ans4Str;
    }

    @Keep
    public int getCorrectAns1() {
        return correctAns1;
    }

    @Keep
    public void setCorrectAns1(int correctAns1) {
        this.correctAns1 = correctAns1;
    }

    @Keep
    public int getCorrectAns2() {
        return correctAns2;
    }

    @Keep
    public void setCorrectAns2(int correctAns2) {
        this.correctAns2 = correctAns2;
    }

    @Keep
    public int getCorrectAns3() {
        return correctAns3;
    }

    @Keep
    public void setCorrectAns3(int correctAns3) {
        this.correctAns3 = correctAns3;
    }

    @Keep
    public int getCorrectAns4() {
        return correctAns4;
    }

    @Keep
    public void setCorrectAns4(int correctAns4) {
        this.correctAns4 = correctAns4;
    }
}
