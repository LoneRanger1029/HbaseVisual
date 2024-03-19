import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 加载等待页案例
 *
 * @author Miaoqx
 */
public class LoadingDialog extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Button loadBtn = new Button("加载");
        loadBtn.setPrefSize(140, 50);
        loadBtn.setOnAction(event -> {
            Loading loading = new Loading(stage);// 1
            loading.show();

            new Thread(() -> {// 2
                try {
                    /**
                     * TODO 处理耗时操作。
                     * 注意：如果期间涉及到更新UI的操作，需要Platform.runLater(() -> {// TODO })处理。
                     */
                    Thread.sleep(3000);
                    loading.showMessage("自定义消息...");
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();// 3
                } finally {
                    loading.closeStage();// 4
                }
            }).start();
        });

        Pane root = new Pane();
        root.getChildren().add(loadBtn);

        stage.setScene(new Scene(root));
        stage.setTitle("Loading");
        stage.setMaximized(true);
        stage.getIcons().add(new Image("https://files-cdn.cnblogs.com/files/miaoqx/logo.bmp"));
        stage.show();
    }

    /**
     * 加载页
     *
     * @author Miaoqx
     */
    public class Loading {

        protected Stage stage;
        protected StackPane root;
        protected Label messageLb;

        public Loading(Stage owner) {
            ImageView loadingView = new ImageView(
                    new Image("https://img.zcool.cn/community/01ae2659e05d52a801204463b45ca9.gif"));// 可替换

            messageLb = new Label("请耐心等待...");
            messageLb.setFont(Font.font(20));

            root = new StackPane();
            root.setMouseTransparent(true);
            root.setPrefSize(owner.getWidth(), owner.getHeight());
            root.setBackground(new Background(new BackgroundFill(Color.rgb(0, 0, 0, 0.3), null, null)));
            root.getChildren().addAll(loadingView, messageLb);

            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);

            stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.initOwner(owner);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.getIcons().addAll(owner.getIcons());
            stage.setX(owner.getX());
            stage.setY(owner.getY());
            stage.setHeight(owner.getHeight());
            stage.setWidth(owner.getWidth());
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

    public static void main(String[] args) {
        launch(args);
    }
}
