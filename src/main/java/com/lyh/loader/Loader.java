package com.lyh.loader;

import com.jfoenix.controls.JFXProgressBar;
import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Loader extends Preloader {

    private JFXProgressBar progressBar = new JFXProgressBar();
    private Parent view;
    private Stage stage;

    @Override
    public void init() throws Exception {
        view = FXMLLoader.load(getClass().getResource("/fxml/loader.fxml"));
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(view);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(getClass().getResource("/css/loader.css").toExternalForm());
        progressBar = (JFXProgressBar) scene.lookup("#progressBar");
        primaryStage.getIcons().add(new Image("icon/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification){
            double x = ((ProgressNotification)info).getProgress();

            double percent = x/100f;
            progressBar.progressProperty().set(percent > 1 ? 1 : percent);
        }
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        StateChangeNotification.Type type = info.getType();
        switch (type){
            case BEFORE_LOAD:
                break;
            case BEFORE_INIT:
                break;
            case BEFORE_START:
                stage.close();
        }
    }
}
