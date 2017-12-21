package com.zookeeper.demo.zkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.BytesPushThroughSerializer;

public class UpdateNode {

    public static void main(String[] args) {

        //序列化new SerializableSerializer()
        //使用原生值new BytesPushThroughSerializer()
        ZkClient zkClient = new ZkClient("10.1.100.34:2181", 1000, 1000, new BytesPushThroughSerializer());
        System.out.println("conneted ok");

        //这里没有序列化，所有需要使用getBytes()
        zkClient.writeData("/node5","2ewe".getBytes());
    }
}
