package com.wachi.hesabu.model;

import java.util.ArrayList;
import java.util.List;

public class DailyModel {


    public int id;
    public String firstDigit;
    public String secondDigit;
    public String answer;
    public String op_1;
    public String question;
    public String op_2;
    public String op_3;
    public String sign;
    public String isRemainder;
    public int main_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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


    public void setOp_1(String op_1) {
        this.op_1 = op_1;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public void setOp_2(String op_2) {
        this.op_2 = op_2;
    }


    public void setOp_3(String op_3) {
        this.op_3 = op_3;
    }


    public String getRem() {
        return rem;
    }


    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public List<String> optionList = new ArrayList<>();

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


    public void setOperation_id(int operation_id) {
        this.operation_id = operation_id;
    }

    public int operation_id;

    public MixedModel mixedModel;

    public DailyModel(MixedModel mixedModel, int operation_id) {
        this.mixedModel = mixedModel;
        this.operation_id = operation_id;

    }

    public DailyModel() {

    }


}
