package com.lyh.panes.model;

/**
 * 用于对应 CreteNewTableController 中的每一行新列族信息
 */
public class ColumnFamilyInfo {

    private String columnFamilyName;
    private int versions;

    public ColumnFamilyInfo(){}

    public ColumnFamilyInfo(String tableName, int versions) {
        this.columnFamilyName = tableName;
        this.versions = versions;
    }

    public int getVersions() {
        return versions;
    }

    public String getColumnFamilyName() {
        return columnFamilyName;
    }

    public void setColumnFamilyName(String columnFamilyName) {
        this.columnFamilyName = columnFamilyName;
    }

    public void setVersions(int versions) {
        this.versions = versions;
    }
}
