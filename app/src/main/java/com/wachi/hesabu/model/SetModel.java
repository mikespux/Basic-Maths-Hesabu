package com.wachi.hesabu.model;

public class SetModel {

    public int id;


    public String getIsRemider() {
        return isRemider;
    }

    public void setIsRemider(String isRemider) {
        this.isRemider = isRemider;
    }

    public String isRemider;

    public int ref_id;

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

}
