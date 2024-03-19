package com.lyh.utils;

import com.lyh.panes.enums.POIType;
import com.lyh.panes.model.HTableFactory;
import com.lyh.panes.model.TablePojo;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.TableView;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.reactfx.util.Tuple2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class POIExcelWrite {

    private final static Logger logger = Logger.getLogger(POIExcelWrite.class);

    // 列(这里用全限定名 防止不同列族下有列重复)在excel中的列号 防止插入数据时乱序
    private Map<String,Integer> column_col_index_map;

    private TablePojo pojo;
    private Workbook workbook;
    private POIType type;
    private String tableName;
    private String outputFilePath;
    private Sheet sheet;

    public POIExcelWrite(String tableName,POIType type,String outputFilePath) throws IOException {
        this.tableName = tableName;
        this.type = type;
        this.outputFilePath = outputFilePath;
        this.column_col_index_map = new HashMap<>();
        if (HTableFactory.tables.get(tableName)==null){
            // 加载表
            HTableFactory.tableViewMap.put(tableName,HTableFactory.initial(tableName));
        }
        this.pojo = HTableFactory.tables.get(tableName);

        if (type == POIType.POI_2003){
            workbook = new HSSFWorkbook();
        }else if (type == POIType.POI_2007){
            workbook = new XSSFWorkbook();
        }
        workbook.createSheet("0");
        sheet = workbook.getSheetAt(0);
    }

    public void writeToExcel() throws IOException {
        createTitle();
        createData();
        //生成一张表 03版本用 xls 结尾
        File file = new File(outputFilePath);
        FileOutputStream outputStream = new FileOutputStream(file);
        //输出到本地
        workbook.write(outputStream);
        //关闭流
        outputStream.close();
        logger.info("文件已保存至-> "+outputFilePath);
    }

    /**
     * 创建表头
     */
    private void createTitle(){
        sheet.addMergedRegion(new CellRangeAddress(0,1,0,0));   // rowKwy 合并单元格
        Row row_0 = sheet.createRow(0);
        row_0.createCell(0).setCellValue("rowKey");

        int col_Index = 1;  // 当前的列
        Map<String, Integer> cf_map = pojo.getColumnFamilySize();   // 获得  k:列族 -> v:列的数量
        // 为列族合并单元格
        for (Map.Entry<String, Integer> entry : cf_map.entrySet()) {
            String columnFamily = entry.getKey();
            int col_size = entry.getValue();
//            System.out.println("列族 "+columnFamily+" 下共有 "+col_size+" 个列");
            if (!columnFamily.equals("rowKey")){
                sheet.addMergedRegion(new CellRangeAddress(0,0,col_Index,col_Index+col_size-1));
                row_0.createCell(col_Index).setCellValue(columnFamily);
                col_Index += col_size;
            }
        }
        // 从1开始给列族下的列赋值
        col_Index = 1;
        Row row_1 = sheet.createRow(1);
        for (String columnFamily : pojo.getColumnFamilies()) {
            for (String column : pojo.getColumnFamily_Map().get(columnFamily)) {
                row_1.createCell(col_Index).setCellValue(column);
                column_col_index_map.put(columnFamily+":"+column,col_Index);
//                column_col_index_map.put(column,col_Index);
//                System.out.println(column+"列的列号为->"+col_Index);
                col_Index++;
            }
        }
    }

    /**
     * 1. 列和列得对应上
     */
    protected void createData(){
        int row_index = 2; // 从第二行开始写
        List<HashMap<String, SimpleStringProperty>> dataSource = pojo.getDataSource();
        for (HashMap<String, SimpleStringProperty> hashMap : dataSource) {
            Row row = sheet.createRow(row_index);
            int col_index = 1;
            for (Map.Entry<String, SimpleStringProperty> entry : hashMap.entrySet()) {
                String column = entry.getKey();
                String value = entry.getValue().get();
                if (column.equals("rowKey")){
                    row.createCell(0).setCellValue(value);
                }else {
                    Integer index = column_col_index_map.get(column);
//                        System.out.println("当前列 = "+column);
//                        System.out.println("当前值 = "+value);
                    // todo 不要输出文本 数据量大时会卡死 logPane
//                    row.createCell(col_index++).setCellValue(value);
                    row.createCell(index).setCellValue(value);
                }
            }
            row_index++;
        }
    }

}
