package com.lyh.panes.controller;

import com.lyh.panes.enums.POIType;
import com.lyh.pojo.ConnectionInfo;
import com.lyh.pojo.MyAlert;
import com.lyh.utils.POIExcelWrite;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.joda.time.DateTime;

import java.io.*;
import java.util.HashSet;
import java.util.List;

public class ExportPaneController {

    @FXML
    private AnchorPane exportPane;
    @FXML
    private ComboBox<String> namespaces;
    @FXML
    private ComboBox<String> tables;
    @FXML
    private ComboBox<String> excel;

    @FXML
    public void initialize(){
        excel.getItems().addAll(POIType.POI_2003.name(),POIType.POI_2007.name());
        initialComboBox();
    }
    public void initialComboBox(){
        namespaces.getItems().setAll(ConnectionInfo.all_namespace);
    }


    @FXML
    public void chooseTable(){
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
    public void export_to_excel() throws IOException {
        String tableName = namespaces.getSelectionModel().getSelectedItem()+":"+tables.getSelectionModel().getSelectedItem();
        POIType type = POIType.valueOf(excel.getSelectionModel().getSelectedItem());
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("选择保存路径");
        File file = directoryChooser.showDialog(exportPane.getScene().getWindow());
        if (file != null){
            boolean flag = false;
            String savePath;

            if (type==POIType.POI_2007)
                savePath = file.getAbsolutePath()+"/"+System.currentTimeMillis()+".xls";
            else
                savePath = file.getAbsolutePath()+"/"+System.currentTimeMillis()+".xlsx";

            POIExcelWrite excelWrite = new POIExcelWrite(tableName,type,savePath);
            try {
                long start = System.currentTimeMillis();
                excelWrite.writeToExcel();
                long end = System.currentTimeMillis();
                String time = new DateTime(end - start).toString("ss:SSS");
                MyAlert.getSuccess("保存成功","表保存到本地文件,path="+savePath+",用时"+time+" s").show();
            } catch (IOException e) {
                MyAlert.getError("保存失败","保存失败,请重试!").show();
            }

        }
    }

    public static boolean saveToLocal(String filepath, String content){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath))) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    @FXML
    public void cancel(){
        Stage stage = (Stage) exportPane.getScene().getWindow();
        stage.close();
    }
}
