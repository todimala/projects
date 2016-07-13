package com.todimala.projects.simpleCache;

public interface Cache<K, V>  {
    public V put(K key, V value);
    public V put(K key, V value, Long d);
    public V get(K key);
    public V remove(K key);
}
