package com.lyh.panes.controller;

import com.jfoenix.controls.*;
import com.lyh.animations.MyAnimationTransition;
import com.lyh.app.AppController;
import com.lyh.app.Main;
import com.lyh.controls.CFLoading;
import com.lyh.controls.LoadingUtils;
import com.lyh.pojo.*;
import com.lyh.panes.model.HBaseTableModel;
import com.lyh.utils.HBaseDDL;
import com.lyh.utils.HBaseDML;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.log4j.Logger;
import com.lyh.utils.HBaseConnection;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 连接面板-控制器
 */
public class ConnectionStageController {

    private final static Logger logger = Logger.getLogger(ConnectionStageController.class);

    // todo 很重要的属性 判断当前是否已经建立连接
    public static SimpleBooleanProperty connect_status = new SimpleBooleanProperty(false);


    private  AppController appController;

    // 当前选中的命名空间 默认为default 所以创建表格的时候默认创建在default命名空间下
    public static String currentNamespace="default";
    public static String currentTable=null;

    public static ScrollPane scrollPane = new ScrollPane();
//    public static VBox sc_content = new VBox(5); // 滚动面板的内容
    private final static double LEFT_PANE_SIZE = 250;

    public MyTreeItem<String> treeRoot;

    private final StackPane mainRoot = Main.root;  // 主界面的面板 通过这里来调用我们的主界面面板 再通过lookup方法对主界面上的控件进行操作

    public static String[] namespaces;

    public static List<TreeItem<String>> treeItems = new ArrayList<>();
    public String connectName;
    public String hosts;
    public String ports;

    // 通过 命名空间 名称获得 TreeItem
    public static Map<String,TreeItem<String>> namespace_map = new HashMap<>();

    public static JFXTreeView<String> treeView;

    // 得到 FXML 中实例化的对象引用,名称必须一致
    @FXML
    public StackPane stackPane;
    @FXML
    public AnchorPane connectionPane;
    @FXML
    private JFXButton testConnect_btn;
    @FXML
    private JFXButton confirm_btn;
    @FXML
    private JFXButton cancel_btn;
    @FXML
    private JFXTextField connect_name;
    @FXML
    private JFXTextField connect_hosts;
    @FXML
    private JFXTextField connect_port;

    static {
        scrollPane.setPrefWidth(LEFT_PANE_SIZE);   // 设置到静态代码块后生效 ，放到initialize就不生效了
        treeView = new JFXTreeView<>();
        treeView.setPrefSize(LEFT_PANE_SIZE,704);
//        treeView.setPrefWidth(LEFT_PANE_SIZE);
        treeView.prefHeightProperty().bind(scrollPane.prefHeightProperty());
    }

    /**
     * 1.空参构造
     */
    public ConnectionStageController(){

    }
    /**
     * 2.初始化完成后调用
     */
    @FXML
    public void initialize(){
        Image image = new Image("icon/hbase.ico");
        ImageView imageView = new ImageView(image);
        testConnect_btn.setGraphic(imageView);
        connect_hosts.setPromptText("Zookeeper地址");
    }

    /**
     * 添加动作事件的两种方法:
     *  1.这里的方法对应 fxml 文件中的 onAction 的参数,
     *   也就是我们按钮触发的事件
     *   注意: fxml文件中的onAction需要加 '#' 符号
     *  2.我们也可以在控制器中提供一个 getButton 方法，返回需要设置监听的按钮
     *   然后通过我们的 FXMLLoader 的 getController 方法获取这个控制器类中的这个按钮
     *  注意：第 2 种的监听器会覆盖掉第 1种
     *   */
    public void test_connect() throws IOException {
        setParams();
        if(!checkIntegrity()){
            return;
        }

        testConnect_btn.setDisable(true);
        testConnect_btn.setGraphic(new CFLoading(CFLoading.Size.SMALL).setBackColor(Color.TRANSPARENT).setColor(Color.WHITE));


        String hosts = connect_hosts.getText();
        String port = null;
        if (connect_port.getText() != null && !connect_port.getText().equals("")){
            port = connect_port.getText();
        }else {
            port = HBaseConnection.DEFAULT_PORT;
        }
        String final_port = port;
        Thread thread = new Thread(()->{
            boolean flag = HBaseConnection.test_connect(hosts, final_port);
            if (flag)
                Platform.runLater(()->{
                    MyAlert.getSuccess("连接成功","连接成功").show();
                    testConnect_btn.setGraphic(null);
                    testConnect_btn.setDisable(false);
                });
            else
                Platform.runLater(()->{
                    MyAlert.getError("连接失败","连接失败,请重试!").show();
                    testConnect_btn.setGraphic(null);
                    testConnect_btn.setDisable(false);
                });
        });
        thread.start();

    }

