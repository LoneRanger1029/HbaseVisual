package com.lyh.service;

import javafx.scene.control.ComboBox;

import java.util.List;

public interface ConnectionService {

    // 删除命名空间
    void deleteNamespace(String namespace);

    // 添加命名空间
    void addNamespace(String namespace);

    // 添加表格映射到namespace
    void addTable(String namespace,String tableName);

    // 添加表格到表格集合
    void setTables(String namespace,List<String> tables);

}
