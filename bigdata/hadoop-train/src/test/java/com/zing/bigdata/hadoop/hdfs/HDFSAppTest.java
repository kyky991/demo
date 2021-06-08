package com.zing.bigdata.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.util.Progressable;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;

public class HDFSAppTest {

    private static final String HDFS_PATH = "hdfs://hadooooop:9000";

    private FileSystem fileSystem = null;
    private Configuration configuration = null;

    @Before
    public void setUp() throws Exception {
        configuration = new Configuration();
        fileSystem = FileSystem.get(new URI(HDFS_PATH), configuration, "root");
    }

    @After
    public void tearDown() throws Exception {
        configuration = null;
        fileSystem = null;
    }

    @Test
    public void mkdir() throws Exception {
        fileSystem.mkdirs(new Path("/hdfsapi/test"));
    }

    @Test
    public void create() throws Exception {
        FSDataOutputStream outputStream = fileSystem.create(new Path("/hdfsapi/test/a.txt"));
        outputStream.write("hello".getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }

    @Test
    public void cat() throws Exception {
        FSDataInputStream inputStream = fileSystem.open(new Path("/hdfsapi/test/a.txt"));
        IOUtils.copyBytes(inputStream, System.out, 1024);
        inputStream.close();
    }

    @Test
    public void rename() throws Exception {
        Path oldPath = new Path("/hdfsapi/test/a.txt");
        Path newPath = new Path("/hdfsapi/test/b.txt");
        fileSystem.rename(oldPath, newPath);
    }

    @Test
    public void copyFromLocalFile() throws Exception {
        Path localPath = new Path("D:\\tmp\\11.jpg");
        Path hdfsPath = new Path("/hdfsapi/test/11.jpg");
        fileSystem.copyFromLocalFile(localPath, hdfsPath);
    }

    @Test
    public void copyFromLocalFileWithProgress() throws Exception {
        InputStream inputStream = new BufferedInputStream(
                new FileInputStream(
                        new File("D:\\tmp\\bigfile.tar.gz")));

        FSDataOutputStream outputStream = fileSystem.create(new Path("/hdfsapi/test/bigfile.tar.gz"),
                new Progressable() {
                    @Override
                    public void progress() {
                        System.out.print(".");
                    }
                });

        IOUtils.copyBytes(inputStream, outputStream, 4096);
    }

    @Test
    public void copyToLocalFile() throws Exception {
        Path localPath = new Path("D:\\tmp\\b.txt");
        Path hdfsPath = new Path("/hdfsapi/test/b.txt");
        fileSystem.copyToLocalFile(false, hdfsPath, localPath, true);
    }

    @Test
    public void listFiles() throws Exception {
        FileStatus[] fileStatuses = fileSystem.listStatus(new Path("/hdfsapi/test"));
        for (FileStatus fileStatus : fileStatuses) {
            System.out.println((fileStatus.isDirectory() ? "文件夹" : " 文件")
                    + " : " + fileStatus.getReplication()
                    + " : " + fileStatus.getLen() + " bytes"
                    + " : " + fileStatus.getPath().toString());
        }
    }

    @Test
    public void delete() throws Exception {
        fileSystem.delete(new Path("/hdfsapi/test/bigfile.tar.gz"), false);
    }
}
