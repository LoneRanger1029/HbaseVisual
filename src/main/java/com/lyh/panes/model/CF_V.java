package com.lyh.panes.model;

import org.jcodings.util.Hash;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;

/**
 * 创建表格时 列族和版本号
 */
public class CF_V {
    private String columnFamily;
    private String version;

    public CF_V(String columnFamily, String version) {
        this.columnFamily = columnFamily;
        this.version = version;
    }

    public String getColumnFamily() {
        return columnFamily;
    }

    public String getVersion() {
        return version;
    }

    public static void main(String[] args) {
        Stack<CF_V> stack = new Stack<>();
        CF_V cf_v = new CF_V("1","1");
        stack.push(cf_v);
        CF_V cf = new CF_V("1","1");
        System.out.println(stack.contains(cf)); //true
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CF_V cf_v = (CF_V) o;
        return Objects.equals(columnFamily, cf_v.columnFamily) && Objects.equals(version, cf_v.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(columnFamily, version);
    }
}
