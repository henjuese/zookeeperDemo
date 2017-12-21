package com.zookeeper.demo.createnode;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

public class UpdateNode implements Watcher {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper("10.1.101.162:2181", 5000, new UpdateNode());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    private void doUpdate() {
        try {
            Stat stat = zooKeeper.setData("/node4", "2222".getBytes(), -1);
            System.out.println("stat:" + stat);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doCreateAsyn() {
        //异步创建一个节点
        zooKeeper.setData("/node4", "234".getBytes(), -1, new IStatCallback(), null);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("envent=====" + watchedEvent);
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()) {
                doUpdate();
                //doCreateAsyn();
            }
        }
    }

    static class IStatCallback implements AsyncCallback.StatCallback {


        @Override
        public void processResult(int rc, String path, Object ctx, Stat stat) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc=" + rc).append("\n");
            sb.append("path=" + path).append("\n");
            sb.append("ctx=" + ctx).append("\n");
            sb.append("stat=" + stat);
            System.out.println(sb.toString());
        }
    }
}










