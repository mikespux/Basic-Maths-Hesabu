package com.wachi.hesabu.model;

public class ProgressModel {

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMain_id() {
        return main_id;
    }

    public void setMain_id(int main_id) {
        this.main_id = main_id;
    }


    public String tableName;
    public int id;
    public int main_id;
    public int level_no;
    public int progress;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int score;


    public int ref_id;

    public ProgressModel() {

    }


    public ProgressModel(String tableName, int ref_id, int main_id, int level_no, int progress) {
        this.tableName = tableName;
        this.ref_id = ref_id;
        this.main_id = main_id;
        this.level_no = level_no;
        this.progress = progress;
    }

}
