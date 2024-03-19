package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.lyh.utils.ui.AddIconUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.IOException;

public class NormalSearchController {
    @FXML
    public JFXButton scan;
    @FXML
    public JFXButton get_btn;
    @FXML
    public VBox rightVbox;

    public Stage parentStage;
    private final static Logger logger = Logger.getLogger(NormalSearchController.class);


    @FXML
    public void initialize(){
        AddIconUtils.addIconToButton(get_btn,"icon/get.png",285,400);
        get_btn.setStyle("-fx-background-color: rgb(214,230,255)");
        AddIconUtils.addIconToButton(scan,"icon/scan.png",285,400);
        scan.setStyle("-fx-background-color: rgb(214,230,255)");
    }
    @FXML
    public void showChoice(){
        rightVbox.getChildren().remove(0);
        JFXButton scan_all = new JFXButton("扫描整表");
        AddIconUtils.addIconToButton(scan_all,"icon/scan_all.png",320,200);
        scan_all.setPrefSize(rightVbox.getWidth(),rightVbox.getHeight()/2);
        scan_all.setOnAction(event -> {
            // 关闭父窗口
            parentStage = (Stage) get_btn.getScene().getWindow();
            parentStage.close();

            Stage stage = new Stage();
            // 打开该窗口后不可点击父窗口
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image("icon/normal_search.png"));
            FXMLLoader loader = new FXMLLoader();
            BorderPane pane = null;
//            pane = (BorderPane) ViewManager.getInstance().get("ScanAllPane");
            try {
                pane = loader.load(NormalSearchController.class.getResourceAsStream("/fxml/ScanAllPane.fxml"));
            } catch (IOException e) {
                logger.error("初始化Scan面板失败",e);
            }
            if (pane!=null){
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.setTitle("Scan - All");
                stage.show();
            }
        });


        JFXButton scan_border = new JFXButton();
        AddIconUtils.addIconToButton(scan_border,"icon/scan_border.png",320,200);
        scan_border.setPrefSize(rightVbox.getWidth(),rightVbox.getHeight()/2);

        scan_border.setOnAction(event -> {
            // 关闭父窗口
            parentStage = (Stage) get_btn.getScene().getWindow();
            parentStage.close();

            Stage stage = new Stage();
            // 打开该窗口后不可点击父窗口
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().add(new Image("icon/normal_search.png"));
            FXMLLoader loader = new FXMLLoader();
            BorderPane pane = null;
//            pane = (BorderPane) ViewManager.getInstance().get("ScanBorderPane");
            try {
                pane = loader.load(NormalSearchController.class.getResourceAsStream("/fxml/ScanBorderPane.fxml"));
            } catch (IOException e) {
                logger.error("初始化面板失败",e);
            }
            if (pane!=null){
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.setTitle("Scan - Search");
                stage.show();
            }
        });


        rightVbox.getChildren().addAll(scan_all,scan_border);
        rightVbox.setStyle("-fx-background-color: rgb(214,230,255)");
    }

    @FXML
    public void openGetPane(){
        System.out.println("");
        // 关闭父窗口
        parentStage = (Stage) get_btn.getScene().getWindow();
        parentStage.close();

        Stage stage = new Stage();
        // 打开该窗口后不可点击父窗口
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image("icon/normal_search.png"));
        FXMLLoader loader = new FXMLLoader();
        BorderPane pane = null;
        try {
            pane = loader.load(NormalSearchController.class.getResourceAsStream("/fxml/GetPane.fxml"));
            stage.setScene(new Scene(pane));
            stage.show();
        } catch (IOException e) {
            logger.error("初始化Get查询面板错误",e);
        }
    }
}
