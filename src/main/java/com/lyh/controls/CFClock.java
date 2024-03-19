package com.lyh.controls;

import javafx.animation.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CFClock extends HBox {

    private ClockItem clockItem1 = new ClockItem();// 小时容器
    private ClockItem clockItem2 = new ClockItem();// 分钟容器
    private ClockItem clockItem3 = new ClockItem();// 秒容器

    public CFClock() {
        setLayout();
        setAnimation();
        setEvent();
        ST.play();
    }

    private void setLayout() {
        setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        this.setSpacing(10);
        this.setPadding(new Insets(10, 10, 10, 10));
        this.getChildren().addAll(clockItem1, clockItem2, clockItem3);
        this.setStyle("-fx-background-radius: 3px;" +
                "-fx-background-color: derive(#303133,90%);");
    }

    // 动画
    private Timeline TL1 = new Timeline();
    private Timeline TL2 = new Timeline();
    private PauseTransition PT1 = new PauseTransition(Duration.millis(100));// 停顿时间
    private PauseTransition PT2 = new PauseTransition(Duration.millis(400));// 停顿时间
    private SequentialTransition ST = new SequentialTransition(PT1, PT2, TL1, TL2);// 顺序动画

    /**
     * 设置动画属性
     */
    private void setAnimation() {
        Label topLabel = clockItem3.getFrontLabel(0);
        Label bottomLabel = clockItem3.getFrontLabel(1);
        // 动画相关1
        Rotate rotate1 = new Rotate(0, Rotate.X_AXIS);//3D x 旋转
        rotate1.pivotYProperty().bind(((Rectangle) topLabel.getClip()).heightProperty());
        topLabel.getTransforms().add(rotate1);
        //
        KeyValue fpx1 = new KeyValue(rotate1.angleProperty(), 0);
        KeyFrame fkfpx1 = new KeyFrame(Duration.millis(0), fpx1);
        //
        KeyValue tpx1 = new KeyValue(rotate1.angleProperty(), 90);
        KeyFrame tkfpx1 = new KeyFrame(Duration.millis(250), tpx1);
        TL1.getKeyFrames().addAll(fkfpx1, tkfpx1);
        // 动画相关2
        Rotate rotate2 = new Rotate(0, Rotate.X_AXIS);//3D x 旋转
        rotate2.setAngle(-90);
        rotate2.pivotYProperty().bind(((Rectangle) bottomLabel.getClip()).layoutYProperty());
        bottomLabel.getTransforms().add(rotate2);
        //
        KeyValue fpx2 = new KeyValue(rotate2.angleProperty(), -90);
        KeyFrame fkfpx2 = new KeyFrame(Duration.millis(0), fpx2);
        //
        KeyValue tpx2 = new KeyValue(rotate2.angleProperty(), 0);
        KeyFrame tkfpx2 = new KeyFrame(Duration.millis(250), tpx2);
        TL2.getKeyFrames().addAll(fkfpx2, tkfpx2);

        ST.setDelay(Duration.seconds(1));// 1 秒后执行
        ST.setCycleCount(-1);
        ST.setInterpolator(Interpolator.LINEAR);
    }

    private String ss = "00";
    private String mm = "00";
    private String hh = "00";
    private boolean hp = false;
    private boolean mp = false;

    private void setEvent() {
        PT1.setOnFinished(event -> {
            LocalTime now = LocalTime.now().plusSeconds(1); // 多加一秒
            String hh = DateTimeFormatter.ofPattern("HH").format(now);
            String mm = DateTimeFormatter.ofPattern("mm").format(now);
            if (!mm.equals(this.mm)) {// 分钟逻辑
                this.mm = mm;
                mp = true;
                clockItem2.setBackLabelText(0, this.mm);
                setItemRotate(clockItem2);
            } else {
                removeItemRotate(clockItem2);
                mp = false;
            }
            if (!hh.equals(this.hh)) {// 小时逻辑
                this.hh = hh;
                hp = true;
                clockItem1.setBackLabelText(0, this.hh);
                setItemRotate(clockItem1);
            } else {
                removeItemRotate(clockItem1);
                hp = false;
            }
            String ss = DateTimeFormatter.ofPattern("ss").format(now);
            this.ss = ss;
            clockItem3.setBackLabelText(0, this.ss);
        });
        TL1.setOnFinished(event -> {
            clockItem3.setFrontLabelText(0, this.ss);
            clockItem3.setFrontLabelText(1, this.ss);
            if (mp) {
                clockItem2.setFrontLabelText(0, this.mm);
                clockItem2.setFrontLabelText(1, this.mm);
            }
            if (hp) {
                clockItem1.setFrontLabelText(0, this.hh);
                clockItem1.setFrontLabelText(1, this.hh);
            }
        });
        TL2.setOnFinished(event -> {
            clockItem3.setBackLabelText(1, this.ss);
            if (mp) {
                clockItem2.setBackLabelText(1, this.mm);
            }
            if (hp) {
                clockItem1.setBackLabelText(1, this.hh);
            }
        });
    }

    /**
     * 给ClockItem设置动画属性：跟随clockItem3的动画改变
     *
     * @param clockItem
     */
    private void setItemRotate(ClockItem clockItem) {
        Rotate rotate1 = new Rotate(0, new Point3D(1, 0, 0));//3D x 旋转
        rotate1.pivotYProperty().bind(((Rectangle) clockItem.getFrontLabel(0).getClip()).heightProperty());
        clockItem.getFrontLabel(0).getTransforms().add(rotate1);
        rotate1.angleProperty().bind(((Rotate) clockItem3.getFrontLabel(0).getTransforms().get(0)).angleProperty());

        Rotate rotate2 = new Rotate(0, new Point3D(1, 0, 0));//3D x 旋转
        rotate2.setAngle(-90);
        rotate2.pivotYProperty().bind(((Rectangle) clockItem.getFrontLabel(1).getClip()).layoutYProperty());
        clockItem.getFrontLabel(1).getTransforms().add(rotate2);
        rotate2.angleProperty().bind(((Rotate) clockItem3.getFrontLabel(1).getTransforms().get(0)).angleProperty());
    }

    /**
     * 删除ClockItem动画属性
     *
     * @param clockItem
     */
    private void removeItemRotate(ClockItem clockItem) {
        clockItem.getFrontLabel(0).getTransforms().clear();
        clockItem.getFrontLabel(1).getTransforms().clear();
    }

    public class ClockItem extends StackPane {

        private StackPane back = new StackPane();
        private StackPane front = new StackPane();

        public ClockItem() {
            setLayout();
        }

        public void setBackLabelText(int i, String text) {
            getBackLabel(i).setText(text);
        }

        public void setFrontLabelText(int i, String text) {
            getFrontLabel(i).setText(text);
        }

        /**
         * 0： 获取上侧Label，1： 获取下侧Label
         *
         * @param i
         */
        public Label getFrontLabel(int i) {
            return (Label) front.getChildren().get(i);
        }

        /**
         * 0： 获取上侧Label，1： 获取下侧Label
         *
         * @param i
         */
        public Label getBackLabel(int i) {
            return (Label) back.getChildren().get(i);
        }

        // 布局
        private void setLayout() {
            setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
            getChildren().addAll(back, front);
            back.getChildren().addAll(getLabel("00", false), getLabel("00", true));
            front.getChildren().addAll(getLabel("00", false), getLabel("00", true));
        }

        private Label getLabel(String text, boolean isBottom) {
            int height = 2;
            Label label = new Label(text);
            label.setStyle("-fx-background-radius: 3px;" +
                    "-fx-background-color: #303133;");
            Font font = Font.font("Impact", 60);
            label.setFont(font);
            label.setAlignment(Pos.CENTER);
            label.setPrefWidth(80);
            label.setPrefHeight(100);
            label.setTextFill(Color.rgb(255, 255, 255, 0.5));
            //
            Rectangle rectangle = new Rectangle();
            rectangle.widthProperty().bind(label.widthProperty());
            rectangle.heightProperty().bind(label.heightProperty().divide(2).subtract(new SimpleDoubleProperty(height).divide(2)));
            if (isBottom) {
                rectangle.layoutYProperty().bind(label.heightProperty().divide(2).add(height));
            }
            label.setClip(rectangle);
            return label;
        }
    }

}
