package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.lyh.app.AppController;
import com.lyh.panes.model.GetAllVersionsResult;
import com.lyh.panes.model.ScanResult;
import com.lyh.pojo.ConnectionInfo;
import com.lyh.pojo.MyAlert;
import com.lyh.utils.HBaseDML;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.RawCell;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class ScanAllController {

    private final static Logger logger = Logger.getLogger(ScanAllController.class);
    private final static int ITEMS_PER_PAGE = 20;   // 表格每页显示20行数据

    public static String currentNamespace;
    public static String currentTableName;

    @FXML
    public BorderPane scanAllPane;
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
        // 警告是否扫描全表
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("删除确认");
        alert.setHeaderText("请确认是否扫描全表");
        alert.setContentText("全表扫描可能会造成巨大的性能开销,是否继续");

        // 获取 DialogPane
        DialogPane dialogPane = alert.getDialogPane();
        // 获取确认按钮并添加事件处理器
        ButtonType confirmButtonType = ButtonType.OK;
        Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
        confirmButton.setText("继 续");

        confirmButton.setOnAction(event -> {
            currentNamespace = namespaces.getSelectionModel().getSelectedItem();
            currentTableName = tables.getSelectionModel().getSelectedItem();
            if (!currentNamespace.equals("") && !currentTableName.equals("")){
                try {
                    scan_table(currentNamespace,currentTableName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // 获取取消按钮并添加事件处理器
        ButtonType cancelButtonType = ButtonType.CANCEL;
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.setOnAction(e -> {
            alert.close();
        });
        alert.showAndWait();
    }
    public void scan_table(String namespace,String table) throws IOException {
        List<Cell[]> cells = null;
        if (ifSearchAll.isSelected()){
            logger.info("查询所有版本");
            cells = HBaseDML.scanAllVersionsOfCells(namespace, table);
            List<String> list = new ArrayList<>();  //list是用来存放查询结果的
            for (Cell[] cell : cells) {
                List<String> tmp = GetPaneController.putCellsToList(cell);
                list.addAll(tmp);
            }
            showData(new GetAllVersionsResult(list));
        }else{
            logger.info("查询最新版本");
            cells = HBaseDML.scanAllOfCells(namespace, table);
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
        scanAllPane.setCenter(tableView);
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
                removeRow(tableView);
            });

            data = FXCollections.observableArrayList(list);
            tableView.setItems(data);

            // 分页显示
            Pagination pagination = new Pagination((int) Math.ceil((double) data.size() / ITEMS_PER_PAGE), 0);
            pagination.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer param) {
                    int fromIndex = param * ITEMS_PER_PAGE;
                    int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, data.size());
                    tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
                    return tableView;
                }
            });
            BorderPane pane = new BorderPane();
            pane.setCenter(pagination);
            scanAllPane.setCenter(pane);
            tableView.refresh();
        }else{

        }
    }
    public static void removeRow(TableView<HashMap<String, SimpleStringProperty>> tableView){
        // 警告是否删除
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("删除确认");
        alert.setHeaderText("请确认是否删除所选行");
        alert.setContentText("这将会从 HBase 中移除,无法恢复!");

        // 获取 DialogPane
        DialogPane dialogPane = alert.getDialogPane();
        // 获取确认按钮并添加事件处理器
        ButtonType confirmButtonType = ButtonType.OK;
        Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
        confirmButton.setOnAction(event -> {
            alert.close();
            // 获得多选的所有行
            ObservableList<Integer> selectedIndices = tableView.getSelectionModel().getSelectedIndices();
            if (selectedIndices==null || selectedIndices.size() == 0)
                return;
            if (selectedIndices.size() == 1){
                HashMap<String, SimpleStringProperty> map = tableView.getSelectionModel().getSelectedItem();
                String rowKey = map.get("rowKey").get();
                tableView.getItems().remove(selectedIndices.get(0).intValue());
                try {
                    boolean status = HBaseDML.deleteRow(currentNamespace, currentTableName, rowKey);
                    if (status){
                        MyAlert.getSuccess("删除成功","删除成功!namespace="+currentNamespace+"tableName="+currentTableName+"rowKey="+rowKey).show();
                    }else {
                        MyAlert.getError("删除失败","删除失败!").show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else {
                boolean status = false;
                StringBuilder builder = new StringBuilder("roKey["); //存储所有被删除的rowKey
                int size = selectedIndices.size();
                for (int i = 0; i < size; i++) {
                    String rowKey = tableView.getSelectionModel().getSelectedItems().get(i).get("rowKey").getValue();
                    try {
                        builder.append(rowKey).append(",");
                        status = HBaseDML.deleteRow(currentNamespace,currentTableName,rowKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItems());
                tableView.refresh();
                if (status){
                    builder.append("]");
                    MyAlert.getSuccess("删除成功","删除成功!namespace="+currentNamespace+"tableName="+currentTableName+"rowKey="+builder.toString()).show();
                }else {
                    MyAlert.getError("删除失败","删除失败!").show();
                }
            }
        });
        // 获取取消按钮并添加事件处理器
        ButtonType cancelButtonType = ButtonType.CANCEL;
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.setOnAction(e -> {
            alert.close();
        });
        alert.showAndWait();
    }
}
