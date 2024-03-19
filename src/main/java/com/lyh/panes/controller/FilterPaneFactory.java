package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.lyh.panes.enums.FilterType;
import com.lyh.panes.model.GetAllVersionsResult;
import com.lyh.panes.model.ScanResult;
import com.lyh.pojo.ConnectionInfo;
import com.lyh.pojo.MyStage;
import com.lyh.utils.HBaseDML;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class FilterPaneFactory {

    private final static int ITEMS_PER_PAGE = 20;   // 表格每页显示20行数据

    private static boolean ifNeedOperator;  //是否需要比较器和比较运算符

    private final static Logger logger = Logger.getLogger(Filter.class);

    public final static String FXML_URL = "/fxml/FilterPane.fxml";
    public final static String ICON_URL = "icon/Hbase.png";

    public final static String[] comparators = {"RegexStringComparator","BinaryPrefixComparator","SubstringComparator","BinaryComparator","BigDecimalComparator","BitComparator","LongComparator","NullComparator"};

    public static String currentNamespace;
    public static String currentTableName;


    @FXML
    public VBox paramsVb;
    @FXML
    public BorderPane filterPane;
    @FXML
    public HBox hb_top;
    @FXML
    public VBox vb_namespace;
    @FXML
    public VBox vb_table;
    @FXML
    public ComboBox<String> namespaces;
    @FXML
    public ComboBox<String> tables;
    @FXML
    public ComboBox<String> operatorComboBox;
    @FXML
    public ComboBox<String> comparatorComboBox;
    @FXML
    public JFXButton search;
    @FXML
    public JFXRadioButton allVersions;
    @FXML
    public TextField paramTextField;
    @FXML
    public Label paramName;
    @FXML
    public TableView<HashMap<String, SimpleStringProperty>> table_area;


    private ObservableList<HashMap<String, SimpleStringProperty>> data;

    @FXML
    public void initialize(){
        initialCombobox();
    }
    public void initialCombobox(){
        // 初始化命名空间列表
        namespaces.getItems().addAll(ConnectionInfo.all_namespace);
        if (FilterChooseController.CURRENT_FILTER.getFilterName().equals("rowKeyFilter") || FilterChooseController.CURRENT_FILTER.getFilterName().equals("valueFilter") || FilterChooseController.CURRENT_FILTER.getFilterName().equals("singleColumnValueFilter")) {

            ifNeedOperator = true;

            // 初始化比较运算符列表
            CompareOperator[] values = CompareOperator.values();
            String[] operators = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                operators[i] = values[i].name();
            }
            logger.info("比较运算符初始化成功");
            if (operatorComboBox!=null && operatorComboBox.getItems().size()>0){
                operatorComboBox.getItems().clear();
            }
            assert operatorComboBox != null;
            operatorComboBox.getItems().addAll(FXCollections.observableArrayList(operators));
            // 需要比较器的过滤器：
            //  1. rowKeyFilter
            //  2. valueFilter
            //  3. singleColumnValueFilter
            if (comparatorComboBox!=null && comparatorComboBox.getItems().size()>0){
                comparatorComboBox.getItems().clear();
            }
            assert comparatorComboBox != null;
            comparatorComboBox.getItems().addAll(FXCollections.observableArrayList(comparators));
            logger.info(FilterChooseController.CURRENT_FILTER.getFilterName()+"比较器初始化成功");
        }else {
            ifNeedOperator = false;
        }
    }

    public MyStage getPane(FilterType filterType) throws IOException {
        MyStage myStage = new MyStage();
        switch (filterType){
            case ROW_KEY_FILTER:
                myStage.initializeStage("rowKey 过滤器",false,ICON_URL,FXML_URL);
                break;
            case PREFIX_FILTER:
                myStage.initializeStage("prefixFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case VALUE_FILTER:
                myStage.initializeStage("valueFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case FIRST_KEY_ONLY_FILTER:
                myStage.initializeStage("firstKeyOnlyFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case KEY_ONLY_FILTER:
                myStage.initializeStage("keyOnlyFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case RANDOM_ROW_FILTER:
                myStage.initializeStage("randomRowFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case INCLUSIVE_STOP_FILTER:
                myStage.initializeStage("inclusiveStopFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case COLUMN_COUNT_GET_FILTER:
                myStage.initializeStage("columnCountGetFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case SINGLE_COLUMN_VALUE_FILTER:
                myStage.initializeStage("singleColumnValueFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case SINGLE_COLUMN_VALUE_EXCLUDE_FILTER:
                myStage.initializeStage("singleColumnValueExcludeFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case SKIP_FILTER:
                myStage.initializeStage("skipFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case WHILE_MATCH_FILTER:
                myStage.initializeStage("whileMatchFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            case FILTER_LIST:
                myStage.initializeStage("filterFilter 过滤器",false,ICON_URL,FXML_URL);
                break;
            default:
                myStage.initializeStage("rowFilter过滤器",false,ICON_URL,FXML_URL);
        }
        return myStage;
    }

    // 点击搜索 执行
    @FXML
    public void scan_filter() throws IOException {
        currentNamespace = namespaces.getSelectionModel().getSelectedItem();
        currentTableName = tables.getSelectionModel().getSelectedItem();
        boolean all = allVersions.isSelected();
        String compareOperator = operatorComboBox.getSelectionModel().getSelectedItem();
        String comparator = comparatorComboBox.getSelectionModel().getSelectedItem();

        List<Cell[]> cells = null;
        // 根据不同类型的过滤器选择参数
        if (ifNeedOperator){
            cells = HBaseDML.filterWithComparator(currentNamespace, currentTableName, all, getCompareOperator(compareOperator), createComparator(comparator), FilterChooseController.CURRENT_FILTER);
            List<String> list = new ArrayList<>();  // list是用来存放查询结果的
            for (Cell[] cell : cells) {
                List<String> tmp = GetPaneController.putCellsToList(cell);
                list.addAll(tmp);
            }
            if (all){
                // 查询所有版本
                showData(new GetAllVersionsResult(list));
            }else {
                // 查询最新版本
                showData(new ScanResult(all,list));
            }
        }else {
            switch (FilterChooseController.CURRENT_FILTER){
                case PREFIX_FILTER:
                    String val = paramTextField.getText();
                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new PrefixFilter(Bytes.toBytes(val)));
                    break;
                case FIRST_KEY_ONLY_FILTER:
                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new FirstKeyOnlyFilter());
                    break;
                case KEY_ONLY_FILTER:
                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new KeyOnlyFilter());
                    // 特殊的过滤器 特殊处理
                    List<String> list = new ArrayList<>();
                    for (Cell[] cell : cells) {
                        for (Cell cell1 : cell) {
                            list.add(new String(CellUtil.cloneRow(cell1)));
                        }
                    }
                    if (all){
                        showData(new GetAllVersionsResult(list));
                    }else {
                        showData(new ScanResult(all,list));
                    }
                    return;
                case RANDOM_ROW_FILTER:
                    String value = paramTextField.getText();
                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new RandomRowFilter(Float.parseFloat(value)));
                    break;
                case INCLUSIVE_STOP_FILTER:
                    String stopRowKey = paramTextField.getText();
                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new InclusiveStopFilter(Bytes.toBytes(stopRowKey)));
                    break;
                case COLUMN_COUNT_GET_FILTER:
                    String param = paramTextField.getText();
                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new ColumnCountGetFilter(Integer.parseInt(param)));
                    break;
                case SINGLE_COLUMN_VALUE_EXCLUDE_FILTER:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new SingleColumnValueExcludeFilter());
                    break;
                case SKIP_FILTER:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new SkipFilter());
                    break;
                case WHILE_MATCH_FILTER:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new WhileMatchFilter());
                    break;
                case FILTER_LIST:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new FilterList());
                    break;
            }
            List<String> list = new ArrayList<>();  // list是用来存放查询结果的
            assert cells != null;
            for (Cell[] cell : cells) {
                List<String> tmp = GetPaneController.putCellsToList(cell);
                list.addAll(tmp);
            }
            if (all){
                // 查询所有版本
                showData(new GetAllVersionsResult(list));
            }else {
                // 查询最新版本
                showData(new ScanResult(all,list));
            }
        }
    }

    public ByteArrayComparable createComparator(String comparator){
        String param = paramTextField.getText();
        switch (comparator){
            case "RegexStringComparator":
                paramName.setText("正则表达式");
                return new RegexStringComparator(param);
            case "BinaryPrefixComparator":
                return new BinaryPrefixComparator(Bytes.toBytes(param));
            case "SubstringComparator":
                return new SubstringComparator(param);
            case "BinaryComparator":
                return new BinaryComparator(Bytes.toBytes(param));
            case "BigDecimalComparator":
                return new BigDecimalComparator(new BigDecimal(param));
            case "BitComparator":
//                Label label = new Label("位运算符号");
//                ComboBox<String> bit_char = new ComboBox<>();
//                bit_char.getItems().addAll("&","|","^");
//                paramsVb.getChildren().addAll(label,bit_char);
//                BitComparator.BitwiseOp param2 = null;
//                if (bit_char.getSelectionModel().getSelectedItem().equals("&")){
//                    param2 = BitComparator.BitwiseOp.AND;
//                }else if (bit_char.getSelectionModel().getSelectedItem().equals("|")){
//                    param2 = BitComparator.BitwiseOp.OR;
//                }else if (bit_char.getSelectionModel().getSelectedItem().equals("^")){
//                    param2 = BitComparator.BitwiseOp.XOR;
//                }
                return new BitComparator(Bytes.toBytes(param),null);
            case "LongComparator":
                return new LongComparator(Long.parseLong(param));
            case "NullComparator":
                return new NullComparator();
            default:
                return null;
        }

    }
    /**
     * 判断用户选择的比较运算符
     */
    public CompareOperator getCompareOperator(String compareOperator){
        for (CompareOperator value : CompareOperator.values()) {
            if (compareOperator.equals(value.name())){
                return value;
            }
        }
        return null;
    }

    public void shoKeyOnly(Cell[] cells){

    }

    public void showData(ScanResult result){
            TableView<HashMap<String, SimpleStringProperty>> tableView = new TableView<>();

            // 1.制作表头
            // 1.1 第一列
            TableColumn<HashMap<String,SimpleStringProperty>,String> rowKeyCol = new TableColumn<>("rowKey");
            rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
            rowKeyCol.setMinWidth(100);
            tableView.getColumns().add(rowKeyCol);

            // 添加列族
            List<TableColumn<HashMap<String,SimpleStringProperty>,String>> columnFamily_list = new ArrayList<>();
            Set<String> columnFamilies = result.getColumnFamilies();
            if(columnFamilies==null ||columnFamilies.size()==0){
                return;
            }
            for (String columnFamily : columnFamilies) {
                TableColumn<HashMap<String,SimpleStringProperty>,String> columnFam = new TableColumn<>(columnFamily);
                columnFamily_list.add(columnFam);
                tableView.getColumns().add(columnFam);
            }

            // 1.4 把列放到指定列族当中
            // 遍历列
            for (TableColumn<HashMap<String,SimpleStringProperty>, String> columnF : columnFamily_list) {
                Set<String> columns = result.getColumns(columnF.getText());
//                System.out.println("列族"+columnF.getText()+"下有"+columns.size()+"个列");
                for (String column : columns) {
                    TableColumn<HashMap<String,SimpleStringProperty>,String> col = new TableColumn<>(column);
                    col.setCellValueFactory(new MapValueFactory(columnF.getText()+":"+column));
                    columnF.getColumns().add(col);
                }
            }
            List<HashMap<String,SimpleStringProperty>> list = new ArrayList<>(1);

            for (String s : result.getRowKeySet()) {
                list.add(result.valueMap.get(s));
            }

            ContextMenu contextMenu = new ContextMenu();
            MenuItem removeItem = new MenuItem("删除该行");
            contextMenu.getItems().add(removeItem);
            tableView.setContextMenu(contextMenu);
            // 设置多选
            tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
            removeItem.setOnAction(event -> {
                ScanAllController.removeRow(tableView);
            });

            data = FXCollections.observableArrayList(list);
            tableView.setItems(data);

            // 分页显示
            Pagination pagination = new Pagination((int) Math.ceil((double) data.size() / ITEMS_PER_PAGE), 0);
            pagination.setPageFactory(new Callback<Integer, Node>() {
                @Override
                public Node call(Integer param) {
                    int fromIndex = param * ITEMS_PER_PAGE;
                    int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, data.size());
                    tableView.setItems(FXCollections.observableArrayList(data.subList(fromIndex, toIndex)));
                    return tableView;
                }
            });
            BorderPane pane = new BorderPane();
            pane.setCenter(pagination);
            filterPane.setCenter(pane);
            tableView.refresh();
        }


    public void showData(GetAllVersionsResult result){
        TableView<HashMap<String, SimpleStringProperty>> tableView = new TableView<>();
        // 1.制作表头
        // 1.1 第一列
        TableColumn<HashMap<String, SimpleStringProperty>, String> rowKeyCol = new TableColumn<>("rowKey");
        rowKeyCol.setCellValueFactory(new MapValueFactory("rowKey"));
        rowKeyCol.setMinWidth(100);
        tableView.getColumns().add(rowKeyCol);

        // 添加列族
        List<TableColumn<HashMap<String, SimpleStringProperty>, String>> columnFamily_list = new ArrayList<>();
        Set<String> columnFamilies = result.getColumnFamilies();
        if (columnFamilies == null || columnFamilies.size() == 0) {
            return;
        }
        for (String columnFamily : columnFamilies) {
            TableColumn<HashMap<String, SimpleStringProperty>, String> columnFam = new TableColumn<>(columnFamily);
            columnFamily_list.add(columnFam);
            tableView.getColumns().add(columnFam);
        }

        // 1.4 把列放到指定列族当中
        // 遍历列
        for (TableColumn<HashMap<String, SimpleStringProperty>, String> columnF : columnFamily_list) {
            Set<String> columns = result.getColumns(columnF.getText());
//            System.out.println("列数="+columns.size());
            for (String column : columns) {
                TableColumn<HashMap<String, SimpleStringProperty>, String> col = new TableColumn<>(column);
                col.setCellValueFactory(new MapValueFactory(columnF.getText() + ":" + column));
                columnF.getColumns().add(col);
            }
        }
        List<HashMap<String, SimpleStringProperty>> list = new ArrayList<>();
        for (String rowKey : result.getRowKeySet()) {
            for (Map.Entry<String,List<SimpleStringProperty>> entry : result.valueMap.get(rowKey).entrySet()){
                String columnQualify = entry.getKey();
                List<SimpleStringProperty> value = entry.getValue();
                for (int i = 0; i < value.size(); i++) {
                    // todo 这是 tableview 使用 hashMap 时要求的数据源类型
                    HashMap<String, SimpleStringProperty> item = new HashMap<>();
                    if (columnQualify.equals("rowKey"))
                        continue;
                    // todo 在数据后面标注版本号码
                    String tmp  =value.get(i).getValue();
                    SimpleStringProperty cellValue = new SimpleStringProperty(tmp + "[version=" + (i + 1) +"/"+ value.size() + "]");
//                    item.put(columnQualify,cellValue);
                    item.put(columnQualify,cellValue);
                    item.put("rowKey",new SimpleStringProperty(rowKey));
                    list.add(item);
                }
            }
        }
        data = FXCollections.observableArrayList(list);
        tableView.setItems(data);
        filterPane.setCenter(tableView);
        tableView.refresh();
    }


    @FXML
    public void chooseTable(){
        for (String item : namespaces.getItems()) {
            if (namespaces.getSelectionModel().getSelectedItem().equals(item)){
                tables.getItems().clear();
                List<String> list = ConnectionInfo.namespaceMap.get(item);
                if(list!=null && list.size()!=0)
                    tables.getItems().addAll(new HashSet<>(list));
            }
        }
    }

    @FXML
    public void chooseOperator(){

    }

    // 只有部分过滤器需要设置比较器 所以根据过滤器类型判断要不要给比较器列表初始化
    @FXML
    public void chooseComparator(){
        if (ifNeedOperator){
            switch (comparatorComboBox.getSelectionModel().getSelectedItem()){
                case "RegexStringComparator":
                    paramName.setText("正则表达式");
                    break;
                case "BinaryPrefixComparator":
                    paramName.setText("prefix");
                    break;
                case "SubstringComparator":
                    paramName.setText("substring");
                    break;
                case "BinaryComparator":
                    break;
                case "BigDecimalComparator":
                    paramName.setText("value");
                    break;
                case "BitComparator":
                    Label label = new Label("位运算符号");
                    ComboBox<String> bit_char = new ComboBox<>();
                    bit_char.getItems().addAll("&","|","^");
                    paramsVb.getChildren().addAll(label,bit_char);
                    break;
                case "LongComparator":
                    paramName.setText("long_value");
                    break;
                case "NullComparator":
                    paramName.setText("不需要添加参数");
                    break;
                default:
                    paramName.setText("param");
            }
        }else{
            switch (FilterChooseController.CURRENT_FILTER){
                case PREFIX_FILTER:
                    paramName.setText("prefix");
                    break;
                case FIRST_KEY_ONLY_FILTER:
                case KEY_ONLY_FILTER:
                    paramName.setText("不需要参数");
                    break;
                case RANDOM_ROW_FILTER:
                    paramName.setText("chance");
                    break;
                case INCLUSIVE_STOP_FILTER:
                    paramName.setText("stopRowKey");
                    break;
                case COLUMN_COUNT_GET_FILTER:
                    paramName.setText("column_count");
                    break;
                case SINGLE_COLUMN_VALUE_EXCLUDE_FILTER:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new SingleColumnValueExcludeFilter());
                    break;
                case SKIP_FILTER:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new SkipFilter());
                    break;
                case WHILE_MATCH_FILTER:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new WhileMatchFilter());
                    break;
                case FILTER_LIST:
//                    cells = HBaseDML.scanWithoutFilter(currentNamespace,currentTableName,all,new FilterList());
                    break;
            }
        }

    }

}
