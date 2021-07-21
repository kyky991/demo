package com.zing.test.netty.idle;

import com.zing.test.netty.Server;

import static com.zing.test.netty.Constants.PORT;

public class IdleServer {

    public static void main(String[] args) throws Exception {
        new Server()
                .childHandler(new IdleStateHandlerInitializer())
                .port(PORT)
                .start();
    }

}
