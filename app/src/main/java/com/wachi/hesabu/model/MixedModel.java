package com.wachi.hesabu.model;

import java.util.ArrayList;
import java.util.List;

public class MixedModel {


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


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int id;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String question, answer, op_1, op_2, op_3;


    public void setOptionList(List<String> optionList) {
        this.optionList = optionList;
    }

    public List<String> optionList = new ArrayList<>();


    public MixedModel(String question, String answer, String op_1, String op_2, String op_3) {
        this.question = question;
        this.answer = answer;
        this.op_1 = op_1;
        this.op_2 = op_2;
        this.op_3 = op_3;
    }


    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
