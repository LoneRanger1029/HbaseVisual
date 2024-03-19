package com.lyh.animations;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class MyAnimationTransition {
    public static FadeTransition getFadeTransition(Node node,double duration,boolean auto_reverse,double from,double to,int count){
        FadeTransition ft = new FadeTransition();
        ft.setNode(node);
        ft.setDuration(Duration.seconds(duration));
        ft.setAutoReverse(auto_reverse);
        ft.setFromValue(from);
        ft.setToValue(to);
        ft.setCycleCount(count);
        return ft;
    }
}
