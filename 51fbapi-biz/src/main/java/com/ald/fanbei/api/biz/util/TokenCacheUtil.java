package com.ald.fanbei.api.biz.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.TokenBo;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.SerializeUtil;



/**
 * 
 *@类描述：缓存服务
 *@author 陈金虎 2017年1月17日 上午12:05:19
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("tokenCacheUtil")
public class TokenCacheUtil extends AbstractThird{
    protected static Logger   logger           = LoggerFactory.getLogger(TokenCacheUtil.class);
    
    public static boolean BIZ_CACHE_SWITCH = true;//业务缓存开关，true：打开（即使用缓存）  false：关闭（即不使用缓存）
    
    @Resource
    private RedisTemplate<String, Object> cacheRedisTemplate;
    
    /**
     * 设置token
     * @param userId
     * @param token
     * @return
     */
    public void saveToken(String userId, final TokenBo tokenBo) {
        final String key = Constants.CACHEKEY_USER_TOKEN + userId;
        if(tokenBo == null){
            return ;
        }
        if(StringUtils.isBlank(tokenBo.getUserId()) || StringUtils.isBlank(tokenBo.getToken())){
            return ;
        }

        if (!BIZ_CACHE_SWITCH || StringUtils.isBlank(userId)) {
            return;
        }
        try{
	        cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	                connection.hMSet(key.getBytes(), tokenBo.getTokenMap());
	                connection.expire(key.getBytes(), Constants.SECOND_OF_ONE_WEEK);
	                return null;
	            }
	        });
        }catch(Exception e){
        	logger.error("saveToken error",e);
        	return;
        }
        
    }
    
    /**
     * 读取token，读取token之后会从新设计token过期时间
     * @param userId
     * @return
     */
    @SuppressWarnings("unchecked")
    public TokenBo getToken(final String userId) {
        final String key = Constants.CACHEKEY_USER_TOKEN + userId;
        if (!BIZ_CACHE_SWITCH || StringUtils.isBlank(userId)) {
            return null;
        }
        Map<byte[], byte[]> resultCache = null;
        try{
	        resultCache = ((Map<byte[], byte[]>)cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	                Map<byte[], byte[]> cacheData = connection.hGetAll(key.getBytes());
	                if(cacheData != null && !cacheData.isEmpty()){
	                    connection.expire(key.getBytes(), Constants.SECOND_OF_ONE_WEEK);
	                }
	                return cacheData;
	            }
	        }));
        }catch(Exception e){
        	logger.error("getToken",e);
        	return null;
        }
        if(resultCache == null || resultCache.isEmpty()){
            return null;
        }
        TokenBo token = new TokenBo();
        token.setToken(resultCache);
        return token;
    }
    
    /**
     * 删除tokean
     * @param userId
     */
    public void delToken(String userId) {
        String key = Constants.CACHEKEY_USER_TOKEN + userId;
        if (!BIZ_CACHE_SWITCH || StringUtils.isBlank(userId)) {
            return ;
        }
        this.delCache(key);
    }
    
    
    /**
     * 保存到缓存，过期时间为默认过期时间
     * 
     * @param key 缓存key
     * @param seriObj 缓存数据
     */
    public void saveObject(final String key, final Serializable seriObj){
        this.saveObject(key, seriObj, Constants.SECOND_OF_TEN_MINITS);
    }
    
    /**
     * 保存到缓存，并设定过期时间
     * 
     * @param key 缓存key
     * @param seriObj 缓存数据
     * @param expire 过期时间
     */
    public void saveObject(final String key, final Serializable seriObj,final long expire) {
        if(!BIZ_CACHE_SWITCH || StringUtils.isBlank(key) || seriObj == null){
            return ;
        }
        try{
	        cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	                connection.set(cacheRedisTemplate.getStringSerializer().serialize(key), SerializeUtil.serialize(seriObj));
	                connection.expire(cacheRedisTemplate.getStringSerializer().serialize(key), expire);
	                return null;
	            }
	        });
        }catch(Exception e){
        	logger.error("saveObject",e);
        }
    }
    
    /**
     * 获取缓存对象
     * @param key 缓存key
     * @return
     */
    public Object getObject(final String key) {
        if (!BIZ_CACHE_SWITCH || StringUtils.isBlank(key)) {
            return null;
        }
        try{
	        return cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	            	byte[] retBytes = connection.get(cacheRedisTemplate.getStringSerializer().serialize(key));
	            	if(retBytes == null || retBytes.length == 0){
	            		return null;
	            	}
	                return  SerializeUtil.unserialize(retBytes);
	            }
	        });
        }catch(Exception e){
        	logger.error("getObject error",e);
        	return null;
        }
    }
    
    /**
     * 锁住某个key值，需要解锁时删除即可
     * @param key
     * @param value
     * @return
     */
    public boolean getLock(final String key, final String value) {
        if(!BIZ_CACHE_SWITCH){
            return false;
        }
        try{
	        return (Boolean)cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
	                return connection.setNX(cacheRedisTemplate.getStringSerializer().serialize(key), value.getBytes());
	            }
	        });
        }catch(Exception e){
        	logger.error("getLock error",e);
        	return false;
        }
    }
    
    /**
     * 重试多次来获取锁，重试times次
     * @param key 锁对应的key
     * @param value 锁对应的value
     * @param times 重试次数
     * @return
     */
    public boolean getLockTryTimes(String key, String value, int times) {
        try {
            if (!BIZ_CACHE_SWITCH) {
                return false;
            }
            if (times < 2) {
                return getLock(key, value);
            }
            for (int i = 0; i < times; i++) {
                Thread.sleep(RandomUtils.nextInt(10));
                boolean ifGet = getLock(key, value);
                if (ifGet) {
                    return true;
                }
                if (i == times - 1) {
                    this.delCache(key);
                }
            }
        } catch (Exception e) {
            logger.error("get object error", e);
            return false;
        }
        return false;
    }
    
    /**
     * 删除缓存
     * @param key 需要删除缓存的id
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void delCache(final String key) {
        if (!BIZ_CACHE_SWITCH) {
            return;
        }
        try{
	        cacheRedisTemplate.execute(new RedisCallback() {
	            public Long doInRedis(RedisConnection connection) throws DataAccessException {
	                return connection.del(cacheRedisTemplate.getStringSerializer().serialize(key));
	            }
	        });
        }catch(Exception e){
        	logger.error("delCache error",e);
        	
        }
    }
    
    /**
     * 数据类型为List的数据写入缓存
     * @param key 写入缓存数据的key
     * @param seriObjList 需要写入缓存的数据
     */
    public <T> void saveObjectList(final String key, final List<T> seriObjList) {
    	this.saveObjectListExpire(key, seriObjList, Constants.SECOND_OF_TEN_MINITS);
    }
    
    /**
     * 数据类型为List的数据写入缓存
     * @param key 写入缓存数据的key
     * @param seriObjList 需要写入缓存的数据
     */
    public <T> void saveObjectListExpire(final String key, final List<T> seriObjList,final Long expire) {
    	try{
	        if (!BIZ_CACHE_SWITCH || StringUtils.isBlank(key) || seriObjList == null || seriObjList.size() < 1) {
	            return;
	        }
	        cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	                connection.set(cacheRedisTemplate.getStringSerializer().serialize(key), SerializeUtil.serializeList(seriObjList));
	                connection.expire(cacheRedisTemplate.getStringSerializer().serialize(key), expire);
	                return null;
	            }
	        });
    	}catch(Exception e){
    		logger.error("saveObjectListExpire" + key,e);
    		
    	}
    }
    
    /**
     * 获取数据类型为List的缓存数据
     * @param key 缓存数据对应的key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> getObjectList(final String key) {
    	try{
	        if (!BIZ_CACHE_SWITCH || StringUtils.isBlank(key)) {
	            return null;
	        }
	        Object result = cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	                byte[] retBytes = connection.get(cacheRedisTemplate.getStringSerializer().serialize(key));
	            	if(retBytes == null || retBytes.length == 0){
	            		return null;
	            	}
	                return SerializeUtil.unserializeList(retBytes);
	            }
	        });
	        return (List<T>) result;
    	}catch(Exception e){
    		logger.error("getObjectList error" + key,e);;
    		return null;
    	}
    }
    
    /**
     * 该接口只为检测缓存健康使用
     * 
     * @param key
     * @param seriObj
     * @param timeOutMilliens
     * @return
     */
    public boolean putObjectForCheckHeath(final String key, final Serializable seriObj, final long expire) {
    	try{
	        cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	                connection.set(cacheRedisTemplate.getStringSerializer().serialize(key), SerializeUtil.serialize(seriObj));
	                connection.expire(cacheRedisTemplate.getStringSerializer().serialize(key), expire);
	                return null;
	            }
	        });
    	}catch(Exception e){
    		logger.error("putObjectForCheckHeath error",e);
    		return false;
    	}
        return true;
    }
    
    /**
     * 该接口只为检测缓存健康使用
     * @param key
     * @return
     */
    public Object getObjectForCheckHeath(final String key) {
    	try{
	        return cacheRedisTemplate.execute(new RedisCallback<Object>() {
	            @Override
	            public Object doInRedis(RedisConnection connection) throws DataAccessException {
	            	byte[] retBytes = connection.get(cacheRedisTemplate.getStringSerializer().serialize(key));
	            	if(retBytes == null || retBytes.length == 0){
	            		return null;
	            	}
	                return  SerializeUtil.unserialize(retBytes);
	            }
	        });
    	}catch(Exception e){
    		logger.error("getObjectForCheckHeath err",e);
    		return null;
    	}
    }
    
}
