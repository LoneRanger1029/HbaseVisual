package com.lyh.utils;

import com.lyh.panes.enums.FilterType;
import com.lyh.pojo.ConnectionInfo;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.security.visibility.CellVisibility;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HBaseDML {

    //静态属性
    public static Connection connection;
    private final static Logger logger = Logger.getLogger(HBaseDML.class);

    /**
     * 插入数据
     * @param namespace 命名空间
     * @param tableName 表名
     * @param rowKey 行键
     * @param columnFamily 列族
     * @param columnName 列名
     * @param value 值
     */
    public static void putCell(String namespace,String tableName,String rowKey,String columnFamily,String columnName,String value) throws IOException {
        // 1. 获取 Table
        Table table = connection.getTable(TableName.valueOf(namespace,tableName));

        // 2. 调用相关方法实现数据插入
        // 2.1 创建 put 对象
        Put put = new Put(Bytes.toBytes(rowKey));
        // 2.2 给 put 对象添加属性
        put.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(columnName),Bytes.toBytes(value));
        // 2.3 将对象写入对应的方法
        try {
            table.put(put);
        } catch (IOException e) {
            logger.error("数据插入失败",e);
        }

        // 3. 关闭table
        table.close();

    }

    /**
     * 需要比较器的过滤器：
     *  1. rowKeyFilter
     *  2. valueFilter
     *  3. singleColumnValueFilter - bytes,bytes
     */
    public static List<Cell[]> filterWithComparator(String namespace, String tableName, boolean allVersion, CompareOperator compareOperator, ByteArrayComparable comparator, FilterType filterType) throws IOException {

        List<Cell[]> cells = null;

        Table table = connection.getTable(TableName.valueOf(namespace+":"+tableName));
        Filter filter = null;

        if (filterType == FilterType.ROW_KEY_FILTER){
            filter = new RowFilter(compareOperator, comparator);
        }else if (filterType == FilterType.VALUE_FILTER){
            filter = new ValueFilter(compareOperator,comparator);
        }

        cells = getScanResult(filter, allVersion, table);
        table.close();
        return cells;
    }

    /**
     * 设置 过滤器 返回结果
     * 减少方法 filterWithComparator 和 scanWithoutFilter 的代码冗余
     */
    public static List<Cell[]> getScanResult(Filter filter,boolean allVersion,Table table) throws IOException {
        List<Cell[]> cells = new ArrayList<>();
        ResultScanner scanner = null;
        Scan scan = new Scan().setFilter(filter);
        // 读取所有版本
        if (allVersion){
            scanner = table.getScanner(scan.readAllVersions());
        }else {
            scanner = table.getScanner(scan);
        }
        for (Result result : scanner) {
            cells.add(result.rawCells());
        }
        return cells;
    }

    /**
     * 不需要比较器也不需要比较运算符
     *  1. firstKeyOnlyFilter
     *  2. keyOnlyFilter
     *  3. randomRowFilter - float
     *  4. inclusiveStopFilter - bytes
     *  5. columnPrefixFilter - bytes
     *  6. columnCountGetFilter - int
     *  7. singleColumnValueExcludeFilter
     *  8. skipFilter 和 valueFilter 结合使用 - filter
     *  9. whileMatchFilter - filter
     *  10. filterList 比较特殊单独讨论
     *  11. prefixFilter - bytes
     **/

    public static List<Cell[]> scanWithoutFilter(String namespace, String tableName, boolean allVersion, Filter filter) throws IOException {

        List<Cell[]> cells;
        // 1. 获取HTable对象
        Table table = connection.getTable(TableName.valueOf(namespace+":"+tableName));

        cells = getScanResult(filter,allVersion,table);
        table.close();
        return cells;
    }

    public static boolean deleteRow(String namespace,String tableName,String rowKey) throws IOException {

        Table table = connection.getTable(TableName.valueOf(namespace+":"+tableName));

        // 2. 根据rowKey构建delete对象
        Delete delete = new Delete(Bytes.toBytes(rowKey));

        // 3. 执行delete请求
        table.delete(delete);

        // 4. 关闭表
        table.close();

        return true;
    }


    /**
     * 读取一整行数据
     */
    public static Cell[] getCells(String namespace,String tableName,String rowKey) throws IOException {

        Table table = connection.getTable(TableName.valueOf(namespace,tableName));

        Get get = new Get(Bytes.toBytes(rowKey));

        // 设置读取数据的版本所有版本
        get.readAllVersions();

        // 读取数据 得到result对象
        Result result = null;
        try {
            result = table.get(get);

        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }finally {
            table.close();
        }

        //返回结果
        if (result!=null)
            return result.rawCells();
        else
            return null;

    }

    /**
     * 读取最新的一行数据
     */
    public static Cell[] getCellsNewest(String namespace,String tableName,String rowKey) throws IOException {

        Table table = connection.getTable(TableName.valueOf(namespace,tableName));

        Get get = new Get(Bytes.toBytes(rowKey));

        // 设置读取数据的版本所有版本
        get.readVersions(1);

        // 读取数据 得到result对象
        Result result = null;
        try {
            result = table.get(get);

        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }finally {
            table.close();
        }

        //返回结果
        if (result!=null)
            return result.rawCells();
        else
            return null;
    }

    /**
     读取数据 读取对应的一行中的某一列
     * @param namespace 命名空间
     * @param tableName 表名
     * @param rowKey 行键
     * @param columnFamily 列族
     * @param columnName 列名
     * @return 返回最小单位集合 Cells
     */
    public static Cell[] getCells(String namespace,String tableName,String rowKey,String columnFamily,String columnName) throws IOException {

        // 1. 获取Table
        Table table = connection.getTable(TableName.valueOf(namespace,tableName));

        // 2. 创建get对象
        Get get = new Get(Bytes.toBytes(rowKey));

        // 3. 读取数据
        // 如果直接调用get方法读取数据 读到的是一整行数据
        // 如果想读取某一列的数据 需要添加对应的参数
        get.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(columnName));

         // 设置读取数据的版本所有版本
         get.readAllVersions();

         // 读取数据 得到result对象
        Result result = null;
        try {
            result = table.get(get);

        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }finally {
            table.close();
        }

        //返回结果
        if (result!=null)
            return result.rawCells();
        else
            return null;
    }


    /**
     * 打印Cell的值
     * @param cells Cell数组
     */
    public static void printCells(Cell[] cells){
        for (Cell cell : cells) {
            // cell 存储数据比较底层
            String rowKey = new String((CellUtil.cloneRow(cell)));
            String columnFamily = new String(CellUtil.cloneFamily(cell));
            String columnName = new String(CellUtil.cloneQualifier(cell));
            String value = new String(CellUtil.cloneValue(cell));
//            System.out.print(rowKey + "-" + columnFamily + "-" + columnName + "-" + value + "\t");
        }
    }

    // TODO 后期优化 防止返回太多行
    public static String[] scanAll(String namespace,String tableName) throws IOException {

        // 1. 获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        List<String> res_list = new ArrayList<>();
        // 2. 创建Scan对象
        Scan scan = new Scan();
        try {
            // 读取多行数据 获得scanner
            ResultScanner scanner = table.getScanner(scan);
            for (Result result : scanner) {
                putCellsToArray(result.rawCells(),res_list);
            }
            scanner.close();    // 记得关闭资源
        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }

        // 3. 关闭table
        table.close();

        String[] res = new String[res_list.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = res_list.get(i);
        }

        return res;
    }

    public static List<Cell[]> scanAllVersionsOfCells(String namespace, String tableName) throws IOException {
        List<Cell[]> cells = new ArrayList<>();
        // 1. 获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        // 2. 创建Scan对象
        Scan scan = new Scan().readAllVersions();
        try {
            // 读取多行数据 获得scanner
            ResultScanner scanner = table.getScanner(scan);

            for (Result result : scanner) {
                cells.add(result.rawCells());
            }
            scanner.close();    // 记得关闭资源
        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }
        // 3. 关闭table
        table.close();
        return cells;
    }


    public static List<Cell[]> scanAllOfCells(String namespace, String tableName) throws IOException {
        List<Cell[]> cells = new ArrayList<>();
        // 1. 获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));
        // 2. 创建Scan对象
        Scan scan = new Scan();
        try {
            // 读取多行数据 获得scanner
            ResultScanner scanner = table.getScanner(scan);
            for (Result result : scanner) {
                cells.add(result.rawCells());
            }
            scanner.close();    // 记得关闭资源
        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }
        // 3. 关闭table
        table.close();
        return cells;
    }

    /**
     * 将读取出来的数据转为 "rowKey:columnFamily:column:value" 的格式，方便同一处理
     */
    public static void putCellsToArray(Cell[] cells,List<String> list){
        for (Cell cell : cells) {
            String rowKey = new String((CellUtil.cloneRow(cell)));
            String columnFamily = new String(CellUtil.cloneFamily(cell));
            String columnName = new String(CellUtil.cloneQualifier(cell));
            String value = new String(CellUtil.cloneValue(cell));
            list.add(rowKey+":"+columnFamily+":"+columnName+":"+value);
        }
    }

    /**
     * 扫描数据 [起始行健,终止行键)
     * @param namespace 命名空间
     * @param tableName 表名
     * @param startRow 起始行健
     * @param stopRow 终止行键
     */
    public static List<Cell[]> scan(String namespace,String tableName,String startRow,String stopRow) throws IOException {

        List<Cell[]> cells = new ArrayList<>();
        // 1. 获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        // 2. 创建Scan对象
        Scan scan = new Scan();
        // 添加参数 来限制扫描的范围 否则扫描整张表
        scan.withStartRow(Bytes.toBytes(startRow)).withStopRow(Bytes.toBytes(stopRow));

        try {
            // 读取多行数据 获得scanner
            ResultScanner scanner = table.getScanner(scan);
            // result 来记录一行数据 本质是一个 Cell 数组
            // resultScanner 来记录多行数据 本质是一个 result 数组
            for (Result result : scanner) {
                cells.add(result.rawCells());
            }

        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }
        // 3. 关闭table
        table.close();
        return cells;
    }

    public static List<Cell[]> scanAllVersionsWithBorder(String namespace,String tableName,String startRow,String stopRow) throws IOException {

        List<Cell[]> cells = new ArrayList<>();
        // 1. 获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        // 2. 创建Scan对象
        Scan scan = new Scan().readAllVersions();
        // 添加参数 来限制扫描的范围 否则扫描整张表
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(stopRow));

        try {
            // 读取多行数据 获得scanner
            ResultScanner scanner = table.getScanner(scan);
            // result 来记录一行数据 本质是一个 Cell 数组
            // resultScanner 来记录多行数据 本质是一个 result 数组
            for (Result result : scanner) {
                cells.add(result.rawCells());
            }

        } catch (IOException e) {
            logger.error("读取数据失败",e);
        }
        // 3. 关闭table
        table.close();
        return cells;
    }

    /**
     * 单列带过滤扫描数据 [起始行健,终止行键)
     * @param namespace 命名空间
     * @param tableName 表名
     * @param startRow 起始行键
     * @param stopRow 终止行键
     * @param columnFamily 列族
     * @param columnName 列名
     * @param value value值
     * @throws IOException IO异常
     */
    public static void filterScan(String namespace,String tableName,String startRow,String stopRow,String columnFamily,String columnName,String value) throws IOException {

        // 1. 获取table
        Table table = connection.getTable(TableName.valueOf(namespace, tableName));

        // 2. 创建Scan对象
        Scan scan = new Scan();
        // 添加参数 来限制扫描的范围 否则扫描整张表
        scan.withStartRow(Bytes.toBytes(startRow));
        scan.withStopRow(Bytes.toBytes(stopRow));

        // 可以添加多个过滤
        FilterList filterList = new FilterList();

        // 创建过滤器
        // (1) 结果只保留当前列的数据
        ColumnValueFilter columnValueFilter = new ColumnValueFilter(
                Bytes.toBytes(columnFamily),
                Bytes.toBytes(columnName),
                // 比较关系
                CompareOperator.EQUAL,
                Bytes.toBytes(value));
        filterList.addFilter(columnValueFilter);

        // (2) 结果保留整行数据 包含其他列
        // 结果会保留没有当前列的数据 比如我们要info:name=张三的整行数据 但是有这么一行数据的info列族下name是空的 这种数据也会被获取到结果集中去
        SingleColumnValueFilter singleColumnValueFilter = new SingleColumnValueFilter(
                Bytes.toBytes(columnFamily),
                Bytes.toBytes(columnName),
                CompareOperator.EQUAL,
                Bytes.toBytes(value)
        );
//        filterList.addFilter(singleColumnValueFilter);

        // 添加过滤
//        scan.setFilter(filterList);
        scan.setFilter(filterList);

        try {
            // 读取多行数据 获得scanner
            ResultScanner scanner = table.getScanner(scan);

            // result 来记录一行数据 本质是一个 Cell 数组
            // resultScanner 来记录多行数据 本质是一个 result 数组
//            for (Result result : scanner) {
//                printCells(result.rawCells());
//                System.out.println();   //打印完一个行键对应的行后换行
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 3. 关闭table
        table.close();
    }

    /**
     * 删除一行中的一列数据
     * @param namespace 命名空间
     * @param tableName 表格名称
     * @param rowKey 行键
     * @param columnFamily 列族
     * @param columnName 列名
     */
    public static void deleteColumn(String namespace,String tableName,String rowKey,String columnFamily,String columnName) throws IOException {

        // 1. 获取table
        Table table = connection.getTable(TableName.valueOf(namespace,tableName));

        // 2. 创建delete对象
        Delete delete = new Delete(Bytes.toBytes(rowKey));

        // 添加列信息
        // addColumn 删除最新版本
        // addColumns 删除所有版本

        delete.addColumn(Bytes.toBytes(columnFamily),Bytes.toBytes(columnName));

//        delete.addColumns(Bytes.toBytes(columnFamily),Bytes.toBytes(columnName));

        // 删除数据
        try {
            table.delete(delete);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭table
        table.close();
    }

    public static void main(String[] args) throws IOException {
        // 1. 获取HTable对象
        HBaseConnection.initial_connection("hadoop102","2181");
        Table table = HBaseConnection.connection.getTable(TableName.valueOf("bigdata","student"));

        ResultScanner scanner = null;
        Scan scan = new Scan().setFilter(new FirstKeyOnlyFilter());
        scanner = table.getScanner(scan);
        for (Result result : scanner) {
            printCells(result.rawCells());
        }
        table.close();

        // 测试插入数据
//        for (int i = 100; i < 1000; i++) {
//            putCell("bigdata","student","9999","info","name","hbase");
//        }
//        HBaseConnection.connectHbaseBySingle("hadoop102");
//        connection = HBaseConnection.connection;
        // 1. 获取table
//        Table table = connection.getTable(TableName.valueOf("bigdata","student"));

        // 2. 创建Scan对象
//        Scan scan = new Scan();

//        try {
//             读取多行数据 获得scanner
//            ResultScanner scanner = table.getScanner(scan);
//             result 来记录一行数据 本质是一个 Cell 数组
//             resultScanner 来记录多行数据 本质是一个 result 数组
//            for (Result result : scanner) {
//                printCells(result.rawCells());
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        table.close();

//        filterWithComparator("bigdata","student",true,CompareOperator.EQUAL,new BinaryComparator(Bytes.toBytes("1")));

//
//        Cell[] cells = getCells("bigdata", "student", "1");
//        assert cells != null;
//        printCells(cells);

        // 测试读取数据
//        Cell[] cells = getCells("bigdata", "student", "1001", "info", "name");
//        if (cells!=null)
//            printCells(cells);


        // 测试扫描数据

//        filterScan("bigdata","student","1001","1006","info","name","ls");

        // 测试删除数据
//        deleteColumn("bigdata","student","1001","info","name");

        System.out.println("其他代码");

        // 关闭连接
        HBaseConnection.closeConnection();
    }
}
