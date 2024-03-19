package com.lyh.utils;

import javafx.beans.property.SimpleStringProperty;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public interface ExcelToHBaseDataFormat {

    // 获得列族名
    Set<String> readTitle(String filePath) throws IOException;

    // 读取excel的数据(key：列族:列名 value:单元格的值)
    List<HashMap<String, SimpleStringProperty>> preview(String filePath, int size) throws IOException;

    void writeIntoHBase(String filePath,String namespace,String tableName) throws IOException;

}
