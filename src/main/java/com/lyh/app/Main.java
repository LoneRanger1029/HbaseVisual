package com.lyh.app;

import com.lyh.loader.Loader;
import com.lyh.manager.ViewManager;
import com.lyh.panes.controller.LogTabPaneController;
import com.lyh.utils.HBaseConnection;
import com.sun.javafx.application.LauncherImpl;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.Stack;


public class Main extends Application {

    private float  increment = 0;
    private float  progress = 0;
    private final static long LOAD_TIME = 100;

    public static String TITLE = "HBase Visual 1.0";
    public static double WIDTH = 1200;
    public static double HEIGHT = 1000;

    public static HostServices hostServices;

    public static FXMLLoader fxmlLoader1 = new FXMLLoader(); // 一个FXMLLoader只能实例化一个 FXML 文件
    public static StackPane root;  // 指向主页 App.fxml
    public static Scene scene;

    static {
        try {
            root = fxmlLoader1.load(Main.class.getResourceAsStream("/fxml/App.fxml"));
            scene = new Scene(root);
            scene.getStylesheets().add("css/message.css");
            scene.getStylesheets().add("css/terminal.css");
            scene.getStylesheets().add("css/app.css");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        LauncherImpl.launchApplication(Main.class, Loader.class,args);
    }

    @Override
    public void init() throws InterruptedException {

        // 总共需要加载数量
        float total = 6;
        increment = 100f/total;

        // 设置 JVM UTF-8
        System.setProperty("file.encoding", "GBK");

        Thread.sleep(LOAD_TIME);
        load("LogTabPane");
//        Thread.sleep(LOAD_TIME);
        load("ClusterStatus");
//        Thread.sleep(LOAD_TIME);
        load("CreateNewTable");
//        Thread.sleep(LOAD_TIME);
        load("AddRow");
        load("waiting");
        // TODO 不能在这里初始化 HtmlEditor 因为需要加载 UI 需要放到 UI 线程
//        Thread.sleep(LOAD_TIME);
        load("FilterChoose");
        load("NormalSearch");

        Thread.sleep(500);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        hostServices = getHostServices();

        // javafx 不支持 ico 格式的 Image
        InputStream ins = getClass().getResourceAsStream("/icon/icon.png");
        if (ins!=null)
            primaryStage.getIcons().add(new Image(ins));
        primaryStage.setScene(scene);
        primaryStage.setWidth(WIDTH);
        primaryStage.setHeight(HEIGHT);
        primaryStage.setTitle(TITLE);
        close(primaryStage);
        primaryStage.show();

    }

    private void load(String name){
        try {
            ViewManager.getInstance().put(
                    name,
                    FXMLLoader.load(this.getClass().getResource("/fxml/"+ name + ".fxml"))
            );
            preloaderNotify();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private synchronized void preloaderNotify() {
        progress += increment;
        LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(progress));
    }


    public void close(Stage stage){
        // 关闭事件
        stage.setOnCloseRequest(event -> {
            try {
                // 关闭 Hbase 连接
                if (HBaseConnection.connection!=null) HBaseConnection.closeConnection();
                // 关闭日志线程
                if (LogTabPaneController.log_thread != null && LogTabPaneController.log_thread.isAlive()) LogTabPaneController.log_thread.interrupt();

//                if (LogTabPaneController.fileMonitor != null) LogTabPaneController.fileMonitor.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
