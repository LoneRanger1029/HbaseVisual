package com.lyh.panes.controller;

import com.lyh.pojo.MyAlert;
import com.lyh.pojo.MyStage;
import com.lyh.utils.ExcelToHBaseDataFormat;
import com.lyh.utils.HBaseDML;
import com.lyh.utils.POIExcel2003Utils;
import com.lyh.utils.POIExcel2007Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import org.apache.log4j.Logger;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.joda.time.DateTime;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

import static com.lyh.panes.model.HTableFactory.getTableName;

public class ExcelPreviewController {

    public static String CURRENT_FILEPATH;
    public static ExcelToHBaseDataFormat excel;
    public static POIExcel2003Utils excel_03;
    public static POIExcel2007Utils excel_07;
    public final static int PREVIEW_SIZE = 100; // 预览时只显示100页
    public static int COUNT = 0;    // excel数据总行数 = list.size
    // todo 这个 list 必须为静态变量 因为import_into_hbase 这个方法是在一个新的对象中使用的 list是在preview方法中初始化的 只有静态变量才能被多个实例共享
    public static List<HashMap<String,SimpleStringProperty>> list; // list(key: )
    private final static Logger logger = Logger.getLogger(ExcelPreviewController.class);
    @FXML
    public Label fileName;
    @FXML
    public TableView<HashMap<String, SimpleStringProperty>> tableView;

    public Label getFileName() {
        return fileName;
    }

    @FXML
    public void preview() throws IOException {
        logger.info("预览数据");
        String filePath = fileName.getText();
        if (filePath == null || filePath.equals("")){
            return;
        }
        InputStream file = new BufferedInputStream(new FileInputStream(filePath));
        FileMagic fileMagic = FileMagic.valueOf(file);
        switch (fileMagic){
            case OLE2:
                excel_03_process(filePath);
                break;
            case OOXML:
                excel_07_process(filePath);
                break;
        }

    }
    public void excel_07_process(String filePath) throws IOException {
        excel_07 = new POIExcel2007Utils();
        logger.info("用户输入的是 07 版本的excel文件");

        excel = excel_07;

        // 获得全限定名
        Set<String> columnSet = excel_07.readTitle(filePath);// 读取表头 初始化列族集合
        // 1.制作表头
        // 1.1 第一列-rowKey
        TableColumn<HashMap<String,SimpleStringProperty>,String> rowKeyCol = new TableColumn<>("rowKey");
        rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
        rowKeyCol.setMinWidth(100);
        tableView.getColumns().add(rowKeyCol);
        // 1.2 添加列族
        List<TableColumn<HashMap<String,SimpleStringProperty>,String>> columnFamily_list = new ArrayList<>();

        for (String columnFamilyName : excel_07.columnFamilySet) {
            TableColumn<HashMap<String,SimpleStringProperty>,String> columnFamily = new TableColumn<>(columnFamilyName);
            columnFamily_list.add(columnFamily);
//            columnFamily.setCellValueFactory(new MapValueFactory(columnFamilyName));
            tableView.getColumns().add(columnFamily);
        }
        // 1.3 添加列族对应的列
        List<TableColumn<HashMap<String,SimpleStringProperty>,String>> column_list = new ArrayList<>(); //全限定名
        for (String c : columnSet) {
            TableColumn<HashMap<String,SimpleStringProperty>,String> column = new TableColumn<>(c);
            column_list.add(column);
        }
        // 1.4 把列放到指定列族当中 千千万万别动！！！！！！耗时14h完成
        for (TableColumn<HashMap<String,SimpleStringProperty>, String> columnF : columnFamily_list) {
            for (TableColumn<HashMap<String,SimpleStringProperty>, String> column : column_list) {
                if (column.getText().contains(columnF.getText())){
                    TableColumn<HashMap<String,SimpleStringProperty>,String> col = new TableColumn<>(getTableName(column.getText()));
                    col.setCellValueFactory(new MapValueFactory(column.getText()));
                    columnF.getColumns().add(col);
//                    System.out.println("列族+列名 = "+column.getText());
                }
            }
        }

        list = excel_07.preview(filePath,PREVIEW_SIZE);
        ObservableList<HashMap<String,SimpleStringProperty>> data = FXCollections.observableArrayList(list);
        tableView.setItems(data);
    }

