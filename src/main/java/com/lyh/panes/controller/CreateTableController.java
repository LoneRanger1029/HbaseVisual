package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.lyh.pojo.ConnectInfoManager;
import com.lyh.pojo.ConnectionInfo;
import com.lyh.pojo.MyTreeItem;
import com.lyh.pojo.MyAlert;
import com.lyh.panes.MyGridPane;
import com.lyh.utils.HBaseDDL;
import com.lyh.utils.StringUtils;
import com.lyh.utils.ui.AddIconUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 设计表格-创建表格
 */
public class CreateTableController {

    private final static Logger logger = Logger.getLogger(CreateTableController.class);

    @FXML
    private JFXButton addColumnProperty;
    @FXML
    private TextField t_name;
    @FXML
    private JFXButton addColumnFamily;
    @FXML
    private TextField f_name;   // 列族名称
    @FXML
    private MyGridPane gridPane;
    @FXML
    private JFXButton sure;
    @FXML
    private JFXButton close;

    private static String current_namespace = "default";
    // 表格参数
    private final static Map<TextField,String > namespace_map = new HashMap<>();
    // 默认的列族版本数
    private final static int DEFAULT_VERSIONS = 5;

    private final JFXComboBox<String> comboBox = new JFXComboBox<>();
    private final TextField columnProperty = new TextField();
    private final JFXButton sub = new JFXButton();
    private final JFXButton add = new JFXButton();

    @FXML
    public void initialize(){

        initialCombobox(comboBox);
        AddIconUtils.addIconToButton(sub,"icon/sub.png");
        AddIconUtils.addIconToButton(add,"icon/add.png");
        // 还要添加属性的话直接打开一个新的窗口添加
        add.setOnAction(event -> {
            Stage stage = new Stage();  //多个列族属性
            // 打开该窗口后不可点击父窗口
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader();
            BorderPane pane = null;
            try {
                pane = loader.load(new FileInputStream(new File("src/main/java/com/lyh/panes/fxml/CreateMoreProperty.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            CreateMoreProperty controller = loader.getController();
            //获取列族名对应的 columnFamily 名称
            if ((int)MyGridPane.getRowIndex(add) == (int)MyGridPane.getRowIndex(addColumnProperty)){
                controller.setColumnFamilyName(f_name.getText()); //获取这一行的textField
            }

            if (pane!=null){
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.setTitle("Set More ColumnFamily Property");
                stage.show();
            }
        });

        AddIconUtils.addIconToButton(addColumnFamily,"icon/add.png");
        AddIconUtils.addIconToButton(addColumnProperty,"icon/add.png");
        MyGridPane.setGridPane(gridPane);
        // 添加列族
        addColumnFamily.setOnAction(event -> {
            //在当前按钮的位置处添加一个TextField
            JFXButton button = new JFXButton("Add a column property");
            TextField textField = new TextField();
            textField.setPromptText("family_name");
            initialAddColumnProperty(button,textField);   //这里给button添加了事件
            MyGridPane.insertNodeOnThisButton(textField,addColumnFamily,button);
        });

    }

    public static void initialCombobox(JFXComboBox<String> comboBox){
        comboBox.getItems().addAll("maxVersion","compression","inMemory","bloomFilterType","bloomFilterVectorSize","blockCacheEnabled","timeToLive");
    }

    /**
     * 初始化新的按钮 addColumnProperty 添加事件
     * @param button 按钮
     * @param textField 我们要通过这个按钮来定位到它这一行的 列族名称 textField
     */
    public void initialAddColumnProperty(JFXButton button,TextField textField){
        AddIconUtils.addIconToButton(button,"icon/add.png");
        TextField columnProperty = new TextField();
        JFXButton add = new JFXButton();
        JFXButton sub = new JFXButton();
        AddIconUtils.addIconToButton(sub,"icon/sub.png");
        AddIconUtils.addIconToButton(add,"icon/add.png");
        JFXComboBox<String> comboBox = new JFXComboBox<>();
        initialCombobox(comboBox);
        button.setOnAction(event -> {
            MyGridPane.addComboBox(comboBox,button,columnProperty,sub,add);
        });
        add.setOnAction(event -> {
            Stage stage = new Stage();  //多个列族属性
            // 打开该窗口后不可点击父窗口
            stage.initModality(Modality.APPLICATION_MODAL);
            FXMLLoader loader = new FXMLLoader();
            BorderPane pane = null;
            try {
                pane = loader.load(new FileInputStream(new File("src/main/java/com/lyh/panes/CreateMoreProperty.fxml")));
            } catch (IOException e) {
                e.printStackTrace();
            }
            CreateMoreProperty controller = loader.getController();
            //获取列族名对应的 columnFamily名称
            controller.setColumnFamilyName(textField.getText());
            if (pane!=null){
                Scene scene = new Scene(pane);
                stage.setScene(scene);
                stage.setTitle("Set More ColumnFamily Property");
                stage.show();
            }
        });
    }

    @FXML
    public void closeStage(){
        //关闭之前把所有静态变量全部清空
        namespace_map.clear();
        Stage stage = (Stage) close.getScene().getWindow();
        stage.close();
    }
    @FXML
    public void createTable() throws IOException {
        // todo 获取所有参数 创建表格

        // 1. 数据完整性验证 判断界面内是否填写完整

        // 2. 提交参数 创建表格
        String namespace = current_namespace;
        String tableName = t_name.getText();
        logger.info("正在创建表格{命名空间="+namespace+",表名="+tableName+",列族数量="+namespace_map.size()+"}");

        List<String> tableNames = new ArrayList<>();
        for (Map.Entry<TextField, String> entry : namespace_map.entrySet()) {
            if (!entry.getValue().trim().equals("")){
                tableNames.add(entry.getValue());   // todo 2024-1-6 创建表格有问题
            }
//            System.out.println(entry.getValue());
        }

//        boolean status = HBaseDDL.createTable(namespace, tableName, DEFAULT_VERSIONS, StringUtils.listToArray(tableNames));

        boolean status = false;
        // 3. 创建成功-提示框
        if (status){
            MyAlert.getSuccess(" 创建成功 ","表格 " + tableName + " 创建成功").show();
            // 更新 connectionInfo
            new ConnectionInfo().addTable(namespace,tableName);
            logger.info("创建表格成功,namespace="+namespace+",tableName="+tableName);
        }else {
            return; // 防止创建失败仍然更新UI
        }

        // 4. 更新 UI 添加一个3级 treeItem
        // 获得namespace对应的treeItem
        TreeItem<String> name_treeItem = ConnectionStageController.namespace_map.get(namespace);
        MyTreeItem<String> tmp = new MyTreeItem<String>(tableName,3);
        ConnectionStageController connectionStage = new ConnectionStageController();
        connectionStage.setTreeTableIcon(tmp);
        name_treeItem.getChildren().add(tmp);
        ConnectionStageController.tree_refresh(); //刷新treeview

        closeStage();
    }



}
