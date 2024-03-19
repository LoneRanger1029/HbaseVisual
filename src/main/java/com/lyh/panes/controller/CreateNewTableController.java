package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.lyh.panes.model.CF_V;
import com.lyh.pojo.ConnectionInfo;
import com.lyh.pojo.MyAlert;
import com.lyh.pojo.MyTreeItem;
import com.lyh.utils.HBaseDDL;
import com.lyh.utils.ui.AddIconUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.ColumnFamilyDescriptorBuilder;
import org.apache.hadoop.hbase.client.TableDescriptorBuilder;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Hash;
import org.apache.log4j.Logger;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateNewTableController {

    public static String CURRENT_NAMESPACE; // 当用户右键时获得当前命名空间
    private static final Logger logger = Logger.getLogger(CreateNewTableController.class);

    private static Pattern num = Pattern.compile("\\d+$");

    public List<TextField> columnFamilyInfos = new ArrayList<>();
    public List<TextField> versions = new ArrayList<>();
    public Stack<CF_V> cf_v = new Stack<>();  // columnFamily -> versions


    @FXML
    public AnchorPane createPane;
    @FXML
    public VBox cf_list;    // 用于存放新添加进来的列族
    @FXML
    public JFXButton add_cf;
    @FXML
    public JFXButton remove_cf;
    @FXML
    public TextField tableName;
    @FXML
    public JFXButton sure;
    @FXML
    public JFXButton cancel;
    @FXML
    public ComboBox<String> namespaces;


    @FXML
    public void initialize(){
        AddIconUtils.addIconToButton(add_cf,"icon/add.png",20);
        AddIconUtils.addIconToButton(remove_cf,"icon/sub.png",20);
        initialComboBox();  // 初始化命名空间
        if (CURRENT_NAMESPACE!=null && !CURRENT_NAMESPACE.equals("")){
            namespaces.setValue(CURRENT_NAMESPACE);
        }
    }

    /**
     * 创建一行 两个输入框: 表名 版本
     */
    public HBox createNewTextField(){
        HBox hBox = new HBox();
        TextField columnFamily = new TextField();
        TextField version = new TextField();
        version.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                Matcher matcher = num.matcher(newValue);
                if (!matcher.find()){
                    Tooltip tooltip = new Tooltip("请输入正确格式的数字");
                    tooltip.getStyleClass().add("red-tooltip");
                    version.setTooltip(tooltip);
                }
            }
        });
        columnFamilyInfos.add(columnFamily);
        versions.add(version);
        hBox.getChildren().addAll(columnFamily,version);
        return hBox;
    }

    public void initialComboBox(){
        namespaces.getItems().setAll(ConnectionInfo.all_namespace);
    }

    @FXML
    public void create() throws IOException {
        // TODO 获得创建表所需要的信息
        // todo 1. 获得当前命名空间
        if (CURRENT_NAMESPACE==null || CURRENT_NAMESPACE.equals("")){
            CURRENT_NAMESPACE = namespaces.getSelectionModel().getSelectedItem();
            if (CURRENT_NAMESPACE.equals("")){
                MyAlert.getWarning("错误","请选择命名空间").show();
                return;
            }
        }
        // todo 2. 获得表名
        String table_name = tableName.getText();
        if (table_name.equals("")){
            MyAlert.getError("错误","请将表名填写完整");
            return;
        }

        if (columnFamilyInfos.size() < 1){
            MyAlert.getWarning("警告","创建表格时列族数量应>=1").show();
            return;
        }

        for (int i = 0; i < columnFamilyInfos.size(); i++) {
            String columnFamily = columnFamilyInfos.get(i).getText();
            String version = versions.get(i).getText();
            if (columnFamily.equals("") || version.equals("")){
                MyAlert.getWarning("警告","请将列族描述信息填写完整!").show();
                return;
            }
            CF_V pojo = new CF_V(columnFamily,version);
            if (cf_v.contains(pojo)){
                MyAlert.getError("错误","有列族重复: "+columnFamily).show();
                return;
            }
            cf_v.push(pojo);
        }
        // 2. 调用方法创建表格
        // 2.1 获取表格的建造者
        TableDescriptorBuilder builder = TableDescriptorBuilder.newBuilder(TableName.valueOf(CURRENT_NAMESPACE,table_name));

        // 2.2 添加参数
        cf_v.iterator().forEachRemaining((pojo)->{
            // 2.3 获取列族建造者
            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(pojo.getColumnFamily()));
            // 2.4 通过建造者创建对应列族描述
            // 添加版本参数-维护的版本数
            columnFamilyDescriptorBuilder.setMaxVersions(Integer.parseInt(pojo.getVersion()));
            // 2.5 创建添加完参数的列族描述
            builder.setColumnFamily(columnFamilyDescriptorBuilder.build());
        });
//        for (Map.Entry<String, String> map : cf_v.entrySet()) {
//            String columnF = map.getKey();
//            String v = map.getValue();
//            // 2.3 获取列族建造者
//            ColumnFamilyDescriptorBuilder columnFamilyDescriptorBuilder = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(columnF));
//            // 2.4 通过建造者创建对应列族描述
//            // 添加版本参数-维护的版本数
//            columnFamilyDescriptorBuilder.setMaxVersions(Integer.parseInt(v));
//            // 2.5 创建添加完参数的列族描述
//            builder.setColumnFamily(columnFamilyDescriptorBuilder.build());
//        }

        logger.info("正在创建表格,{namespace="+CURRENT_NAMESPACE+",table_name="+table_name+"}");
        boolean res = HBaseDDL.createTable(CURRENT_NAMESPACE, table_name, builder);
        if (res){
            MyAlert.getSuccess("成功","表格创建成功,tableName="+table_name).show();
            // 4. 更新 UI 添加一个3级 treeItem
            // 获得namespace对应的treeItem
            TreeItem<String> name_treeItem = ConnectionStageController.namespace_map.get(CURRENT_NAMESPACE);
            MyTreeItem<String> tmp = new MyTreeItem<String>(table_name,3);
            ConnectionStageController connectionStage = new ConnectionStageController();
            connectionStage.setTreeTableIcon(tmp);
            name_treeItem.getChildren().add(tmp);
            ConnectionStageController.tree_refresh(); //刷新treeview
            // 5.更新 connectionInfo
            new ConnectionInfo().addTable(CURRENT_NAMESPACE,table_name);
        }else {
            MyAlert.getError("失败","表格创建失败,请重试").show();
        }
        close();    //  关闭创建表格的窗口
    }
    @FXML
    public void add(){
        cf_list.getChildren().add(createNewTextField());
    }

    @FXML
    public void sub(){
        int last_index = cf_list.getChildren().size()-1;
        cf_list.getChildren().remove(last_index);
        // 除去删除的文本框
        columnFamilyInfos.remove(columnFamilyInfos.size()-1);   // 删除最后一个元素
        // 删除保存的列族信息
        cf_v.pop();
    }


    @FXML
    public void close(){
        Stage stage = (Stage) createPane.getScene().getWindow();
        stage.close();
    }
}
