package com.wachi.hesabu.model;

import java.util.List;

public class MainModel {
    public String title;
    public int id, totalQuestion;
    public List<String> operationList;

    public MainModel(int id, String title, int totalQuestion, List<String> operationList) {
        this.id = id;
        this.title = title;
        this.totalQuestion = totalQuestion;
        this.operationList = operationList;
    }
}
