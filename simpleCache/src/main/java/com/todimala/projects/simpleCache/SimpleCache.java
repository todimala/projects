package com.todimala.projects.simpleCache;
import java.time.Duration;
import java.util.HashMap;

public class SimpleCache<K, V> implements Cache<K, V>{

	HashMap<K, V> _cache; 
	
	SimpleCache() {
		_cache = new HashMap<K, V>();
	}
	
	public V put(K key, V value) {
		return _cache.put(key, value);
	}

	public V put(K key, V value, Duration d) {
		return _cache.put(key, value);
	}

	public V get(K key) {
		return _cache.get(key);
	}

	public V remove(K key) {
		return _cache.remove(key);
	}
}
