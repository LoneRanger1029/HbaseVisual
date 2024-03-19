package com.lyh.utils;

import com.lyh.pojo.MyAlert;
import javafx.application.Platform;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class EmailUtil {

    private final static Logger logger = Logger.getLogger(EmailUtil.class);

    /**
     *
     * @param username 登录用户名
     * @param password 登录密码
     * @param fromNickname  发件人昵称
     * @param toEmail       收件人邮箱
     * @param toNickname    收件人昵称
     * @param title         邮件主题
     * @param content       邮件内容
     */
    public static boolean sendEmail(String username, String password, String fromNickname, String toEmail, String toNickname, String title, String content) {

        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.qq.com");   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");// 需要请求认证

        // nhmfckxetdlfdaeb
        // 2. 创建会话
        Session session = Session.getDefaultInstance(props);

        try {

            Transport transport = session.getTransport();

            /* 3. 创建一封邮件 */

            // 3.1. 创建邮件对象
            MimeMessage message = new MimeMessage(session);

            // 3.2. From: 发件人
            message.setFrom(new InternetAddress(username, fromNickname, "UTF-8"));

            // 3.3. To: 收件人
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toEmail, toNickname, "UTF-8"));

            // 3.4. Subject: 邮件主题
            message.setSubject(title, "UTF-8");

            // 3.5. Content: 邮件内容
            message.setContent(content, "text/html;charset=UTF-8");


            /*  使用 邮箱账号 和 密码 连接邮件服务器
                这里认证的邮箱必须与 message 中的发件人邮箱一致，否则报错*/
            transport.connect(username, password);

            //发送邮件
            transport.sendMessage(message, message.getAllRecipients());

            // 关闭连接
            transport.close();
        } catch (MessagingException | UnsupportedEncodingException m) {
            logger.error("邮件发送失败",m);
            Platform.runLater(()->{
                MyAlert.getError("失败","发送失败,请重试!").show();
            });
            return false;
        }
        return true;
    }
}
