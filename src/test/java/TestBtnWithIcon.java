import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TestBtnWithIcon extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        AnchorPane pane = new AnchorPane();
        TextArea textArea =new TextArea();
        pane.getChildren().addAll(textArea);
        Scene scene = new Scene(pane,400,500);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();
    }
}
