package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.lyh.panes.model.GetAllVersionsResult;
import com.lyh.panes.model.GetResult;
import com.lyh.pojo.ConnectionInfo;
import com.lyh.pojo.MyAlert;
import com.lyh.utils.HBaseDML;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.log4j.Logger;
import org.jcodings.util.Hash;
import org.omg.CORBA.PUBLIC_MEMBER;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class GetPaneController {

    private final static Logger logger = Logger.getLogger(GetPaneController.class);

    @FXML
    public BorderPane getPane;
    @FXML
    public HBox hb_top;
    @FXML
    public VBox vb_namespace;
    @FXML
    public VBox vb_table;
    @FXML
    public ComboBox<String> namespaces;
    @FXML
    public ComboBox<String> tables;
    @FXML
    public JFXButton search;
    @FXML
    public TextField rowKey_field;
    @FXML
    public JFXRadioButton ifSearchAll;
    @FXML
    public TableView<HashMap<String, SimpleStringProperty>> table_area;

    private ObservableList<HashMap<String, SimpleStringProperty>> data;


    @FXML
    public void initialize(){
        hb_top.setPadding(new Insets(5,10,5,10));
        hb_top.setSpacing(10.0);
        Insets insets = new Insets(5, 10, 5, 10);
        vb_namespace.setSpacing(5.0);
        vb_namespace.setPadding(insets);
        vb_table.setSpacing(5.0);
        vb_table.setPadding(insets);
        initialComboBox();
    }

    public void initialComboBox(){
        namespaces.getItems().setAll(ConnectionInfo.all_namespace);
    }

    /**
     * 当点击命名空间下拉框的时候，初始化表名下拉框
     * 根据不同的namespace对应自己的table
     */
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
    public void get_search() throws IOException {
        String namespace = namespaces.getSelectionModel().getSelectedItem();
        String table = tables.getSelectionModel().getSelectedItem();
        String rowKey = rowKey_field.getText();
        if (!namespace.equals("") && !table.equals("") && !rowKey.equals("")){
            search_table(namespace,table,rowKey);
        }
    }

    public void search_table(String namespace,String table,String rowKey) throws IOException {
        if (ifSearchAll.isSelected()){
            logger.info("查询所有版本");
            Cell[] cells = HBaseDML.getCells(namespace, table, rowKey);
            if (cells!=null){
                List<String> list = putCellsToList(cells);
                logger.info("共查询到"+list.size()+"条记录");
                showData(new GetAllVersionsResult(list));
            }
        }else{
            logger.info("查询最新版本");
            Cell[] cells = HBaseDML.getCellsNewest(namespace, table, rowKey);
            if (cells!=null){
//                List<String> list = new ArrayList<>();
//                for (Cell cell : cells) {
//                    String columnFamily = new String(CellUtil.cloneFamily(cell));
//                    String columnName = new String(CellUtil.cloneQualifier(cell));
//                    String value = new String(CellUtil.cloneValue(cell));
//                    String rowK = new String(CellUtil.cloneRow(cell));
//                    String res = rowK + "-" + columnFamily + "-" + columnName + "-" + value;
//                    list.add(res.trim());
//                }
                List<String> list = putCellsToList(cells);
                logger.info("查询到"+list.size()+"条记录");
                showData(new GetResult(ifSearchAll.isSelected(),list));
            }
        }
    }

    public static List<String> putCellsToList(Cell[] cells){
        List<String> list = new ArrayList<>();
        for (Cell cell : cells) {
            String columnFamily = new String(CellUtil.cloneFamily(cell));
            String columnName = new String(CellUtil.cloneQualifier(cell));
            String value = new String(CellUtil.cloneValue(cell));
            String rowK = new String(CellUtil.cloneRow(cell));
            String res = rowK + "-" + columnFamily + "-" + columnName + "-" + value;
            list.add(res.trim());
        }
        return list;
    }

    public void showData(GetAllVersionsResult result){
        TableView<HashMap<String, SimpleStringProperty>> tableView = new TableView<>();
        // 1.制作表头
        // 1.1 第一列
        TableColumn<HashMap<String, SimpleStringProperty>, String> rowKeyCol = new TableColumn<>("rowKey");
        rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
        rowKeyCol.setMinWidth(100);
        tableView.getColumns().add(rowKeyCol);

        // 添加列族
        List<TableColumn<HashMap<String, SimpleStringProperty>, String>> columnFamily_list = new ArrayList<>();
        Set<String> columnFamilies = result.getColumnFamilies();
        if (columnFamilies == null || columnFamilies.size() == 0) {
            return;
        }
        for (String columnFamily : columnFamilies) {
            TableColumn<HashMap<String, SimpleStringProperty>, String> columnFam = new TableColumn<>(columnFamily);
            columnFamily_list.add(columnFam);
            tableView.getColumns().add(columnFam);
        }

        // 1.4 把列放到指定列族当中
        // 遍历列
        for (TableColumn<HashMap<String, SimpleStringProperty>, String> columnF : columnFamily_list) {
            Set<String> columns = result.getColumns(columnF.getText());
            for (String column : columns) {
                TableColumn<HashMap<String, SimpleStringProperty>, String> col = new TableColumn<>(column);
                col.setCellValueFactory(new MapValueFactory(columnF.getText() + ":" + column));
                columnF.getColumns().add(col);
            }
        }
        List<HashMap<String, SimpleStringProperty>> list = new ArrayList<>();
        for (String rowKey : result.getRowKeySet()) {
            for (Map.Entry<String,List<SimpleStringProperty>> entry : result.valueMap.get(rowKey).entrySet()){
                String columnQualify = entry.getKey();
                List<SimpleStringProperty> value = entry.getValue();

                for (int i = 0; i < value.size(); i++) {
                    // todo 这是 tableview 使用 hashMap 时要求的数据源类型
                    HashMap<String, SimpleStringProperty> item = new HashMap<>();
                    if (columnQualify.equals("rowKey"))
                        continue;
                    // todo 在数据后面标注版本号码
                    String tmp  =value.get(i).getValue();
                    SimpleStringProperty cellValue = new SimpleStringProperty(tmp + "[version=" + (i + 1) +"/"+ value.size() + "]");

                    item.put(columnQualify,cellValue);
                    item.put("rowKey",new SimpleStringProperty(rowKey));
                    list.add(item);
                }
            }
        }
        data = FXCollections.observableArrayList(list);
        tableView.setItems(data);
        getPane.setCenter(tableView);
        tableView.refresh();
    }

    /**
     * 显示查询结果 - 最新版本
     */
    public void showData(GetResult result) {

        if (!result.isAllVersions) {
            TableView<HashMap<String, SimpleStringProperty>> tableView = new TableView<>();

            // 1.制作表头
            // 1.1 第一列
            TableColumn<HashMap<String, SimpleStringProperty>, String> rowKeyCol = new TableColumn<>("rowKey");
            rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
            rowKeyCol.setMinWidth(100);
            tableView.getColumns().add(rowKeyCol);

            // 添加列族
            List<TableColumn<HashMap<String, SimpleStringProperty>, String>> columnFamily_list = new ArrayList<>();
            Set<String> columnFamilies = result.getColumnFamilies();
            if (columnFamilies == null || columnFamilies.size() == 0) {
                return;
            }
            for (String columnFamily : columnFamilies) {
                TableColumn<HashMap<String, SimpleStringProperty>, String> columnFam = new TableColumn<>(columnFamily);
                columnFamily_list.add(columnFam);
                tableView.getColumns().add(columnFam);
            }

            // 1.4 把列放到指定列族当中
            // 遍历列
            for (TableColumn<HashMap<String, SimpleStringProperty>, String> columnF : columnFamily_list) {
                Set<String> columns = result.getColumns(columnF.getText());
                for (String column : columns) {
                    TableColumn<HashMap<String, SimpleStringProperty>, String> col = new TableColumn<>(column);
                    col.setCellValueFactory(new MapValueFactory(columnF.getText() + ":" + column));
                    columnF.getColumns().add(col);
                }
            }
            List<HashMap<String, SimpleStringProperty>> list = new ArrayList<>(1);
            list.add(result.valueMap);

            data = FXCollections.observableArrayList(list);
            tableView.setItems(data);
            getPane.setCenter(tableView);
            tableView.refresh();
        }
    }
}