package com.zookeeper.demo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.*;
import org.apache.curator.retry.RetryUntilElapsed;


public class GetNodeCurator {

    public static void main(String[] args) throws Exception {
        //连接超时重试机制
        //RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        //RetryPolicy retryPolicy = new RetryNTimes(5, 1000);
        RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2182")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .retryPolicy(retryPolicy)
                .build();
        client.start();

        //监听node2节点的数据变化
        listenerDataChange(client);

        //监听节点的子节点发生的变化
        listenerNodeChange(client);

        Thread.sleep(Integer.MAX_VALUE);
    }

    private static void listenerNodeChange(CuratorFramework client) throws Exception {
        //监听/node2节点的子节点的变化。true为持续监听，false为只监听一次。
        final PathChildrenCache cache = new PathChildrenCache(client, "/node2", true);
        cache.start();
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()) {
                    case CHILD_ADDED:
                        System.out.println("子节点添加" + event.getData());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("更新");
                        break;
                    case CHILD_REMOVED:
                        System.out.println("移除");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private static void listenerDataChange(CuratorFramework client) throws Exception {
        final NodeCache cache = new NodeCache(client, "/node2");
        cache.start();
        cache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                byte[] data = cache.getCurrentData().getData();
                System.out.println("data:" + new String(data));
            }
        });
    }


}
