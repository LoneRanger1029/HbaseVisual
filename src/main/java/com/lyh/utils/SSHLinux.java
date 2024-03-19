package com.lyh.utils;
 
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.io.IOUtils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SSHLinux {

	public static void main(String[] args) {
		String host = "192.168.88.134";
		int port = 22;
		String user = "lyh";
		String password = "123456";
        // 使用多个命令时用分号隔开
		StringBuilder command = new StringBuilder();
		command.append("pwd;");
//		command.append("date;");
		command.append("cd /opt/module/;");
		command.append("ls -l;");
//		command.append("cd SHG_0115038_FUZHOU_PORT_TOWN_INTL;");
//		command.append("ls -l;");
//		command.append("cd upload;");
//		command.append("ls -l;");
//		command.append("pwd");
		Session session = connect(host, port, user, password);
		String res = exeCommand(session,command.toString());
		System.out.println(res);
		Scanner in = new Scanner(System.in);
		for (int i = 0; i < 100; i++) {
			String comman = in.nextLine();
			System.out.println(exeCommand(session,comman.toString()));
		}
		// 关闭session
		session.disconnect();
	}
	
	public static String exeCommand(Session session, String command) {
		try {
			// 打开通道，设置通道类型，和执行的命令
	        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
	        InputStream in = channelExec.getInputStream();
	        channelExec.setCommand(command);
	        channelExec.setErrStream(System.err);
	        channelExec.connect();
	        String out = IOUtils.toString(in, "UTF-8");
	        // 关闭通道 
	        channelExec.disconnect();
	        return out;
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
    }
	
	public static Session connect(String host, int port, String user, String password) {
        JSch jsch = new JSch();
        Session session;
		try {
			// 创建session并且打开连接，因为创建session之后要主动打开连接 
			session = jsch.getSession(user, host, port);
			session.setPassword(password);
			// 连接时不进行公钥确认，如果第一次登陆会让你确定是否接受公钥，改配置跳过这一步
			session.setConfig("StrictHostKeyChecking", "no");
			// 设置超时时间
			session.setTimeout(6000);
	        session.connect();
	        return session;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
}