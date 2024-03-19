package com.lyh.panes.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Tab;

import java.util.*;

/**
 * HBase 表的对象
 */
public class TablePojo {

    private Set<String> columnFamilies; // 列族集合
    private Map<String,Set<String>> columnFamily_Map;   // 列族映射列
    private List<HashMap<String, SimpleStringProperty>> dataSource;

    public TablePojo(){
        // 这里需要使用 linkedHashset 防止因为自动排序而导致列或列族乱序 从而使得数据和列对应错误
        columnFamilies = new LinkedHashSet<>();
        columnFamily_Map = new LinkedHashMap<>();
    }

    public void setColumnFamilies(Set<String> columnFamilies) {
        this.columnFamilies = columnFamilies;
    }

    public void setColumnFamily_Map(Map<String, Set<String>> columnFamily_Map) {
        this.columnFamily_Map = columnFamily_Map;
    }

    public void setDataSource(List<HashMap<String, SimpleStringProperty>> dataSource) {
        this.dataSource = dataSource;
    }

    public TablePojo(Set<String> columnFamilies, Map<String, Set<String>> columnFamily_Map, List<HashMap<String, SimpleStringProperty>> dataSource) {
        this.columnFamilies = columnFamilies;
        this.columnFamily_Map = columnFamily_Map;
        this.dataSource = dataSource;
    }

    public Set<String> getColumnFamilies() {
        return columnFamilies;
    }

    public void addColumnFamily(String columnFamily){
        columnFamilies.add(columnFamily);
    }

    public void addColumnToCF(String column,String columnFamily){
        if (!columnFamily_Map.containsKey(columnFamily)){
            Set<String> set = new HashSet<>();
            set.add(column);
            columnFamily_Map.put(columnFamily,set);
        }else {
            columnFamily_Map.get(columnFamily).add(column);
        }
    }

    public Map<String, Set<String>> getColumnFamily_Map() {
        return columnFamily_Map;
    }

    public List<HashMap<String, SimpleStringProperty>> getDataSource() {
        return dataSource;
    }

    public Map<String,Integer> getColumnFamilySize(){
        Map<String,Integer> res = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : columnFamily_Map.entrySet()) {
            if (!entry.getKey().equals("rowKey"))
                res.put(entry.getKey(),entry.getValue().size());
        }
        return res;
    }
}
