package com.lyh.utils.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import sun.security.pkcs11.P11Util;

public class AddIconUtils {
    public AddIconUtils(){

    }

    public static void addIconToButton(Button button,String iconUrl,String button_text){
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(50);
        imageView.setFitHeight(50);
        imageView.setImage(image);
        VBox vBox = new VBox();
        vBox.setSpacing(5.0);
        vBox.getChildren().addAll(imageView,new Text(button_text));
        button.setGraphic(vBox);
    }
    public static void addIconToButton(Button button,String iconUrl){
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(13);
        imageView.setFitHeight(13);
        imageView.setImage(image);
        button.setGraphic(imageView);
    }
    public static void addIconToButton(Button button,String iconUrl,double width,double height){
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setImage(image);
        button.setGraphic(imageView);
    }

    public static void addIconToMessage(Label label,String url){
        ImageView imageView = new ImageView(new Image(url));
        imageView.setFitWidth(25);
        imageView.setFitHeight(25);
        label.setGraphic(imageView);
    }

    public static void addIconToMenuItem(MenuItem menuItem,String iconUrl){
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(21);
        imageView.setFitHeight(21);
        imageView.setImage(image);
        menuItem.setGraphic(imageView);
    }
    public static void addIconToButton(Button button,String iconUrl,String button_text,int size){
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setImage(image);
        HBox hBox = new HBox();
        hBox.getChildren().addAll(imageView,new Text(button_text));
        button.setGraphic(hBox);
    }
    public static void addIconToButton(Button button,String iconUrl,int size){
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView();
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setImage(image);
        button.setGraphic(imageView);
    }
}
