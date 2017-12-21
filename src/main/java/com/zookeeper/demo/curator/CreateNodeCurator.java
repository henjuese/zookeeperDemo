package com.zookeeper.demo.curator;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.util.ArrayList;

public class CreateNodeCurator {

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


        //createNode(client);


        //delNode(client);
        //createAclNode(client);


        //getDataAcl(client);
        updateNode(client);

    }

    private static void getDataAcl() throws Exception {
        RetryPolicy retryPolicy = new RetryUntilElapsed(5000, 1000);

        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2182")
                .sessionTimeoutMs(5000)
                .connectionTimeoutMs(5000)
                .authorization("digest", "bao:123456".getBytes())
                .retryPolicy(retryPolicy)
                .build();
        client.start();
        Stat stat = new Stat();
        byte[] ret = client.getData().storingStatIn(stat).forPath("/node2");
        System.out.println("getdata()" + new String(ret));
        System.out.println(stat);
    }

    private static void delNode(CuratorFramework client) throws Exception {
        //删除节点，自动判断是否有子节点
        client.delete().guaranteed()
                .deletingChildrenIfNeeded()
                .withVersion(-1)
                .forPath("/nodec");
    }

    private static void createNode(CuratorFramework client) throws Exception {
        String path = client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath("/nodec", "sfdd".getBytes());

        System.out.println("path==" + path);
    }

    private static void createAclNode(CuratorFramework client) throws Exception {
        ACL aclIp = new ACL(ZooDefs.Perms.READ, new Id("ip", "127.0.0.1"));
        ACL aclDigest = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE, new Id("digest", DigestAuthenticationProvider.generateDigest("bao:123456")));
        ArrayList<ACL> acls = new ArrayList<ACL>();
        acls.add(aclDigest);
        acls.add(aclIp);

        String path2 = client.create().creatingParentsIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .withACL(acls)
                .forPath("/nodeAcl", "sfdd".getBytes());

        System.out.println("path2==" + path2);
    }

    public static void updateNode(CuratorFramework client) throws Exception {
        Stat stat = new Stat();
        client.getData().storingStatIn(stat).forPath("/node2");

        client.setData().withVersion(stat.getVersion()).forPath("/node2", "123".getBytes());


    }


}
