package com.ald.fanbei.api.common.redisson;

import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.redisson.Redisson;
import org.redisson.api.ClusterNodesGroup;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.Node;
import org.redisson.api.NodesGroup;
import org.redisson.api.RAtomicDouble;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBatch;
import org.redisson.api.RBinaryStream;
import org.redisson.api.RBitSet;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RBoundedBlockingQueue;
import org.redisson.api.RBucket;
import org.redisson.api.RBuckets;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RDeque;
import org.redisson.api.RGeo;
import org.redisson.api.RHyperLogLog;
import org.redisson.api.RKeys;
import org.redisson.api.RLexSortedSet;
import org.redisson.api.RList;
import org.redisson.api.RListMultimap;
import org.redisson.api.RListMultimapCache;
import org.redisson.api.RLiveObjectService;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RPatternTopic;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RQueue;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RRemoteService;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RScript;
import org.redisson.api.RSemaphore;
import org.redisson.api.RSet;
import org.redisson.api.RSetCache;
import org.redisson.api.RSetMultimap;
import org.redisson.api.RSetMultimapCache;
import org.redisson.api.RSortedSet;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.CodecProvider;
import org.redisson.config.Config;
import org.redisson.liveobject.provider.ResolverProvider;
/**
 * redisson分布式锁解决方案
 * @author rongbo
 *
 */
public class RedissonProxy implements RedissonClient{

	private String hostName;
	
	private String port;
	
	private String password;
	
	private int connectionPoolSize;
	
	private RedissonClient delegateRedissonClient;
	
	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public int getConnectionPoolSize() {
		return connectionPoolSize;
	}

	public void setConnectionPoolSize(int connectionPoolSize) {
		this.connectionPoolSize = connectionPoolSize;
	}

	@PostConstruct
	public void init() {
		Config config = new Config();
		String address = hostName + ":" + port;
        config.useSingleServer()
        	  .setAddress(address)
        	  .setPassword(password)
        	  .setConnectionPoolSize(connectionPoolSize);
        delegateRedissonClient = Redisson.create(config);
	}

	@Override
	public RBinaryStream getBinaryStream(String name) {
		return delegateRedissonClient.getBinaryStream(name);
	}

	@Override
	public <V> RGeo<V> getGeo(String name) {
		return delegateRedissonClient.getGeo(name);
	}

	@Override
	public <V> RGeo<V> getGeo(String name, Codec codec) {
		return delegateRedissonClient.getGeo(name, codec);
	}

	@Override
	public <V> RSetCache<V> getSetCache(String name) {
		return delegateRedissonClient.getSetCache(name);
	}

	@Override
	public <V> RSetCache<V> getSetCache(String name, Codec codec) {
		return delegateRedissonClient.getSetCache(name, codec);
	}

	@Override
	public <K, V> RMapCache<K, V> getMapCache(String name, Codec codec) {
		return delegateRedissonClient.getMapCache(name, codec);
	}

	@Override
	public <K, V> RMapCache<K, V> getMapCache(String name) {
		return delegateRedissonClient.getMapCache(name);
	}

	@Override
	public <V> RBucket<V> getBucket(String name) {
		return delegateRedissonClient.getBucket(name);
	}

	@Override
	public <V> RBucket<V> getBucket(String name, Codec codec) {
		return delegateRedissonClient.getBucket(name, codec);
	}

	@Override
	public RBuckets getBuckets() {
		return delegateRedissonClient.getBuckets();
	}

	@Override
	public RBuckets getBuckets(Codec codec) {
		return delegateRedissonClient.getBuckets(codec);
	}

	@Override
	public <V> RHyperLogLog<V> getHyperLogLog(String name) {
		return delegateRedissonClient.getHyperLogLog(name);
	}

	@Override
	public <V> RHyperLogLog<V> getHyperLogLog(String name, Codec codec) {
		return delegateRedissonClient.getHyperLogLog(name, codec);
	}

	@Override
	public <V> RList<V> getList(String name) {
		return delegateRedissonClient.getList(name);
	}

	@Override
	public <V> RList<V> getList(String name, Codec codec) {
		return delegateRedissonClient.getList(name, codec);
	}

	@Override
	public <K, V> RListMultimap<K, V> getListMultimap(String name) {
		
		return delegateRedissonClient.getListMultimap(name);
	}

	@Override
	public <K, V> RListMultimap<K, V> getListMultimap(String name, Codec codec) {
		
		return delegateRedissonClient.getListMultimap(name, codec);
	}

