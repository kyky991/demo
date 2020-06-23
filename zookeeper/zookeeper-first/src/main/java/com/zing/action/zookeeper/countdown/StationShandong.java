package com.zing.action.zookeeper.countdown;

import java.util.concurrent.CountDownLatch;

public class StationShandong extends DangerCenter {

    public StationShandong(CountDownLatch countDown) {
        super(countDown, "山东调度站");
    }

    @Override
    public void check() {
        System.out.println("正在检查 [" + this.getStation() + "]...");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("检查 [" + this.getStation() + "] 完毕，可以发车~");
    }

}
