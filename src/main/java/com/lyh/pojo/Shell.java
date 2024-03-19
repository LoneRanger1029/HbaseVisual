package com.lyh.pojo;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.lyh.app.AppController;
import com.lyh.app.Main;
import com.lyh.utils.CommandInputStream;
import com.lyh.utils.CommandOutputStream;
import com.lyh.utils.GUIOutputStream;
import com.lyh.utils.MyLogAppender;
import org.fxmisc.richtext.CodeArea;

public class Shell {

    /**
     *
     * @param host 服务器ip地址
     * @param username 用户名
     * @param password 密码
     * @param port 端口
     */
    public void show(String host,String username,String password,int port){
        try {
            //1、创建JSch对象
            JSch jSch = new JSch();

            //2、设置连接服务器参数
            //用户名、主机ip、端口 获取session
            Session session = jSch.getSession(username, host, port);
            //设置密码
            session.setPassword(password);
            //设置用户信息（必须）
            session.setUserInfo(new MyUserInfo());
            //设置session连接超时时间
            session.connect(30000);

            AppController controller = Main.fxmlLoader1.getController();

            //3、设置操作服务器的方式
            //采用shell方式（即命令交互）
            Channel channel = session.openChannel("shell");
            //命令从控制台输入
            channel.setInputStream(System.in);
//            channel.setInputStream(new CommandInputStream(controller.getTerminalArea()));
            //显示信息从控制台输出
            channel.setOutputStream(System.out);
//            channel.setOutputStream(new GUIOutputStream(MyLogAppender.codeArea));
//            channel.setOutputStream(new CommandOutputStream(controller.getTerminalArea()));
            //设置命令执行超时时间
            channel.connect(3*1000);
        } catch (JSchException e) {
            e.printStackTrace();
        }
        
    }

}
