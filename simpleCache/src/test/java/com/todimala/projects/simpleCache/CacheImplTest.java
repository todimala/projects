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
        SimpleCache<String> testCache = new SimpleCache<String>();
        assertNotNull(testCache);
        assertNull(testCache.put("name", "Mickey"));
    }

    public void testPutKVDuration() {
        SimpleCache<Integer> testCache = new SimpleCache<Integer>();
        assertNull(testCache.put("Id", 123));
    }

    public void testGet() {
        SimpleCache<String> testCache = new SimpleCache<String>();
        assertNull(testCache.put("name", "Mickey"));
        assertEquals("Mickey", testCache.get("name"));
    }

    public void testRemove() {
        SimpleCache<String> testCache = new SimpleCache<String>();
        assertNull(testCache.put("name", "Mickey"));
        assertEquals("Mickey", testCache.remove("name"));
    }

    @Test
    public void testPutNegative() {
        SimpleCache<String> testCache = new SimpleCache<String>();
        assertNull(testCache.put("name", "Mickey"));
        assertNull(testCache.get("name1"));
    }
}
