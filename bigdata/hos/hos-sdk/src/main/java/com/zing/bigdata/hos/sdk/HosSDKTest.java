package com.zing.bigdata.hos.sdk;

import com.zing.bigdata.hos.common.BucketModel;
import com.zing.bigdata.hos.common.HosObjectSummary;

import java.util.List;

public class HosSDKTest {

    private static String token = "726b1d3a9e81496eb915128e2b422daa";
    private static String endPoints = "http://127.0.0.1:8090";

    public static void main(String[] args) {
        final IHosClient client = HosClientFactory.getOrCreateHosClient(endPoints, token);
        try {
            List<BucketModel> bucketModels = client.listBucket();
            bucketModels.forEach(bucketModel -> {
                System.out.println(bucketModel.getBucketName() + " | " + bucketModel.getCreator());
            });

            HosObjectSummary objectSummary = client.getObjectSummary("userbucket", "/dir1/dir2/file2.jpg");
            System.out.println(objectSummary.getBucket() + " | " + objectSummary.getName() + " | " + objectSummary.getKey());
        } catch (Exception e) {
            //nothing to do
        }
    }

}