    public void excel_03_process(String filePath) throws IOException {
        excel_03 = new POIExcel2003Utils();
        logger.info("用户输入的是 03 版本的excel文件");

        excel = excel_03;

        // 获得全限定名
        Set<String> columnSet = excel_03.readTitle(filePath);// 读取表头 初始化列族集合
        // 1.制作表头
        // 1.1 第一列-rowKey
        TableColumn<HashMap<String,SimpleStringProperty>,String> rowKeyCol = new TableColumn<>("rowKey");
        rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
        rowKeyCol.setMinWidth(100);
        tableView.getColumns().add(rowKeyCol);
        // 1.2 添加列族
        List<TableColumn<HashMap<String,SimpleStringProperty>,String>> columnFamily_list = new ArrayList<>();

        for (String columnFamilyName : excel_03.columnFamilySet) {
            TableColumn<HashMap<String,SimpleStringProperty>,String> columnFamily = new TableColumn<>(columnFamilyName);
            columnFamily_list.add(columnFamily);
            //            columnFamily.setCellValueFactory(new MapValueFactory(columnFamilyName));
            tableView.getColumns().add(columnFamily);
        }
        // 1.3 添加列族对应的列
        List<TableColumn<HashMap<String,SimpleStringProperty>,String>> column_list = new ArrayList<>(); //全限定名
        for (String c : columnSet) {
            TableColumn<HashMap<String,SimpleStringProperty>,String> column = new TableColumn<>(c);
            column_list.add(column);
        }
        // 1.4 把列放到指定列族当中 千千万万别动！！！！！！耗时14h完成
        for (TableColumn<HashMap<String,SimpleStringProperty>, String> columnF : columnFamily_list) {
            for (TableColumn<HashMap<String,SimpleStringProperty>, String> column : column_list) {
                if (column.getText().contains(columnF.getText())){
                    TableColumn<HashMap<String,SimpleStringProperty>,String> col = new TableColumn<>(getTableName(column.getText()));
                    col.setCellValueFactory(new MapValueFactory(column.getText()));
                    columnF.getColumns().add(col);
                }
            }
        }

        list = excel_03.preview(filePath,PREVIEW_SIZE);
        ObservableList<HashMap<String,SimpleStringProperty>> data = FXCollections.observableArrayList(list);
        tableView.setItems(data);
    }


