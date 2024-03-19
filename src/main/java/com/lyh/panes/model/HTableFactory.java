package com.lyh.panes.model;

import com.lyh.utils.HBaseConnection;
import com.lyh.utils.HBaseDML;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import java.io.IOException;
import java.util.*;

/**
 * 从HBase中读取数据并通过 tableView展示
 */
public class HTableFactory implements AbstractHTableFactory {

    public final static Map<String,TableView<HashMap<String, SimpleStringProperty>>> tableViewMap = new HashMap<>();

    // 提供给 POI 输出使用 (K -> 表的全限定名 , V -> List(列的全限定名(rowKey...)->值))
    public final static Map<String,List<HashMap<String,SimpleStringProperty>>> table_data = new HashMap<>();
    public final static Map<String,Set<String>> table_cf = new HashMap<>();

    public final static Map<String,TablePojo> tables = new HashMap<>();

    /**
     * 初始化加载表格 - 需要优化
     * @param tableName 表名：全限定名
     * @return tableview
     */
    public static TableView<HashMap<String, SimpleStringProperty>> initial(String tableName) throws IOException {
        TablePojo pojo = new TablePojo();

        TableView<HashMap<String,SimpleStringProperty>> tableView = new TableView<>();
        tableView.setPrefHeight(10);
        ObservableList<HashMap<String,SimpleStringProperty>> data = null;
        Set<String> rowKeySet = new LinkedHashSet<>();   //存放 rowKey（唯一）
        Set<String> columnFamilySet = new LinkedHashSet<>();   //列族
        Set<String> columnSet = new LinkedHashSet<>();   //列名(全限定名)
        HashMap<String,HashMap<String,SimpleStringProperty>> rowKey_map = new HashMap<>();

        String namespace;
        String tname;
        if (tableName.contains(":")){
            String[] split = tableName.split(":");
            namespace = split[0];
            tname = split[1];
        }else{
            namespace = "default";
            tname = tableName;
        }
        String[] info = createTableData(namespace, tname);

        for (String s : info) {
            String[] words = s.split(":");
            String rowKey = words[0];
            String value = words[3];
            String column = words[1]+":"+words[2];
            String columnName = words[2];
            String columnFamily = words[1];

            if (!rowKey_map.containsKey(rowKey)){
//                System.out.println("!!! 当前的表为"+tableName);
                HashMap<String,SimpleStringProperty> map = new HashMap<>();
                map.put("rowKey",new SimpleStringProperty(rowKey));
                rowKey_map.put(rowKey,map);
            }

            HashMap<String, SimpleStringProperty> map = rowKey_map.get(rowKey);
//            System.out.println("！！！正在设置cell的值 "+column+" 的值将被设置为 "+value);
            map.put(column,new SimpleStringProperty(value));  // 列的全限定名对应的值
            rowKeySet.add(rowKey);
            columnFamilySet.add(columnFamily);
            pojo.addColumnFamily(columnFamily);
            pojo.addColumnToCF(columnName,columnFamily);
//            columnSet.add(words[1]+":"+words[2]);
            columnSet.add(column);
        }

        if(info.length==0) {
            return tableView;
        }

        // 1.制作表头
        // 1.1 第一列
        TableColumn<HashMap<String,SimpleStringProperty>,String> rowKeyCol = new TableColumn<>("rowKey");
        rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
        rowKeyCol.setMinWidth(100);
        tableView.getColumns().add(rowKeyCol);
        // 1.2 添加列族
        List<TableColumn<HashMap<String,SimpleStringProperty>,String>> columnFamily_list = new ArrayList<>();

        for (String columnFamilyName : columnFamilySet) {
            TableColumn<HashMap<String,SimpleStringProperty>,String> columnFamily = new TableColumn<>(columnFamilyName);
            columnFamily_list.add(columnFamily);
//            columnFamily.setCellValueFactory(new MapValueFactory(columnFamilyName));
            tableView.getColumns().add(columnFamily);
        }
        // 1.3 添加列族对应的列
        List<TableColumn<HashMap<String,SimpleStringProperty>,String>> column_list = new ArrayList<>(); //全限定名
        for (String c : columnSet) {
            TableColumn<HashMap<String,SimpleStringProperty>,String> column = new TableColumn<>(c);
//            column.setCellValueFactory(new MapValueFactory(c)); //全限定名 绑定属性
            column_list.add(column);
//            System.out.println("列名: "+c);
        }
        // 1.4 把列放到指定列族当中 千千万万别动！！！！！！耗时14h完成
        for (TableColumn<HashMap<String,SimpleStringProperty>, String> columnF : columnFamily_list) {
//            System.out.println("正在判断 "+columnF.getText());
            for (TableColumn<HashMap<String,SimpleStringProperty>, String> column : column_list) {
                if (column.getText().contains(columnF.getText())){
                    TableColumn<HashMap<String,SimpleStringProperty>,String> col = new TableColumn<>(getTableName(column.getText()));
                    col.setCellValueFactory(new MapValueFactory(column.getText()));
                    // todo 设置单元格可以选中
                    col.setCellFactory(TextFieldTableCell.forTableColumn());
                    columnF.getColumns().add(col);
//                    System.out.println("列族+列名 = "+column.getText());
                }
            }
        }
        tableView.setTooltip(new Tooltip("单元格内容可复制,但不支持修改~"));
        // todo 设置单元格可编辑
        tableView.setEditable(true);

        // 数据集 遍历rowKey_Set 通过 rowKey 得到所有的 HashMap映射到表格的每一行
        List<HashMap<String,SimpleStringProperty>> list = new ArrayList<>(rowKeySet.size());
        for (String s : rowKeySet) {
            list.add(rowKey_map.get(s));
        }

        // TODO 提供给 POI 输出使用
        table_data.put(tableName,list);
        table_cf.put(tableName,columnFamilySet);

        pojo.setDataSource(list);


        data = FXCollections.observableArrayList(
                list
        );
        tableView.setItems(data);
        tables.put(tableName,pojo);
        return tableView;
    }

