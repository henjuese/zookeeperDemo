package com.zookeeper.demo.zkclient;

import org.I0Itec.zkclient.IZkChildListener;
import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.IZkStateListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;
import org.apache.zookeeper.Watcher;

import java.util.List;

/**
 * 订阅数据的改变
 */
public class SubscribeChanges {

    public static void main(String[] args) throws InterruptedException {
        ZkClient zkClient = new ZkClient("localhost:2181", 1000, 1000, new BytesPushThroughSerializer());
        System.out.println("conneted ok");
        //订阅/node2节点的数据变化
        zkClient.subscribeDataChanges("/node2", new ZkDataListener());

        //订阅跟节点的子节点变化:创建或者删除
        zkClient.subscribeChildChanges("/node2", new ZkChildListener());

        //订阅状态的变化
        zkClient.subscribeStateChanges(new IZkStateListener() {
            @Override
            public void handleStateChanged(Watcher.Event.KeeperState keeperState) throws Exception {

            }

            @Override
            public void handleNewSession() throws Exception {

            }

            @Override
            public void handleSessionEstablishmentError(Throwable throwable) throws Exception {

            }
        });
        Thread.sleep(Integer.MAX_VALUE);
    }

    //订阅zk数据变化
    private static class ZkDataListener implements IZkDataListener {

        @Override
        public void handleDataChange(String s, Object o) throws Exception {
            System.out.println("dataPath===" + s + ":" + o.toString());
        }

        @Override
        public void handleDataDeleted(String s) throws Exception {
            System.out.println(s);
        }
    }

    //订阅子节点的变化
    private static class ZkChildListener implements IZkChildListener {

        @Override
        public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
            System.out.println("parentPath:" + parentPath);
            System.out.println("currentChilds==" + currentChilds.toString());
        }
    }
}
