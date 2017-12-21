package com.zookeeper.demo.createnode;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class CreateNode implements Watcher {

    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException {
        zooKeeper = new ZooKeeper("10.1.101.162:2181", 5000, new CreateNode());
        System.out.println(zooKeeper.getState());
        Thread.sleep(Integer.MAX_VALUE);
    }

    private void doCreate() {
        try {
            //OPEN_ACL_UNSAFE表示所有人都可以做它做所有操作
            //同步创建一个节点
            String path = zooKeeper.create("/node4", "1233".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("return path:===" + path);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //创建带有权限访问的节点
    private void doCreateAuth() {
        try {
            //对IP为10.1.101.162创建权限，其他节点将不在有权限读取
            ACL aclIp = new ACL(ZooDefs.Perms.READ, new Id("ip", "10.1.101.162"));
            //为节点添加登录用户名和密码，其他节点只有登录了用户名和密码之后才能进行读写权限。
            //用户名：密码为：bao:123456
            ACL aclDigest = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE, new Id("digest", DigestAuthenticationProvider.generateDigest("bao:123456")));

            ArrayList<ACL> acls = new ArrayList<ACL>();
            acls.add(aclDigest);
            acls.add(aclIp);
            String path = zooKeeper.create("/node4", "1233".getBytes(), acls, CreateMode.PERSISTENT);

            //需要get节点信息时，可以使用
            //zookeeper.addAuthInfo("digest", "bao:123456".getBytes());来登录
            System.out.println("return path:===" + path);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void doCreateAsyn() {
        //异步创建一个节点
        zooKeeper.create("/node5", "1233".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new IStringCallback(), "创建");
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        System.out.println("envent=====" + watchedEvent);
        //是否已经连接
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (watchedEvent.getType() == Event.EventType.None && null == watchedEvent.getPath()) {
                //doCreate();
                doCreateAsyn();
            }
        }
    }

    static class IStringCallback implements AsyncCallback.StringCallback {

        @Override
        public void processResult(int rc, String path, Object ctx, String name) {
            StringBuilder sb = new StringBuilder();
            sb.append("rc=" + rc).append("\n");
            sb.append("path=" + path).append("\n");
            sb.append("ctx=" + ctx).append("\n");
            sb.append("name=" + name);
            System.out.println(sb.toString());
        }
    }
}










