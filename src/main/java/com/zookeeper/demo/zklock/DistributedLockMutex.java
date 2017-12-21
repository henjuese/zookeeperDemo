package com.zookeeper.demo.zklock;


import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DistributedLockMutex extends BaseDistributedLock implements DistributedLock {


    //锁前缀
    private static final String LOCK_PREFIX = "lock-";

    private final Map<Thread, LockData> threadData = new ConcurrentHashMap<>();
    //上面的threadData可以换成下面的ThreadLocal来实现
    private final ThreadLocal<LockData> dataThreadLocal = new ThreadLocal<>();

    private final String basePath;

    private static class LockData {

        final String lockPath;
        final AtomicInteger lockCount = new AtomicInteger();

        private LockData(String lockPath) {
            this.lockPath = lockPath;
        }
    }

    //如果是同一把锁，可以进入多次（重入锁）定义了一把锁，A方法用到了加锁，B也用到了加锁，A调用B，应该为一把锁，不然将会一直等待。
    private boolean internalLock(long time, TimeUnit unit) throws Exception {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);
        if (lockData != null) {
            lockData.lockCount.incrementAndGet();
            return true;
        }

        String lockPath = attemptLock(time, unit);
        if (StringUtils.hasText(lockPath)) {
            //获取到锁，把临时节点路径存储到threadData里面
            LockData newLockData = new LockData(lockPath);
            threadData.put(currentThread, newLockData);
            return true;
        }
        return false;
    }


    public DistributedLockMutex(ZkclientExt client, String path) {
        super(client, path, LOCK_PREFIX);
        this.basePath = path;
    }

    @Override
    public void acquire() throws Exception {
        if (!internalLock(-1, null)) {
            throw new IOException("连接丢失:" + basePath + "下不存在锁");
        }
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return internalLock(time, unit);
    }

    @Override
    public void release() throws Exception {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);
        if (lockData == null) {
            throw new IllegalMonitorStateException("释放锁失败,该锁不存在");
        }
        //为了实现锁重入，多次获取同一把锁，解锁时减1就OK
        if (lockData.lockCount.get() > 0) {
            lockData.lockCount.decrementAndGet();
            return;
        }

        try {
            releaseLock(lockData.lockPath);
        } finally {
            threadData.remove(currentThread);
        }
    }

    //释放所有锁
    public void releaseAll() throws Exception {
        Thread currentThread = Thread.currentThread();
        LockData lockData = threadData.get(currentThread);
        if (lockData == null) {
            throw new IllegalMonitorStateException("释放锁失败,该锁不存在");
        }
        try {
            releaseLock(lockData.lockPath);
        } finally {
            threadData.remove(currentThread);
        }
    }
}
