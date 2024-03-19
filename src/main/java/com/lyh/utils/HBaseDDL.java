package com.lyh.utils;

import com.lyh.pojo.ConnectionInfo;
import com.lyh.pojo.MyAlert;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class HBaseDDL {

    public static Connection connection = null;
    private final static Logger logger = Logger.getLogger(HBaseDDL.class);

    public static void main(String[] args) throws IOException {

//        System.out.println(isTableExists("hbase","test"));
//        String[] namespace = getNamespace();
//        StringUtils.printArray(namespace);
//        HBaseConnection.closeConnection();

//        String[] tablesNames = getAllTablesNames();
//        StringUtils.printArray(tablesNames);
//        HBaseConnection.closeConnection();


        //测试创建命名空间
//        createNamespace("lyh");

        //测试是否表格存在
//        boolean b = isTableExists("bigdata", "student");
//        System.out.println(b);
//
//        //测试创建表格
//        createTable("bigdata","teacher","info","msg");
//
//        //测试修改表格
//        modifyTable("bigdata","teacher","info",3);
//
//        //测试删除表格
//        deleteTable("bigdata","teacher");
//
//        //记得关闭HBase连接
//        HBaseConnection.closeConnection();
    }

    /**
     *
     */
    public static String[] getNamespace() throws IOException {
        Admin admin = null;
        try{
            admin = connection.getAdmin();
            // 获取所有的命名空间
            if (admin!=null) {
                try {
                    return admin.listNamespaces();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                logger.warn("获取命名空间失败!");
                return null;
            }
        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (admin!=null)
                admin.close();
        }
        return null;
    }

    public static String[] getNamespace(String hosts) throws IOException {
        // todo 用的时候在这里再引用,要是一开始就设置为全局变量,那么一开始的引用就是一个null
        connection = HBaseConnection.connection;
        Admin admin = connection.getAdmin();
        // 获取所有的命名空间
        return admin.listNamespaces();
    }

    public static String[] getTablesNamesByNameSpace(String nameSpace) throws IOException {
        // todo 用的时候在这里再引用,要是一开始就设置为全局变量,那么一开始的引用就是一个null
        connection = HBaseConnection.connection;
        Admin admin = connection.getAdmin();
        // 获取所有的命名空间
        TableName[] tableNames = admin.listTableNamesByNamespace(nameSpace);
        int size = tableNames.length;
        String[] tables = new String[size];
        for (int i = 0; i < size; i++) {
            tables[i] = tableNames[i].getQualifierAsString();   //只要表名不要命名空间
        }
        return tables;
    }

    public static String[] getAllTablesNames(String hosts) throws IOException {

        Admin admin = connection.getAdmin();
        // 获取所有的命名空间
        TableName[] tableNames = admin.listTableNames();
        int size = tableNames.length;
        String[] tables = new String[size];
        for (int i = 0; i < size; i++) {
            tables[i] = tableNames[i].getNameAsString();   //只要表名不要命名空间
        }
        return tables;
    }


    public static String[] getAllTablesNames() throws IOException {
        Admin admin = connection.getAdmin();
        // 获取所有的命名空间
        TableName[] tableNames = admin.listTableNames();
        int size = tableNames.length;
        String[] tables = new String[size];
        for (int i = 0; i < size; i++) {
            tables[i] = tableNames[i].getNameAsString();   //只要表名不要命名空间
        }
        return tables;
    }




    /**
     * 创建命名空间
     * @param namespace 命名空间的名称
     */
    public static boolean createNamespace(String namespace) throws IOException {

        // 1. 获取admin
        //admin是轻量级的 并且不是线程安全的 不推荐池化或者缓存这个连接
        //也就是说 用的时候再去获取 不用就把它关闭掉
        Admin admin = connection.getAdmin();

        // 2. 调用方法创建 namespace
        // 代码比shell更加底层 所以shell能实现的功能代码 一定也可以
        // 所以代码实现时 需要更完整的命名空间描述

        // 2.1 获取一个命名空间的建造者 => 设计师
        NamespaceDescriptor.Builder builder = NamespaceDescriptor.create(namespace);

        // 2.2 给命名空间添加属性
        // 给namespace添加键值对属性其实并没有什么意义 只是给人注释一样
        builder.addConfiguration("user","lyh");

        // 2.3 使用builder构造出namespace对象
        // 创建命名空间造成的问题 属于方法本身的问题 不应该抛出 也不影响别的代码的执行
        try {
            admin.createNamespace(builder.build());
        } catch (IOException e) {
            logger.error("该命名空间已经存在!");
            e.printStackTrace();
            return false;
        }

        // 3. 关闭资源
        admin.close();
        return true;
    }

    public static boolean addColumnFamily(String namespace,String table,String columnFamily) throws IOException {

        Admin admin = connection.getAdmin();

        TableName tableName = TableName.valueOf(namespace, table);
        ColumnFamilyDescriptor newColumnFamily = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnFamily)).build();
        admin.addColumnFamily(tableName,newColumnFamily);

        admin.close();
        logger.info("增加一个新的列族: "+newColumnFamily.getNameAsString());
        admin.close();
        return true;
    }

    /**
     * 判断表格是否存在
     * @param namespace 命名空间
     * @param tableName 表名
     * @return true-存在 false-不存在
     */
    public static boolean isTableExists(String namespace,String tableName){

        Admin admin = null;

        // 2. 使用方法判断表格是否存在
        boolean b = false;
        try {
            admin = connection.getAdmin();
            b = admin.tableExists(TableName.valueOf(namespace, tableName));
            admin.close();
        } catch (IOException e) {
            logger.error("表格已存在",e);
        }
        // 4.返回结果
        return b;
    }

    public static boolean createTable(String namespace,String tableName,TableDescriptorBuilder builder) throws IOException {
        // 判断表格是否已经存在
        if (isTableExists(namespace,tableName)){
            logger.error("表格已经存在,namespace="+namespace+",tableName="+tableName);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("创建失败");
            alert.setContentText("表格已存在");
            alert.show();
            return false;
        }
        // 1. 获取admin
        Admin admin = connection.getAdmin();

        // 2.3 创建表格描述
        try {
            admin.createTable(builder.build());
        } catch (IOException e) {
//            logger.error("创建失败",e);
            MyAlert.getError("创建失败",e.getMessage()).show();
            return false;
        }

        // 2.6 关闭admin
        admin.close();
        return true;
    }

    public static boolean deleteNamespace(String namespace){
        Admin admin = null;
        try {
            admin = connection.getAdmin();
            admin.deleteNamespace(namespace);
            admin.close();
        } catch (IOException e) {
            MyAlert.getError("删除失败","请重试").show();
            return false;
        }
        MyAlert.getSuccess("删除成功","已将命名空间"+namespace+"从HBase中删除").show();
        return true;
    }

    /**
     * 修改表格中一个列族的版本
     * @param namespace 命名空间
     * @param tableName 表名
     * @param columnFamily 列族
     * @param version 维护的版本
     */
    public static void modifyTable(String namespace,String tableName,String columnFamily,int version) throws IOException {

        // 判断表格是否存在
        if (!isTableExists(namespace,tableName)){
            logger.error("表格不存在");
            return;
        }

        // 1. 获取admin
        Admin admin = connection.getAdmin();

        // 2. 调用方法修改表格
        // 2.0 获取之前的表格描述
        TableDescriptor tableDescriptor = null;
        try {
            tableDescriptor = admin.getDescriptor(TableName.valueOf(namespace, tableName));
        } catch (IOException e) {
            System.out.println("表格不存在");
            e.printStackTrace();
        }

        // 2.1 创建一个表格描述建造者
        // 如果使用填写 tableName 的方法 相当于创建了一个新的表格描述 没有之前的信息
        // 如果想要修改表格的信息 必须调用方法填写一个旧的表格描述
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(tableDescriptor);

        // 2.2 对应建造者进行表格数据的修改
        // 获取旧的列族描述
        ColumnFamilyDescriptor columnFamily1 = tableDescriptor.getColumnFamily(Bytes.toBytes(columnFamily));

        // 创建列族描述建造者
        ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(columnFamily1);

        // 修改对应的版本
        columnFamilyDescriptorBuilder.setMaxVersions(version);

        // 在这里修改的时候 如果填写的是新创建的列族描述 那么我们表格之前的其它属性会被初始化 所以要使用旧的列族描述
        builder.modifyColumnFamily(columnFamilyDescriptorBuilder.build());

        try {
            admin.modifyTable(builder.build());
        } catch (IOException e) {

            e.printStackTrace();
        }

        // 3. 关闭admin
        admin.close();
    }

    /**
     * 删除表格
     * @param namespace 命名空间
     * @param tableName 表名
     * @return true-删除成功
     */
    public static boolean deleteTable(String namespace,String tableName) throws IOException {
        AtomicBoolean status = new AtomicBoolean(false);    //默认删除失败

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("删除确认");
        alert.setHeaderText("请确认是否删除");
        alert.setContentText("您确认删除表格"+tableName+"吗,这将会从 HBase 中移除");

        // 获取 DialogPane
        DialogPane dialogPane = alert.getDialogPane();

        // 获取确认按钮并添加事件处理器
        ButtonType confirmButtonType = ButtonType.OK;
        Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);

        // 获取取消按钮并添加事件处理器
        ButtonType cancelButtonType = ButtonType.CANCEL;
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.setOnAction(event -> {
            alert.close();
        });
        confirmButton.setOnAction(event -> {
            // 2. 获取admin
            try {
                // 1. 判断表格是否存在
                if (!isTableExists(namespace,tableName)){
                    System.out.println("表格不存在 无法删除");
                    return;
                }

                Admin admin = connection.getAdmin();

                // 3. 调用相关的方法删除表格
                // hbase 删除表格前必须标记标记表格为不可用才能删除
                admin.disableTable(TableName.valueOf(namespace,tableName));
                admin.deleteTable(TableName.valueOf(namespace,tableName));
                // 4. 关闭admin
                admin.close();
                status.set(true);
            }catch (Exception e){
                status.set(false);
                e.printStackTrace();
            }
        });
        // 显示 Alert
        alert.showAndWait();

        return status.get();
    }

}
