package com.lyh;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.Callback;

import java.util.Objects;

public class PaginationTableViewExample extends Application {

    private static final int ITEMS_PER_PAGE = 10;
    private TableView<String> tableView;
    private ObservableList<String> data;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        data = FXCollections.observableArrayList();
        for (int i = 1; i <= 100; i++) {
            data.add("Item " + i);
        }

        tableView = new TableView<>();
        TableColumn<String, String> column = new TableColumn<>("Data");
        column.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<String, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<String, String> param) {
                return new SimpleStringProperty(param.getValue());
            }
        });
        tableView.getColumns().add(column);

        Pagination pagination = new Pagination((int) Math.ceil((double) data.size() / ITEMS_PER_PAGE), 0);
        pagination.setPageFactory(this::createPage);
        BorderPane pane = new BorderPane();
        pane.setCenter(pagination);
        primaryStage.setTitle("Pagination TableView Example");
        Scene scene = new Scene(pane, 400, 300);
        scene.getStylesheets().add(getClass().getResource("./css/test.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("./css/material-color.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("./css/bootstrap.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("./css/helpers.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("./css/shape.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("./css/simple-green.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("./css/skeleton.css").toExternalForm());
//        scene.getStylesheets().add(getClass().getResource("./css/typographic.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TableView<String> createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, data.size());
        tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return tableView;
    }
}
