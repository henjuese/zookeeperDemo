package com.zookeeper.demo.nameservice;

import com.zookeeper.demo.nameservice.IdMaker.RemoveMethod;

public class TestIdMaker {

	public static void main(String[] args) throws Exception {
		
		IdMaker idMaker = new IdMaker("127.0.0.1:2181",
				"/NameService/IdGen", "ID");
		idMaker.start();

		try {
			for (int i = 0; i < 10; i++) {
				String id = idMaker.generateId(RemoveMethod.DELAY);
				System.out.println(id);

			}
		} finally {
			idMaker.stop();

		}
	}

}