    public void import_into_hbase(String namespace, String table) throws IOException {

        logger.info("待导入的excel文件路径: "+CURRENT_FILEPATH);
        List<HashMap<String, SimpleStringProperty>> all_list = excel.preview(CURRENT_FILEPATH, Integer.MAX_VALUE-2);
        long start = System.currentTimeMillis();
        // 多线程 500+行/s 10w/3min
        all_list.parallelStream().forEach(new Consumer<HashMap<String, SimpleStringProperty>>() {
            @Override
            public void accept(HashMap<String, SimpleStringProperty> hashMap) {
                String rowKey = hashMap.get("rowKey").get();
                for (Map.Entry<String, SimpleStringProperty> entry : hashMap.entrySet()) {
                    if (!entry.getKey().equals("rowKey")){
                        String[] words = entry.getKey().split(":");
                        try {
                            HBaseDML.putCell(namespace,table,rowKey,words[0],words[1],entry.getValue().get());
                        } catch (IOException e) {
                            logger.error("数据插入失败 namespace="+namespace+",tableName="+table+",rowKey="+rowKey+",columnFamily="+words[0]+",column="+words[1]+",value="+entry.getValue().get());
                        }
                    }
                }
            }
        });
        long end = System.currentTimeMillis();
        String time = new DateTime(end - start).toString("ss:SSS");
        logger.info("用时"+time+"s");
        logger.info("插入"+all_list.size()+"条数据,共用时"+time+"s");
        MyAlert.getSuccess("成功","插入成功,插入"+all_list.size()+"条数据,共用时"+time+"s").show();
        // 绑定属性监听 当前导入的数量
//        DoubleProperty progress = new SimpleDoubleProperty(0);
//        ProgressBar progressBar = new ProgressBar(0.0);
//        progressBar.progressProperty().bind(progress);
//        progressBar.setPrefHeight(200);
//        progressBar.setPrefWidth(400);
//        Stage stage = new Stage();
//        stage.setHeight(300);
//        stage.setWidth(500);
//        stage.setScene(new Scene(progressBar));
//        stage.show();
//        progressBar.progressProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                if (newValue.doubleValue() >= 100)
//                    stage.close();
//            }
//        });
//        Thread thread = new Thread(() -> {
//            long start = System.currentTimeMillis();
//            for (int i = 0; i < all_list.size(); i++) {
//                System.out.println("正在读取第"+(i+1)+"行,共"+all_list.size()+"行");
//                HashMap<String, SimpleStringProperty> hashMap = all_list.get(i);
//                String rowKey = hashMap.get("rowKey").get();
//                for (Map.Entry<String, SimpleStringProperty> entry : hashMap.entrySet()) {
//                    if (!entry.getKey().equals("rowKey")){
//                        String[] words = entry.getKey().split(":");
//                        try {
//                            HBaseDML.putCell(namespace,table,rowKey,words[0],words[1],entry.getValue().get());
//                        } catch (IOException e) {
//                            logger.error("数据插入失败 namespace="+namespace+",tableName="+table+",rowKey="+rowKey+",columnFamily="+words[0]+",column="+words[1]+",value="+entry.getValue().get());
//                        }
//                        logger.info("数据插入成功 namespace="+namespace+",tableName="+table+",rowKey="+rowKey+",columnFamily="+words[0]+",column="+words[1]+",value="+entry.getValue().get());
//                    }
//                }
//                progress.set(((i+1)*1.0)/ all_list.size());
//            }
//            long end = System.currentTimeMillis();
//            String time = new DateTime(end - start).toString("ss:SSS");
//            logger.info("用时"+time+"s");
//            System.out.println("插入"+all_list.size()+"条数据,共用时"+time+"s");
//        });
//        thread.start();
//        if (progress.get()>=1.0){
//            thread.interrupt();
//        }

        // todo
//        logger.info("数据大小: "+list.size()+"行");
//        for (int i = 0; i < list.size(); i++) {
//            System.out.println("正在读取第"+(i+1)+"行");
//            HashMap<String, SimpleStringProperty> hashMap = list.get(i);
//            String rowKey = hashMap.get("rowKey").get();
//            for (Map.Entry<String, SimpleStringProperty> entry : hashMap.entrySet()) {
//                if (!entry.getKey().equals("rowKey")){
//                    String[] words = entry.getKey().split(":");
//                    try {
//                        HBaseDML.putCell(namespace,table,rowKey,words[0],words[1],entry.getValue().get());
//                    } catch (IOException e) {
//                        logger.error("数据插入失败 namespace="+namespace+",tableName="+table+",rowKey="+rowKey+",columnFamily="+words[0]+",column="+words[1]+",value="+entry.getValue().get());
//                    }
//                    logger.info("数据插入成功 namespace="+namespace+",tableName="+table+",rowKey="+rowKey+",columnFamily="+words[0]+",column="+words[1]+",value="+entry.getValue().get());
//                }
//            }
//            progress.set(((1.0*i)+1)/list.size());
//            System.out.println("当前进度"+progress.get());
//        }
//        for (HashMap<String, SimpleStringProperty> hashMap : list) {
//            String rowKey = hashMap.get("rowKey").get();
//            for (Map.Entry<String, SimpleStringProperty> entry : hashMap.entrySet()) {
//                if (!entry.getKey().equals("rowKey")){
//                    String[] words = entry.getKey().split(":");
//                    try {
//                        HBaseDML.putCell(namespace,table,rowKey,words[0],words[1],entry.getValue().get());
//                    } catch (IOException e) {
//                        logger.error("数据插入失败 namespace="+namespace+",tableName="+table+",rowKey="+rowKey+",columnFamily="+words[0]+",column="+words[1]+",value="+entry.getValue().get());
//                    }
//                    logger.info("数据插入成功 namespace="+namespace+",tableName="+table+",rowKey="+rowKey+",columnFamily="+words[0]+",column="+words[1]+",value="+entry.getValue().get());
//                }
//            }
//        }

    }

    /**
     * 打开导入面板 选择命名空间和要导入的表
     */
    @FXML
    public void openImportPane() throws IOException {

        MyStage myStage = new MyStage();
        myStage.initializeStage("导入数据到HBase",false,"icon/Hbase.png","/fxml/ImportPane.fxml");
        myStage.show();
    }
}
