package com.lyh.panes.model;

import javafx.beans.property.SimpleStringProperty;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Get 搜索模块中 选中所有版本的效果
 */
public class GetAllVersionsResult {

    private final static Logger logger = Logger.getLogger(GetAllVersionsResult.class);

    private Set<String> rowKeySet = new HashSet<>();
    private List<String> content;  // 查询结果
    private Set<String> columnFamilies; // 列族集合
    private Map<String,Set<String>> columnFMap; // K:列族->V:列集合

    public HashMap<String,HashMap<String, List<SimpleStringProperty>>> valueMap = new HashMap<>();

    /**
     * valueMap:
     *  key:    rowKey
     *  value:  HashMap<K,V>:
     *              K:  "columnFamily:columnName"
     *              V:  list<version1,version2>
     */

    public GetAllVersionsResult(List<String> list){
        if (list==null || list.size()==0){
            logger.error("list 异常");
            return;
        }
        this.content = list;
        columnFamilies = new HashSet<>();
        columnFMap = new HashMap<>();
        for (String data : content) {
            HashMap<String,List<SimpleStringProperty>> columnMap; // K:列全限定名->所有value集合

            String[] datas = data.split("-");
            String rowKey = datas[0];
            String columnFamily = datas[1];
            String columnName = datas[2];
            String value = datas[3];
            rowKeySet.add(rowKey);
            columnFamilies.add(columnFamily);
            if (!columnFMap.containsKey(columnFamily)){
                columnFMap.put(columnFamily,new HashSet<>());
            }
            columnFMap.get(columnFamily).add(columnName);
            String qualifyName = columnFamily+":"+columnName;
            if (!valueMap.containsKey(rowKey)){
                columnMap = new HashMap<>();
                // rowKey 这个列特殊 它只有一个版本 所以它的ArrayList只有一个元素
                ArrayList<SimpleStringProperty> rowKeyList = new ArrayList<>(1);
                rowKeyList.add(new SimpleStringProperty("1"));
                columnMap.put("rowKey",rowKeyList);
            }else {
                columnMap = valueMap.get(rowKey);
            }
            if (!columnMap.containsKey(qualifyName)){
                ArrayList<SimpleStringProperty> colValueList = new ArrayList<>(1);
                columnMap.put(qualifyName,colValueList);
            }
            columnMap.get(qualifyName).add(new SimpleStringProperty(value));
            valueMap.put(rowKey,columnMap);
        }
    }

    public Set<String> getRowKeySet() {
        return rowKeySet;
    }

    public List<String> getContent() {
        return content;
    }

    public Set<String> getColumnFamilies() {
        return columnFamilies;
    }

    public Map<String, Set<String>> getColumnFMap() {
        return columnFMap;
    }

    public HashMap<String, HashMap<String, List<SimpleStringProperty>>> getValueMap() {
        return valueMap;
    }

    public Set<String> getColumns(String columnFamily){
        return columnFMap.get(columnFamily);
    }
}
