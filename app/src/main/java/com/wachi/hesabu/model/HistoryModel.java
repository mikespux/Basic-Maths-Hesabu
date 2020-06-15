package com.wachi.hesabu.model;


public class HistoryModel {


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


    public String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String question;
    public String answer;
    public String userAnswer;
    public int id;

    public HistoryModel(int id, String question, String answer, String userAnswer) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.userAnswer = userAnswer;

    }

    public HistoryModel(String question, String answer, String userAnswer, String date) {
        this.question = question;
        this.answer = answer;
        this.userAnswer = userAnswer;
        this.date = date;

    }

    public HistoryModel() {


    }


}