    /**
     * 通过 tableviewMap<K,V> K:命名空间 V:命名空间对于的表名的tableview对象
     * @param namespace 命名空间
     * @param tableName 表名
     * @return 表对应的 tableview对象
     * @throws IOException
     */
    @Override
    public TableView<HashMap<String, SimpleStringProperty>> createTableView(String namespace,String tableName) throws IOException {

        String tName = namespace+":"+tableName;
        if (namespace.equals("default") && tableViewMap.containsKey(tName)){
//            System.out.println("当前点击的是 default 命名空间下的表");
            return tableViewMap.get(tableName);
        }else if (namespace.equals("default")){
            return initial(namespace+":"+tableName);  //没有该表就返回一个空表
        }

        if(tableViewMap.containsKey(tName)){
//            System.out.println("直接返回"+tName);
            return tableViewMap.get(tName);
        }else {
            // 没有该表 可能是新创建的表 我们需要重新查询
            tableViewMap.put(tName,initial(tName));
//            return initial(namespace+":"+tableName);  //没有该表就返回一个空表
            return tableViewMap.get(tName);
        }
    }

    public static String[] createTableData(String namespace, String tableName) throws IOException {
//        // 1. 创建连接配置参数
//        Configuration conf = new Configuration();
//        // 2. 设置Zookeeper集群参数
//        conf.set("hbase.zookeeper.quorum", "hadoop102,hadoop103,hadoop104");    //后期修改一下
//        Connection connection = ConnectionFactory.createConnection(conf);

        // TODO 只有这里使用了这个方法 scanAll 后期优化
        return HBaseDML.scanAll(namespace, tableName);
    }

    /**
     * 返回全限定名中的表名
     * @param str 全限定名
     * @return 表名
     */
    public static String getTableName(String str){
        String[] split = str.split(":");
        return split[1];
    }


}
