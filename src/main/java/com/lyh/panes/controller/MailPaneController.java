package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.lyh.pojo.MyAlert;
import com.lyh.utils.EmailUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MailPaneController {

    private final static Logger logger = Logger.getLogger(MailPaneController.class);

    private ImageView view;
    private Image image1;
    private Image image2;

    @FXML
    private TextField pwd;  // 当密码可见时，使用这个文本框
    @FXML
    private PasswordField pwd_hide; // 当密码不可见时，使用这个文本框
    @FXML
    public BorderPane mailPane;
    @FXML
    private TextField account;
    @FXML
    private JFXButton hide;
    @FXML
    private TextField receiverAccount;
    @FXML
    private TextField receiverNickname;
    @FXML
    private TextField senderNickname;
    @FXML
    private TextField subject;
    @FXML
    private HTMLEditor content;

    @FXML
    public void initialize(){

        receiverAccount.setText("LYH18235813457@outlook.com");
        receiverAccount.setEditable(false);

        pwd.setPromptText("请填写 SMTP 编号");
        pwd_hide.setPromptText("请填写 SMTP 编号");

        image1 = new Image("icon/hide.png");
        image2 = new Image("icon/eye-fill.png");

        view = new ImageView();
        view.setImage(image1);
        view.setFitWidth(13);
        view.setFitHeight(13);

        hide.setGraphic(view);

        pwd_hide.setVisible(true);
        pwd.setVisible(false);
    }

    @FXML
    public void send_mail(){
        // 校验
        String username = account.getText();
        String password;
        if (view.getImage()==image1){ //不可见
            password = pwd_hide.getText();
        }else { //可见
            password = pwd.getText();
        }
        String senderNicknameText = senderNickname.getText();
        String receiverAccountText = receiverAccount.getText();
        String receiverNicknameText = receiverNickname.getText();
        String title = subject.getText();
        String htmlText = content.getHtmlText();
        if (username.equals("") || password.equals("") || senderNicknameText.equals("") || receiverNicknameText.equals("") || receiverAccountText.equals("") || title.equals("") || htmlText.equals("")){
            Alert warning = MyAlert.getWarning("警告", "请把表单填写完整");
            warning.show();
            return;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*\\.[a-zA-Z0-9]{2,6}$");
        Matcher matcher = pattern.matcher(username);
        if (!matcher.matches()){
            MyAlert.getWarning("警告","非正确的邮箱格式: "+username).show();
            return;
        }
        // 耗时操作
        Loading loading = new Loading((Stage) mailPane.getScene().getWindow());
        loading.show();
        Thread thread = new Thread(() -> {
            boolean res = true;
            try {
                loading.showMessage("正在发送中,请耐心等待");
                res = EmailUtil.sendEmail(username, password, senderNicknameText, receiverAccountText, receiverNicknameText, title, htmlText);
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error("发送过程出现的错误:", e);
                e.printStackTrace();
            } finally {
                loading.closeStage();
                if (res) {
                    Platform.runLater(() -> {
                        MyAlert.getSuccess("成功", "发送成功").show();
                    });
                }
                logger.info("成功发送邮件," + username + " -> " + receiverAccountText);
            }
        });
        thread.start();
    }

    @FXML
    public void give_up(){
        Stage stage = (Stage) mailPane.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void hide_pwd(){

        if (view.getImage()==image1){   // 密码设为可见
            pwd.setText(pwd_hide.getText());
            pwd.setVisible(true);
            pwd_hide.setVisible(false);
            view.setImage(image2);
        }else { // 密码设为不可见
            view.setImage(image1);
            pwd_hide.setText(pwd.getText());
            pwd_hide.setVisible(true);
            pwd.setVisible(false);
        }
        hide.setGraphic(view);
    }
    public static class Loading {

        protected Stage stage;
        protected StackPane root;
        protected Label messageLb;

        public Loading(Stage owner) {
//            File file = new File("icon/mail.mp4");
            // Media 的参数必须是绝对路径,放在resources的文件的检测不到，mp4尽量放到项目根目录下
            File file = new File("mp4/mail.mp4");
            Media media = new Media(file.toURI()+"");
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setAutoPlay(true);
            mediaPlayer.setCycleCount(Integer.MAX_VALUE);
            MediaView mediaView = new MediaView(mediaPlayer);
//            ImageView loadingView = new ImageView(
//                    new Image("icon/mail.gif"));// 可替换

            messageLb = new Label("请耐心等待...");
            messageLb.setFont(Font.font(20));

            root = new StackPane();
            root.setMouseTransparent(true);
            root.setPrefSize(owner.getWidth(), owner.getHeight());
            root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.3), null, null)));
            root.getChildren().addAll(mediaView,messageLb);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(owner);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().addAll(owner.getIcons());
//            stage.setX(owner.getX());
//            stage.setY(owner.getY());
            stage.setHeight(300);
            stage.setWidth(400);
        }

        // 更改信息
        public void showMessage(String message) {
            Platform.runLater(() -> messageLb.setText(message));
        }

        // 显示
        public void show() {
            Platform.runLater(() -> stage.show());
        }

        // 关闭
        public void closeStage() {
            Platform.runLater(() -> stage.close());
        }
    }
}
