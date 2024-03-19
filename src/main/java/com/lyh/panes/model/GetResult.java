package com.lyh.panes.model;

import javafx.beans.property.SimpleStringProperty;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * get查询结果
 *  get-最新版本: 1001-info-age-20	1001-info-name-zs
 *  get-所有版本: 1001-info-age-20	1001-info-name-zs(新的)	1001-info-name-lidaxi(旧的)
 */
public class GetResult {
    private final static Logger logger = Logger.getLogger(GetResult.class);

    public String rowKey;
    public boolean isAllVersions;    // 是否为多版本
    private List<String> content;  // 查询结果
    private Set<String> columnFamilies;
    private Map<String,Set<String>> columnsMap; //k: 列族 v: 列集合
    public HashMap<String, SimpleStringProperty> valueMap = new HashMap<>();

    public GetResult(boolean isAllVersions,List<String> list){
        if (list==null || list.size()==0){
            logger.error("list 异常");
            return;
        }
        this.isAllVersions = isAllVersions;
        this.content = list;
        this.rowKey = list.get(0).split("-")[0];
        columnFamilies = new HashSet<>();
        columnsMap = new HashMap<>();
        valueMap.put("rowKey",new SimpleStringProperty(rowKey));
        for (String s : content) {
            String[] words = s.split("-");
            String columnFamily = words[1];
            String columnName = words[2];
            String value = words[3];
            columnFamilies.add(columnFamily);
            if (!columnsMap.containsKey(columnFamily)){
                columnsMap.put(columnFamily,new HashSet<>());
            }
            Set<String> tables = columnsMap.get(columnFamily);
            tables.add(columnName);
            if (!isAllVersions){
                valueMap.put(columnFamily+":"+columnName,new SimpleStringProperty(value));
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

}
