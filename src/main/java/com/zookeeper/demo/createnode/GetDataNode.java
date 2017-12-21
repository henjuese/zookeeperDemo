package com.zookeeper.demo.createnode;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

public class GetDataNode implements Watcher {

    private static ZooKeeper zooKeeper;

    private static Stat stat = new Stat();

    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper("10.1.101.162:2181", 5000, new GetDataNode());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    //同步获取节点
    private void doGetData() {
        //如果节点设置了登录权限，则需要先登录才行
        //zooKeeper.addAuthInfo("digest", "bbb:123456".getBytes());
        try {
            System.out.println("doGetData====" + new String(zooKeeper.getData("/node5", true, stat)));
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //异步获取节点
    private void doGetDataAsyn() {
        zooKeeper.getData("/node5", true, new IDataCallback(), null);
    }

    //同步获取子节点
    private void doGetListChildren() {
        try {
            //第二个参数为true则表示对子节点的变动感兴趣，如果有变动，则会则下面的事件中会收到通知。
            List<String> children = zooKeeper.getChildren("/", false);
            System.out.println("children====" + children);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //异步获取子节点
    private void doGetListChildrenAsyn() {
        zooKeeper.getChildren("/", true, new IChildren2Callback(), null);
    }


    //服务回调事件
    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("envent=====" + watchedEvent);
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            //刚刚建立连接时状态为null，路径也会null，这样过滤则表示doXXX()方法只会被执行一次。
            if (watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()) {
                //doGetData();
                //doGetDataAsyn()
                doGetListChildren();
            } else {
                //获取节点
                if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                    try {
                        System.out.println("sdfsdfsdf=====");
                        //watch只会触发一次，第二个参数为true，表示循环监听，第二次触发时也能监听到
                        System.out.println(new String(zooKeeper.getData(watchedEvent.getPath(), true, stat)));
                        System.out.println("stat:" + stat);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                    //获取子节点
                    try {
                        System.out.println(zooKeeper.getChildren(watchedEvent.getPath(), true));
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    static class IDataCallback implements AsyncCallback.DataCallback {

        @Override
        public void processResult(int rc, String path, Object ctx, byte[] data,
                                  Stat stat) {
            try {
                System.out.println(new String(zooKeeper.getData(path, true, stat)));
                System.out.println("stat:" + stat);
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        }

    }

    //异步获取自节点
    static class IChildren2Callback implements AsyncCallback.Children2Callback {

        @Override
        public void processResult(int rc, String path, Object ctx,
                                  List<String> children, Stat stat) {

            StringBuilder sb = new StringBuilder();
            sb.append("rc=" + rc).append("\n");
            sb.append("path=" + path).append("\n");
            sb.append("ctx=" + ctx).append("\n");
            sb.append("children=" + children).append("\n");
            sb.append("stat=" + stat).append("\n");
            System.out.println(sb.toString());

        }


    }


}










