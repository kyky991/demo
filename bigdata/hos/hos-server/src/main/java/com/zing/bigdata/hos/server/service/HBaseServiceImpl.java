package com.zing.bigdata.hos.server.service;

import com.zing.bigdata.hos.core.ErrorCode;
import com.zing.bigdata.hos.server.HosServerException;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HBaseServiceImpl {

    /**
     * createTable.
     *
     * @param tableName tableName
     * @param cfs       cfs
     * @param splitKeys splitKeys
     * @return success of failed
     */
    public static boolean createTable(Connection connection, String tableName, String[] cfs, byte[][] splitKeys) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            if (admin.tableExists(tableName)) {
                return false;
            }
            HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
            Arrays.stream(cfs).forEach(cf -> {
                HColumnDescriptor hColumnDescriptor = new HColumnDescriptor(cf);
                hColumnDescriptor.setMaxVersions(1);
                tableDescriptor.addFamily(hColumnDescriptor);
            });
            admin.createTable(tableDescriptor, splitKeys);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String.format("create table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * createTable.
     *
     * @param tableName tableName
     * @param cfs       cfs
     * @return success of failed
     */
    public static boolean createTable(Connection connection, String tableName, String[] cfs) {
        return createTable(connection, tableName, cfs, null);
    }

    /**
     * deleteTable.
     *
     * @param tableName tableName
     * @return success of failed
     */
    public static boolean deleteTable(Connection connection, String tableName) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String.format("delete table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 删除ColumnFamily.删除列族
     */
    public static boolean deleteColumnFamily(Connection connection, String tableName, String family) {
        try (HBaseAdmin admin = (HBaseAdmin) connection.getAdmin()) {
            admin.deleteColumn(tableName, family);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String
                    .format("delete table=%s , column family=%s error. msg=%s",
                            tableName, family, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 删除qualifier.删除列
     */
    public static boolean deleteColumnQualifier(Connection connection, String tableName, String row, String family, String qualifier) {
        Delete delete = new Delete(row.getBytes());
        delete.addColumn(family.getBytes(), qualifier.getBytes());
        return deleteRow(connection, tableName, delete);
    }

    public static boolean deleteRow(Connection connection, String tableName, Delete delete) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            table.delete(delete);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String
                    .format("delete table=%s, delete=%s, error. msg=%s",
                            tableName, delete, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 删除行
     */
    public static boolean deleteRow(Connection connection, String tableName, String row) {
        Delete delete = new Delete(row.getBytes());
        return deleteRow(connection, tableName, delete);
    }

    /**
     * delete rows.
     *
     * @param tableName tableName
     * @param rows      rows
     * @return success of failed
     */
    public static boolean deleteRows(Connection connection, String tableName, List<String> rows) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Delete> list = new ArrayList<Delete>();
            for (String row : rows) {
                Delete d = new Delete(Bytes.toBytes(row));
                list.add(d);
            }
            if (list.size() > 0) {
                table.delete(list);
            }
        } catch (IOException e) {
            String msg = String
                    .format("delete table=%s , rows error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 读取行
     *
     * @param connection
     * @param tableName
     * @param get
     * @return
     */
    public static Result getRow(Connection connection, String tableName, Get get) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            return table.get(get);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String
                    .format("get row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
    }

    public static Result getRow(Connection connection, String tableName, String row, FilterList filterList) {
        Get get = new Get(Bytes.toBytes(row));
        get.setFilter(filterList);
        return getRow(connection, tableName, get);
    }

    public static Result getRow(Connection connection, String tableName, String row) {
        Get get = new Get(row.getBytes());
        return getRow(connection, tableName, get);
    }

    public static Result getRow(Connection connection, String tableName, String row, byte[] family, byte[] qualifier) {
        Get get = new Get(Bytes.toBytes(row));
        get.addColumn(family, qualifier);
        return getRow(connection, tableName, get);
    }

    /**
     * getRows.
     *
     * @param tableName  tableName
     * @param rows       rows
     * @param filterList filterList
     * @return Result
     */
    public static Result[] getRows(Connection connection, String tableName, List<String> rows, FilterList filterList) {
        Result[] results = null;
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            List<Get> gets = null;
            gets = new ArrayList<Get>();
            for (String row : rows) {
                if (row != null) {
                    Get get = new Get(Bytes.toBytes(row));
                    get.setFilter(filterList);
                    gets.add(get);
                }
            }
            if (gets.size() > 0) {
                results = table.get(gets);
            }
        } catch (IOException e) {
            String msg = String
                    .format("get rows from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return results;
    }


    /**
     * getRows.
     *
     * @param tableName tableName
     * @param rows      rows
     * @return Result
     */
    public static Result[] getRows(Connection connection, String tableName, List<String> rows) {
        return getRows(connection, tableName, rows, null);
    }

    /**
     * 获取scanner
     *
     * @param connection
     * @param tableName
     * @param scan
     * @return
     */
    public static ResultScanner getScanner(Connection connection, String tableName, Scan scan) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            return table.getScanner(scan);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String
                    .format("scan table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
    }

    public static ResultScanner getScanner(Connection connection, String tableName, byte[] startRow, byte[] stopRow, FilterList filterList) {
        Scan scan = new Scan();
        scan.setStartRow(startRow);
        scan.setStopRow(stopRow);
        scan.setFilter(filterList);
        scan.setCaching(1000);
        return getScanner(connection, tableName, scan);
    }

    public static ResultScanner getScanner(Connection connection, String tableName, String startRow, String stopRow, FilterList filterList) {
        return getScanner(connection, tableName, Bytes.toBytes(startRow), Bytes.toBytes(stopRow), filterList);
    }

    public static ResultScanner getScanner(Connection connection, String tableName, byte[] startRow, byte[] stopRow) {
        return getScanner(connection, tableName, startRow, stopRow, null);
    }

    public static ResultScanner getScanner(Connection connection, String tableName, String startRow, String stopRow) {
        return getScanner(connection, tableName, Bytes.toBytes(startRow), Bytes.toBytes(stopRow));
    }

    /**
     * exists Row
     *
     * @param connection
     * @param tableName
     * @param row
     * @return
     */
    public static boolean existsRow(Connection connection, String tableName, String row) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            Get g = new Get(Bytes.toBytes(row));
            return table.exists(g);
        } catch (IOException e) {
            String msg = String
                    .format("check exists row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
    }

    /**
     * 插入行
     *
     * @param connection
     * @param tableName
     * @param put
     * @return
     */
    public static boolean putRow(Connection connection, String tableName, Put put) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            table.put(put);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String
                    .format("put row from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * 批量插入
     *
     * @param connection
     * @param tableName
     * @param puts
     * @return
     */
    public static boolean putRow(Connection connection, String tableName, List<Put> puts) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            table.put(puts);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String
                    .format("put rows from table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
        return true;
    }

    /**
     * incrementColumnValue 通过这个方法 生成目录的seqid
     */
    public static long incrementColumnValue(Connection connection, String tableName, String row,
                                            byte[] family, byte[] qualifier, int num) {
        try (Table table = connection.getTable(TableName.valueOf(tableName))) {
            return table.incrementColumnValue(row.getBytes(), family, qualifier, num);
        } catch (Exception e) {
            e.printStackTrace();
            String msg = String
                    .format("incrementColumnValue table=%s error. msg=%s", tableName, e.getMessage());
            throw new HosServerException(ErrorCode.ERROR_HBASE, msg);
        }
    }
}
