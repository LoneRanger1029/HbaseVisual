package com.lyh.panes.controller;

import com.lyh.app.Main;
import com.lyh.pojo.MyAlert;
import com.lyh.utils.CodeAreaLogAppender;
import com.lyh.utils.GUIOutputStream;
import com.lyh.utils.listener.FileListener;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.*;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogTabPaneController {

    // 对应log4j配置文件: log4j.appender.textArea.layout.ConversionPattern=%-d{yyyy-MM-dd HH:mm:ss}  [ %p ]  %m%n
    public final static Pattern INFO = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}  \\[ INFO \\].*)");
    public final static Pattern WARN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}  \\[ WARN \\].*)");
    public final static Pattern ERROR = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}  \\[ ERROR \\].*)");
//    public final static Pattern LOG = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}  \\[ ERROR \\].*)");

//    public static PrintStream logger;
    public static Thread log_thread;
    @FXML
    public BorderPane logTabPane;

    @FXML
    private CodeArea codeArea;

    @FXML
    public void initialize() throws Exception {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem clear = new MenuItem("清空日志记录");
        clear.setOnAction(event -> {
            codeArea.clear();// 清空所有段落
        });
        MenuItem save = new MenuItem("保存日志到本地");
        save.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("选择保存路径");
            File file = directoryChooser.showDialog(logTabPane.getScene().getWindow());
            if (file != null){
                boolean flag = false;
                String savePath = file.getAbsolutePath()+"/"+System.currentTimeMillis()+"_log.txt";
                flag = saveToLocal(savePath,codeArea.getText());
                if (flag){
                    MyAlert.getSuccess("保存成功","日志已经成功保存到本地文件,path="+savePath).show();
                }else {
                    MyAlert.getError("保存失败","日志保存失败,请重试!").show();
                }
            }
        });
        contextMenu.getItems().addAll(save,clear);
        codeArea.setContextMenu(contextMenu);
        codeArea.setWrapText(true);
        codeArea.setEditable(false);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));   //添加行号
        codeArea.getStylesheets().add(getClass().getResource("/css/code.css").toExternalForm());
        codeArea.textProperty().addListener((obs,oldValue,newValue) -> {
            computeHighLighting(newValue);
        });

        // 开启日志线程
        log_thread = new CodeAreaLogAppender(codeArea);
        log_thread.start();

        logTabPane.setOnMouseDragged(event -> {
            // 可恶啊！！！！
            double height = logTabPane.getScene().getHeight();
            logTabPane.setPrefHeight(height - event.getSceneY());
        });
        logTabPane.setOnMousePressed(e -> {
            logTabPane.setCursor(Cursor.V_RESIZE);
        });
        logTabPane.setOnMouseReleased(e -> {
            logTabPane.setCursor(Cursor.DEFAULT);
        });
    }

    /**
     * 检测到的内容使用高光
     * @param text codeArea 中的日志文本
     */
    private void computeHighLighting(String text) {
        Matcher equalsInfo = INFO.matcher(text);
        while (equalsInfo.find()){
//            System.out.println("info start"+equalsInfo.start());
//            System.out.println("info end"+equalsInfo.end());
            codeArea.setStyle(equalsInfo.start(),equalsInfo.end(), Collections.singleton("info"));
        }
        Matcher equalsWarn = WARN.matcher(text);
        while(equalsWarn.find()){
            codeArea.setStyle(equalsWarn.start(),equalsWarn.end(), Collections.singleton("warn"));
        }
        Matcher equalsError = ERROR.matcher(text);
        while(equalsError.find()){
            codeArea.setStyle(equalsError.start(),equalsError.end(), Collections.singleton("error"));
        }

    }

    public static boolean saveToLocal(String filepath, String content){
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filepath))) {
            bufferedWriter.write(content);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static void updateLog(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        try {
            line = reader.readLine();
            // 定义日期时间格式
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
            // 解析日期时间字符串为 DateTime 对象
            DateTime dateTime = formatter.parseDateTime(line.substring(0,19));
            // 将 DateTime 对象转换为毫秒
            long milliseconds = dateTime.getMillis()+10000L; // 10s 后

            DateTime lastRow = new DateTime(milliseconds);
            while (line != null){
//                System.out.println("判断"+line);
//                System.out.println(lastRow.isAfterNow());
                if (lastRow.isAfterNow()){
//                    System.out.println("增加日志");
                    sb.append(line).append("\n");
                    Platform.runLater(()->{
//                        codeArea.replaceText(sb.toString());
                    });
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reader.close();
        }
    }
}
