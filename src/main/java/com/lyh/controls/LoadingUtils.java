package com.lyh.controls;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.function.Supplier;

public class LoadingUtils {

    /**
     * 按钮加载
     *
     * @param button
     * @param supplier
     */
    public static void buttonLoad(Button button, Supplier<Boolean> supplier) {
        Node graphic = button.getGraphic();
        button.setDisable(true);
        button.setGraphic(new CFLoading(CFLoading.Size.SMALL).setBackColor(Color.TRANSPARENT).setColor(Color.WHITE));
        new Thread(() -> {
            if (supplier.get()) {//加载完成
                Platform.runLater(() -> {
                    button.setGraphic(graphic);
                    button.setDisable(false);
                });
            }
        }).start();
    }

    /**
     * 指定容器加载
     *
     * @param node
     * @param supplier
     */
    public static void nodeLoad(StackPane node, Supplier<Boolean> supplier) {
        StackPane back = new StackPane(new CFLoading(CFLoading.Size.NORMAL));
        nodeLoad(node, supplier, back);
    }

    /**
     * 指定容器加载
     *
     * @param node
     * @param cfLoading
     * @param supplier
     */
    public static void nodeLoad(StackPane node, CFLoading cfLoading, Supplier<Boolean> supplier) {
        StackPane back = new StackPane(cfLoading);
        nodeLoad(node, supplier, back);
    }

    private static void nodeLoad(StackPane node, Supplier<Boolean> supplier, StackPane back) {
        back.setBackground(new Background(new BackgroundFill(Color.rgb(255, 255, 255, 0.8), null, null)));
        node.getChildren().add(back);
        new Thread(() -> {
            if (supplier.get()) {//加载完成
                Platform.runLater(() -> node.getChildren().remove(back));
            }
        }).start();
    }

}
