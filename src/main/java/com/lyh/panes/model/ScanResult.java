package com.lyh.panes.model;

import javafx.beans.property.SimpleStringProperty;
import org.apache.log4j.Logger;

import java.util.*;

public class ScanResult {
    private final static Logger logger = Logger.getLogger(ScanResult.class);
    Set<String> rowKeySet = new HashSet<>();   //存放 rowKey（唯一）
    public boolean isAllVersions;    // 是否为多版本
    private List<String> content;  // 查询结果
    private Set<String> columnFamilies;
    private Map<String,Set<String>> columnsMap;// 列族->列名
    public HashMap<String,HashMap<String, SimpleStringProperty>> valueMap = new HashMap<>();

    public ScanResult(boolean isAllVersions,List<String> list){
        if (list==null || list.size()==0){
            logger.error("list 异常");
            return;
        }
        this.isAllVersions = isAllVersions;
        this.content = list;
        columnFamilies = new HashSet<>();
        columnsMap = new HashMap<>();
        for (String s : content) {
            HashMap<String, SimpleStringProperty> colMap;
            String[] words = s.split("-");
            String columnFamily = words[1];
            String columnName = words[2];
            String value = words[3];
            String rowKey = words[0];
            if (valueMap.containsKey(rowKey)){
                colMap = valueMap.get(rowKey);
            }else {
                colMap = new HashMap<>();
                colMap.put("rowKey",new SimpleStringProperty(rowKey));
            }
            rowKeySet.add(rowKey);
            columnFamilies.add(columnFamily);
            if (!columnsMap.containsKey(columnFamily)){
                columnsMap.put(columnFamily,new HashSet<>());
            }
            columnsMap.get(columnFamily).add(columnName);

            if (!isAllVersions){
                colMap.put(columnFamily+":"+columnName,new SimpleStringProperty(value));
                valueMap.put(rowKey,colMap);
            }
        }
    }

    public Set<String> getColumnFamilies() {
        if (columnFamilies == null || columnFamilies.size() == 0){
            return null;
        }
        return columnFamilies;
    }

    public Set<String> getColumns(String columnFamily){
        return columnsMap.get(columnFamily);
    }

    public Set<String> getRowKeySet() {
        return rowKeySet;
    }
}
