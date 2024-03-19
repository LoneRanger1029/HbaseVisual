package com.lyh.utils;

import com.lyh.pojo.MyAlert;
import javafx.application.Platform;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.log4j.Logger;

import java.io.IOException;

public class HBaseConnection {
    private final static Logger logger = Logger.getLogger(HBaseConnection.class);
    public final static String DEFAULT_PORT = "2181";
    // 声明一个静态属性
    public static Connection connection = null;

    public static boolean initial_connection(String hosts,String port){
//        Configuration conf = new Configuration();
        Configuration conf = HBaseConfiguration.create();
        conf.clear();
        conf.set("hbase.zookeeper.quorum",hosts);
        conf.set("hbase.zookeeper.property.clientPort",port);
        try {
            HBaseAdmin.available(conf);
            connection = ConnectionFactory.createConnection(conf);
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(()->{MyAlert.getError("失败","连接失败,请输入正确的zookeeper地址和端口号!").show();});
            return false;
        }
        return true;
    }

    public static boolean test_connect(String hosts,String port){
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum",hosts);
        conf.set("hbase.zookeeper.property.clientPort",port);
        Connection test = null;
        try {
            HBaseAdmin.available(conf);
            test = ConnectionFactory.createConnection(conf);
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("连接失败",e);
            return false;
//            MyAlert.getError("连接失败","连接失败,请重试!").show();
        }finally {
//            MyAlert.getSuccess("连接成功","连接成功").show();
            if (test != null) {
                try {
                    test.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    public static void closeConnection() throws IOException {
        // 判断连接是否为 null
        if (connection != null){
            connection.close();
        }
    }

}
