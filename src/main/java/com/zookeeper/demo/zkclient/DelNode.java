package com.zookeeper.demo.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;

public class DelNode {

    public static void main(String[] args) {
        ZkClient zkClient = new ZkClient("10.1.100.34:2181", 1000, 1000, new SerializableSerializer());
        System.out.println("conneted ok");

        zkClient.delete("/node7");
    }
}
