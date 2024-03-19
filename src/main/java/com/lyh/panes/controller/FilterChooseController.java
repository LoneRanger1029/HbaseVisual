package com.lyh.panes.controller;

import com.jfoenix.controls.JFXButton;
import com.lyh.panes.enums.FilterType;
import com.lyh.pojo.MyStage;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class FilterChooseController {

    // 用户选择的过滤器
    public static FilterType CURRENT_FILTER;

    @FXML
    private GridPane filterChoosePane;
    @FXML
    private JFXButton rowFilter;
    @FXML
    private JFXButton prefixFilter;
    @FXML
    private JFXButton firstKeyOnlyFilter;
    @FXML
    private JFXButton valueFilter;
    @FXML
    private JFXButton keyOnlyFilter;
    @FXML
    private JFXButton randomRowFilter;
    @FXML
    private JFXButton inclusiveStopFilter;
    @FXML
    private JFXButton columnPrefixFilter;
    @FXML
    private JFXButton columnCountGetFilter;
    @FXML
    private JFXButton singleColumnValueFilter;
    @FXML
    private JFXButton singleColumnValueExcludeFilter;
    @FXML
    private JFXButton skipFilter;
    @FXML
    private JFXButton whileMatchFilter;
    @FXML
    private JFXButton filterList;
    @FXML
    private JFXButton rowFilter13;
    @FXML
    private JFXButton rowFilter14;


    @FXML
    private void initialize(){
        rowFilter.setText("rowKey");
//        rowFilter.setStyle("-fx-background-image: url('icon/rowkf.png');-fx-background-size:100%");
        rowFilter.setStyle("-fx-background-color:#1191a3;");
        rowFilter.setTooltip(new Tooltip("筛选出匹配的所有的行，对于这个过滤器的应用场景，是非常直观的，使用BinaryComparator可以筛选出具有某个行键的行，或者通过改变比较运算符来筛选出符合某一条件的多条数据"));

        prefixFilter.setText("prefixFilter");
        prefixFilter.setStyle("-fx-background-color: #8a8af1;");
        prefixFilter.setTooltip(new Tooltip("筛选出具有特定前缀的行键的数据"));

        firstKeyOnlyFilter.setText("firstKeyOnlyFilter");
        firstKeyOnlyFilter.setStyle("-fx-background-color: #51b4ea");
        firstKeyOnlyFilter.setTooltip(new Tooltip("如果你只想返回的结果集中只包含第一列的数据，那么这个过滤器能够满足你的要求。"));

        valueFilter.setText("valueFilter");
        valueFilter.setStyle("-fx-background-color: #0684f3");
        valueFilter.setTooltip(new Tooltip("按照具体的值来筛选单元格的过滤器，这会把一行中值不能满足的单元格过滤掉，对于每一行的一个列，如果其对应的值不包含row_value，那么这个列将不会返回到客户端。"));

        keyOnlyFilter.setText("keyOnlyFilter");
        keyOnlyFilter.setStyle("-fx-background-color: #07ad7f");
        keyOnlyFilter.setTooltip(new Tooltip("只返回每行的行健"));

        randomRowFilter.setText("randomRowFilter");
        randomRowFilter.setStyle("-fx-background-color: #07ad54");
        randomRowFilter.setTooltip(new Tooltip("按照一定的几率来返回随机的结果集"));

        inclusiveStopFilter.setText("inclusiveStopFilter");
        inclusiveStopFilter.setStyle("-fx-background-color: #0bf5db");
        inclusiveStopFilter.setTooltip(new Tooltip("在使用InclusiveStopFilter时，需要提供一个参数，即终止行键。这个参数用于指定查询的终止条件。当扫描达到终止行键时，扫描操作将停止。"));

        columnPrefixFilter.setText("columnPrefixFilter");
        columnPrefixFilter.setStyle("-fx-background-color: #4dc2b9");
        columnPrefixFilter.setTooltip(new Tooltip("按照列名的前缀来筛选单元格，如果我们想要对返回的列的前缀加以限制的话，可以使用这个过滤器"));

        columnCountGetFilter.setText("columnCountGetFilter");
        columnCountGetFilter.setStyle("-fx-background-color: #07f895");
        columnCountGetFilter.setTooltip(new Tooltip("这个过滤器来返回每行最多返回多少列，并在遇到一行的列数超过我们所设置的限制值的时候，结束扫描操作"));

        singleColumnValueFilter.setText("singleColumnValueFilter");
        singleColumnValueFilter.setStyle("-fx-background-color: #8ce5bb");
        singleColumnValueFilter.setTooltip(new Tooltip("用一列的值决定这一行的数据是否被过滤"));

        singleColumnValueExcludeFilter.setText("singleColumnValueExcludeFilter");
        singleColumnValueExcludeFilter.setStyle("-fx-background-color: #dff518");
        singleColumnValueExcludeFilter.setTooltip(new Tooltip("这个与10种的过滤器唯一的区别就是，作为筛选条件的列的不会包含在返回的结果中。"));

        skipFilter.setText("skipFilter");
        skipFilter.setStyle("-fx-background-color: #f5ec8d");
        skipFilter.setTooltip(new Tooltip("这是一种附加过滤器，其与ValueFilter结合使用，如果发现一行中的某一列不符合条件，那么整行就会被过滤掉"));

        whileMatchFilter.setText("whileMatchFilter");
        whileMatchFilter.setStyle("-fx-background-color: #f6b714;");
        whileMatchFilter.setTooltip(new Tooltip("如果你想要在遇到某种条件数据之前的数据时，就可以使用这个过滤器；当遇到不符合设定条件的数据的时候，整个扫描也就结束了"));

        filterList.setText("filterList");
        filterList.setStyle("-fx-background-color: #f37e17");
        filterList.setTooltip(new Tooltip("HBase的FilterList是一个过滤器，它允许用户组合多个过滤器以执行更复杂的查询。FilterList的主要作用是组合多个过滤条件，并将它们应用到HBase表的扫描操作中。通过使用FilterList，用户可以构建复杂的查询条件，以筛选出满足特定需求的行或列。,FilterList可以嵌套使用FilterList，面对复杂的查询需求也能够得心应手。"));
    }

    @FXML
    public void openRowFilerPane() throws IOException {
        CURRENT_FILTER = FilterType.ROW_KEY_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.ROW_KEY_FILTER);
        stage.show();
    }

    @FXML
    public void openPrefixFilter() throws IOException {
        CURRENT_FILTER = FilterType.PREFIX_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.PREFIX_FILTER);
        stage.show();
    }

    @FXML
    public void openValueFilter() throws IOException {
        CURRENT_FILTER = FilterType.VALUE_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.VALUE_FILTER);
        stage.show();
    }

    @FXML
    public void openFirstKeyOnlyFilter() throws IOException {
        CURRENT_FILTER = FilterType.FIRST_KEY_ONLY_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.FIRST_KEY_ONLY_FILTER);
        stage.show();
    }

    @FXML
    public void openKeyOnlyFilter() throws IOException {
        CURRENT_FILTER = FilterType.KEY_ONLY_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.KEY_ONLY_FILTER);
        stage.show();
    }

    @FXML
    public void openRandomRowFilter() throws IOException {
        CURRENT_FILTER = FilterType.RANDOM_ROW_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.RANDOM_ROW_FILTER);
        stage.show();
    }

    @FXML
    public void openInclusiveStopFilter() throws IOException {
        CURRENT_FILTER = FilterType.INCLUSIVE_STOP_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.INCLUSIVE_STOP_FILTER);
        stage.show();
    }

    @FXML
    public void openColumnPrefixFilter() throws IOException {
        CURRENT_FILTER = FilterType.COLUMN_PREFIX_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.COLUMN_PREFIX_FILTER);
        stage.show();
    }

    @FXML
    public void openColumnCountGetFilter() throws IOException {
        CURRENT_FILTER = FilterType.COLUMN_COUNT_GET_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.COLUMN_COUNT_GET_FILTER);
        stage.show();
    }

    @FXML
    public void openSingleColumnValueFilter() throws IOException {
        CURRENT_FILTER = FilterType.SINGLE_COLUMN_VALUE_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.SINGLE_COLUMN_VALUE_FILTER);
        stage.show();
    }
    @FXML
    public void openSingleColumnValueExcludeFilter() throws IOException {
        CURRENT_FILTER = FilterType.SINGLE_COLUMN_VALUE_EXCLUDE_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.SINGLE_COLUMN_VALUE_EXCLUDE_FILTER);
        stage.show();
    }
    @FXML
    public void openSkipFilter() throws IOException {
        CURRENT_FILTER = FilterType.SKIP_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.SKIP_FILTER);
        stage.show();
    }
    @FXML
    public void openWhileMatchFilter() throws IOException {
        CURRENT_FILTER = FilterType.WHILE_MATCH_FILTER;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.WHILE_MATCH_FILTER);
        stage.show();
    }

    @FXML
    public void openFilterList() throws IOException {
        CURRENT_FILTER = FilterType.FILTER_LIST;
        MyStage stage = new FilterPaneFactory().getPane(FilterType.FILTER_LIST);
        stage.show();
    }



}
