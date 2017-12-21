package com.zookeeper.demo.zkclient;


import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class CreateNode {

    public static void main(String[] args) {
//        noSerializer();
        objectSrializer();
    }

    //将对象序列化后存入zk中
    private static void objectSrializer() {
        //无须序列化：new BytesPushThroughSerializer()，不使用序列化时，不能添加对象。字符串也不行，必须用"sdfsdf".getby
        //使用序列化：new SerializableSerializer()
        ZkClient zkClient = new ZkClient("10.1.100.34:2181", 1000, 1000, new SerializableSerializer());
        System.out.println("conneted ok");

        UserInfo userInfo = new UserInfo();
        userInfo.setId(12);
        userInfo.setName("sdf");

        String path = zkClient.create("/node7", userInfo, CreateMode.PERSISTENT);
        System.out.println("path====" + path);
    }

    private static void createAclNode(ZkClient zkClient) throws NoSuchAlgorithmException {
        //对IP为10.1.101.162创建权限，其他节点将不在有权限读取
        ACL aclIp = new ACL(ZooDefs.Perms.READ, new Id("ip", "10.1.101.162"));
        //为节点添加登录用户名和密码，其他节点只有登录了用户名和密码之后才能进行读写权限。
        //用户名：密码为：bao:123456
        ACL aclDigest = new ACL(ZooDefs.Perms.READ | ZooDefs.Perms.WRITE, new Id("digest", DigestAuthenticationProvider.generateDigest("bao:123456")));

        ArrayList<ACL> acls = new ArrayList<ACL>();
        acls.add(aclDigest);
        acls.add(aclIp);
        zkClient.create("/node8", "sdff", acls, CreateMode.PERSISTENT);
    }

    //普通对象存入zk中
    private static void noSerializer() {
        ZkClient zkClient = new ZkClient("10.1.100.34:2181", 1000, 1000);
        System.out.println("conneted ok");

        String path = zkClient.create("/node6", "sdfsdf", CreateMode.PERSISTENT);
        System.out.println("path====" + path);
    }

}
