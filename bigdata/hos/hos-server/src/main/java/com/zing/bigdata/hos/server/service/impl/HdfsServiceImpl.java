package com.zing.bigdata.hos.server.service.impl;

import com.zing.bigdata.hos.core.ErrorCode;
import com.zing.bigdata.hos.server.HosServerException;
import com.zing.bigdata.hos.server.service.IHdfsService;
import org.apache.commons.io.FileExistsException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public class HdfsServiceImpl implements IHdfsService {

    private static final Logger logger = Logger.getLogger(HdfsServiceImpl.class);

    private static final long defaultBlockSize = 128 * 1024 * 1024;

    private static final long initBlockSize = defaultBlockSize / 2;

    private static final int readBuffer = 512 * 1024;

    private FileSystem fileSystem;

    @Value("${hadoop.conf.dir}")
    private String HADOOP_CONF_DIR;

    @Value("${hadoop.url}")
    private String HADOOP_URL;

    public void init() throws Exception {
        // 1.读取hdfs相关的配置信息
        String confDir = HADOOP_CONF_DIR;
        String hdfsUrI = HADOOP_URL;
        // hdfs://localhost:9000

        // 2.通过配置获取一个filesystem的实例
        Configuration configuration = new Configuration();
//        configuration.set("fs.hdfs.impl","org.apache.hadoop.hdfs.DistributedFileSystem");
//        configuration.addResource(new Path(confDir + "/hdfs-site.xml"));
//        configuration.addResource(new Path(confDir + "/core-site.xml"));
        fileSystem = FileSystem.get(new URI(HADOOP_URL), configuration, "root");
    }

    @Override
    public void saveFile(String dir, String name, InputStream inputStream, long length, short replication) throws IOException {
        // 1.判断dir是否存在，不存在则创建
        Path dirPath = new Path(dir);
        try {
            if (!fileSystem.exists(dirPath)) {
                boolean success = fileSystem.mkdirs(dirPath, FsPermission.getDirDefault());
                if (!success) {
                    throw new HosServerException(ErrorCode.ERROR_HDFS, "create dir " + dirPath + " error");
                }
            }
        } catch (FileExistsException e) {
            e.printStackTrace();
        }
        // 2.保存文件
        Path path = new Path(dir + "/" + name);
        long blockSize = length <= initBlockSize ? initBlockSize : defaultBlockSize;
        FSDataOutputStream outputStream = fileSystem.create(path, true, readBuffer, replication, blockSize);

        try {
            fileSystem.setPermission(path, FsPermission.getFileDefault());
            byte[] buffer = new byte[readBuffer];
            int len = -1;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }

    @Override
    public void deleteFile(String dir, String name) throws IOException {
        fileSystem.delete(new Path(dir + "/" + name), false);
    }

    @Override
    public InputStream openFile(String dir, String name) throws IOException {
        return fileSystem.open(new Path(dir + "/" + name));
    }

    @Override
    public void mkDir(String dir) throws IOException {
        fileSystem.mkdirs(new Path(dir));
    }

    @Override
    public void deleteDir(String dir) throws IOException {
        fileSystem.delete(new Path(dir), true);
    }
}
