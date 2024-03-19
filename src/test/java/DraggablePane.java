import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DraggablePane extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Tooltip tip = new Tooltip("鼠标左键拖拽可移动面板");
        tip.setShowDelay(new Duration(20));

        Pane root = new Pane();
        Tooltip.install(root, tip);
        root.setBorder(new Border(
                new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, new CornerRadii(20), new BorderWidths(1.0))));
        root.setPrefSize(800, 600);

        DragScrollPane scrollPane = new DragScrollPane();
        scrollPane.setPadding(new Insets(10));
        scrollPane.setDragContent(root);

        Scene scene = new Scene(scrollPane, 500, 300);
        stage.setTitle("DraggablePane");
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * 可拖拽滚动面板
     */
    public class DragScrollPane extends ScrollPane {

        /** 是否正在拖拽 */
        private boolean isDrag;
        private double startX;
        private double startY;
        private double startHvalue;
        private double startVvalue;

        private Region content;

        public DragScrollPane() {
            this(null);
        }

        public DragScrollPane(Region content) {
            setDragContent(content);
        }

        public void setDragContent(Region content) {
            this.content = content;
            setContent(content);
            addDragEvent();
        }

        private void addDragEvent() {
            if (content != null) {
                content.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
                    startX = event.getSceneX();
                    startY = event.getSceneY();
                    startHvalue = getHvalue();
                    startVvalue = getVvalue();
                    isDrag = false;
                });

                content.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
                    if (event.isPrimaryButtonDown()) {
                        double moveX = startX - event.getSceneX();
                        double moveY = startY - event.getSceneY();
                        setHvalue(startHvalue + moveX / (content.getWidth() - getWidth()));
                        setVvalue(startVvalue + moveY / (content.getHeight() - getHeight()));
                        isDrag = true;
                    }
                });
            }
        }

        public boolean isDrag() {
            return isDrag;
        }
    }
}
