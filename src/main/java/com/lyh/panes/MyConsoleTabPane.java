package com.lyh.panes;

import com.lyh.pojo.FXMLPaneCreator;
import com.lyh.pojo.MyStage;
import javafx.scene.Cursor;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

// 单例模式 确保只有一个MyConsoleTabPane对象
public class MyConsoleTabPane extends TabPane {

    private final static Tab info = new Tab("  信 息  ");
    private final static Tab result = new Tab("  结 果  ");
    private volatile static MyConsoleTabPane tabPane;

    private MyConsoleTabPane() throws IOException {
    }

    public static MyConsoleTabPane getTabPane() throws IOException {
        if (tabPane == null) { // 先检查实例是否已经创建
            synchronized (MyConsoleTabPane.class) { // 同步块，确保只有一个线程可以执行下面的代码块
                if (tabPane == null) { // 再次检查实例是否已经创建，防止双重检查锁定的竞态条件问题
                    tabPane = new MyConsoleTabPane(); // 如果实例还未创建，则创建实例
                    tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE); //设置Tab不可关闭
                    FXMLPaneCreator fxmlPaneCreator = new FXMLPaneCreator();
                    BorderPane pane = (BorderPane) fxmlPaneCreator.initialPane("/fxml/LogTabPane.fxml");
                    info.setContent(pane);
                    AnchorPane anchorPane2 = new AnchorPane();
                    result.setContent(anchorPane2);
                    tabPane.getTabs().addAll(info, result);

                    // 拖拽设置底部面本大小
                    tabPane.setOnMouseDragged(event -> {
                        // 可恶啊！！！！
                        double height = tabPane.getScene().getHeight();
                        tabPane.setPrefHeight(height - event.getSceneY());
                    });
                    tabPane.setOnMousePressed(e -> {
                        tabPane.setCursor(Cursor.V_RESIZE);
                    });
                    tabPane.setOnMouseReleased(e -> {
                        tabPane.setCursor(Cursor.DEFAULT);
                    });
                }
            }
            return tabPane;
        }
        return tabPane;
    }
}