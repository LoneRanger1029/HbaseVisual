package com.lyh.pojo;

import com.lyh.app.AppController;
import com.lyh.app.Main;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.IOException;

public class MyTreeContextMenu<S> extends ContextMenu {

    /**
     * 单例模式 - 节约内存!!!! 不会出现子菜单不断叠加
     */
    private static final ContextMenu contextMenu1 = new ContextMenu();
    private static final ContextMenu contextMenu2 = new ContextMenu();
    private static final ContextMenu contextMenu3 = new ContextMenu();
    private static MenuItem closeConnect = null;
    private static MenuItem createNamespace = null;
    private static MenuItem deleteNamespace = null;
    private static MenuItem createTable = null;
    private static MenuItem openTable = null;
    private static MenuItem deleteTable = null;
    private static MenuItem flushTable  = null;

    static{
        closeConnect = new MenuItem("关闭连接");
//        connectAttr = new MenuItem("连接属性");
        contextMenu1.getItems().addAll(closeConnect);

        createNamespace = new MenuItem("新建命名空间");
        deleteNamespace = new MenuItem("删除命名空间");
        createTable = new MenuItem("新建表");
        contextMenu2.getItems().addAll(createNamespace,deleteNamespace,createTable);

        openTable = new MenuItem("打开表");
        deleteTable = new MenuItem("删除表");
        flushTable = new MenuItem("刷新表");
        contextMenu3.getItems().addAll(openTable,deleteTable,flushTable);


    }

    private MyTreeContextMenu(){
    }

    public static ContextMenu getContextMenu(int grade) {
        if (grade==1){
            return contextMenu1;
        }else if(grade==2){
            return contextMenu2;
        }else if (grade==3){
            return contextMenu3;
        }
        return null;
    }

    public static ContextMenu getContextMenu1() {
        return contextMenu1;
    }

    public static ContextMenu getContextMenu2() {
        return contextMenu2;
    }

    public static ContextMenu getContextMenu3() {
        return contextMenu3;
    }

    public static MenuItem getCloseConnect() {
        return closeConnect;
    }


    public static MenuItem getCreateNamespace() {
        return createNamespace;
    }

    public static MenuItem getDeleteNamespace() {
        return deleteNamespace;
    }


    public static MenuItem getCreateTable() {
        return createTable;
    }

    public static MenuItem getOpenTable() {
        return openTable;
    }

    public static MenuItem getDeleteTable() {
        return deleteTable;
    }

    public static MenuItem getFlushTable() {
        return flushTable;
    }

}
