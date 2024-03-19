package com.lyh.pojo;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.Window;

public class MyAlert extends Alert {

    private static final Alert success;
    private static final Alert info;
    private static final Alert warning;
    private static final Alert confirm;
    private static final Alert error;

    static{
        success = new Alert(AlertType.INFORMATION);
        Stage stage1 = (Stage) success.getDialogPane().getScene().getWindow();
        stage1.getIcons().add(new Image("/icon/success.png"));
        info = new Alert(AlertType.INFORMATION);
        warning = new Alert(AlertType.WARNING);
        Stage stage = (Stage) warning.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("/icon/warn.png"));
        confirm = new Alert(AlertType.CONFIRMATION);
        error = new Alert(AlertType.ERROR);
        Stage stage2 = (Stage) success.getDialogPane().getScene().getWindow();
        stage2.getIcons().add(new Image("/icon/success.png"));
    }

    public MyAlert(AlertType alertType) {
        super(alertType);
    }

    public MyAlert(AlertType alertType, String contentText, ButtonType... buttons) {
        super(alertType, contentText, buttons);
    }

    public static Alert getError(String errorTitle,String errorInfo) {
        error.setTitle(errorTitle);
        error.setContentText(errorInfo);
        ImageView imageView = new ImageView(new Image("/icon/false.png"));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        error.setGraphic(imageView);
        return error;
    }

    public static Alert getInfo(String infoTitle,String infoContext) {
        info.setTitle(infoTitle);
        info.setContentText(infoContext);
        ImageView imageView = new ImageView(new Image("/icon/info.png"));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        info.setGraphic(imageView);
        return info;
    }

    public static Alert getSuccess(String successTitle,String successInfo) {
        success.setTitle(successTitle);
        success.setContentText(successInfo);
        ImageView imageView = new ImageView(new Image("/icon/success.png"));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        success.setGraphic(imageView);
        return success;
    }

    public static Alert getWarning(String warningTitle,String warningInfo) {
        warning.setTitle(warningTitle);
        warning.setContentText(warningInfo);
        ImageView imageView = new ImageView(new Image("/icon/warn.png"));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        warning.setGraphic(imageView);
        return warning;
    }
    public static Alert getConfirmation(String confirmTitle,String confirmInfo) {
        confirm.setContentText(confirmInfo);
        confirm.setTitle(confirmTitle);
        ImageView imageView = new ImageView(new Image("/icon/warn.png"));
        imageView.setFitHeight(50);
        imageView.setFitWidth(50);
        confirm.setGraphic(imageView);
        // 获取 DialogPane
        DialogPane dialogPane = confirm.getDialogPane();

        // 获取确认按钮并添加事件处理器
        ButtonType confirmButtonType = ButtonType.OK;
        Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
        confirmButton.setOnAction(event -> {

        });

        // 获取取消按钮并添加事件处理器
        ButtonType cancelButtonType = ButtonType.CANCEL;
        Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
        cancelButton.setOnAction(event -> {
            confirm.close();
        });
        return confirm;
    }


}
