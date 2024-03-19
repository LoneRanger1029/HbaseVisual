package com.lyh.pojo;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TreeItem;

// 不同等级的 TreeItem对于不同的操作
public class MyTreeItem<S> extends TreeItem<String> {
    /**
     * grade：等级
     * 1级： 连接名称
     * 2级： namespace
     * 3级： table
     */
    private int grade;
    public MyTreeItem(){
    }

    public MyTreeItem(String value, int grade) {
        super(value);
        this.grade = grade;
    }

    public int getGrade() {
        return grade;
    }


    public void setGrade(int grade) {
        this.grade = grade;
    }
}
