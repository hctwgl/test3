package com.ald.fanbei.web.test.common;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import redis.clients.jedis.Jedis;

@SuppressWarnings("resource")
public class RedisClient {
	static final String HOST = "redistest.server.com";
	static final int PORT = 6379;
	static final String PASSWORD = "Hello1234";
	
	public static void hset(String key, String hkey, String value) {
		Jedis jedis = new Jedis(HOST, PORT);
		jedis.auth(PASSWORD);
		
		jedis.hset(key, hkey, value);
	}
	
	public static void hmset(String key, Map<byte[], byte[]> hash) {
		Jedis jedis = new Jedis(HOST, PORT);
		jedis.auth(PASSWORD);
		
		jedis.hmset(key.getBytes(), hash);
		jedis.expire(key.getBytes(), 7 * 24 * 60 * 60);
	}
	
	public static String hget(String key, String hkey) {
		Jedis jedis = new Jedis(HOST, PORT);
		jedis.auth(PASSWORD);
		
		return jedis.hget(key, hkey);
	}
	
	public static void set(String key, String value) {
		Jedis jedis = new Jedis(HOST, PORT);
		jedis.auth(PASSWORD);
		
		jedis.set(key, value);
	}
	
	public static void setRaw(String key, Object value) {
		Jedis jedis = new Jedis(HOST, PORT);
		jedis.auth(PASSWORD);
		
        ObjectOutputStream oos = null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            byte[] bytes = baos.toByteArray();
            
            jedis.set(key.getBytes("UTF-8"), bytes);
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
}