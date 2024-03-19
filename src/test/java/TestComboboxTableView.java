import com.jfoenix.controls.JFXComboBox;
import javafx.application.Application;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;

public class TestComboboxTableView extends Application {
        private static final TableView<Person> table = new TableView<Person>();
        private final ObservableList<Person> data = FXCollections.observableArrayList(
                new Person("张三", 10, "男", "123123@qq.com"),
                new Person("李四", 90, "女", "123321@qq.com"),
                new Person("王富贵", 80, "男", "321123@qq.com")
        );
        @Override
        public void start(Stage stage) {
            //创建根面板
            BorderPane root = new BorderPane();
            Scene scene = new Scene(root, 500, 500);
            //创建表格的列
            TableColumn<Person, String> nameCol = new TableColumn<>("姓名");
            TableColumn<Person, String> ageCol = new TableColumn<>("年龄");
            TableColumn<Person, String> sexCol = new TableColumn<>("性别");
            TableColumn<Person, String> emailCol = new TableColumn<>("邮箱");
            //将表格的列和类的属性进行绑定 - 必须和类的属性名一致
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
            ageCol.setCellValueFactory(new PropertyValueFactory<>("ag"));
            sexCol.setCellValueFactory(new PropertyValueFactory<>("sex"));
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

//            nameCol.setCellFactory(ComboBoxTableCell.forTableColumn());
            nameCol.setCellFactory(new Callback<TableColumn<Person, String>, TableCell<Person, String>>() {
                @Override
                public TableCell<Person, String> call(TableColumn<Person, String> param) {
                    return new TableCell<Person,String>(){
                        private final JFXComboBox<String> comboBox = new JFXComboBox<>();

                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || getTableRow() == null) {
                                setGraphic(null);
                                setText(null);
                            } else {
                                // 用户选择的内容
                                Person person = (Person) getTableRow().getItem();
                                if (person.getName().equals("张三")){
                                    String[] items = {person.getName(),"zs","zd"};
                                    int size = items.length;
                                    for (int i = 1; i <= items.length; i++) {
                                        items[i-1] = items[i-1]+"[version="+i+"/"+size+"]";
                                    }
                                    comboBox.setItems(FXCollections.observableArrayList(items)); // 设置特定于行的选项
                                    comboBox.setValue(items[0]);
                                }
                                setGraphic(comboBox);
                                setText(null);
                            }
                        }
                    };
                }
            });

            //设置可编辑
            table.setEditable(true);
            //将列添加到TableView中
            table.getColumns().addAll(nameCol, ageCol, sexCol, emailCol);
            //设置数据源
            table.setItems(data);
            //将表格添加至根面板
            root.setCenter(table);
            //为舞台设置场景
            stage.setScene(scene);
            //场景显示
            stage.show();
        }

        public static void main(String[] args) {
            Application.launch(args);
        }

        //内部类
        public static class Person {
            private final StringProperty name;
            private final IntegerProperty ag;
            private final StringProperty sex;
            private final StringProperty email;

            public Person(String name, int age, String sex, String email) {
                this.name = new SimpleStringProperty(name);
                this.ag = new SimpleIntegerProperty(age);
                this.sex = new SimpleStringProperty(sex);
                this.email = new SimpleStringProperty(email);
            }

            public int getAg() {
                return ag.get();
            }

            public String getEmail() {
                return email.get();
            }

            public String getName() {
                return name.get();
            }

            public String getSex() {
                return sex.get();
            }
        }

}

