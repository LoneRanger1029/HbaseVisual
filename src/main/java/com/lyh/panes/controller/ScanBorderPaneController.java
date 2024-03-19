package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.lyh.panes.model.GetAllVersionsResult;
import com.lyh.panes.model.GetResult;
import com.lyh.panes.model.ScanResult;
import com.lyh.pojo.ConnectionInfo;
import com.lyh.utils.HBaseDML;
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
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class ScanBorderPaneController {

    private final static Logger logger = Logger.getLogger(ScanBorderPaneController.class);

    public static String currentNamespace;
    public static String currentTableName;

    @FXML
    public BorderPane scanBorderPane;
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
    public TextField start;
    @FXML
    public TextField stop;
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
        namespaces.getItems().addAll(ConnectionInfo.all_namespace);
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
    public void get_scan() throws IOException {
        currentNamespace = namespaces.getSelectionModel().getSelectedItem();
        currentTableName = tables.getSelectionModel().getSelectedItem();
        String startRow = start.getText();
        String stopRow = stop.getText();
        if (!currentNamespace.equals("") && !currentTableName.equals("") && !startRow.equals("") && !stopRow.equals("")){
            scan_table(currentNamespace,currentTableName,startRow,stopRow);
        }
    }
    public void scan_table(String namespace,String table,String startRow,String stopRowKey) throws IOException {
        List<Cell[]> cells = null;
        if (ifSearchAll.isSelected()){
            logger.info("查询所有版本");
            cells = HBaseDML.scan(namespace, table,startRow,stopRowKey);
            List<String> list = new ArrayList<>();  //list是用来存放查询结果的
            for (Cell[] cell : cells) {
                List<String> tmp = GetPaneController.putCellsToList(cell);
                list.addAll(tmp);
            }
            showData(new GetAllVersionsResult(list));
        }else{
            logger.info("查询最新版本");
            cells = HBaseDML.scan(namespace, table, startRow,stopRowKey);
            List<String> list = new ArrayList<>();
            for (Cell[] cell_arr : cells) {
                for (Cell cell : cell_arr) {
                    String columnFamily = new String(CellUtil.cloneFamily(cell));
                    String columnName = new String(CellUtil.cloneQualifier(cell));
                    String value = new String(CellUtil.cloneValue(cell));
                    String rowKey = new String(CellUtil.cloneRow(cell));
                    String res = rowKey + "-" + columnFamily + "-" + columnName + "-" + value;
                    list.add(res.trim());
                }
            }
            logger.info("查询到"+list.size()+"条记录");
            showData(new ScanResult(ifSearchAll.isSelected(),list));
        }
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
//                    item.put(columnQualify,cellValue);
                    item.put(columnQualify,cellValue);
                    item.put("rowKey",new SimpleStringProperty(rowKey));
                    list.add(item);
                }
            }
        }
        data = FXCollections.observableArrayList(list);
        tableView.setItems(data);
        scanBorderPane.setCenter(tableView);
        tableView.refresh();
    }

    public void showData(ScanResult result){

        if (!result.isAllVersions){
            TableView<HashMap<String, SimpleStringProperty>> tableView = new TableView<>();

            // 1.制作表头
            // 1.1 第一列
            TableColumn<HashMap<String,SimpleStringProperty>,String> rowKeyCol = new TableColumn<>("rowKey");
            rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
            rowKeyCol.setMinWidth(100);
            tableView.getColumns().add(rowKeyCol);

            // 添加列族
            List<TableColumn<HashMap<String,SimpleStringProperty>,String>> columnFamily_list = new ArrayList<>();
            Set<String> columnFamilies = result.getColumnFamilies();
            if(columnFamilies==null ||columnFamilies.size()==0){
                return;
            }
            for (String columnFamily : columnFamilies) {
                TableColumn<HashMap<String,SimpleStringProperty>,String> columnFam = new TableColumn<>(columnFamily);
                columnFamily_list.add(columnFam);
                tableView.getColumns().add(columnFam);
            }

            // 1.4 把列放到指定列族当中
            // 遍历列
            for (TableColumn<HashMap<String,SimpleStringProperty>, String> columnF : columnFamily_list) {
                Set<String> columns = result.getColumns(columnF.getText());

                for (String column : columns) {
                    TableColumn<HashMap<String,SimpleStringProperty>,String> col = new TableColumn<>(column);
                    col.setCellValueFactory(new MapValueFactory(columnF.getText()+":"+column));
                    columnF.getColumns().add(col);
                }
            }
            List<HashMap<String,SimpleStringProperty>> list = new ArrayList<>(1);

            for (String s : result.getRowKeySet()) {
                list.add(result.valueMap.get(s));
            }

            ContextMenu contextMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("删除该行");
            contextMenu.getItems().add(removeItem);
            tableView.setContextMenu(contextMenu);
            // 设置多选
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            removeItem.setOnAction(event -> {
                ScanAllController.removeRow(tableView);
            });

            data = FXCollections.observableArrayList(list);
            tableView.setItems(data);
            scanBorderPane.setCenter(tableView);
            tableView.refresh();
        }else{

        }
    }
}
