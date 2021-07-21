package com.zing.test.netty.marshalling;

import com.zing.test.netty.Client;
import io.netty.handler.logging.LoggingHandler;

import static com.zing.test.netty.Constants.HOST;
import static com.zing.test.netty.Constants.PORT;

public class MarshallingClient {

    public static void main(String[] args) throws Exception {
        new Client()
                .handler(new MarshallingInitializer())
                .host(HOST)
                .port(PORT)
                .start();
    }

}
