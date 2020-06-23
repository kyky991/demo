package com.zing.action.zookeeper.utils;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;

public class AclUtils {

    public static String getDigestPassword(String id) throws Exception {
        return DigestAuthenticationProvider.generateDigest(id);
    }

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException, Exception {
        String id = "test:test";
        String idDigested = getDigestPassword(id);
        System.out.println(idDigested);
    }

}