	@Override
	public <K, V> RListMultimapCache<K, V> getListMultimapCache(String name) {
		
		return delegateRedissonClient.getListMultimapCache(name);
	}

	@Override
	public <K, V> RListMultimapCache<K, V> getListMultimapCache(String name, Codec codec) {
		return delegateRedissonClient.getListMultimapCache(name, codec);
	}


	@Override
	public <K, V> RMap<K, V> getMap(String name) {
		return delegateRedissonClient.getMap(name);
	}

	@Override
	public <K, V> RMap<K, V> getMap(String name, Codec codec) {
		return delegateRedissonClient.getMap(name, codec);
	}

	@Override
	public <K, V> RSetMultimap<K, V> getSetMultimap(String name) {
		return delegateRedissonClient.getSetMultimap(name);
	}

	@Override
	public <K, V> RSetMultimap<K, V> getSetMultimap(String name, Codec codec) {
		return delegateRedissonClient.getSetMultimap(name, codec);
	}

	@Override
	public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name) {
		return delegateRedissonClient.getSetMultimapCache(name);
	}

	@Override
	public <K, V> RSetMultimapCache<K, V> getSetMultimapCache(String name, Codec codec) {
		return delegateRedissonClient.getSetMultimapCache(name, codec);
	}

	@Override
	public RSemaphore getSemaphore(String name) {
		return delegateRedissonClient.getSemaphore(name);
	}

	@Override
	public RPermitExpirableSemaphore getPermitExpirableSemaphore(String name) {
		return delegateRedissonClient.getPermitExpirableSemaphore(name);
	}

	@Override
	public RLock getLock(String name) {
		return delegateRedissonClient.getLock(name);
	}

	@Override
	public RLock getFairLock(String name) {
		
		return delegateRedissonClient.getFairLock(name);
	}

	@Override
	public RReadWriteLock getReadWriteLock(String name) {
		
		return delegateRedissonClient.getReadWriteLock(name);
	}

	@Override
	public <V> RSet<V> getSet(String name) {
		
		return delegateRedissonClient.getSet(name);
	}

	@Override
	public <V> RSet<V> getSet(String name, Codec codec) {
		
		return delegateRedissonClient.getSet(name, codec);
	}

	@Override
	public <V> RSortedSet<V> getSortedSet(String name) {
		
		return delegateRedissonClient.getSortedSet(name);
	}

	@Override
	public <V> RSortedSet<V> getSortedSet(String name, Codec codec) {
		
		return delegateRedissonClient.getSortedSet(name, codec);
	}

	@Override
	public <V> RScoredSortedSet<V> getScoredSortedSet(String name) {
		
		return delegateRedissonClient.getScoredSortedSet(name);
	}

	@Override
	public <V> RScoredSortedSet<V> getScoredSortedSet(String name, Codec codec) {
		
		return delegateRedissonClient.getScoredSortedSet(name, codec);
	}

	@Override
	public RLexSortedSet getLexSortedSet(String name) {
		
		return delegateRedissonClient.getLexSortedSet(name);
	}

	@Override
	public <M> RTopic<M> getTopic(String name) {
		return delegateRedissonClient.getTopic(name);
	}

	@Override
	public <M> RTopic<M> getTopic(String name, Codec codec) {
		
		return delegateRedissonClient.getTopic(name, codec);
	}

	@Override
	public <M> RPatternTopic<M> getPatternTopic(String pattern) {
		return delegateRedissonClient.getPatternTopic(pattern);
	}

	@Override
	public <M> RPatternTopic<M> getPatternTopic(String pattern, Codec codec) {
		return delegateRedissonClient.getPatternTopic(pattern, codec);
	}

	
	@Override
	public <V> RQueue<V> getQueue(String name) {
		return delegateRedissonClient.getQueue(name);
	}

	
	@Override
	public <V> RQueue<V> getQueue(String name, Codec codec) {
		return delegateRedissonClient.getQueue(name, codec);
	}

	@Override
	public <V> RBlockingQueue<V> getBlockingQueue(String name) {
		return delegateRedissonClient.getBlockingDeque(name);
	}

	@Override
	public <V> RBlockingQueue<V> getBlockingQueue(String name, Codec codec) {
		return delegateRedissonClient.getBlockingQueue(name, codec);
	}

	@Override
	public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name) {
		return delegateRedissonClient.getBoundedBlockingQueue(name);
	}

	@Override
	public <V> RBoundedBlockingQueue<V> getBoundedBlockingQueue(String name, Codec codec) {
		return delegateRedissonClient.getBoundedBlockingQueue(name, codec);
	}

	@Override
	public <V> RDeque<V> getDeque(String name) {
		return delegateRedissonClient.getDeque(name);
	}

	@Override
	public <V> RDeque<V> getDeque(String name, Codec codec) {
		return delegateRedissonClient.getDeque(name, codec);
	}

	@Override
	public <V> RBlockingDeque<V> getBlockingDeque(String name) {
		return delegateRedissonClient.getBlockingDeque(name);
	}

	@Override
	public <V> RBlockingDeque<V> getBlockingDeque(String name, Codec codec) {
		return delegateRedissonClient.getBlockingDeque(name, codec);
	}

	@Override
	public RAtomicLong getAtomicLong(String name) {
		return delegateRedissonClient.getAtomicLong(name);
	}

	@Override
	public RAtomicDouble getAtomicDouble(String name) {
		return delegateRedissonClient.getAtomicDouble(name);
	}

	@Override
	public RCountDownLatch getCountDownLatch(String name) {
		return delegateRedissonClient.getCountDownLatch(name);
	}

	@Override
	public RBitSet getBitSet(String name) {
		return delegateRedissonClient.getBitSet(name);
	}

	@Override
	public <V> RBloomFilter<V> getBloomFilter(String name) {
		return delegateRedissonClient.getBloomFilter(name);
	}

	@Override
	public <V> RBloomFilter<V> getBloomFilter(String name, Codec codec) {
		return delegateRedissonClient.getBloomFilter(name, codec);
	}

	@Override
	public RScript getScript() {
		return delegateRedissonClient.getScript();
	}

	@Override
	public RScheduledExecutorService getExecutorService(String name) {
		return delegateRedissonClient.getExecutorService(name);
	}

	@Override
	@Deprecated
	public RScheduledExecutorService getExecutorService(Codec codec, String name) {
		return delegateRedissonClient.getExecutorService(codec, name);
	}


	@Override
	public RRemoteService getRemoteService() {
		return delegateRedissonClient.getRemoteService();
	}

	@Override
	public RRemoteService getRemoteService(Codec codec) {
		return delegateRedissonClient.getRemoteService(codec);
	}

	@Override
	public RRemoteService getRemoteService(String name) {
		return delegateRedissonClient.getRemoteService(name);
	}

	@Override
	public RRemoteService getRemoteService(String name, Codec codec) {
		return delegateRedissonClient.getRemoteService(name, codec);
	}

	@Override
	public RBatch createBatch() {
		return delegateRedissonClient.createBatch();
	}

	@Override
	public RKeys getKeys() {
		return delegateRedissonClient.getKeys();
	}

	@Override
	public RLiveObjectService getLiveObjectService() {
		return delegateRedissonClient.getLiveObjectService();
	}

	@Override
	public void shutdown() {
		delegateRedissonClient.shutdown();
	}

	@Override
	public void shutdown(long quietPeriod, long timeout, TimeUnit unit) {
		delegateRedissonClient.shutdown(quietPeriod,timeout,unit);
	}

	@Override
	public Config getConfig() {
		return delegateRedissonClient.getConfig();
	}


	@Override
	public NodesGroup<Node> getNodesGroup() {
		return delegateRedissonClient.getNodesGroup();
	}

	@Override
	public ClusterNodesGroup getClusterNodesGroup() {
		return delegateRedissonClient.getClusterNodesGroup();
	}

	@Override
	public boolean isShutdown() {
		return delegateRedissonClient.isShutdown();
	}

	@Override
	public boolean isShuttingDown() {
		return delegateRedissonClient.isShuttingDown();
	}

	@Override
	public CodecProvider getCodecProvider() {
		return delegateRedissonClient.getCodecProvider();
	}

	@Override
	public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String key, LocalCachedMapOptions options) {
		return delegateRedissonClient.getLocalCachedMap(key, options);
	}

	@Override
	public <K, V> RLocalCachedMap<K, V> getLocalCachedMap(String key, Codec codec, LocalCachedMapOptions options) {
		return delegateRedissonClient.getLocalCachedMap(key, codec,options);
	}

	@Override
	public ResolverProvider getResolverProvider() {
		return delegateRedissonClient.getResolverProvider();
	}

	

}
