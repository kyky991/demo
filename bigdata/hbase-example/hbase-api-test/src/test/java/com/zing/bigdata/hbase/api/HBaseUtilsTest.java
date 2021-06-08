package com.zing.bigdata.hbase.api;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

public class HBaseUtilsTest {

    public static void result(Result result) {
        System.out.println("rowkey = " + Bytes.toString(result.getRow()));
        System.out.println("fileName = " + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
        System.out.println("fileType = " + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("type"))));
    }

    public static void scanner(ResultScanner scanner) {
        if (scanner != null) {
            scanner.forEach(HBaseUtilsTest::result);
            scanner.close();
        }
    }

    @Test
    public void createTable() {
        HBaseUtils.createTable("FileTable", new String[] {"fileInfo", "saveInfo"});
    }

    @Test
    public void addFileDetail() {
        HBaseUtils.putRow("FileTable", "rowkey1", "fileInfo", "name", "file1.txt");
        HBaseUtils.putRow("FileTable", "rowkey1", "fileInfo", "type", "txt");
        HBaseUtils.putRow("FileTable", "rowkey1", "fileInfo", "size", "1024");
        HBaseUtils.putRow("FileTable", "rowkey1", "saveInfo", "creator", "root");
        HBaseUtils.putRow("FileTable", "rowkey2", "fileInfo", "name", "file2.jpg");
        HBaseUtils.putRow("FileTable", "rowkey2", "fileInfo", "type", "jpg");
        HBaseUtils.putRow("FileTable", "rowkey2", "fileInfo", "size", "1024");
        HBaseUtils.putRow("FileTable", "rowkey2", "saveInfo", "creator", "root");
        HBaseUtils.putRow("FileTable", "rowkey3", "fileInfo", "name", "file3.jpg");
        HBaseUtils.putRow("FileTable", "rowkey3", "fileInfo", "type", "jpg");
        HBaseUtils.putRow("FileTable", "rowkey3", "fileInfo", "size", "1024");
        HBaseUtils.putRow("FileTable", "rowkey3", "saveInfo", "creator", "user");
    }

    @Test
    public void getFileDetails() {
        Result result = HBaseUtils.getRow("FileTable", "rowkey1");
        if (result != null) {
            System.out.println("rowkey=" + Bytes.toString(result.getRow()));
            System.out.println("fileName=" + Bytes.toString(result.getValue(Bytes.toBytes("fileInfo"), Bytes.toBytes("name"))));
        }
    }

    @Test
    public void scanFileDetails() {
        ResultScanner scanner = HBaseUtils.getScanner("FileTable", "rowkey1", "rowkey2");
        scanner(scanner);
    }

    @Test
    public void deleteRow() {
        HBaseUtils.deleteRow("FileTable", "rowkey1");
    }

    @Test
    public void deleteTable() {
        HBaseUtils.deleteTable("FileTable");
    }
}