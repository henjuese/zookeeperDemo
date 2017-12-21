package com.zookeeper.demo.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class GetNode {

    public static void main(String[] args) {
//        getUser();
        getChildren();

    }

    private static void getChildren() {
        ZkClient zkClient = new ZkClient("10.1.100.34:2181", 1000, 1000, new SerializableSerializer());
        System.out.println("conneted ok");
        List<String> cList = zkClient.getChildren("/");
        System.out.println(cList);
    }


    private static void getUser() {
        ZkClient zkClient = new ZkClient("10.1.100.34:2181", 1000, 1000, new SerializableSerializer());
        System.out.println("conneted ok");
        Stat stat = new Stat();
        UserInfo userInfo= zkClient.readData("/node7",stat);
        System.out.println(userInfo.toString());
        System.out.println(stat);
    }
}
