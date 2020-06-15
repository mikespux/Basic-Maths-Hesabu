package com.wachi.hesabu.model;

public class StoreSetModel {

    public String title, category;
    public int id;

    public StoreSetModel(int id, String title, String category) {
        this.id = id;
        this.title = title;
        this.category = category;
    }
}
