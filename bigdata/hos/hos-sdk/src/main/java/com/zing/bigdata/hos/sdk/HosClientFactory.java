package com.zing.bigdata.hos.sdk;

import com.zing.bigdata.hos.sdk.impl.DefaultHosClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HosClientFactory {

    private static Map<String, IHosClient> clientCache = new ConcurrentHashMap<>();

    public static IHosClient getOrCreateHosClient(String endpoints, String token) {
        String key = endpoints + "_" + token;
        // 判断clientCache 含有key的hosClient
        if (clientCache.containsKey(key)) {
            return clientCache.get(key);
        }

        // 创建client 放到cache
        IHosClient hosClient = new DefaultHosClient(endpoints, token);
        clientCache.put(key, hosClient);
        return hosClient;
    }

}
