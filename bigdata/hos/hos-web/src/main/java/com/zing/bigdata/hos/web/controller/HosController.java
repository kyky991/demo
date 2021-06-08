package com.zing.bigdata.hos.web.controller;

import com.google.common.base.Splitter;
import com.zing.bigdata.hos.common.*;
import com.zing.bigdata.hos.core.user.model.CoreUtils;
import com.zing.bigdata.hos.core.user.model.SystemRole;
import com.zing.bigdata.hos.core.user.model.UserInfo;
import com.zing.bigdata.hos.server.service.IBucketService;
import com.zing.bigdata.hos.server.service.IHosStoreService;
import com.zing.bigdata.hos.web.security.ContextUtils;
import com.zing.bigdata.hos.common.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/hos/v1")
public class HosController extends BaseController {

    private static final Logger logger = Logger.getLogger(HosController.class);

    @Autowired
    private IBucketService bucketService;

    @Autowired
    private IHosStoreService hosStoreService;

    private static long MAX_FILE_IN_MEMORY = 2 * 1024 * 1024;

    private final int readBufferSize = 32 * 1024;

    private static String TMP_DIR = System.getProperty("user.dir") + File.separator + "tmp";

    public HosController() {
        File file = new File(TMP_DIR);
        file.mkdirs();
    }

