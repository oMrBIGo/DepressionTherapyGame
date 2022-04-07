package com.depressiontherapygame.Users.QuizDepression.Quest;

public class QuizQuest {

    String question;
    String optionA;
    String optionB;
    String optionC;
    String optionD;
    int correctAns1;
    int correctAns2;
    int correctAns3;
    int correctAns4;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }


    public int getCorrectAns1() {
        return correctAns1;
    }

    public void setCorrectAns1(int correctAns1) {
        this.correctAns1 = correctAns1;
    }

    public int getCorrectAns2() {
        return correctAns2;
    }

    public void setCorrectAns2(int correctAns2) {
        this.correctAns2 = correctAns2;
    }

    public int getCorrectAns3() {
        return correctAns3;
    }

    public void setCorrectAns3(int correctAns3) {
        this.correctAns3 = correctAns3;
    }

    public int getCorrectAns4() {
        return correctAns4;
    }

    public void setCorrectAns4(int correctAns4) {
        this.correctAns4 = correctAns4;
    }

    public QuizQuest(String question, String optionA, String optionB, String optionC, String optionD, int correctAns1, int correctAns2 , int correctAns3 , int correctAns4) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAns1 = correctAns1;
        this.correctAns2 = correctAns2;
        this.correctAns3 = correctAns3;
        this.correctAns4 = correctAns4;
    }
}
