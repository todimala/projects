package com.todimala.projects.simpleCache;
import java.util.HashMap;

public class SimpleCache<V> implements Cache<String, V>{

	HashMap<String, CacheObject<V>> _cache; 
	
	SimpleCache() {
		_cache = new HashMap<String, CacheObject<V>>();
	}
	
	public V put(String key, V value) {
		CacheObject<V> obj = (CacheObject<V>) _cache.put(key, new CacheObject<V>(value));
		if (obj != null) return obj.getValue();
		return null; 
	}

	public V put(String key, V value, Long d) {
		CacheObject<V> obj = (CacheObject<V>) _cache.put(key, new CacheObject<V>(value));
		if (obj != null) return obj.getValue();
		return null; 
	}

	public V get(String key) {
		CacheObject<V> obj = _cache.get(key);
		if (obj != null) return obj.getValue();
		return null;
	}

	public V remove(String key) {
		CacheObject<V> obj = _cache.remove(key);
		if (obj != null) return obj.getValue();
		return null;
	}
}
