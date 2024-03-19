import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;

public class Log4j extends Application {
    public static final Logger LOGGER = Logger.getLogger(Log4j.class);


    private static TextArea textArea;
//    public static void main(String[] args) { }


    //配置为 界面输出 和 文件输出
    public static void configureLogger(TextArea textArea) {
        // 创建UI界面输出的Appender
        JTextAreaAppender uiAppender = new JTextAreaAppender(textArea);
        //uiAppender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %c [%p] - %m%n"));
        uiAppender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%p] - %m%n"));
        uiAppender.activateOptions();
        Logger.getRootLogger().addAppender(uiAppender);

        // 创建文件输出的Appender
        try {
            FileAppender fileAppender = new FileAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} [%p] - %m%n"), "logs/logfile.log");
            fileAppender.setAppend(true); // 如果true，则追加到现有的日志文件末尾；如果false，则覆盖文件中的现有内容
            fileAppender.activateOptions();
            // 将Appender添加到日志记录器
            Logger.getRootLogger().addAppender(fileAppender);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 设置日志记录级别
        LOGGER.setLevel(Level.DEBUG);
    }
    //配置为 控制台输出 和 文件输出
    public static void configureLogger() {
        // 创建控制台输出的Appender
        ConsoleAppender consoleAppender = new ConsoleAppender();
        consoleAppender.setLayout(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %c [%p] - %m%n"));
        consoleAppender.setTarget(ConsoleAppender.SYSTEM_OUT);
        consoleAppender.activateOptions();

        // 创建文件输出的Appender
        try {
            FileAppender fileAppender = new FileAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %c [%p] - %m%n"), "logs/logfile.log");
            fileAppender.setAppend(true); // 如果true，则追加到现有的日志文件末尾；如果false，则覆盖文件中的现有内容
            fileAppender.activateOptions();
            // 将Appender添加到日志记录器
            Logger.getRootLogger().addAppender(fileAppender);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 设置日志记录级别
        LOGGER.setLevel(Level.DEBUG);
    }

    @Override
    public void start(Stage stage) throws Exception {
        AnchorPane anchorPane = new AnchorPane();
        textArea = new TextArea();
        textArea.setEditable(false);

        ScrollPane scrollPane = new ScrollPane(textArea);
        anchorPane.getChildren().add(scrollPane);
        Scene scene = new Scene(anchorPane);
        stage.setScene(scene);
        stage.show();
        // 配置log4j日志记录器
        configureLogger(textArea);

        // 示例日志输出
        for (int i = 0; i < 500; i++) {
            LOGGER.debug("Debug log message");
            LOGGER.info("Info log message");
            LOGGER.warn("Warn log message");
            LOGGER.error("Error log message chenhao");
            Thread.sleep(5000);
        }

        // 关闭log4j日志记录器
//        LogManager.shutdown();
    }

    private static class JTextAreaAppender extends AppenderSkeleton {
        private TextArea textArea;

        public JTextAreaAppender(TextArea textArea) {
            this.textArea = textArea;
        }

        @Override
        protected void append(LoggingEvent loggingEvent) {
            String logMessage = layout.format(loggingEvent);
            Platform.runLater(()->textArea.appendText(logMessage));
        }

        @Override
        public void close() {
            // Nothing to do here
        }

        @Override
        public boolean requiresLayout() {
            return true;
        }
    }
}