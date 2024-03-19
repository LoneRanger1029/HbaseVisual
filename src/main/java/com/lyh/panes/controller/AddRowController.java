package com.lyh.panes.controller;

import com.lyh.pojo.ConnectionInfo;
import com.lyh.utils.HBaseDML;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class AddRowController {

    public static String CURRENT_NAMESPACE;
    public static String CURRENT_TABLE_NAME;

    private final static Logger logger = Logger.getLogger(AddRowController.class);

    @FXML
    public BorderPane addRowPane;
    @FXML
    public TextField rowKey_field;
    @FXML
    public TextField col_value;
    @FXML
    public TextField cell_value;
    @FXML
    public ComboBox<String> namespace_combo;
    @FXML
    public ComboBox<String> tableName_combo;

    @FXML
    public void initialize(){
        initialComboBox();
    }

    public void initialComboBox(){
        namespace_combo.getItems().addAll(ConnectionInfo.all_namespace);
    }

    @FXML
    public void chooseTable(){
        for (String item : namespace_combo.getItems()) {
            if (namespace_combo.getSelectionModel().getSelectedItem().equals(item)){
                tableName_combo.getItems().clear();
                List<String> list = ConnectionInfo.namespaceMap.get(item);
                if(list!=null && list.size()!=0)
                    tableName_combo.getItems().addAll(new HashSet<>(list));
            }
        }
    }

    @FXML
    public void add_row(){

        String rowKey = rowKey_field.getText();
        String[] col = col_value.getText().split(":");
        String cell = cell_value.getText();
        try {
            HBaseDML.putCell(CURRENT_NAMESPACE,CURRENT_TABLE_NAME,rowKey,col[0],col[1],cell);
            logger.info("数据添加成功,");
        } catch (IOException e) {
            logger.error("数据添加失败");
        }
    }
    @FXML
    public void cancel(){
        Stage stage = (Stage)addRowPane.getScene().getWindow();
        stage.close();
    }
}
