package com.lyh.controls.controller;

import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AlertSuccessController {
    private Stage primaryStage;

    @FXML
    private ImageView successIcon;
    @FXML
    private JFXButton btn_dialog_sure;

    private JFXAlert alert;

    @FXML
    public void initialize(){
        successIcon.setImage(new Image("icon/success.png"));
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
