package com.zookeeper.demo.zklock;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkNoNodeException;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BaseDistributedLock {


    private final ZkclientExt client;

    private final String path;

    private final String basePath;

    private final String lockName;

    private static final Integer MAX_RETRY_COUNT = 10;


    public BaseDistributedLock(ZkclientExt client, String path, String lockName) {
        this.client = client;
        this.basePath = path;
        this.lockName = lockName;
        this.path = path.concat("/").concat(lockName);
    }

    private String createLockNode(ZkClient client, String path) {
        return client.createEphemeralSequential(path, null);
    }

    private void deleteOurPath(String ourPath) {
        client.delete(ourPath);
    }


    private boolean waitToLock(long startMillis, Long millisToWait, String ourPath) throws Exception {
        boolean haveTheLock = false;
        boolean doDelete = false;

        try {
            while (!haveTheLock) {
                List<String> children = getSortedChiledren();
                String sequenceNodeName = ourPath.substring(basePath.length() + 1);
                //获取当前创建的临时节点位置
                int ourIndex = children.indexOf(sequenceNodeName);
                if (ourIndex < 0) {
                    throw new ZkNoNodeException("节点不存在：" + sequenceNodeName);
                }
                //第0个，说明是排在所有节点最全面的，那么他将拥有锁
                boolean isGetTheLock = ourIndex == 0;
                //children.get(ourIndex - 1)如果没有获取到锁，则监听自己的上一个节点（这里设计很巧妙，不是监听/Mutex
                //节点的变化，如果监听了/Mutex节点的变化，当锁节点释放锁时/Mutex发生了变化，但是为导致所有等待的锁节点都收到通知，而他们会一起去抢夺锁资源）
                String pathToWatch = isGetTheLock ? null : children.get(ourIndex - 1);

                //拥有锁
                if (isGetTheLock) {
                    haveTheLock = true;
                } else {
                    //没有锁，则等待
                    System.out.println("没有获取到锁，等待它:" + pathToWatch + " 释放锁");
                    String previousSequencePath = basePath.concat("/").concat(pathToWatch);
                    final CountDownLatch latch = new CountDownLatch(1);
                    final IZkDataListener preiousListener = new IZkDataListener() {
                        @Override
                        public void handleDataChange(String dataPath, Object data) throws Exception {
                            //数据为null，不存在去改变它的内容，所以这里不需要做什么
                        }

                        @Override
                        public void handleDataDeleted(String dataPath) throws Exception {
                            latch.countDown();
                        }
                    };

                    try {
                        //如果监听节点在此时已经被删除，则监听会抛异常。捕获异常，继续while循环，再次尝试获取锁
                        //订阅节点改变
                        client.subscribeDataChanges(previousSequencePath, preiousListener);
                        //如果设置了等待时间，则按照等待时间等待
                        if (millisToWait != null) {
                            millisToWait -= (System.currentTimeMillis() - startMillis);
                            startMillis = System.currentTimeMillis();
                            if (millisToWait <= 0) {
                                doDelete = true;
                                break;
                            }
                            latch.await(millisToWait, TimeUnit.MICROSECONDS);
                        } else {
                            //等待释放
                            latch.await();
                        }
                    } catch (ZkNoNodeException e) {
                        e.printStackTrace();
                        System.out.println("节点不存在");
                    } finally {
                        client.unsubscribeDataChanges(previousSequencePath, preiousListener);
                    }
                }

            }
        } catch (Exception e) {
            doDelete = true;
            throw e;
        } finally {
            if (doDelete) {
                //程序出现异常，或者设置了获取锁的超时时间，则应该删除该节点
                deleteOurPath(ourPath);
            }
        }
        return haveTheLock;
    }

    //获取排序后的子节点
    List<String> getSortedChiledren() {
        List<String> children = client.getChildren(basePath);
        children.sort(new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                return getLockNodeNumber(lhs, lockName).compareTo(getLockNodeNumber(rhs, lockName));
            }
        });
        return children;
    }

    private String getLockNodeNumber(String str, String lockName) {
        int index = str.lastIndexOf(lockName);
        if (index >= 0) {
            index += lockName.length();
            return index <= str.length() ? str.substring(index) : "";
        }
        return str;

    }

    protected void releaseLock(String lockPath) throws Exception {
        deleteOurPath(lockPath);
    }


    protected String attemptLock(long time, TimeUnit unit) throws Exception {
        final long startMillis = System.currentTimeMillis();
        final Long millisToWait = unit != null ? unit.toMillis(time) : null;

        String ourPath = null;
        boolean hasTheLock = false;
        boolean isDone = false;
        int retryCount = 0;
        while (!isDone) {
            isDone = true;
            try {
                //创建临时节点
                ourPath = createLockNode(client, path);
                System.out.println("ourPath=" + ourPath);
                //获取锁
                hasTheLock = waitToLock(startMillis, millisToWait, ourPath);

            } catch (ZkNoNodeException e) {
                //重试机制
                if (retryCount++ < MAX_RETRY_COUNT) {
                    isDone = false;
                } else {
                    throw e;
                }
            }
        }
        //获取到锁，返回节点路径
        if (hasTheLock) {
            return ourPath;
        }
        return null;
    }


}
