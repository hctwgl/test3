package com.ald.fanbei.web.test.common;

import java.util.Map;

import redis.clients.jedis.Jedis;

public class RedisClient {
	static final String HOST = "192.168.106.76";
	static final int PORT = 6379;
	static final String PASSWORD = "Hello1234";
	static Jedis jedis;
	static {
		jedis = new Jedis(HOST, PORT);
		jedis.auth(PASSWORD);
	}
	
	public static void hset(String key, String hkey, String value) {
		jedis.hset(key, hkey, value);
	}
	
	public static void hmset(String key, Map<byte[], byte[]> hash) {
		jedis.hmset(key.getBytes(), hash);
		jedis.expire(key.getBytes(), 7 * 24 * 60 * 60);
	}
	
	public static String hget(String key, String hkey) {
		return jedis.hget(key, hkey);
	}
	
	public static void set(String key, String value) {
		jedis.set(key, value);
	}
	
}