package com.lyh.utils;
 
import javafx.application.Platform;
import org.fxmisc.richtext.CodeArea;
import java.io.IOException;
import java.util.Scanner;

public class CodeAreaLogAppender extends LogAppender {

    private CodeArea codeArea;

    public CodeAreaLogAppender(CodeArea codeArea) throws IOException {
        super("textArea");
        this.codeArea = codeArea;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(reader);
        // 将扫描到的字符流显示在指定的JLabel上
        while (scanner.hasNextLine()) {
            try {
                // 非 UI 操作涉及到变量的不要放到 UI 线程去，因为线程的运行不同步
                String line = scanner.nextLine();
                Platform.runLater(()->{
                    codeArea.appendText(line+"\n");
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}