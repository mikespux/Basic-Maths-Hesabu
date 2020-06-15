package com.wachi.hesabu.model;

public class PDFModel {

    public String question, answer;
    public int id, category_id, sub_category_id;


    public PDFModel(int id, String question, String answer, int category_id, int sub_category_id) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.category_id = category_id;
        this.sub_category_id = sub_category_id;
    }

}
