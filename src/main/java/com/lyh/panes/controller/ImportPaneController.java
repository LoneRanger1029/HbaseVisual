package com.lyh.panes.controller;

import com.lyh.pojo.ConnectionInfo;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

public class ImportPaneController {

    @FXML
    public AnchorPane importPane;
    @FXML
    public ComboBox<String> namespaces;
    @FXML
    public ComboBox<String> tables;

    @FXML
    public void initialize(){
        initialComboBox();
    }

    public void initialComboBox(){
        namespaces.getItems().setAll(ConnectionInfo.all_namespace);
    }

    @FXML
    public void chooseTable(){
        // 防止新创建的表格看不到
        initialComboBox();

        for (String item : namespaces.getItems()) {
            if (namespaces.getSelectionModel().getSelectedItem().equals(item)){
                tables.getItems().clear();
                List<String> list = ConnectionInfo.namespaceMap.get(item);
                if(list!=null && list.size()!=0)
                    tables.getItems().addAll(new HashSet<>(list));
            }
        }
    }

    @FXML
    public void import_to_hbase() throws IOException {
        String namespace = namespaces.getSelectionModel().getSelectedItem();
        String table = tables.getSelectionModel().getSelectedItem();
        // todo 扫描excel表，插入到hbase中去
        ExcelPreviewController controller = new ExcelPreviewController();
        controller.import_into_hbase(namespace,table);
        cancel();
    }

    @FXML
    public void cancel(){
        Stage stage = (Stage) importPane.getScene().getWindow();
        stage.close();
    }
}