    /**
     * 取消事件-关闭窗口
     */
    @FXML
    public void cancel(){
        AppController.connectionStage.close();
    }

    /**
     * 用户在连接面板点击确认-
     *  1.展示命名空间
     *  2.展示表
     */
    @FXML
    public void confirmConnect() throws IOException {

        // 1. todo 检验用户输入内容是否正确
        //保存输入框参数
        setParams();
        if (!checkIntegrity()){
            return;
        }

        String port = null;
        if (connect_port.getText() != null && !connect_port.getText().equals("")){
            port = connect_port.getText();
        }else {
            port = HBaseConnection.DEFAULT_PORT;
        }
        String final_port = port;

        confirm_btn.setDisable(true);
        confirm_btn.setGraphic(new CFLoading(CFLoading.Size.SMALL).setBackColor(Color.TRANSPARENT).setColor(Color.WHITE));

        Thread thread = new Thread(() -> {
            ConnectionInfo.CURRENT_CONNECT_HOST = hosts;
            boolean flag = HBaseConnection.initial_connection(hosts, final_port);// 只实例化一次
            if (!flag) {
                confirm_btn.setGraphic(null);
                confirm_btn.setDisable(false);
                return;
            }
            Platform.runLater(()->{
                confirm_btn.setGraphic(null);
            });
            confirm_btn.setDisable(false);
            Platform.runLater(()->{
                MyAlert.getSuccess("成功","连接成功").show();
            });
            connect_status.set(true);
            HBaseDML.connection = HBaseConnection.connection;
            HBaseDDL.connection = HBaseConnection.connection;
            appController = Main.fxmlLoader1.getController();
            ClusterStatusController.setHosts(hosts);    // 设置节点状态需要的参数
            Platform.runLater(this::closeCurrentStage);

        });
        thread.start();

        connect_status.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                try {
//                appController.showRight(true);
//            AppController.clusterStatus.setSelected(true);

                    // 2. 获取主页的BorderPane 对象,再添加treeView
                    // 连接名作为根节点
                    treeRoot = new MyTreeItem<String>(getConnectName(),1);
                    setTreeRootIcon(treeRoot);

                    treeView.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            MyTreeItem<String> selectedItem = (MyTreeItem<String>) treeView.getSelectionModel().getSelectedItem();
                            int grade = -1;
                            if (selectedItem!=null){
                                grade = selectedItem.getGrade();
                            }
                            if (grade==1){
                                MenuItem closeConnect = MyTreeContextMenu.getCloseConnect();
                                closeConnect.setOnAction(e -> {
                                    ConnectionInfo.destroy();
                                    scrollPane.setContent(null);
                                });
                                treeView.setContextMenu(MyTreeContextMenu.getContextMenu(1));
                            } else if (grade==2){
                                currentNamespace = selectedItem.getValue();
                                CreateNewTableController.CURRENT_NAMESPACE = currentNamespace;
                                MenuItem createTable = MyTreeContextMenu.getCreateTable();
                                createTable.setOnAction(es -> {
                                    try {
                                        appController.openDesignPane();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                MyTreeContextMenu.getCreateNamespace().setOnAction(e -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("创建命名空间");
                                    DialogPane dialogPane = alert.getDialogPane();
                                    Label label = new Label("命名空间: ");
                                    TextField textField = new TextField();
                                    VBox vBox = new VBox();
                                    vBox.getChildren().addAll(label,textField);
                                    vBox.setSpacing(8.0);
                                    dialogPane.setContent(vBox);
                                    ButtonType confirmButtonType = ButtonType.OK;
                                    Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
                                    confirmButton.setOnAction(ev -> {
                                        String namespace = textField.getText();
                                        if (namespace ==null || namespace.equals("")){
                                            MyAlert.getWarning("警告","请将表单填写完整").show();
                                            return;
                                        }
                                        try {
                                            boolean flag = HBaseDDL.createNamespace(namespace);
                                            if (flag){
                                                MyTreeItem<String> treeItem = new MyTreeItem<String>(namespace,2);
                                                setTreeItemIcon(treeItem);
                                                treeRoot.getChildren().add(treeItem);
                                                new ConnectionInfo().addNamespace(namespace);
                                            }
                                        } catch (IOException ioException) {
                                            MyAlert.getError("创建失败","创建命名空间失败").show();
                                        }
                                    });
                                    alert.show();
                                });
                                MyTreeContextMenu.getDeleteNamespace().setOnAction(e -> {
                                    // 警告是否删除
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("删除确认");
                                    alert.setHeaderText("请确认是否删除命名空间 "+selectedItem.getValue());
                                    alert.setContentText("这将会从 HBase 中移除,无法恢复!");

                                    // 获取 DialogPane
                                    DialogPane dialogPane = alert.getDialogPane();
                                    // 获取确认按钮并添加事件处理器
                                    ButtonType confirmButtonType = ButtonType.OK;
                                    Button confirmButton = (Button) dialogPane.lookupButton(confirmButtonType);
                                    confirmButton.setOnAction(ev -> {
                                        boolean flag = HBaseDDL.deleteNamespace(selectedItem.getValue());
                                        if (flag){
                                            new ConnectionInfo().deleteNamespace(selectedItem.getValue());
                                            treeRoot.getChildren().remove(selectedItem);
                                        }
                                    });
                                    alert.show();
                                });
                                treeView.setContextMenu(MyTreeContextMenu.getContextMenu(2));
                            }else if (grade==3){
//                    System.out.println("当前是3级TreeItem");
                                currentTable = selectedItem.getValue();
                                treeView.setContextMenu(MyTreeContextMenu.getContextMenu(3));
                                MenuItem deleteTable = MyTreeContextMenu.getDeleteTable();
                                deleteTable.setOnAction(e -> {
                                    String tableName = selectedItem.getValue();
                                    currentNamespace = selectedItem.getParent().getValue();
                                    try {
                                        boolean status = HBaseDDL.deleteTable(currentNamespace, tableName);
                                        if (status){
                                            selectedItem.getParent().getChildren().remove(selectedItem);
                                            MyAlert.getSuccess("删除成功", "表格" + tableName + "已经成功删除").show();
                                            List<String> tables = ConnectionInfo.namespaceMap.get(currentNamespace);
                                            for (int i = 0; i < tables.size(); i++) {
                                                if (tables.get(i).equals(tableName)){
                                                    tables.remove(i);
                                                    break;
                                                }
                                            }
                                            logger.info("成功删除表格: "+tableName);
                                        }else{
                                            MyAlert.getError("删除失败","表格" + tableName + "未被删除").show();
                                            logger.warn("表格: "+tableName+" 未被");
                                        }
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                    tree_refresh();
                                });
                                MenuItem openTable = MyTreeContextMenu.getOpenTable();
                                openTable.setOnAction(et->{
                                    try {
                                        openTable(selectedItem);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                MenuItem flushTable = MyTreeContextMenu.getFlushTable();
                                flushTable.setOnAction(e -> {
                                    String tableName = selectedItem.getValue();
                                    String columnF = selectedItem.getParent().getValue();
                                    // todo 核心就是调用 appPage.setCenter(tableview)
                                    try {
                                        TableView<HashMap<String, SimpleStringProperty>> tableView = null;
                                        AppController controller = Main.fxmlLoader1.getController();
                                        controller.flushTable(columnF+":"+tableName);
                                    } catch (IOException ioException) {
                                        ioException.printStackTrace();
                                    }
                                });
                                if (event.getClickCount()==2){  //双击打开表格
                                    try {
                                        openTable(selectedItem);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }else if (event.getClickCount()==1){
                                    if (selectedItem.getGrade()==3){
                                        currentNamespace = selectedItem.getParent().getValue();
                                        currentTable = selectedItem.getValue();
                                    }
                                    if (selectedItem.getGrade()==2){
                                        currentNamespace = selectedItem.getValue();
                                    }
//                        CreateTableController.setCurrent_namespace(currentNamespace);
                                    CreateNewTableController.CURRENT_NAMESPACE = currentNamespace;
                                }
                            }
                        }
                    });

                    //连接HBase
                    String hosts = connect_hosts.getText();

                    //todo 获取所有命名空间的 TreeItem
                    List<TreeItem<String>> subTreeItems = createSubTreeItems();
                    for (TreeItem<String> treeItem : subTreeItems) {
                        treeRoot.getChildren().add(treeItem);
                    }


                    treeView.setRoot(treeRoot);

                    //todo 获得所有表名
                    String[] tablesNames = HBaseDDL.getAllTablesNames(hosts);
                    splitTableName(tablesNames);    //把表名数据存放到 HBaseTableModel中

                    // todo 根据TreeItem为所有namespace添加自己的表
                    createSubTableTreeItem(subTreeItems);

                    // 添加到管理信息中
                    ConnectInfoManager.addConnection(hosts);
                    ConnectionInfo connectionInfo = ConnectInfoManager.getConnectionInfo(hosts);
                    if (namespaces!=null && connectionInfo!=null){
                        for (String namespace : namespaces) {
                            connectionInfo.addNamespace(namespace);
                            List<String> list = HBaseTableModel.getList(namespace);
                            connectionInfo.setTables(namespace,list);
                        }
                    }

                    BorderPane appPage = (BorderPane) mainRoot.lookup("#appPage");
                    scrollPane.setContent(treeView);
                    Platform.runLater(()->{
                        appPage.setLeft(scrollPane);
                        AppController.connectTree.setSelected(true);
                    });
                    AppController.setIfConnect(true);
                    logger.info("当前连接主机为 -> " + hosts+" , 当前连接端口为 ->"+final_port);
                } catch (IOException e) {
                    logger.error("连接失败",e);
                }
            }
        });

    }

    /**
     * 检查表单内容的完整性
     */
    public boolean checkIntegrity() throws IOException {
        boolean flag = true;
        FadeTransition ft = MyAnimationTransition.getFadeTransition(connect_name, 0.5, true, 1, 0, 4);
        FadeTransition ft1 = MyAnimationTransition.getFadeTransition(connect_hosts, 0.5, true, 1, 0, 4);
        // 端口号默认 2181
        if (connectName.trim().equals("")){
//            MyAlert.getWarning("警告","请将表单内容填写完整!").show();
            ft.play();
            flag = false;
        }
        if (hosts.trim().equals("")){
            ft.setNode(connect_hosts);
            ft1.play();
            flag = false;
        }
        return flag;
    }


    public void openTable(MyTreeItem<String> selectedItem) throws IOException {
        AppController controller = Main.fxmlLoader1.getController();
        String tableName = selectedItem.getValue();
        String namespace = selectedItem.getParent().getValue();
        currentNamespace = namespace;
        try {
            controller.showTableInCenter(namespace,tableName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 方便当没有建立连接时使用历史连接
    public static JFXTreeView<String> getTreeView() {
        return treeView;
    }

    /**
     * 从用户的输入内容读取来设置参数
     */
    public void setParams(){
        connectName = connect_name.getText();
        hosts = connect_hosts.getText();
        ports = connect_port.getText();
    }


    public String getConnectName() {
        return connectName;
    }


    public JFXButton getTestConnect_btn() {
        return testConnect_btn;
    }

    /**
     * 给根节点 连接名 添加一个图标
     * @param treeRoot 连接名
     */
    public void setTreeRootIcon(TreeItem<String> treeRoot){
        InputStream ins = getClass().getResourceAsStream("/icon/Hbase.png");
        assert ins != null;
        Image image = new Image(ins);
        ImageView icon = new ImageView(image);
        icon.setFitWidth(16);
        icon.setFitHeight(16);
        treeRoot.setGraphic(icon);
    }

    /**
     * 给 namespace 添加一个图标
     * @param treeItem namespace
     */
    public void setTreeItemIcon(TreeItem<String> treeItem){
        InputStream ins = getClass().getResourceAsStream("/icon/namespace.png");
        assert ins != null;
        Image image = new Image(ins);
        ImageView icon = new ImageView(image);
        icon.setFitWidth(14);
        icon.setFitHeight(14);
        treeItem.setGraphic(icon);
    }

    /**
     * 给 table 添加一个图标
     * @param treeItem table
     */
    public static void setTreeTableIcon(TreeItem<String> treeItem){
        InputStream ins = ConnectionStageController.class.getResourceAsStream("/icon/table.png");
        assert ins != null;
        Image image = new Image(ins);
        ImageView icon = new ImageView(image);
        icon.setFitWidth(14);
        icon.setFitHeight(14);
        treeItem.setGraphic(icon);
    }


    /**
     * 获得连接面板的 Stage 对象
     */
    public void closeCurrentStage(){
        Stage currentStage = (Stage) testConnect_btn.getScene().getWindow();
        currentStage.close();
    }

    /**
     * 构建目录树 - 根节点：连接名  子节点：namespace
     * @return TreeItem集合
     */
    public List<TreeItem<String>> createSubTreeItems() throws IOException {
        namespaces = HBaseDDL.getNamespace(hosts);

        int size = namespaces.length;
        for (int i = 0; i < size; i++) {
            String namespaceName = namespaces[i].trim();
            MyTreeItem<String> tmp = new MyTreeItem<String>(namespaceName,2);
            setTreeItemIcon(tmp);
            treeItems.add(tmp);
        }
        return treeItems;
    }

    /**
     * 在每个命名空间下创建 table 树
     * @param treeItems 命名空间集合
     */
    public static void createSubTableTreeItem(List<TreeItem<String>> treeItems) throws IOException {
//        log.info("Method @createSubTableTreeItem start");
        for (TreeItem<String> treeItem : treeItems) {
            String namespace = treeItem.getValue();
            namespace_map.put(namespace,treeItem);
            List<String> list = HBaseTableModel.getList(namespace);
            //有的命名空间下是没有表的,所以存在null的情况
            if (list==null){
//                log.info("table is null : " + namespace);
                continue;//坑!!!!! 这里不能设置为 return;也不能设置为 break; !!!!!  否则读到list为null的namespace后直接退出,其他namespace下的table无法加载
            }
//            log.info("namespace is " + namespace);
            String[] tables = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                tables[i] = list.get(i);
            }
            for (String table : tables) {
                MyTreeItem<String> tmp = new MyTreeItem<String>(table,3);
                setTreeTableIcon(tmp);
                treeItem.getChildren().add(tmp);
            }
        }
    }

    /**
     * 根据是否包含 : 把namespace 和 table 分割开来
     * @param tableNames 数据格式: namespace:table
     */
    public static void splitTableName(String[] tableNames) throws IOException {
        for (String tableName : tableNames) {
            if (tableName.contains(":")){   //非default命名空间
                String[] str = tableName.split(":");
                String namespace = str[0];
                String table = str[1];
                if (!HBaseTableModel.model.containsKey(namespace)){
                    HBaseTableModel.put(str[0],new ArrayList<>());
                }
                List<String> list = HBaseTableModel.getList(str[0]);
                list.add(table);
            }else{  //为 default 命名空间下的表
                if(!HBaseTableModel.model.containsKey("default")){
                    HBaseTableModel.put("default",new ArrayList<>());
                }
                List<String> list = HBaseTableModel.getList("default");
                list.add(tableName);
            }
        }
    }

    public static void tree_refresh(){
        treeView.refresh();
    }

    public static ScrollPane getScrollPane(){
        return scrollPane;
    }

}
