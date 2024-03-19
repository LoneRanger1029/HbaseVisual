package com.lyh.pojo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MyStage extends Stage {

    private Scene scene = null;
    public Pane root = null;
    public FXMLLoader loader = null;

    public MyStage(){

    }

    /**
     *
     * @param title stage 标题
     * @param resizeable false-不可变
     * @param iconUrl 图标路径-stage的图标可以是ico
     * @param fxmlUrl fxml路径
     * @return
     * @throws IOException
     */
    public void initializeStage(String title, boolean resizeable, String iconUrl, String fxmlUrl) throws IOException {
        this.setTitle(title);
        this.setResizable(resizeable);
        this.getIcons().add(new Image(iconUrl));
        loader = new FXMLLoader();
        root = loader.load(MyStage.class.getResourceAsStream(fxmlUrl));
        scene = new Scene(root);
        // todo 注意 是this.setScene
        this.setScene(scene);
    }

    public Pane getRootPane() {
        return root;
    }

    public Scene getMyScene() {
        return scene;
    }
}
