package com.lyh.panes.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 存放表名数据
 * 格式: (Map(String namespace,List<String>(table1,table2...)))
 */
public class HBaseTableModel{

    public static Map<String,List<String>> model = new HashMap<>();

    public static void put(String namespace, List<String> tableNames) {
        model.put(namespace,tableNames);
    }

    public static List<String> getList(String namespace){
        return model.get(namespace);
    }

}
