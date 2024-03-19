package com.lyh.utils;

import javafx.beans.property.SimpleStringProperty;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.joda.time.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * TODO 必须对用户传进来的文件格式进行限制:
 *  1. 前两行代表表头
 *  2. 剩余行均为数据行
 */
public class POIExcel2003Utils implements ExcelToHBaseDataFormat{

    public Set<String> columnFamilySet = new HashSet<>();

    public static void main(String[] args) throws IOException, InterruptedException {
//        List<String> res = new POIExcel2003Utils().readTitle("D:\\Desktop\\test.xls");
//        for (String s : res) {
//            System.out.println(s);
//        }
        long start = System.currentTimeMillis();
        Thread.sleep(1012);
        long end = System.currentTimeMillis();
        System.out.println(new DateTime(end-start).toString("ss.SSS"));
//        new POIExcel2003Utils().readTitle("D:\\Desktop\\test.xls");
//        System.out.println();
    }



    @Override
    public Set<String> readTitle(String filePath) throws IOException {
        Set<String> res = new HashSet<>();

        FileInputStream file = new FileInputStream(new File(filePath));

        Workbook workbook = new HSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);


        List<CellRangeAddress> combineCell = getCombineCell(sheet);
        for (CellRangeAddress ca : combineCell) {
            Row row = sheet.getRow(ca.getFirstRow());
            Cell cell = row.getCell(ca.getFirstColumn());
            cell.setCellType(Cell.CELL_TYPE_STRING);
            String columnFamily = cell.getStringCellValue();
//            System.out.println("columnFamily:"+columnFamily);
            int first_Row = ca.getFirstRow();
            int last_Row = ca.getLastRow();
            int high = last_Row-first_Row;
            if (high == 0){   // 0-0=0 rowKey: 1-0=1
                int lastRow = ca.getLastRow();
                int firstRow = ca.getFirstRow();
                int lastColumn = ca.getLastColumn();
                int firstColumn = ca.getFirstColumn();
                for (int i = firstRow; i <= lastRow; i++) {
                    Row tmp = sheet.getRow(i+1);
                    for (int j = firstColumn; j <= lastColumn; j++) {
                        Cell cell1 = tmp.getCell(j);
                        cell1.setCellType(Cell.CELL_TYPE_STRING);
                        String columnName = cell1.getStringCellValue();
//                        System.out.println("columnName:"+columnName);
                        res.add(columnFamily+":"+columnName);
                        columnFamilySet.add(columnFamily);
                    }
                }
//                System.out.println("===============");
            }else { // rowKey
               res.add(columnFamily);
            }
        }
        // 总行数
        return res;
    }


    /**
     * 预览数据-在tableview展示
     * @param size 展示的行数
     * @return 直接返回 tableview 可以查看的格式
     * TODO 如果要返回所有数据 只需要 (size=Integer.MAX_VALUE-2)
     */
    @Override
    public List<HashMap<String, SimpleStringProperty>> preview(String filePath, int size) throws IOException {

        List<HashMap<String,SimpleStringProperty>> data = new ArrayList<>();

        FileInputStream file = new FileInputStream(new File(filePath));
        Workbook workbook = new HSSFWorkbook(file);
        Sheet sheet = workbook.getSheetAt(0);
        // 从第 2 行开始是值
        int lastRowNum = sheet.getLastRowNum()+1;
        for (int i = 2; i < Math.min(lastRowNum,size+2); i++) { //size+2 因为前两行是表头
            HashMap<String ,SimpleStringProperty> hashMap = new HashMap<>();
            Row row = sheet.getRow(i);
            short lasCol = row.getLastCellNum();
            for (int j = 0; j < lasCol; j++) {
                Cell cell = row.getCell(j);
                if (cell==null) continue;
                cell.setCellType(Cell.CELL_TYPE_STRING);
                if (j==0){  // 第0列是rowKey
//                    String rowKey = cell.getSheet().getRow(0).getCell(j).getStringCellValue();
//                    System.out.println("rowKey");
                    hashMap.put("rowKey",new SimpleStringProperty(cell.getStringCellValue()));
                }else {
                    String columnFamily = getMergedRegionValue(sheet, 0, j);
                    String columnName = cell.getSheet().getRow(1).getCell(j).getStringCellValue();
//                    System.out.println(columnFamily+":"+columnName);
                    hashMap.put(columnFamily+":"+columnName,new SimpleStringProperty(cell.getStringCellValue()));
                }
//                System.out.println("第"+i+"行，第"+j+"列 -> "+cell.getStringCellValue());
            }
            data.add(hashMap);
        }
        return data;
    }

    @Override
    public void writeIntoHBase(String filePath,String namespace, String tableName) throws IOException {

    }

    /**
     * 获得所有合并单元格
     * @param sheet 从 sheet 表单中获得
     */
    public static List<CellRangeAddress> getCombineCell(Sheet sheet) {
        List<CellRangeAddress> list = new ArrayList<CellRangeAddress>();
        //获得一个 sheet 中合并单元格的数量
        int count = sheet.getNumMergedRegions();
        //遍历所有的合并单元格
        for(int i = 0; i<count;i++) {
        // 获得合并单元格保存进list中
            CellRangeAddress ca = sheet.getMergedRegion(i);
            list.add(ca);
        }
        return list;
    }

    public String getCellValue(Cell cell){

        if(cell == null) return "";

        if(cell.getCellType() == Cell.CELL_TYPE_STRING){

            return cell.getStringCellValue();

        }else if(cell.getCellType() == Cell.CELL_TYPE_BOOLEAN){

            return String.valueOf(cell.getBooleanCellValue());

        }else if(cell.getCellType() == Cell.CELL_TYPE_FORMULA){

            return cell.getCellFormula() ;

        }else if(cell.getCellType() == Cell.CELL_TYPE_NUMERIC){

            return String.valueOf(cell.getNumericCellValue());

        }
        return "";
    }

    public String getMergedRegionValue(Sheet sheet ,int row , int column){
        int sheetMergeCount = sheet.getNumMergedRegions();
        for(int i = 0 ; i < sheetMergeCount ; i++){
            CellRangeAddress ca = sheet.getMergedRegion(i);
            int firstColumn = ca.getFirstColumn();
            int lastColumn = ca.getLastColumn();
            int firstRow = ca.getFirstRow();
            int lastRow = ca.getLastRow();
            if(row >= firstRow && row <= lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    Row fRow = sheet.getRow(firstRow);
                    Cell fCell = fRow.getCell(firstColumn);
                    return getCellValue(fCell) ;
                }
            }
        }
        return null ;
    }

    private boolean isMergedRow(Sheet sheet,int row ,int column) {
        int sheetMergeCount = sheet.getNumMergedRegions();
        for (int i = 0; i < sheetMergeCount; i++) {
            CellRangeAddress range = sheet.getMergedRegion(i);
            int firstColumn = range.getFirstColumn();
            int lastColumn = range.getLastColumn();
            int firstRow = range.getFirstRow();
            int lastRow = range.getLastRow();
            if(row == firstRow && row == lastRow){
                if(column >= firstColumn && column <= lastColumn){
                    return true;
                }
            }
        }
        return false;
    }
}
