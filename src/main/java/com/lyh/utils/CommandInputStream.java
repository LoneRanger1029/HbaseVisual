package com.lyh.utils;

import javafx.scene.control.TextArea;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CommandInputStream extends InputStream {

    private TextArea textArea;

    public CommandInputStream(TextArea textArea){
        this.textArea = textArea;
    }

    @Override
    public int read(byte[] buf, int off, int len){
        final byte[][] bytes = new byte[1][1];
        textArea.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                // 如果这一行有title 不允许光标上移
                case UP: {
                    event.consume();
                    break;
                }
                case ENTER: {
                    String text = textArea.getText();
                    System.out.println("命令是 "+text.substring(text.length()-2));
                    bytes[0] = text.getBytes(StandardCharsets.UTF_8);
                    System.arraycopy(bytes[0],0,buf,off, bytes[0].length);
                }
            }
        });
        return bytes.length;
    }

    @Override
    public int read() throws IOException {
        return 0;
    }
}
