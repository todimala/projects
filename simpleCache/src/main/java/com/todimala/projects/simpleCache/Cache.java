package com.todimala.projects.simpleCache;
import java.time.Duration;

public interface Cache<K, V>  {
	public V put(K key, V value);
	public V put(K key, V value, Duration d);
	public V get(K key);
	public V remove(K key);
}
