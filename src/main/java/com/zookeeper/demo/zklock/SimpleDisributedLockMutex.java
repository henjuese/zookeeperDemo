package com.zookeeper.demo.zklock;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SimpleDisributedLockMutex extends BaseDistributedLock implements DistributedLock {


    private static final String LOCK_NAME = "lock-";

    private final String basePath;

    private String ourLockPath;

    public SimpleDisributedLockMutex(ZkclientExt client, String path) {
        super(client, path, LOCK_NAME);
        this.basePath = path;
    }

    //简单加锁，没有锁重入功能。同一把锁，不能进入多次。
    private boolean internalLock(long time, TimeUnit unit) throws Exception {
        ourLockPath = attemptLock(time, unit);
        return ourLockPath != null;
    }

    @Override
    public void acquire() throws Exception {
        if (!internalLock(-1, null)) {
            throw new IOException("连接丢失" + basePath);
        }
    }

    @Override
    public boolean acquire(long time, TimeUnit unit) throws Exception {
        return internalLock(time, unit);
    }

    @Override
    public void release() throws Exception {
        releaseLock(ourLockPath);
    }
}
