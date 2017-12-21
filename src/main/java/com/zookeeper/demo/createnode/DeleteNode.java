package com.zookeeper.demo.createnode;

import org.apache.zookeeper.*;

import java.io.IOException;

public class DeleteNode implements Watcher {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper("10.1.101.162:2181", 5000, new DeleteNode());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    private void doDel() {
        try {
            zooKeeper.delete("/node4", -1);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doDelAsyn() {
        //异步删除一个节点
        zooKeeper.delete("/node4", -1, new IVoidCallback(), null);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("envent=====" + watchedEvent);
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()) {
                doDel();
                //doDelAsyn();
            }
        }
    }

    static class IVoidCallback implements AsyncCallback.VoidCallback {

        @Override
        public void processResult(int rc, String path, Object ctx) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc="+rc).append("\n");
            sb.append("path"+path).append("\n");
            sb.append("ctx="+ctx).append("\n");
            System.out.println(sb.toString());

        }
    }
}










