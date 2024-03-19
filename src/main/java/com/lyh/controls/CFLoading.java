package com.lyh.controls;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.util.Duration;

public class CFLoading extends StackPane {

    private static final String STYLE_SHEET = CFLoading.class.getResource("/css/cf-loading.css").toExternalForm();
    // 大小
    private Size size;
    // 动画相关
    private Button TRANSITION_NODE = new Button();
    private RotateTransition RT = new RotateTransition();

    // 加载圈
    private Arc backArc;
    private Arc frontArc;
    // 布局
    private Pane arcPane = new Pane();
    private Label message = new Label("Loading...");

    public CFLoading(Size size) {
        this.size = size;
        setLayout();
        setAnimation();
    }

    public CFLoading setMessage(String message) {
        this.message.setText(message);
        return this;
    }

    public CFLoading setColor(Color color) {
        frontArc.setStroke(color);
        return this;
    }

    public CFLoading setBackColor(Color color) {
        backArc.setStroke(color);
        return this;
    }

    // 布局
    private void setLayout() {
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        switch (size) {
            case SMALL:
                this.getChildren().addAll(this.arcPane);
                break;
            case NORMAL:
                VBox vBox = new VBox();
                vBox.setSpacing(10);
                vBox.setAlignment(Pos.CENTER);
                vBox.getChildren().addAll(this.arcPane, this.message);
                this.arcPane.setMaxWidth(USE_PREF_SIZE);
                this.getChildren().addAll(vBox);
                break;
            case LARGE:
                this.getChildren().addAll(this.arcPane, this.message);
                break;
            default:
                break;
        }
        this.backArc = getArc(360);
        this.frontArc = getArc(90);
        this.arcPane.getChildren().addAll(backArc, frontArc);
        this.arcPane.setPrefSize(size.getSize(), size.getSize());
        //styleClass
        this.backArc.getStyleClass().add("back-arc");
        this.frontArc.getStyleClass().add("front-arc");
        this.message.getStyleClass().add("message");
    }

    private Arc getArc(double arcLength) {
        double size = this.size.getSize() / 2;
        Arc arc = new Arc(size, size, size, size, 0, arcLength);
        arc.setFill(null);
        arc.setStrokeWidth(this.size.getStrokeWidth());
        return arc;
    }

    /**
     * 动画
     */
    private void setAnimation() {
        //动画监听
        frontArc.startAngleProperty().bind(TRANSITION_NODE.rotateProperty());
        //旋转动画
        RT.setNode(TRANSITION_NODE);
        RT.setFromAngle(360);
        RT.setToAngle(0);
        RT.setDuration(Duration.millis(700));
        RT.setInterpolator(Interpolator.LINEAR);
        RT.setCycleCount(Timeline.INDEFINITE);
        RT.play();
    }

    @Override
    public String getUserAgentStylesheet() {
        return STYLE_SHEET;
    }

    // 加载容器大小
    public enum Size {
        LARGE(80, 4),
        NORMAL(50, 3),
        SMALL(15, 2);

        Size(double size, double strokeWidth) {
            this.size = size;
            this.strokeWidth = strokeWidth;
        }

        private double size;
        private double strokeWidth;

        public double getSize() {
            return size;
        }

        public double getStrokeWidth() {
            return strokeWidth;
        }
    }
}
