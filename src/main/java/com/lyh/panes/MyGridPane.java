package com.lyh.panes;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class MyGridPane extends GridPane {

    private static MyGridPane myGridPane;

    public MyGridPane(){

    }

    public static void setGridPane(MyGridPane gridPane) {
        myGridPane = gridPane;
    }


    // 插入一个 node和一个 btn 到组件 button 的上方,并排显示 btn 在 node的右一列
    public static void insertNodeOnThisButton(TextField node, JFXButton button, JFXButton btn){
        int rowIndex = GridPane.getRowIndex(button);
        int columnIndex = GridPane.getColumnIndex(button);
        moveNodeToNewSite(button,rowIndex+1,columnIndex);   //把button移动到下一行
        myGridPane.add(node,columnIndex,rowIndex);    //把node移动到原button的位置
        myGridPane.add(btn,columnIndex+1,rowIndex);    //把node移动到原button的位置
    }
    public static void moveNodeToNewSite(Node node,int row,int col){
        MyGridPane.setRowIndex(node,row);
        MyGridPane.setColumnIndex(node,col);
    }

    // 添加一个列族属性的下拉框 按钮 addColumnProperty
    public static void addComboBox(JFXComboBox<String> comboBox,Node node,TextField textField,JFXButton sub,JFXButton add){
        myGridPane.add(comboBox,MyGridPane.getColumnIndex(node),MyGridPane.getRowIndex(node));
        myGridPane.add(textField,3,MyGridPane.getRowIndex(node));
        myGridPane.add(sub,4,MyGridPane.getRowIndex(node));
        myGridPane.add(add,5,MyGridPane.getRowIndex(node));

        sub.setOnAction(event -> {
            myGridPane.getChildren().remove(textField);
            myGridPane.getChildren().remove(comboBox);
            myGridPane.getChildren().remove(sub);
            myGridPane.getChildren().remove(add);
            node.setVisible(true);
        });


        node.setVisible(false);
    }


}
