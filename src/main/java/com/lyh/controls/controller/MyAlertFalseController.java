package com.lyh.controls.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class MyAlertFalseController {
    private Stage primaryStage;

    @FXML
    private ImageView falseIcon;
    @FXML
    private JFXButton btn_dialog_sure;

    private JFXAlert alert;

    @FXML
    public void initialize(){
        falseIcon.setImage(new Image("icon/false.png"));
    }

    public void init(Stage primaryStage){
        this.primaryStage=primaryStage;
    }
    /**
     * 关闭对话框
     */
    @FXML
    public void dialogClose(){
        if(alert!=null){
            alert.hide();
        }
    }

    public void dialogSure(){
        if(alert!=null) {
            alert.hide();
        }
    }

    public void close(JFXAlert alert){
        this.alert=alert;
    }


}
