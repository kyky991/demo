package com.zing.bigdata.hos.server.service.impl;

import com.google.common.base.Strings;
import com.zing.bigdata.hos.common.HosObject;
import com.zing.bigdata.hos.common.HosObjectSummary;
import com.zing.bigdata.hos.common.ObjectListResult;
import com.zing.bigdata.hos.common.ObjectMetaData;
import com.zing.bigdata.hos.common.util.JsonUtil;
import com.zing.bigdata.hos.core.ErrorCode;
import com.zing.bigdata.hos.server.HosServerException;
import com.zing.bigdata.hos.server.HosUtils;
import com.zing.bigdata.hos.server.service.HBaseServiceImpl;
import com.zing.bigdata.hos.server.service.IHdfsService;
import com.zing.bigdata.hos.server.service.IHosStoreService;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.io.ByteBufferInputStream;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.*;

public class HosStoreServiceImpl implements IHosStoreService {

    private static Logger logger = Logger.getLogger(HosStoreServiceImpl.class);

    private Connection connection = null;

    private IHdfsService fileStore;

    private String zkUrls;

    private CuratorFramework zkClient;

    public HosStoreServiceImpl(Connection connection, IHdfsService fileStore, String zkUrls) {
        this.connection = connection;
        this.fileStore = fileStore;
        this.zkUrls = zkUrls;
        zkClient = CuratorFrameworkFactory.newClient(zkUrls, new ExponentialBackoffRetry(20, 5));
        zkClient.start();
    }

    @Override
    public void createBucketStore(String bucket) throws IOException {
        // 1.创建目录表
        HBaseServiceImpl.createTable(connection, HosUtils.getDirTableName(bucket), HosUtils.getDirColumnFamily());

        // 2.创建文件夹
        HBaseServiceImpl.createTable(connection, HosUtils.getObjTableName(bucket), HosUtils.getObjColumnFamily(), HosUtils.OBJ_REGIONS);

        // 3.将其添加到seq表
        Put put = new Put(bucket.getBytes());
        put.addColumn(HosUtils.BUCKET_DIR_SEQ_CF_BYTES, HosUtils.BUCKET_DIR_SEQ_QUALIFIER, Bytes.toBytes(0L));
        HBaseServiceImpl.putRow(connection, HosUtils.BUCKET_DIR_SEQ_TABLE, put);

        // 4.创建hdfs目录
        fileStore.mkDir(HosUtils.FILE_STORE_ROOT + "/" + bucket);
    }

    @Override
    public void deleteBucketStore(String bucket) throws IOException {
        // 1.删除目录表和文件表
        HBaseServiceImpl.deleteTable(connection, HosUtils.getDirTableName(bucket));
        HBaseServiceImpl.deleteTable(connection, HosUtils.getObjTableName(bucket));

        // 2.删除seq表中的记录
        HBaseServiceImpl.deleteRow(connection, HosUtils.BUCKET_DIR_SEQ_TABLE, bucket);

        // 3.删除hdfs上的目录
        fileStore.deleteDir(HosUtils.FILE_STORE_ROOT + "/" + bucket);
    }

    @Override
    public void createSeqTable() throws IOException {
        Admin admin = connection.getAdmin();
        if (admin.tableExists(TableName.valueOf(HosUtils.BUCKET_DIR_SEQ_TABLE))) {
            return;
        }
        HBaseServiceImpl.createTable(connection, HosUtils.BUCKET_DIR_SEQ_TABLE, new String[]{HosUtils.BUCKET_DIR_SEQ_CF});
    }

