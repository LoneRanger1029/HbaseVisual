package com.lyh.utils;

import javafx.embed.swing.SwingNode;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.IOException;

/**
 * SQL 转换器
 */
public class HBaseSQLParser {
    private static DefaultTableModel model;
    private static String[] tableTitle;

    static {
        try {
            org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper.launchBeautyEyeLNF();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static SwingNode list_namespaces() throws IOException {
        tableTitle = new String[]{"namespace"};
//        String[] namespace = HBaseDDL.getNamespace();
        String[] namespace = new String[]{"lyh","nosql","bigdata","student"};
        String[][] tableData = new String[namespace.length][1];
        tableData[0][0]=namespace[0];
        tableData[1][0]=namespace[1];
        tableData[2][0]=namespace[2];
        tableData[3][0]=namespace[3];

        model = new DefaultTableModel(tableData,tableTitle);
        JTable table = new JTable(model);
        table.setRowHeight(20);
        JScrollPane jScrollPane =new JScrollPane(table);
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(jScrollPane);
        return swingNode;
    }

}