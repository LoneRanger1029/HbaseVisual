import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class ListTreeAndCheckbox extends Application {

    ObservableList<Data> items = FXCollections.observableArrayList();
    ObservableList<Data> tmpList = FXCollections.observableArrayList();

    @Override
    public void start(Stage primaryStage) throws Exception{

        HBox hb = new HBox(20);
        VBox root = new VBox(1);
        ListView<Data> listView = new ListView<Data>();
        ObservableList<Data> list = FXCollections.observableArrayList();

        Button selectFull = new Button("全选");
        Button clearBtn = new Button("清除");
        hb.getChildren().addAll(selectFull,clearBtn);
        TextField tf = new TextField();

        Data data1 = new Data("item1");
        Data data2 = new Data("item2",true);
        Data data3 = new Data("item3");
        Data data4 = new Data("item4");
        list.addAll(data1,data2,data3,data4);

        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println("搜索:"+newValue);
                tmpList = items.filtered(new Predicate<Data>() {
                    @Override
                    public boolean test(Data data) {
                        return data.getName().contains(newValue);
                    }
                });
                listView.setItems(tmpList);
            }
        });

        listView.setItems(list);

        tmpList = listView.getItems();
        items = listView.getItems();

        Callback<ListView<Data>,ListCell<Data>> call = CheckBoxListCell.forListView(new Callback<Data, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(Data param) {
                if (param.isIsSelected()){
                    return param.isSelectedProperty();
                }
                param.setIsSelected(false);
                return param.isSelectedProperty();
            }
        }, new StringConverter<Data>() {
            @Override
            public String toString(Data object) {
                return object.getName();
            }

            @Override
            public Data fromString(String string) {
                return new Data(string);
            }
        });

        listView.setCellFactory(call);

        listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (listView.getSelectionModel().getSelectedItem() != null){
                    listView.getSelectionModel().getSelectedItem().setIsSelected(!listView.getSelectionModel().getSelectedItem().isIsSelected());
                }
            }
        });

        listView.getItems().forEach(new Consumer<Data>() {
            @Override
            public void accept(Data data) {
                data.isSelectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        System.out.println("data:"+data.getName()+":"+newValue);
                    }
                });
            }
        });

        selectFull.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tmpList.forEach(new Consumer<Data>() {
                    @Override
                    public void accept(Data data) {
                        data.setIsSelected(true);
                    }
                });
            }
        });

        clearBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                tmpList.forEach(new Consumer<Data>() {
                    @Override
                    public void accept(Data data) {
                        data.setIsSelected(false);
                    }
                });
            }
        });

        root.getChildren().addAll(tf,hb,listView);

        primaryStage.setTitle("带搜索的多选");
        primaryStage.setScene(new Scene(root, 250, 250));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}

class Data{
    private String name;
    private SimpleBooleanProperty isSelected = new SimpleBooleanProperty();

    public Data(String name, Boolean isSelected) {
        this.name = name;
        this.isSelected = new SimpleBooleanProperty(isSelected);
    }

    public Data(String name){
        this.name = name;
        this.isSelected = new SimpleBooleanProperty(false);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIsSelected() {
        return isSelected.get();
    }

    public SimpleBooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected.set(isSelected);
    }
}