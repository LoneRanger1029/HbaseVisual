package com.lyh.app;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToolbar;
import com.jfoenix.controls.JFXTreeView;
import com.lyh.manager.ViewManager;
import com.lyh.panes.controller.*;
import com.lyh.panes.model.HBaseTableModel;
import com.lyh.panes.model.HTableFactory;
import com.lyh.pojo.*;
import com.lyh.panes.*;
import com.lyh.utils.*;
import com.lyh.utils.ui.AddIconUtils;
import javafx.animation.*;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.commons.math3.analysis.function.Add;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Picture;

import javax.swing.text.View;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AppController {

    private final static int ITEMS_PER_PAGE = 20;   // 表格每页显示20行数据
    private TableView<HashMap<String, SimpleStringProperty>> tableView; //显示分页的的tableView
    private ObservableList<HashMap<String, SimpleStringProperty>> data;
    private static boolean ifConnect = false;   //默认还没有连接到 HBase
    private static AppController appController = Main.fxmlLoader1.getController();

    @FXML
    public ImageView background;
    @FXML
    public BorderPane root;
    @FXML
    public StackPane stackPane;
    public BorderPane logPane;
    @FXML
    public Menu visibleControls;
    @FXML
    public MenuItem createConnectItem;
    @FXML
    public MenuItem closeFileItem;
    @FXML
    public MenuItem bug;
    @FXML
    public MenuItem poi_input;
    @FXML
    public MenuItem poi_output;
    @FXML
    public MenuItem blog;
    @FXML
    public MenuItem mail;
    @FXML
    public JFXButton btn_connect;
    @FXML
    public JFXButton search;    // 普通查询按钮
    @FXML
    public JFXButton btn_table;
    @FXML
    public JFXButton filter_search; // 过滤查询按钮
    @FXML
    public JFXButton import_excel;
    @FXML
    public JFXButton export_excel;
    @FXML
    public BorderPane appPage;
    @FXML
    private JFXComboBox<String> connectName;
    @FXML
    private JFXToolbar toolbar;
    @FXML
    private MyTextArea terminalArea;
//    @FXML
//    private TextField terminalInput;

    private StackPane messages = new StackPane();

    public static CheckMenuItem connectTree = new CheckMenuItem("连接树");
    public static CheckMenuItem clusterStatus = new CheckMenuItem("集群状态信息");
    public static CheckMenuItem console = new CheckMenuItem("控制台");

    private final static Logger logger = Logger.getLogger(AppController.class);

    // 这里不要初始化 窗口应该是单例对象 每点击创建按钮重新new一个窗口
    public static Stage normalSearchStage;
    public static Stage filterSearchStage;
    public static MyStage connectionStage;
    public static MyStage mailStage;

//    private void initialLogPane() throws IOException {
////        FXMLPaneCreator fxmlPaneCreator = new FXMLPaneCreator();
////        logPane = (BorderPane) fxmlPaneCreator.initialPane("src/main/java/com/lyh/panes/fxml/LogTabPane.fxml");
//        logPane = (BorderPane) ViewManager.getInstance().get("LogTabPane");
//        logPane.setPrefWidth(Main.WIDTH-10);
//
//    }


    public MyTextArea getTerminalArea() {
        return terminalArea;
    }

    public StackPane getMessages() {
        return messages;
    }

    @FXML
    public void poi_in() throws IOException {
        // 先判断是否已经连接到Hbase
        if (!ifConnect){
            //如果没有连接到 提示请先连接 Hbase
            Alert warning = MyAlert.getWarning("警告！ 请先连接HBase", "请先连接到 HBase 再进行操作");
            warning.show();
//            MyMessage.init("请先连接").setLevel(Alert.AlertType.WARNING).show(this);
            return; // 阻止打开面板
        }
        openExcelChooser();
    }
    @FXML
    public void poi_out() throws IOException {
        // 先判断是否已经连接到Hbase
        if (!ifConnect){
            //如果没有连接到 提示请先连接 Hbase
            Alert warning = MyAlert.getWarning("警告！ 请先连接HBase", "请先连接到 HBase 再进行操作");
            warning.show();
            return; // 阻止打开面板
        }
        MyStage myStage = new MyStage();
        myStage.initModality(Modality.APPLICATION_MODAL);   // 不可点击父窗口
        myStage.initializeStage("导出数据",false,"icon/excel.png","/fxml/ExportPane.fxml");
        myStage.show();
    }

    @FXML
    public void openBlog(){
        HostServices hostServices = Main.hostServices;
        hostServices.showDocument("https://blog.csdn.net/m0_64261982");
    }

    @FXML
    public void sendMail(){
        try {
            Desktop.getDesktop().mail(new URI("mailto:"+"LYH18235813457@OUTLOOK.com"));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openSavePath() throws IOException {
        poi_out();
    }

    @FXML
    public void initialize() throws IOException {

        root.getChildren().add(messages);
        messages.setLayoutX(400);
        StackPane.setAlignment(messages, Pos.CENTER);
        messages.setEffect(new DropShadow(BlurType.THREE_PASS_BOX, Color.rgb(0, 0, 0, 0.5), 10, 0, 1.5, 1.5));

        background.setImage(new Image("icon/bg.png"));

        // 开启命令行窗口
//        terminalArea.startShell();

//        terminalInput.setOnKeyPressed((e)->{
//            if (e.getCode()== KeyCode.ENTER){
//                String sql = terminalInput.getText();
//                terminalArea.appendText("sqlite > "+sql+"\n");
//                terminalInput.clear();
//                if (sql.contains("select")){
//                    System.out.println("select");
//                    ResultSet set = DBUtils.executeQuery(sql);
//                    try {
//                        while (set.next()){
//                            String message = set.getString("rowKey");
//                            terminalArea.appendText("sqlite > rowKey="+message+"\n");
//                        }
//                    }catch (Exception es){
//                        logger.error("",es);
//                    }
//                }
//                else if (sql.contains("insert"))
//                    DBUtils.executeUpdate(sql);
//            }
//        });

        // 先初始化日志面板 日志才能正常输出到控制台GUI
//        initialLogPane();
        //设置实时时间
        Label time = new Label();		//设置标签
        time.setStyle("-fx-text-fill: white;-fx-font-size: 14px;-fx-font-weight: bold");
        time.setFont(new Font(15));		//设置标签字体
        DateFormat currentTime = new SimpleDateFormat("时间: yyyy.MM.dd hh:mm:ss");	//设置时间格式

        EventHandler<ActionEvent> eventHandler = e->{
            time.setText(currentTime.format(new Date()));
        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(1000), 		(javafx.event.EventHandler<ActionEvent>) eventHandler));				//一秒刷新一次
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
        toolbar.setBottom(time);

        AddIconUtils.addIconToMenuItem(createConnectItem,"icon/connect.png");
        AddIconUtils.addIconToMenuItem(closeFileItem,"icon/close.png");
        AddIconUtils.addIconToMenuItem(bug,"icon/bug.png");
        AddIconUtils.addIconToMenuItem(poi_input,"icon/excel.png");
        AddIconUtils.addIconToMenuItem(poi_output,"icon/excel.png");
        AddIconUtils.addIconToMenuItem(connectTree,"icon/tree.png");
        AddIconUtils.addIconToMenuItem(console,"icon/log.png");
        AddIconUtils.addIconToMenuItem(clusterStatus,"icon/status.png");
        AddIconUtils.addIconToMenuItem(blog,"icon/csdn.png");
        AddIconUtils.addIconToMenuItem(mail,"icon/outlook.png");


//        codeArea = new CodeArea();
//        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
//        codeAreaPane.setCenter(MyCenterPane.getInstance());
        AddIconUtils.addIconToButton(btn_connect,"icon/connect.png"," 连 接 ");
        AddIconUtils.addIconToButton(search,"icon/search.png"," 普通查询 ");
        AddIconUtils.addIconToButton(btn_table,"icon/table.png"," 创建表 ");
        AddIconUtils.addIconToButton(filter_search,"icon/filter.png"," 过滤查询 ");
//        AddIconUtils.addIconToButton(execute,"icon/run.png","运行",20);
        AddIconUtils.addIconToButton(import_excel,"icon/excel.png","excel表格导入");
        AddIconUtils.addIconToButton(export_excel,"icon/Hbase.png","HBase表格导出");


        connectTree.setSelected(false);

        connectTree.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                try {
                    showLeft(newValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        console.setSelected(false);
        console.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                try {
                    showBottom(newValue);
                } catch (IOException e) {
                    logger.error(e);
                    e.printStackTrace();
                }
            }
        });
        clusterStatus.setSelected(false);
        clusterStatus.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                try {
                    showRight(newValue);
                    clusterStatus.setSelected(newValue);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        visibleControls.getItems().addAll(connectTree,console,clusterStatus);
    }
    // 创建新连接-新建连接面板
    @FXML
    public void createNewConnection() throws IOException {
        openConnectionPane();
    }


    public static void setIfConnect(boolean ifConnect) {
        AppController.ifConnect = ifConnect;
    }

    // 打开设计表格面板
    @FXML
    public void openDesignPane() throws IOException {
        // 先判断是否已经连接到Hbase
        if (!ifConnect){
            //如果没有连接到 提示请先连接 Hbase
            Alert warning = MyAlert.getWarning("警告！ 请先连接HBase", "请先连接到 HBase 再进行操作");
            warning.show();

            return; // 阻止打开面板
        }
        Stage designStage = new Stage();
//        designStage.setTitle("Create New Table");
//        designStage.getIcons().add(new Image("icon/table.png"));
        // 打开该窗口后不可点击父窗口
//        designStage.initModality(Modality.APPLICATION_MODAL);
//        designStage.setResizable(true);//设置大小不可变
        FXMLLoader loader = new FXMLLoader();
        AnchorPane connectionPane = loader.load(getClass().getResourceAsStream("/fxml/CreateNewTable.fxml"));
//        AnchorPane connectionPane = (AnchorPane) ViewManager.getInstance().get("CreateNewTable");
        Scene connect_Scene = new Scene(connectionPane);
        designStage.setScene(connect_Scene);
        designStage.show();
    }

    @FXML
    public void executeSQL() throws IOException {

    }

    @FXML
    public void openMailPane() throws IOException {
        mailStage = new MyStage();
//        mailStage.setTitle("发送邮件");
//        mailStage.setResizable(false);
//        mailStage.getIcons().add(new Image("icon/mail.png"));
//        mailStage.initModality(Modality.APPLICATION_MODAL);
//        mailStage.setScene(new Scene((BorderPane)ViewManager.getInstance().get("MailPane")));
        mailStage.initializeStage("发送邮件", false, "icon/mail.png", "/fxml/MailPane.fxml");
//        mailStage.initModality(Modality.APPLICATION_MODAL);
        mailStage.show();
    }
    /**
     * 打开过滤查询面板
     */
    @FXML
    public void openFilterScanPane() throws IOException {

        // 先判断是否已经连接到Hbase
        if (!ifConnect){
            //如果没有连接到 提示请先连接 Hbase
            Alert warning = MyAlert.getWarning("警告！ 请先连接HBase", "请先连接到 HBase 再进行操作");
            warning.show();
//            MyMessage.init("请先连接").setLevel(Alert.AlertType.WARNING).show(this);
            return; // 阻止打开面板
        }
        if (filterSearchStage==null) {
            filterSearchStage = new Stage();
            filterSearchStage.setResizable(false);
            filterSearchStage.setTitle("过滤查询");
            filterSearchStage.initModality(Modality.APPLICATION_MODAL);
            filterSearchStage.getIcons().add(new Image("icon/Hbase.png"));
//        normalSearchStage.initializeStage("过滤查询", false, "icon/Hbase.png", "src/main/java/com/lyh/panes/fxml/FilterChoose.fxml");
            filterSearchStage.setScene(new Scene((GridPane) ViewManager.getInstance().get("FilterChoose")));
        }
        filterSearchStage.show();
    }

    /**
     * 打开普通查询面板
     */
    @FXML
    public void openNormalSearch() throws IOException {
        // 先判断是否已经连接到Hbase
        if (!ifConnect){
            //如果没有连接到 提示请先连接 Hbase
            Alert warning = MyAlert.getWarning("警告！ 请先连接HBase", "请先连接到 HBase 再进行操作");
            warning.show();
            return; // 阻止打开面板
        }
        if (normalSearchStage==null){
            normalSearchStage = new Stage();
            normalSearchStage.setTitle("普通查询");
            normalSearchStage.setResizable(false);
            normalSearchStage.initModality(Modality.APPLICATION_MODAL);
            normalSearchStage.getIcons().add(new Image("icon/Hbase.png"));
//        normalSearchStage.initializeStage("普通查询", false, "icon/Hbase.png", "src/main/java/com/lyh/panes/fxml/NormalSearch.fxml");
            normalSearchStage.setScene(new Scene((AnchorPane)ViewManager.getInstance().get("NormalSearch")));
        }
        normalSearchStage.show();
    }

    public AppController(){
    }

    @FXML
    public void closeFile(){
        Stage stage = (Stage) Main.root.getScene().getWindow();
        stage.close();
    }

    public void showLeft(boolean flag) throws IOException {
        if (flag) {
            // 加载历史连接 下个版本添加
            appPage.setLeft(ConnectionStageController.getScrollPane());
        } else {
            appPage.setLeft(null);
        }
    }

    public void showRight(boolean flag) throws IOException {
        if (flag){
//            FXMLLoader loader = new FXMLLoader();
//            AnchorPane rightPane = loader.load(new FileInputStream(new File("src/main/java/com/lyh/panes/fxml/ClusterStatus.fxml")));
            AnchorPane rightPane = (AnchorPane) ViewManager.getInstance().get("ClusterStatus");
            appPage.setRight(rightPane);
            ClusterStatusController.getInstance().getCurrentClusterStatus();
        }else {
            appPage.setRight(null);
        }
    }
    public void showBottom(boolean flag) throws IOException {
        if (flag){
            if (logPane==null){
                logPane = (BorderPane) ViewManager.getInstance().get("LogTabPane");
//                logPane.setPrefWidth(Main.WIDTH-10);    // todo 后期优化
//                logger.info("这是测试内容~");
//                logger.warn("这是警告内容~");
//                logger.error("这是错误内容~");
            logPane.prefWidthProperty().bind(Main.root.widthProperty());// todo 优化成功
            }
            appPage.setBottom(logPane);
        }else {
            appPage.setBottom(null);
        }
    }

    @FXML
    public void openExcelChooser() throws IOException {
        // 先判断是否已经连接到Hbase
        if (!ifConnect){
            //如果没有连接到 提示请先连接 Hbase
            Alert warning = MyAlert.getWarning("警告！ 请先连接HBase", "请先连接到 HBase 再进行操作");
            warning.show();
            return; // 阻止打开面板
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择excel文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("excel_03-2003","*.xls"),
                new FileChooser.ExtensionFilter("excel_03-2007","*.xlsx")
        );
        File file = fileChooser.showOpenDialog(appPage.getScene().getWindow());
        MyStage myStage = new MyStage();
        myStage.initModality(Modality.APPLICATION_MODAL);   // 不可点击父窗口
        myStage.initializeStage("预览数据",false,"icon/excel.png","/fxml/ExcelPreview.fxml");
        ExcelPreviewController controller = myStage.loader.getController();
        logger.info("用户打开了文件:"+file.getAbsolutePath());
        ExcelPreviewController.CURRENT_FILEPATH = file.getAbsolutePath();
        controller.getFileName().setText(file.getAbsolutePath());
        myStage.show();
    }

    public void flushTable(String tableName) throws IOException {

        TableView<HashMap<String, SimpleStringProperty>> tableView = HTableFactory.initial(tableName);
        BorderPane pane = new BorderPane();
        data = tableView.getItems();
        Pagination pagination = new Pagination((int) Math.ceil((double) data.size() / ITEMS_PER_PAGE), 0);
//        pagination.setPageFactory(this::createPage);
        pagination.setPageFactory((Integer param)->{
            int fromIndex = param * ITEMS_PER_PAGE;
            int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, data.size());
            tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
            return tableView;
        });

//        pane.setCenter(tableView);
        pane.setCenter(pagination);
        appPage.setCenter(pane);
    }

    /**击表格触发操作 打开
     * 双表格
     */
    public void showTableInCenter(String namespace,String tableName) throws IOException {
        HTableFactory factory = new HTableFactory();
        tableView = factory.createTableView(namespace, tableName);
        data = tableView.getItems();
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("删除该行");
        MenuItem addItem = new MenuItem("添加数据");
        contextMenu.getItems().addAll(removeItem,addItem);
        tableView.setContextMenu(contextMenu);
        addItem.setOnAction(event -> {
//            MyStage myStage = new MyStage();
            Stage addRowStage = new Stage();
//            try {
            addRowStage.setScene(new Scene((BorderPane)ViewManager.getInstance().get("AddRow")));
            addRowStage.show();
//                myStage.initializeStage("添加数据",true,"icon/Hbase.png","src/main/java/com/lyh/panes/fxml/AddRow.fxml");
//                myStage.show();
//            } catch (IOException e) {
//                logger.error("初始化添加数据面板失败");
//            }
        });
        // 设置多选
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        removeItem.setOnAction(event -> {
            // 警告是否删除
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("删除确认");
            alert.setHeaderText("请确认是否删除所选行");
            alert.setContentText("这将会从 HBase 中移除,无法恢复!");

            // 获取 DialogPane
            DialogPane dialogPane = alert.getDialogPane();
            // 获取确认按钮并添加事件处理器
            ButtonType confirmButtonType = ButtonType.OK;
            Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
            confirmButton.setOnAction(ev -> {
                alert.close();
                // 获得多选的所有行
                ObservableList<Integer> selectedIndices = tableView.getSelectionModel().getSelectedIndices();
                if (selectedIndices==null || selectedIndices.size() == 0)
                    return;
                String currentNamespace = ConnectionStageController.currentNamespace;
                String currentTable = ConnectionStageController.currentTable;
                if (selectedIndices.size() == 1){
                    HashMap<String, SimpleStringProperty> map = tableView.getSelectionModel().getSelectedItem();
                    String rowKey = map.get("rowKey").get();
                    tableView.getItems().remove(selectedIndices.get(0).intValue());
                    try {
                        boolean status = HBaseDML.deleteRow(currentNamespace, currentTable, rowKey);
                        if (status){
                            MyAlert.getSuccess("删除成功","删除成功!namespace="+currentNamespace+"tableName="+currentTable+"rowKey="+rowKey).show();
                            logger.info("成功删除了一行数据,rowKey="+rowKey);
                        }else {
                            MyAlert.getError("删除失败","删除失败!").show();
                            logger.info("删除数据失败,rowKey="+rowKey);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    boolean status = false;
                    StringBuilder builder = new StringBuilder("roKey["); //存储所有被删除的rowKey
                    int size = selectedIndices.size();
                    for (int i = 0; i < size; i++) {
                        String rowKey = tableView.getSelectionModel().getSelectedItems().get(i).get("rowKey").getValue();
                        try {
                            builder.append(rowKey).append(",");
                            status = HBaseDML.deleteRow(currentNamespace,currentTable,rowKey);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    tableView.getItems().removeAll(tableView.getSelectionModel().getSelectedItems());
                    tableView.refresh();
                    if (status){
                        builder.append("]");
                        MyAlert.getSuccess("删除成功","删除成功!namespace="+currentNamespace+"tableName="+currentTable+"rowKey="+builder.toString()).show();
                        logger.info("成功删除了 "+size+" 行数据数据,rowKeys=["+builder.toString()+"]");
                    }else {
                        MyAlert.getError("删除失败","删除失败!").show();
                        logger.info("删除数据失败,,rowKeys=["+builder.toString()+"]");
                    }
                }
            });
            // 获取取消按钮并添加事件处理器
            ButtonType cancelButtonType = ButtonType.CANCEL;
            Button cancelButton = (Button) dialogPane.lookupButton(cancelButtonType);
            cancelButton.setOnAction(e -> {
                alert.close();
            });
            alert.show();
//            alert.showAndWait();
        });
        if (data.size()!=0){
            Pagination pagination = new Pagination((int) Math.ceil((double) data.size() / ITEMS_PER_PAGE), 0);
            pagination.setPageFactory(this::createPage);
            BorderPane pane = new BorderPane();
            pane.setCenter(pagination);
            appPage.setCenter(pane);
        }else {
            appPage.setCenter(tableView);
        }
    }


    private TableView<HashMap<String, SimpleStringProperty>> createPage(int pageIndex) {
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, data.size());
        tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
        return tableView;
    }

    @FXML
    public void openConnectionPane() throws IOException {
        connectionStage =new MyStage();
        connectionStage.initializeStage("HBase - 新建连接",false,"icon/icon.png","/fxml/ConnectionStage.fxml");
        // 打开该窗口后不可点击父窗口
//        connectionStage.initModality(Modality.APPLICATION_MODAL);
        connectionStage.show();
    }
}
