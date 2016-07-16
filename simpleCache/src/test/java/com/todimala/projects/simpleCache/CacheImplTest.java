package com.todimala.projects.simpleCache;

import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

public class CacheImplTest extends TestCase {
    
    @BeforeClass
    public void setupBeforeClass() {
        System.out.println("@BeforeClass - runOnceBeforeClass");
    }
    
    public void testPutKV() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertNotNull(testCache);
        assertNull(testCache.put("name", "Mickey"));
    }

    public void testPutKVDuration() {
        SimpleCache<Integer> testCache = (SimpleCache<Integer>) SimpleCache.getCache();
        assertNull(testCache.put("Id", 123));
    }

    public void testGet() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertEquals("Mickey", testCache.get("name"));
    }

    public void testRemove() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertEquals("Mickey", testCache.remove("name"));
    }

    @Test
    public void testPutNegative() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertNull(testCache.put("name", "Mickey"));
        assertNull(testCache.get("name1"));
    }
}
