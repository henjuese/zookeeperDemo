package com.zookeeper.demo.zklock;

import java.util.concurrent.TimeUnit;

public interface DistributedLock {

    /**
     * 获取锁，没有获取到则等待
     */
    public void acquire() throws Exception;

    public boolean acquire(long time, TimeUnit unit) throws Exception;

    /**
     * 释放锁
     * @throws Exception
     */
    public void release() throws Exception;

}
