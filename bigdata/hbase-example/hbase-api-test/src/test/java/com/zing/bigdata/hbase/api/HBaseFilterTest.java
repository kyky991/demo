package com.zing.bigdata.hbase.api;

import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.Arrays;

public class HBaseFilterTest {

    @Test
    public void rowFilterTest() {
        Filter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("rowkey1")));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE, Arrays.asList(filter));

        ResultScanner scanner = HBaseUtils.getScanner("FileTable", "rowkey1", "rowkey3", filterList);
        HBaseUtilsTest.scanner(scanner);
    }

    @Test
    public void prefixFilterTest() {
        Filter filter = new PrefixFilter(Bytes.toBytes("rowkey2"));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, Arrays.asList(filter));

        ResultScanner scanner = HBaseUtils.getScanner("FileTable", "rowkey1", "rowkey3", filterList);
        HBaseUtilsTest.scanner(scanner);
    }

    @Test
    public void keyOnlyFilterTest() {
        Filter filter = new KeyOnlyFilter(true);
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, Arrays.asList(filter));

        ResultScanner scanner = HBaseUtils.getScanner("FileTable", "rowkey1", "rowkey3", filterList);
        HBaseUtilsTest.scanner(scanner);
    }

    @Test
    public void columnPrefixFilterTest() {
        Filter filter = new ColumnPrefixFilter(Bytes.toBytes("nam"));
        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL, Arrays.asList(filter));

        ResultScanner scanner = HBaseUtils.getScanner("FileTable", "rowkey1", "rowkey3", filterList);
        HBaseUtilsTest.scanner(scanner);
    }
}