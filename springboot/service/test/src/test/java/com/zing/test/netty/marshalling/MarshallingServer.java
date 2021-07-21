package com.zing.test.netty.marshalling;

import com.zing.test.netty.Server;

import static com.zing.test.netty.Constants.PORT;

public class MarshallingServer {

    public static void main(String[] args) throws Exception {
        new Server()
                .childHandler(new MarshallingInitializer())
                .port(PORT)
                .start();
    }

}
