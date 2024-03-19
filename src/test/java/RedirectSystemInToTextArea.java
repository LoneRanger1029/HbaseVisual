import javafx.application.Application;  
import javafx.scene.Scene;  
import javafx.scene.control.TextArea;  
import javafx.scene.input.KeyEvent;  
import javafx.scene.layout.StackPane;  
import javafx.stage.Stage;  
  
import java.io.IOException;  
import java.io.InputStream;  
import java.io.InputStreamReader;  
import java.io.Reader;  
import java.nio.charset.Charset;  
  
public class RedirectSystemInToTextArea extends Application {  
  
    @Override  
    public void start(Stage primaryStage) throws IOException {  
        TextArea textArea = new TextArea();  
        textArea.addEventHandler(KeyEvent.KEY_TYPED, event -> {  
            String c = event.getCharacter();
            textArea.appendText(c + "");  
        });

        // 创建一个自定义的输入流，将System.in的输入重定向到TextArea
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws IOException {
                int ch = System.in.read(); // 读取System.in的输入
                if (ch == -1) { // -1表示已经读取到流的末尾
                    return -1;
                } else {
                    return ch; // 返回读取到的字符的字节值
                }
            }
        };
        Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset()); // 将字节流转换为字符流
        java.util.Scanner scanner = new java.util.Scanner(reader); // 使用Scanner读取字符流中的数据
        javafx.util.converter.DefaultStringConverter converter = new javafx.util.converter.DefaultStringConverter(); // 将Scanner转换为String类型，并更新到TextArea中
        converter.fromString(scanner.nextLine()); // 读取一行数据并更新到TextArea中

        StackPane root = new StackPane(textArea);  
        Scene scene = new Scene(root, 300, 250);  
        primaryStage.setScene(scene);  
        primaryStage.show();  
    }  
  
    public static void main(String[] args) {

        Application.launch(args);

//        // 创建一个自定义的输入流，将System.in的输入重定向到TextArea
//        InputStream inputStream = new InputStream() {
//            @Override
//            public int read() throws IOException {
//                int ch = System.in.read(); // 读取System.in的输入
//                if (ch == -1) { // -1表示已经读取到流的末尾
//                    return -1;
//                } else {
//                    return ch; // 返回读取到的字符的字节值
//                }
//            }
//        };
//        Reader reader = new InputStreamReader(inputStream, Charset.defaultCharset()); // 将字节流转换为字符流
//        java.util.Scanner scanner = new java.util.Scanner(reader); // 使用Scanner读取字符流中的数据
//        javafx.util.converter.DefaultStringConverter converter = new javafx.util.converter.DefaultStringConverter(); // 将Scanner转换为String类型，并更新到TextArea中
//        converter.fromString(scanner.nextLine()); // 读取一行数据并更新到TextArea中
    }  
}