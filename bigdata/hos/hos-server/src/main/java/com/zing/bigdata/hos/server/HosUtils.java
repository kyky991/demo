package com.zing.bigdata.hos.server;

import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;

public class HosUtils {

    public static final byte[][] OBJ_REGIONS = new byte[][] {
            Bytes.toBytes("1"),
            Bytes.toBytes("4"),
            Bytes.toBytes("7")
    };

    public static final String DIR_TABLE_PREFIX = "hos_dir_";
    public static final String OBJ_TABLE_PREFIX = "hos_obj_";

    public static final String DIR_META_CF = "cf";
    public static final byte[] DIR_META_CF_BYTES = DIR_META_CF.getBytes();
    public static final String DIR_SUBDIR_CF = "sub";
    public static final byte[] DIR_SUBDIR_CF_BYTES = DIR_SUBDIR_CF.getBytes();

    public static final String OBJ_META_CF = "cf";
    public static final byte[] OBJ_META_CF_BYTES = OBJ_META_CF.getBytes();
    public static final String OBJ_CONT_CF = "c";
    public static final byte[] OBJ_CONT_CF_BYTES = OBJ_CONT_CF.getBytes();

    public static final byte[] DIR_SEQID_QUALIFIER = "u".getBytes();
    public static final byte[] OBJ_CONT_QUALIFIER = "c".getBytes();
    public static final byte[] OBJ_LEN_QUALIFIER = "l".getBytes();
    public static final byte[] OBJ_PROPS_QUALIFIER = "p".getBytes();
    public static final byte[] OBJ_MEDIATYPE_QUALIFIER = "m".getBytes();

    public static final String FILE_STORE_ROOT = "/hos";
    public static final int FILE_STORE_THRESHOLD = 20 * 1024 * 1024;

    public static final int OBJ_LIST_MAX_COUNT = 200;
    public static final String BUCKET_DIR_SEQ_TABLE = "hos_dir_seq";
    public static final String BUCKET_DIR_SEQ_CF = "s";
    public static final byte[] BUCKET_DIR_SEQ_CF_BYTES = BUCKET_DIR_SEQ_CF.getBytes();
    public static final byte[] BUCKET_DIR_SEQ_QUALIFIER = "s".getBytes();

    public static final FilterList OBJ_META_SCAN_FILTER = new FilterList(FilterList.Operator.MUST_PASS_ONE);

    static {
        try {
            byte[][] qualifiers = new byte[][] {
                    HosUtils.DIR_SEQID_QUALIFIER,
                    HosUtils.OBJ_LEN_QUALIFIER,
                    HosUtils.OBJ_MEDIATYPE_QUALIFIER
            };
            for (byte[] b : qualifiers) {
                Filter filter = new QualifierFilter(CompareFilter.CompareOp.EQUAL, new BinaryComparator(b));
                OBJ_META_SCAN_FILTER.addFilter(filter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDirTableName(String bucket) {
        return DIR_TABLE_PREFIX + bucket;
    }

    public static String getObjTableName(String bucket) {
        return OBJ_TABLE_PREFIX + bucket;
    }

    public static String[] getDirColumnFamily() {
        return new String[] { DIR_SUBDIR_CF, DIR_META_CF };
    }

    public static String[] getObjColumnFamily() {
        return new String[] { OBJ_META_CF, OBJ_CONT_CF };
    }
}
