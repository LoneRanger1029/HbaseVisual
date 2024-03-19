package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.lyh.panes.controller.CreateTableController;
import com.lyh.utils.ui.AddIconUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;



public class CreateMoreProperty {

    private final static int MAX_COLUMN = 7;    // gridPane最大行数: (0~7)
    @FXML
    private BorderPane createMoreProperty;
    @FXML
    private GridPane grid;
    @FXML
    public Label family_label; //列族名称
    private static String family_name="";
    @FXML
    private JFXComboBox<String> combo;
    @FXML
    private TextField combo_value;
    @FXML
    private JFXButton sub;
    @FXML
    private JFXButton add;

    private static int currentRow = 1;  //当前在第一行(从0开始计)

    @FXML
    public void initialize(){
        CreateTableController.initialCombobox(combo);
        AddIconUtils.addIconToButton(sub,"icon/sub.png");
        AddIconUtils.addIconToButton(add,"icon/add.png");
        add.setOnAction(event -> {
            // 再添加一行
            currentRow++;
            if (currentRow>MAX_COLUMN){
                alertOutOfMaxCol();
                currentRow--;
                return; //记得return 阻止下面代码的执行
            }
            JFXComboBox<String> comboBox = new JFXComboBox<>();
            CreateTableController.initialCombobox(comboBox);
            grid.add(comboBox,3,currentRow);
            TextField textField = new TextField();
            grid.add(textField,4,currentRow);
            JFXButton remove = new JFXButton();
            AddIconUtils.addIconToButton(remove,"icon/sub.png");
            remove.setOnAction(e -> {
                grid.getChildren().remove(comboBox);
                grid.getChildren().remove(textField);
                grid.getChildren().remove(remove);
                currentRow--;
            });
            grid.add(remove,5,currentRow);
        });
    }

    /**
     * 设置列族标签的名
     * @param name 列族名
     */
    public void setColumnFamilyName(String name){
        family_label.setText(name);
    }

    public void alertOutOfMaxCol(){
        // 超出了最大行
        Alert alert = new Alert(Alert.AlertType.WARNING);
        ImageView imageView = new ImageView("icon/warn.png");
        alert.setGraphic(imageView);
        alert.setContentText("超过了最大可限制的属性数,请检查!");
        alert.show();
    }


}
