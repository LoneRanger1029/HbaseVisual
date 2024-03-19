package com.lyh.panes.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.HashMap;

public interface AbstractHTableFactory {
    TableView<HashMap<String, SimpleStringProperty>> createTableView (String namespace,String tableName) throws IOException;
}
