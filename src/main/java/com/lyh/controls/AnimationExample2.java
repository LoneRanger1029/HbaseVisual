package com.lyh.controls;

import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.layout.StackPane;

/**
 * 时钟翻页效果
 */
public class AnimationExample2 extends StackPane {

    private StackPane root = new StackPane(new CFClock());
    private SubScene subScene = new SubScene(root, 400, 200);

    public AnimationExample2() {
        getChildren().add(subScene);
        root.getStylesheets().add("/css/cf-base.css");
        subScene.setCamera(new PerspectiveCamera());
    }
}
