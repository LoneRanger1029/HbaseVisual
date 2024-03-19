package com.lyh.utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.fxmisc.richtext.CodeArea;

public class MyLogAppender extends AppenderSkeleton {

    private final static Logger logger = Logger.getLogger(MyLogAppender.class);
    private static MyLogAppender appender;
    public static TextArea textArea = new TextArea();
    public static CodeArea codeArea = new CodeArea();

    public static MyLogAppender getInstance(){
         if (appender == null) appender = new MyLogAppender();
         logger.info("初始化 textArea 成功");
         return appender;
    }

    /**
     * 打印日志的核心方法
     * @param loggingEvent
     */
    @Override
    protected void append(LoggingEvent loggingEvent) {
        String message = loggingEvent.getMessage().toString();
        Platform.runLater(()->{
            textArea.setText(message);
        });
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        // 不用就不需要实现
    }

    /**
     * 是否需要按照格式输出文本
     * @return
     */
    @Override
    public boolean requiresLayout() {
        return false;
    }
}
