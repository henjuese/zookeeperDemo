package com.zookeeper.demo.zklock;

import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

public class TestLock2 {

    public static void main(String[] args) {


        final ZkclientExt zkclientExt1 = new ZkclientExt("127.0.0.1:2181", 5000, 5000, new BytesPushThroughSerializer());

        final DistributedLockMutex mutex = new DistributedLockMutex(zkclientExt1, "/Mutex");

        try {
            mutex.acquire();
            System.out.println("client 2 获取到锁了");
            Thread.sleep(20000);//10s
            mutex.release();
            System.out.println("client 2 释放了锁");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
