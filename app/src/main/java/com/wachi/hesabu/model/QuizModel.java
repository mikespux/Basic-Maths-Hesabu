package com.wachi.hesabu.model;

import java.util.ArrayList;
import java.util.List;

public class QuizModel {

    public String sign;


    public String getRem() {
        return rem;
    }

    public void setRem(String rem) {
        this.rem = rem;
    }

    public String rem;

    public int getMain_id() {
        return main_id;
    }

    public void setMain_id(int main_id) {
        this.main_id = main_id;
    }

    public int main_id;


    public int ref_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public QuizModel() {
    }

    public int id;
    public String firstDigit;
    public String secondDigit;
    public String answer;
    public String op_1;
    public String op_2;
    public String op_3;


    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public List<String> optionList = new ArrayList<>();


    public String getFirstDigit() {
        return firstDigit;
    }

    public void setFirstDigit(String firstDigit) {
        this.firstDigit = firstDigit;
    }

    public String getSecondDigit() {
        return secondDigit;
    }

    public void setSecondDigit(String secondDigit) {
        this.secondDigit = secondDigit;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
