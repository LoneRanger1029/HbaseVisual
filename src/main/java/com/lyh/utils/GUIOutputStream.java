package com.lyh.utils;

import javafx.application.Platform;
import org.fxmisc.richtext.CodeArea;
import java.io.IOException;
import java.io.OutputStream;

public class GUIOutputStream extends OutputStream{

    private CodeArea codeArea;
    private StringBuilder sb = new StringBuilder();

    public GUIOutputStream(CodeArea textArea){
        this.codeArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {

    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException {
        final String message = new String(buf, off, len);
        Platform.runLater(()->{
            sb.append(message);
            codeArea.setWrapText(true);
            codeArea.replaceText(0,0,sb.toString());
            if (sb.toString().length() > 3000) {
                sb.replace(0, sb.toString().length() - 2000, "");
            }
        });
    }
}
