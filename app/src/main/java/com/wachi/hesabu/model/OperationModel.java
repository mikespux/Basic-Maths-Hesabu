package com.wachi.hesabu.model;

public class OperationModel {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String name;


    public void setCheck(boolean check) {
        isCheck = check;
    }

    public boolean isCheck;

    public OperationModel(String name, boolean isCheck) {
        this.name = name;
        this.isCheck = isCheck;
    }

    public int id;

    public OperationModel(String name, boolean isCheck, int id) {
        this.name = name;
        this.isCheck = isCheck;
        this.id = id;
    }
}
