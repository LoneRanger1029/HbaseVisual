package com.lyh.pojo;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FXMLPaneCreator {
    private Pane root;
    public FXMLLoader loader;

    public FXMLPaneCreator(){
    }

    public Pane initialPane(String fxmlUrl) throws IOException {
        loader = new FXMLLoader();
        root = loader.load(new FileInputStream(new File(fxmlUrl)));
        return root;
    }

    public FXMLLoader getLoader() {
        return loader;
    }
}
