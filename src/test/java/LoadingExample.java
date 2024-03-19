import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class LoadingExample extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // 创建一个ProgressIndicator  
        ProgressIndicator progressIndicator = new ProgressIndicator();

        // 创建一个模拟长时间查询的任务  
        Task<Void> longRunningQueryTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                // 模拟查询  
                Thread.sleep(5000);
                return null;
            }
        };

        // 当任务开始时，显示ProgressIndicator  
        longRunningQueryTask.setOnRunning(event -> progressIndicator.setVisible(true));

        // 当任务成功结束时，隐藏ProgressIndicator  
        longRunningQueryTask.setOnSucceeded(event -> progressIndicator.setVisible(false));

        // 启动任务  
        new Thread(longRunningQueryTask).start();

        StackPane root = new StackPane();
        root.getChildren().add(progressIndicator);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}