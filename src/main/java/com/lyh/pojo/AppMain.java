package com.lyh.pojo;

import java.io.IOException;

public class AppMain {

    public static void main(String[] args) throws InterruptedException, IOException {

        String username="lyh";//用户名
        String host="hadoop102";//ip地址
        int port=22;//端口  22端口，即为ssh应用端口，远程连接端口
        String password="123456";//密码

        Shell shell = new Shell();
        shell.show(host,username,password,port);
    }

}
