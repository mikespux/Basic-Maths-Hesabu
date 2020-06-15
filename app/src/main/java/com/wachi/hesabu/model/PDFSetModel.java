package com.wachi.hesabu.model;

public class PDFSetModel {

    public int id;



    public String getIsRemider() {
        return isRemider;
    }

    public void setIsRemider(String isRemider) {
        this.isRemider = isRemider;
    }

    public String isRemider;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPractice_set() {
        return practice_set;
    }

    public void setPractice_set(int practice_set) {
        this.practice_set = practice_set;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public int practice_set;
    public String title, example;
    public String tableName;


    public PDFSetModel(int id, String title, String tableName, String example, int practice_set, String isRemider) {
        this.id = id;
        this.title = title;
        this.tableName = tableName;
        this.example = example;
        this.practice_set = practice_set;
        this.isRemider = isRemider;
    }
}
