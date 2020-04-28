package com.zing.concurrency.example.singleton;

import com.zing.concurrency.annotation.Recommend;
import com.zing.concurrency.annotation.ThreadSafe;


@ThreadSafe
@Recommend
public class SingletonExample07 {

    private SingletonExample07() {
    }

    public static SingletonExample07 getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        private SingletonExample07 singleton;

        /**
         * JVM保证此方法只调用一次
         */
        Singleton() {
            singleton = new SingletonExample07();
        }

        public SingletonExample07 getInstance() {
            return singleton;
        }
    }
}
