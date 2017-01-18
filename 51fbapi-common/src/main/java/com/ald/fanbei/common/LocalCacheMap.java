package com.ald.fanbei.common;

import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * 
 *@类LocalCacheMap.java 的实现描述：本地缓存类
 *@author 陈金虎 2017年1月16日 下午11:26:23
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class LocalCacheMap<Key, Value> extends AbstractMap<Key, Value>{
	private int maxCount = 10000;
	private Queue<Entry> queue = new LinkedList<Entry>();
	
	private class Entry implements Map.Entry<Key, Value>{
		private Key key;
		private Value value;
		
		public Entry(Key key, Value value){
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return key + "=" + value;
		}
		
		public Key getKey() {
			return key;
		}

		public Value getValue() {
			return value;
		}

		public Value setValue(Value value) {
			return this.value = value;
		}
	}
	
	public LocalCacheMap() {
	}
	
	public LocalCacheMap(int size) {
		maxCount = size;
	}

	@Override
	public Value put(Key key, Value value) {
		while(queue.size() >= maxCount){
			queue.remove();
		}
		queue.add(new Entry(key, value));
		return value;
	}
	
	@Override
	public Value get(Object key){
		for(Iterator<Entry> iter = queue.iterator();iter.hasNext();){
			Entry type = iter.next();
			if(type.key.equals(key)){
				queue.remove(type);
				queue.add(type);
				return type.value;
			}
		}
		return null;
	}
	
	@Override
	public Set<Map.Entry<Key, Value>> entrySet() {
		Set<Map.Entry<Key, Value>> set = new HashSet<Map.Entry<Key, Value>>();
		set.addAll(queue);
		return set;
	}

	@Override
	public void clear() {
		queue.clear();
	}
	
	@Override
	public Set<Key> keySet() {
		Set<Key> set = new HashSet<Key>();
		for(Entry e : queue){
			set.add(e.getKey());
		}
		return set;
	}

	@Override
	public Value remove(Object obj) {
		for(Entry e : queue){
			if(e.getKey().equals(obj)){
				queue.remove(e);
				return e.getValue();
			}
		}
		return null;
	}
}