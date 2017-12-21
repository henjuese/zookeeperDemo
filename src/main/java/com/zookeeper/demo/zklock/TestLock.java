package com.zookeeper.demo.zklock;

import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

public class TestLock {

    public static void main(String[] args) {


        final ZkclientExt zkclientExt1 = new ZkclientExt("127.0.0.1:2181", 5000, 5000, new BytesPushThroughSerializer());

        final DistributedLockMutex mutex = new DistributedLockMutex(zkclientExt1, "/Mutex");

        try {
            mutex.acquire();
            System.out.println("client 1 获取到锁了");
            mutex.acquire();//锁重入，同一把锁，可以多次加锁
            mutex.release();//多次释放锁
            System.out.println("client 1 再次获取到同一把锁");
            Thread.sleep(30000);//10s
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mutex.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("client 1 释放了锁");
        }
    }
}
