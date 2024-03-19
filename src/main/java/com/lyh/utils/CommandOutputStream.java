package com.lyh.utils;

import com.lyh.pojo.MyTextArea;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.io.OutputStream;

public class CommandOutputStream extends OutputStream {
    private MyTextArea textArea;

    public CommandOutputStream(MyTextArea textArea){
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        final String message = new String(buf, off, len);
        Platform.runLater(()-> textArea.appendText(message));
    }
}