    @Override
    public void put(String bucket, String key, ByteBuffer content, long length, String mediaType, Map<String, String> properties) throws Exception {
        // 判断是否是创建目录
        InterProcessMutex lock = null;
        try {
            if (key.endsWith("/")) {
                putDir(bucket, key);
                return;
            }

            // 获取seqId
            String dir = key.substring(0, key.lastIndexOf("/") + 1);
            String hash = null;
            while (hash == null) {
                if (!dirExist(bucket, dir)) {
                    hash = putDir(bucket, dir);
                } else {
                    hash = getDirSeqId(bucket, dir);
                }
            }

            // 上传文件到文件表
            // 获取锁
            String lockKey = key.replace("/", "_");
            lock = new InterProcessMutex(zkClient, "/hos/" + bucket + "/" + lockKey);
            lock.acquire();

            // 上传文件
            String name = key.substring(key.lastIndexOf("/") + 1);
            String fileKey = hash + "_" + name;
            Put contentPut = new Put(fileKey.getBytes());
            if (!Strings.isNullOrEmpty(mediaType)) {
                contentPut.addColumn(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_MEDIATYPE_QUALIFIER, mediaType.getBytes());
            }

            // TODO add props length
            if (properties != null) {
                String props = JsonUtil.toJson(properties);
                contentPut.addColumn(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_PROPS_QUALIFIER, props.getBytes());
            }
            contentPut.addColumn(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_LEN_QUALIFIER, Bytes.toBytes(length));

            // 判断文件大小 小于20m 存储在hbase 否则存储到hdfs
            if (length <= HosUtils.FILE_STORE_THRESHOLD) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(HosUtils.OBJ_CONT_QUALIFIER);
                contentPut.addColumn(HosUtils.OBJ_CONT_CF_BYTES, byteBuffer, System.currentTimeMillis(), content);
                byteBuffer.clear();
            } else {
                String fileDir = HosUtils.FILE_STORE_ROOT + "/" + bucket + "/" + hash;
                InputStream inputStream = new ByteBufferInputStream(content);
                fileStore.saveFile(fileDir, name, inputStream, length, (short) 1);
            }
            HBaseServiceImpl.putRow(connection, HosUtils.getObjTableName(bucket), contentPut);
        } finally {
            // 释放锁
            if (lock != null) {
                lock.release();
            }
        }
    }

    private String putDir(String bucket, String dir) throws Exception {
        if (dirExist(bucket, dir)) {
            return null;
        }
        // 从zk获取锁
        InterProcessMutex lock = null;
        try {
            String lockKey = dir.replace("/", "_");
            lock = new InterProcessMutex(zkClient, "/hos/" + bucket + "/" + lockKey);
            lock.acquire();

            String dir1 = dir.substring(0, dir.lastIndexOf("/"));
            String name = dir1.substring(dir1.lastIndexOf("/") + 1);
            // 创建目录
            if (name.length() > 0) {
                String parent = dir.substring(0, dir1.lastIndexOf("/") + 1);
                if (!dirExist(bucket, parent)) {
                    putDir(bucket, parent);
                }

                // 在父目录添加sub列族内 列族子项
                Put put = new Put(Bytes.toBytes(parent));
                put.addColumn(HosUtils.DIR_SUBDIR_CF_BYTES, Bytes.toBytes(name), Bytes.toBytes("1"));
                HBaseServiceImpl.putRow(connection, HosUtils.getDirTableName(bucket), put);
            }

            // 再去添加到目录表
            String seqId = getDirSeqId(bucket, dir);
            String hash = seqId == null ? makeDirSeqId(bucket) : seqId;
            Put dirPut = new Put(dir.getBytes());
            dirPut.addColumn(HosUtils.DIR_META_CF_BYTES, HosUtils.DIR_SEQID_QUALIFIER, Bytes.toBytes(hash));
            HBaseServiceImpl.putRow(connection, HosUtils.getDirTableName(bucket), dirPut);
            return hash;
        } finally {
            // 释放锁
            if (lock != null) {
                lock.release();
            }
        }
    }

    private String makeDirSeqId(String bucket) throws IOException {
        long value = HBaseServiceImpl.incrementColumnValue(connection, HosUtils.BUCKET_DIR_SEQ_TABLE,
                bucket, HosUtils.BUCKET_DIR_SEQ_CF_BYTES, HosUtils.BUCKET_DIR_SEQ_QUALIFIER, 1);
        return String.format("%da%d", value % 64, value);
    }

    private String getDirSeqId(String bucket, String row) throws IOException {
        Result result = HBaseServiceImpl.getRow(connection, HosUtils.getDirTableName(bucket), row);
        if (result.isEmpty()) {
            return null;
        }
        return Bytes.toString(result.getValue(HosUtils.DIR_META_CF_BYTES, HosUtils.DIR_SEQID_QUALIFIER));
    }

    private boolean dirExist(String bucket, String row) throws IOException {
        return HBaseServiceImpl.existsRow(connection, HosUtils.getDirTableName(bucket), row);
    }

    @Override
    public HosObjectSummary getSummary(String bucket, String key) throws IOException {
        // 判断是否是文件夹
        if (key.endsWith("/")) {
            Result result = HBaseServiceImpl.getRow(connection, HosUtils.getDirTableName(bucket), key);
            if (!result.isEmpty()) {
                // 读取文件夹的基础属性 转换为HosObjectSummary
                return dirObjectToSummary(result, bucket, key);
            }
            return null;
        }

        // 获取文件的基本属性
        String dir = key.substring(0, key.lastIndexOf("/") + 1);
        String seqId = getDirSeqId(bucket, dir);
        if (seqId == null) {
            return null;
        }
        String objKey = seqId + "_" + key.substring(key.lastIndexOf("/") + 1);
        Result result = HBaseServiceImpl.getRow(connection, HosUtils.getObjTableName(bucket), objKey);
        if (result.isEmpty()) {
            return null;
        }

        return resultToObjectSummary(result, bucket, dir);
    }

    private HosObjectSummary resultToObjectSummary(Result result, String bucket, String dir) throws IOException {
        HosObjectSummary summary = new HosObjectSummary();

        long timestamp = result.rawCells()[0].getTimestamp();
        summary.setLastModifyTime(timestamp);

        String id = new String(result.getRow());
        summary.setId(id);

        String name = id.split("_", 2)[1];
        summary.setName(name);
        summary.setKey(dir + name);
        summary.setBucket(bucket);
        summary.setMediaType(Bytes.toString(result.getValue(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_MEDIATYPE_QUALIFIER)));

        // TODO length attr
        String s = Bytes.toString(result.getValue(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_PROPS_QUALIFIER));
        if (s != null) {
            summary.setAttrs(JsonUtil.fromJson(Map.class, s));
        }
        summary.setLength(Bytes.toLong(result.getValue(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_LEN_QUALIFIER)));

        return summary;
    }

    private HosObjectSummary dirObjectToSummary(Result result, String bucket, String dir) {
        HosObjectSummary summary = new HosObjectSummary();
        summary.setId(Bytes.toString(result.getRow()));
        summary.setAttrs(new HashMap<>(0));
        summary.setBucket(bucket);
        summary.setKey(dir);
        summary.setLastModifyTime(result.rawCells()[0].getTimestamp());
        summary.setLength(0);
        summary.setMediaType("");
        if (dir.length() > 1) {
            summary.setName(dir.substring(dir.lastIndexOf("/") + 1));
        } else {
            summary.setName("");
        }
        return summary;
    }

    @Override
    public List<HosObjectSummary> list(String bucket, String startRow, String stopRow) throws IOException {
        String startDir = startRow.substring(0, startRow.lastIndexOf("/") + 1).trim();
        if (startDir.length() == 0) {
            startDir = "/";
        }

        String stopDir = stopRow.substring(0, startRow.lastIndexOf("/") + 1).trim();
        if (stopDir.length() == 0) {
            stopDir = "/";
        }

        String startName = startRow.substring(startRow.lastIndexOf("/") + 1);
        String stopName = stopRow.substring(startRow.lastIndexOf("/") + 1);
        String seqId = getDirSeqId(bucket, startDir);

        // 查询startDir中大于startName的全部文件
        List<HosObjectSummary> objectSummaries = new ArrayList<>();
        if (seqId != null && startName.length() > 0) {
            byte[] max = Bytes.createMaxByteArray(100);
            byte[] stop = Bytes.add(Bytes.toBytes(seqId), max);
            if (startDir.equals(stopDir)) {
                stop = (seqId + "_" + stopName).getBytes();
            }
            byte[] start = (seqId + "_" + startName).getBytes();
            ResultScanner scanner = HBaseServiceImpl.getScanner(connection, HosUtils.getObjTableName(bucket), start, stop);
            Result result = null;
            while ((result = scanner.next()) != null) {
                HosObjectSummary summary = resultToObjectSummary(result, bucket, startDir);
                objectSummaries.add(summary);
            }
            if (scanner != null) {
                scanner.close();
            }
        }

        // startRow ~ stopRow之间的全部目录
        ResultScanner scanner = HBaseServiceImpl.getScanner(connection, HosUtils.getDirTableName(bucket), startRow, stopRow);
        Result result = null;
        while ((result = scanner.next()) != null) {
            String seqId2 = Bytes.toString(result.getValue(HosUtils.DIR_META_CF_BYTES, HosUtils.DIR_SEQID_QUALIFIER));
            if (seqId2 == null) {
                continue;
            }
            String dir = Bytes.toString(result.getRow());
            objectSummaries.add(dirObjectToSummary(result, bucket, dir));
            getDirAllFiles(bucket, dir, seqId2, objectSummaries, stopRow);
        }
        if (scanner != null) {
            scanner.close();
        }
        Collections.sort(objectSummaries);
        return objectSummaries;
    }

    private void getDirAllFiles(String bucket, String dir, String seqId, List<HosObjectSummary> objectSummaries, String stopRow) throws IOException {
        byte[] max = Bytes.createMaxByteArray(100);
        byte[] stop = Bytes.add(Bytes.toBytes(seqId), max);
        if (stopRow.startsWith(dir)) {
            String stopRowLeft = stopRow.replace(dir, "");
            String fileNameMax = stopRowLeft;
            if (stopRowLeft.indexOf("/") > 0) {
                fileNameMax = stopRowLeft.substring(0, stopRowLeft.indexOf("/"));
            }
            stop = Bytes.toBytes(seqId + "_" + fileNameMax);
        }

        Scan scan = new Scan(Bytes.toBytes(seqId), stop);
        scan.setFilter(HosUtils.OBJ_META_SCAN_FILTER);
        ResultScanner scanner = HBaseServiceImpl.getScanner(connection, HosUtils.getObjTableName(bucket), scan);
        Result result = null;
        while ((result = scanner.next()) != null) {
            HosObjectSummary summary = resultToObjectSummary(result, bucket, dir);
            objectSummaries.add(summary);
        }
        if (scanner != null) {
            scanner.close();
        }
    }

    @Override
    public ObjectListResult listDir(String bucket, String dir, String startRow, int maxCount) throws IOException {
        // 查询目录表
        startRow = Strings.nullToEmpty(startRow);
        Get get = new Get(Bytes.toBytes(dir));
        get.addFamily(HosUtils.DIR_SUBDIR_CF_BYTES);
        if (!Strings.isNullOrEmpty(startRow)) {
            get.setFilter(new QualifierFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(startRow))));
        }

        Result dirResult = HBaseServiceImpl.getRow(connection, HosUtils.getDirTableName(bucket), get);
        List<HosObjectSummary> subDirs = null;
        if (!dirResult.isEmpty()) {
            subDirs = new ArrayList<>();
            for (Cell cell : dirResult.rawCells()) {
                HosObjectSummary summary = new HosObjectSummary();
                byte[] qualifierBytes = new byte[cell.getQualifierLength()];
                CellUtil.copyQualifierTo(cell, qualifierBytes, 0);
                String name = Bytes.toString(qualifierBytes);
                summary.setKey(dir + name + "/");
                summary.setName(name);
                summary.setLastModifyTime(cell.getTimestamp());
                summary.setMediaType("");
                summary.setBucket(bucket);
                summary.setLength(0);
                subDirs.add(summary);
                if (subDirs.size() > maxCount + 1) {
                    break;
                }
            }
        }
        // 查询文件表
        String dirSeqId = getDirSeqId(bucket, dir);
        byte[] objStart = Bytes.toBytes(dirSeqId + "_" + startRow);
        Scan objScan = new Scan();
        objScan.setStartRow(objStart);
        objScan.setRowPrefixFilter(Bytes.toBytes(dirSeqId + "_"));
        objScan.setFilter(new PageFilter(maxCount + 1));
        objScan.setMaxResultsPerColumnFamily(maxCount + 2);
        objScan.addFamily(HosUtils.OBJ_META_CF_BYTES);
        logger.info("scan start: " + Bytes.toString(objStart) + " - ");
        ResultScanner scanner = HBaseServiceImpl.getScanner(connection, HosUtils.getObjTableName(bucket), objScan);

        List<HosObjectSummary> objectSummaries = new ArrayList<>();
        Result result = null;
        while (objectSummaries.size() < maxCount + 2 && (result = scanner.next()) != null) {
            HosObjectSummary summary = resultToObjectSummary(result, bucket, dir);
            objectSummaries.add(summary);
        }
        if (scanner != null) {
            scanner.close();
        }
        logger.info("scan complete: " + Bytes.toString(objStart) + " - ");

        if (subDirs != null && subDirs.size() > 0) {
            objectSummaries.addAll(subDirs);
        }

        // 返回用户表 maxcount
        Collections.sort(objectSummaries);
        if (objectSummaries.size() > maxCount) {
            objectSummaries = objectSummaries.subList(0, maxCount);
        }

        ObjectListResult listResult = new ObjectListResult();
        HosObjectSummary nextMarkerObj = objectSummaries.size() > maxCount ? objectSummaries.get(objectSummaries.size() - 1) : null;
        if (nextMarkerObj != null) {
            listResult.setNextMarker(nextMarkerObj.getKey());
        }
        listResult.setMaxKeyNumber(maxCount);
        if (objectSummaries.size() > 0) {
            listResult.setMinKey(objectSummaries.get(0).getKey());
            listResult.setMaxKey(objectSummaries.get(objectSummaries.size() - 1).getKey());
        }
        listResult.setObjectCount(objectSummaries.size());
        listResult.setObjectSummaries(objectSummaries);
        listResult.setBucket(bucket);
        return listResult;
    }

    @Override
    public ObjectListResult listByPrefix(String bucket, String dir, String prefix, String start, int maxCount) throws IOException {
        start = Strings.nullToEmpty(start);

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);
        filterList.addFilter(new ColumnPrefixFilter(prefix.getBytes()));
        if (start.length() > 0) {
            filterList.addFilter(new QualifierFilter(CompareFilter.CompareOp.GREATER_OR_EQUAL, new BinaryComparator(Bytes.toBytes(start))));
        }

        int maxCount1 = maxCount + 2;
        Result dirResult = HBaseServiceImpl.getRow(connection, HosUtils.getDirTableName(bucket), dir, filterList);
        List<HosObjectSummary> subDirs = null;
        if (!dirResult.isEmpty()) {
            subDirs = new ArrayList<>();
            for (Cell cell : dirResult.rawCells()) {
                HosObjectSummary summary = new HosObjectSummary();
                byte[] qualifierBytes = new byte[cell.getQualifierLength()];
                CellUtil.copyQualifierTo(cell, qualifierBytes, 0);
                String name = Bytes.toString(qualifierBytes);
                summary.setKey(dir + name + "/");
                summary.setName(name);
                summary.setLastModifyTime(cell.getTimestamp());
                summary.setMediaType("");
                summary.setBucket(bucket);
                summary.setLength(0);
                subDirs.add(summary);
                if (subDirs.size() >= maxCount1) {
                    break;
                }
            }
        }

        String dirSeqId = getDirSeqId(bucket, dir);
        byte[] objStart = Bytes.toBytes(dirSeqId + "_" + start);

        Scan objScan = new Scan();
        objScan.setRowPrefixFilter(Bytes.toBytes(dirSeqId + "_" + prefix));
        objScan.setFilter(new PageFilter(maxCount + 1));
        objScan.setStartRow(objStart);
        objScan.setMaxResultsPerColumnFamily(maxCount1);
        objScan.addFamily(HosUtils.OBJ_META_CF_BYTES);

        logger.info("scan start: " + Bytes.toString(objStart) + " - ");

        ResultScanner objScanner = HBaseServiceImpl.getScanner(connection, HosUtils.getObjTableName(bucket), objScan);
        List<HosObjectSummary> objectSummaries = new ArrayList<>();
        Result result = null;
        while (objectSummaries.size() < maxCount1 && (result = objScanner.next()) != null) {
            HosObjectSummary summary = resultToObjectSummary(result, bucket, dir);
            objectSummaries.add(summary);
        }
        if (objScanner != null) {
            objScanner.close();
        }

        logger.info("scan complete: " + Bytes.toString(objStart) + " - ");

        if (subDirs != null && subDirs.size() > 0) {
            objectSummaries.addAll(subDirs);
        }
        Collections.sort(objectSummaries);

        ObjectListResult listResult = new ObjectListResult();
        HosObjectSummary nextMarkerObj = objectSummaries.size() > maxCount ?
                objectSummaries.get(objectSummaries.size() - 1) : null;
        if (nextMarkerObj != null) {
            listResult.setNextMarker(nextMarkerObj.getKey());
        }
        if (objectSummaries.size() > maxCount) {
            objectSummaries = objectSummaries.subList(0, maxCount);
        }
        listResult.setMaxKeyNumber(maxCount);
        if (objectSummaries.size() > 0) {
            listResult.setMinKey(objectSummaries.get(0).getKey());
            listResult.setMaxKey(objectSummaries.get(objectSummaries.size() - 1).getKey());
        }
        listResult.setObjectCount(objectSummaries.size());
        listResult.setObjectSummaries(objectSummaries);
        listResult.setBucket(bucket);
        return listResult;
    }

    @Override
    public HosObject getObject(String bucket, String key) throws IOException {
        // 判断是否是目录
        if (key.endsWith("/")) {
            // 读取目录表
            Result result = HBaseServiceImpl.getRow(connection, HosUtils.getDirTableName(bucket), key);
            if (result.isEmpty()) {
                return null;
            }

            ObjectMetaData objectMetaData = new ObjectMetaData();
            objectMetaData.setBucket(bucket);
            objectMetaData.setKey(key);
            objectMetaData.setLength(0);
            objectMetaData.setLastModifyTime(result.rawCells()[0].getTimestamp());

            HosObject hosObject = new HosObject();
            hosObject.setMetaData(objectMetaData);
            return hosObject;
        }

        // 读取文件表
        String dir = key.substring(0, key.lastIndexOf("/") + 1);
        String seqId = getDirSeqId(bucket, dir);
        if (seqId == null) {
            return null;
        }

        String name = key.substring(key.lastIndexOf("/") + 1);
        String objKey = seqId + "_" + name;
        Result result = HBaseServiceImpl.getRow(connection, HosUtils.getObjTableName(bucket), objKey);
        if (result.isEmpty()) {
            return null;
        }

        long length = Bytes.toLong(result.getValue(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_LEN_QUALIFIER));
        ObjectMetaData objectMetaData = new ObjectMetaData();
        objectMetaData.setBucket(bucket);
        objectMetaData.setKey(key);
        objectMetaData.setLastModifyTime(result.rawCells()[0].getTimestamp());
        objectMetaData.setLength(length);
        byte[] value = result.getValue(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_PROPS_QUALIFIER);
        if (value != null) {
            objectMetaData.setAttrs(JsonUtil.fromJson(Map.class, Bytes.toString(value)));
        }

        HosObject hosObject = new HosObject();
        hosObject.setMetaData(objectMetaData);
        // 读取文件内容
        if (result.containsNonEmptyColumn(HosUtils.OBJ_CONT_CF_BYTES, HosUtils.OBJ_CONT_QUALIFIER)) {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(result.getValue(HosUtils.OBJ_CONT_CF_BYTES, HosUtils.OBJ_CONT_QUALIFIER));
            hosObject.setContent(inputStream);
        } else {
            String fileDir = HosUtils.FILE_STORE_ROOT + "/" + bucket + "/" + seqId;
            InputStream inputStream = fileStore.openFile(fileDir, name);
            hosObject.setContent(inputStream);
        }
        return hosObject;
    }

    @Override
    public void deleteObject(String bucket, String key) throws Exception {
        // 判断当前key是否为目录
        if (key.endsWith("/")) {
            // 删除目录
            // 判断目录是否为空
            if (!isDirExist(bucket, key)) {
                throw new HosServerException(ErrorCode.ERROR_PERMISSION_DENIED, "dir is not empty");
            }

            // 获取锁
            InterProcessMutex lock = null;
            try {
                String lockKey = key.replace("/", "_");
                lock = new InterProcessMutex(zkClient, "/hos/" + bucket + "/" + lockKey);
                lock.acquire();

                // 从父目录删除数据
                String dir = key.substring(0, key.lastIndexOf("/"));
                String name = dir.substring(dir.lastIndexOf("/") + 1);
                if (name.length() > 0) {
                    String parent = key.substring(0, key.lastIndexOf(name));
                    HBaseServiceImpl.deleteColumnQualifier(connection, HosUtils.getDirTableName(bucket), parent, HosUtils.DIR_SUBDIR_CF, name);
                }
                // 从目录表删除数据
                HBaseServiceImpl.deleteRow(connection, HosUtils.getDirTableName(bucket), key);
            } finally {
                // 释放锁
                if (lock != null) {
                    lock.release();
                }
            }
        }

        // 删除文件
        // 首先从文件表获取文件的length
        // 通过length判断 hdfs or hbase
        String dir = key.substring(0, key.lastIndexOf("/") + 1);
        String name = key.substring(key.lastIndexOf("/") + 1);
        String seqId = getDirSeqId(bucket, dir);
        String objKey = seqId + "_" + name;
        Get get = new Get(objKey.getBytes());
        get.addColumn(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_LEN_QUALIFIER);
        Result result = HBaseServiceImpl.getRow(connection, HosUtils.getObjTableName(bucket), get);
        if (result.isEmpty()) {
            return;
        }

        long length = Bytes.toLong(result.getValue(HosUtils.OBJ_META_CF_BYTES, HosUtils.OBJ_LEN_QUALIFIER));
        if (length > HosUtils.FILE_STORE_THRESHOLD) {
            String fileDir = HosUtils.FILE_STORE_ROOT + "/" + bucket + "/" + seqId;
            fileStore.deleteFile(fileDir, name);
        }
        HBaseServiceImpl.deleteRow(connection, HosUtils.getObjTableName(bucket), objKey);
    }

    private boolean isDirExist(String bucket, String dir) throws IOException {
        return listDir(bucket, dir, null, 2).getObjectSummaries().size() == 0;
    }
}
