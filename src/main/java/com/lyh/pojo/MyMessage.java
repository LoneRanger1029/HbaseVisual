package com.lyh.pojo;

import com.lyh.app.AppController;
import com.lyh.utils.ui.AddIconUtils;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;


public class MyMessage extends HBox {
    private Label icon_label = new Label();
    private Label message_label = new Label("消息提示! ");
    private Label close_label = new Label();
    private Alert.AlertType level;

    // 动画
    private TranslateTransition TT = new TranslateTransition(Duration.millis(300), this);
    private FadeTransition FT = new FadeTransition(Duration.millis(300), this);
    private ParallelTransition PPT = new ParallelTransition(TT, FT);
    private PauseTransition PT = new PauseTransition(Duration.seconds(1.3));// 停顿时间1.5相当三秒
    private SequentialTransition ST = new SequentialTransition(PPT, PT);


    private MyMessage(String message){
        this.setId("message-box");
        icon_label.setId("message-icon");
        message_label.setId("message-text");
        close_label.setId("message-close");
        this.setSpacing(20);
        message_label.setText(message);
        this.setPrefWidth(300);
        this.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        message_label.setMaxSize(300, USE_PREF_SIZE);
    }

    public static MyMessage init(String message){
        MyMessage myMessage = new MyMessage(message);
        myMessage.setOpacity(0);
        myMessage.setAnimationInfo();
        return myMessage;
    }

    public MyMessage setLevel(Alert.AlertType level){
        this.level = level;
        switch (level){
            case INFORMATION:
                AddIconUtils.addIconToMessage(icon_label,"icon/info.png");
                break;
            case ERROR:
                AddIconUtils.addIconToMessage(icon_label,"icon/false.png");
                break;
            case WARNING:
                AddIconUtils.addIconToMessage(icon_label,"icon/warn.png");
                break;
        }
        return this;
    }

    public void show(AppController node){
        StackPane messages = node.getMessages();
        this.getChildren().add(icon_label);
        this.getChildren().add(message_label);
        this.getChildren().add(close_label);
        messages.getChildren().add(this);
        StackPane.setAlignment(this, Pos.CENTER);
        this.play();
    }

    private void setAnimationInfo() {
        TT.setFromY(0);
        TT.setToY(100);
        FT.setFromValue(0);
        FT.setToValue(1);
        ST.setCycleCount(2);
        ST.setAutoReverse(true);
        //鼠标移入暂停动画，移出继续动画
        hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                ST.pause();
            } else {
                ST.play();
            }
        });
        ST.setOnFinished(event -> destroy());
        // 终止动画
        close_label.setOnMouseClicked(event -> {
            event.consume();
            ST.stop();
            destroy();
        });
    }
    // 移出本身
    protected void destroy() {
        Pane parent = (Pane) this.getParent();
        if (parent != null) {
            parent.getChildren().remove(this);
        }
    }
    private void play() {
        ST.play();
    }
}