    // 创建bucket
    @RequestMapping(value = "bucket", method = RequestMethod.POST)
    public Object createBucket(@RequestParam("bucket") String bucketName,
                               @RequestParam(name = "detail", required = false, defaultValue = "") String detail) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!currentUser.getSystemRole().equals(SystemRole.VISITOR)) {
            bucketService.addBucket(currentUser, bucketName, detail);

            try {
                hosStoreService.createBucketStore(bucketName);
            } catch (IOException e) {
                bucketService.deleteBucket(bucketName);
                return "error";
            }
            return "success";
        }
        return "PERMISSION DENIED";
    }

    // 删除bucket
    @RequestMapping(value = "bucket", method = RequestMethod.DELETE)
    public Object deleteBucket(@RequestParam("bucket") String bucket) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (operationAccessService.checkBucketOwner(currentUser.getUsername(), bucket)) {
            try {
                hosStoreService.deleteBucketStore(bucket);
            } catch (IOException e) {
                return "delete bucket error";
            }
            bucketService.deleteBucket(bucket);
            return "success";
        }
        return "PERMISSION DENIED";
    }

    @RequestMapping(value = "bucket", method = RequestMethod.PUT)
    public Object updateBucket(@RequestParam(name = "bucket") String bucket,
                               @RequestParam(name = "detail") String detail) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        BucketModel bucketModel = bucketService.getBucketByName(bucket);
        if (operationAccessService.checkBucketOwner(currentUser.getUsername(), bucketModel.getBucketName())) {
            bucketService.updateBucket(bucket, detail);
            return "success";
        }
        return "PERMISSION DENIED";
    }

    @RequestMapping(value = "bucket", method = RequestMethod.GET)
    public Object getBucket(@RequestParam(name = "bucket") String bucket) {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        BucketModel bucketModel = bucketService.getBucketByName(bucket);
        if (operationAccessService.checkPermission(currentUser.getUserId(), bucketModel.getBucketName())) {
            return bucketModel;
        }
        return "PERMISSION DENIED";

    }

    // 获取bucket列表
    @RequestMapping(value = "bucket/list", method = RequestMethod.GET)
    public Object getBucket() {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        List<BucketModel> bucketModels = bucketService.getUserBuckets(currentUser.getUserId());
        return bucketModels;
    }

    // 上传文件（创建目录）
    @RequestMapping(value = "object", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseBody
    public Object putObject(@RequestParam("bucket") String bucket,
                            @RequestParam("key") String key,
                            @RequestParam(value = "mediaType", required = false) String mediaType,
                            @RequestParam(value = "content", required = false) MultipartFile file,
                            HttpServletRequest request,
                            HttpServletResponse response) throws Exception {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return "Permission denied";
        }

        if (!key.startsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("object key must start with /");
        }

        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> attrs = new HashMap<>();
        String contentEncoding = request.getHeader("content-encoding");
        if (contentEncoding != null) {
            attrs.put("content-encoding", contentEncoding);
        }
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if (header.startsWith(HosHeaders.COMMON_ATTR_PREFIX)) {
                attrs.put(header.replace(HosHeaders.COMMON_ATTR_PREFIX, ""), request.getHeader(header));
            }
        }

        ByteBuffer byteBuffer = null;
        File dstFile = null;
        try {
            if (key.endsWith("/")) {
                if (file != null) {
                    response.setStatus(HttpStatus.SC_BAD_REQUEST);
                    file.getInputStream().close();
                    return null;
                }
                hosStoreService.put(bucket, key, null, 0, mediaType, attrs);
                response.setStatus(HttpStatus.SC_OK);
                return "success";
            }
            if (file == null || file.getSize() == 0) {
                response.setStatus(HttpStatus.SC_BAD_REQUEST);
                response.getWriter().write("object content could not be empty");
                return "object content could not be empty";
            } else {
                if (file.getSize() > MAX_FILE_IN_MEMORY) {
                    dstFile = new File(TMP_DIR + File.separator + CoreUtils.getUUIDStr());
                    file.transferTo(dstFile);
                    file.getInputStream().close();
                    byteBuffer = new FileInputStream(dstFile).getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.getSize());
                } else {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    IOUtils.copy(file.getInputStream(), outputStream);
                    byteBuffer = ByteBuffer.wrap(outputStream.toByteArray());
                    file.getInputStream().close();
                }
                hosStoreService.put(bucket, key, byteBuffer, file.getSize(), mediaType, null);
                return "success";
            }

        } catch (IOException e) {
            logger.error(e);
            response.setStatus(HttpStatus.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("server error");
            return "server error";
        } finally {
            if (byteBuffer != null) {
                byteBuffer.clear();
            }
            if (file != null) {
                try {
                    file.getInputStream().close();
                } catch (Exception e) {
                    // nothing to do
                }
            }
            if (dstFile != null) {
                dstFile.delete();
            }
        }
    }

    @RequestMapping(value = "object/list", method = RequestMethod.GET)
    public ObjectListResult listObject(@RequestParam("bucket") String bucket,
                                       @RequestParam("startKey") String startKey,
                                       @RequestParam("stopKey") String stopKey,
                                       HttpServletResponse response) throws IOException {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }

        if (startKey.compareTo(stopKey) > 0) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            return null;
        }

        List<HosObjectSummary> objectSummaries = hosStoreService.list(bucket, startKey, stopKey);
        ObjectListResult result = new ObjectListResult();
        result.setBucket(bucket);
        if (objectSummaries.size() > 0) {
            result.setMaxKey(objectSummaries.get(objectSummaries.size() - 1).getKey());
            result.setMinKey(objectSummaries.get(0).getKey());
        }
        result.setObjectSummaries(objectSummaries);
        result.setObjectCount(objectSummaries.size());
        return result;
    }

    @RequestMapping(value = "object/info", method = RequestMethod.GET)
    public HosObjectSummary getSummary(String bucket, String key, HttpServletResponse response) throws IOException {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }

        HosObjectSummary summary = hosStoreService.getSummary(bucket, key);
        if (summary == null) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
        }
        return summary;
    }

    @RequestMapping(value = "object/list/prefix", method = RequestMethod.GET)
    public ObjectListResult listObjectByPrefix(@RequestParam("bucket") String bucket,
                                               @RequestParam("dir") String dir,
                                               @RequestParam("prefix") String prefix,
                                               @RequestParam(value = "startKey", required = false, defaultValue = "") String start,
                                               HttpServletResponse response)
            throws IOException {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (!dir.startsWith("/") || !dir.endsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("dir must start with / and end with /");
            return null;
        }
        if ("".equals(start) || start.equals("/")) {
            start = null;
        }
        if (start != null) {
            List<String> segs = StreamSupport.stream(Splitter
                    .on("/")
                    .trimResults()
                    .omitEmptyStrings().split(start).spliterator(), false).collect(Collectors.toList());
            start = segs.get(segs.size() - 1);
        }
        ObjectListResult result = hosStoreService.listByPrefix(bucket, dir, prefix, start, 100);
        return result;
    }

    // 列出目录下文件（浏览）
    @RequestMapping(value = "object/list/dir", method = RequestMethod.GET)
    public ObjectListResult listObjectByDir(@RequestParam("bucket") String bucket,
                                            @RequestParam("dir") String dir,
                                            @RequestParam(value = "startKey", required = false, defaultValue = "") String start,
                                            HttpServletResponse response) throws Exception {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return null;
        }
        if (!dir.startsWith("/") || !dir.endsWith("/")) {
            response.setStatus(HttpStatus.SC_BAD_REQUEST);
            response.getWriter().write("dir must start with / and end with /");
            return null;
        }
        if ("".equals(start) || start.equals("/")) {
            start = null;
        }
        if (start != null) {
            List<String> segs = StreamSupport.stream(Splitter
                    .on("/")
                    .trimResults()
                    .omitEmptyStrings().split(start).spliterator(), false).collect(Collectors.toList());
            start = segs.get(segs.size() - 1);
        }

        ObjectListResult result = hosStoreService.listDir(bucket, dir, start, 100);
        return result;
    }

    // 删除文件
    @RequestMapping(value = "object", method = RequestMethod.DELETE)
    public Object deleteObject(@RequestParam("bucket") String bucket,
                               @RequestParam("key") String key) throws Exception {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
            return "PERMISSION DENIED";
        }
        hosStoreService.deleteObject(bucket, key);
        return "success";
    }

    // 下载文件
    @RequestMapping(value = "object/content", method = RequestMethod.GET)
    public void getObject(@RequestParam("bucket") String bucket,
                          @RequestParam("key") String key, HttpServletRequest request,
                          HttpServletResponse response) throws IOException {
        UserInfo currentUser = ContextUtils.getCurrentUser();
        if (!operationAccessService.checkPermission(currentUser.getUserId(), bucket)) {
            response.setStatus(HttpStatus.SC_FORBIDDEN);
            response.getWriter().write("Permission denied");
            return;
        }

        HosObject object = hosStoreService.getObject(bucket, key);
        if (object == null) {
            response.setStatus(HttpStatus.SC_NOT_FOUND);
            return;
        }

        response.setHeader(HosHeaders.COMMON_OBJ_BUCKET, bucket);
        response.setHeader(HosHeaders.COMMON_OBJ_KEY, key);
        response.setHeader(HosHeaders.RESPONSE_OBJ_LENGTH, object.getMetaData().getLength() + "");
        String iflastModify = request.getHeader("If-Modified-Since");
        String lastModify = object.getMetaData().getLastModifyTime() + "";
        response.setHeader("Last-Modified", lastModify);
        String contentEncoding = object.getMetaData().getContentEncoding();
        if (contentEncoding != null) {
            response.setHeader("content-encoding", contentEncoding);
        }
        if (iflastModify != null && iflastModify.equals(lastModify)) {
            response.setStatus(HttpStatus.SC_NOT_MODIFIED);
            return;
        }
        response.setHeader(HosHeaders.COMMON_OBJ_BUCKET, object.getMetaData().getBucket());
        response.setContentType(object.getMetaData().getMediaType());

        OutputStream outputStream = response.getOutputStream();
        InputStream inputStream = object.getContent();
        try {
            byte[] buffer = new byte[readBufferSize];
            int len = -1;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            response.flushBuffer();
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }
}
