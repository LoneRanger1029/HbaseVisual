package com.lyh.utils;

import java.sql.*;

/**
 * 数据库；连接工具
 */
public class DBUtils {
    private static Connection con = null;
    private static PreparedStatement stm = null;
    private static Statement stmt = null;
    private static ResultSet rs = null;

    /**
     * 获取数据库连接对象
     * @return 连接
     * @throws Exception 异常
     */
    public static Connection getConnection() throws Exception{
        //通过反射加载驱动
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:database.db");
        return con;
    }

    public static void main(String[] args) throws Exception {
//        executeUpdate("insert into test values(1,\"lidaxi\")");
        ResultSet set = executeQuery("select * from student");
        while (set.next()){
            System.out.print(set.getInt("rowKey"));
            System.out.print(set.getString("name"));
            System.out.print(set.getString("age"));
            System.out.print(set.getString("sex"));
        }
    }
    /**
     * 执行SQL语句
     * @param sql sql语句
     * @return 返回查询结果集
     */
    public static ResultSet executeQuery(String sql){
        // SQLite连接串，指向本地的test.db文件
        try {
            // 建立连接
            con = getConnection();
            // 创建Statement对象
            stmt = con.createStatement();
            // 执行SELECT语句
            rs = stmt.executeQuery(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rs;
    }
    /**
     * 执行增删改操作
     * @param sql SQL语句
     * @return 返回操作的表的行数
     */
    public static int executeUpdate(String sql){
//        DBUtils.close();
        int count = 0;
        try {
            con = getConnection();
            stm = con.prepareStatement(sql);
            count = stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close();
        }
        return count;
    }

    /**
     * 执行增删改操作
     * @param sql SQL语句
     * @param params 参数数组
     * @return 返回操作的表的行数
     */
    public static int executeUpdate(String sql,String[] params){
//        DBUtils.close();
        int count = 0;
        try {
            con = getConnection();
            stm = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stm.setString(i+1,params[i]);
            }
            count = stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close();
        }
        return count;
    }

    /**
     * 执行增删改操作
     * @param sql SQL语句
     * @param params 参数数组
     * @return 返回操作的表的行数
     */
    public static int executeUpdate(String sql,String[] params,byte[] face_feature){
        int count = 0;
        try {
            con = getConnection();
            stm = con.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stm.setString((i+1),params[i]);
            }
            stm.setBytes(7,face_feature);//默认第7个参数位置是特征值
            count = stm.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            close();
        }

        return count;
    }

    /**
     * 关闭所有资源
     */
    public static void close(){
        if (rs!=null){
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stm!=null){
            try {
                stm.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stmt!=null){
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (con!=null){
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
