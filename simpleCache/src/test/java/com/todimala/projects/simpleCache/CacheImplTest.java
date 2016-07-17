package com.todimala.projects.simpleCache;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.TestCase;

public class CacheImplTest extends TestCase {
    
	static final Logger logger = Logger.getLogger(CacheImplTest.class);
	
    @BeforeClass
    public void setupBeforeClass() {
        System.out.println("@BeforeClass - runOnceBeforeClass");
    }
    
    public void testPutKV() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertNotNull(testCache);
        assertNull(testCache.put("name", "Mickey"));
        logger.info("Number of elements in cache = " + testCache.size());
        
    }

    public void testPutKVDuration() {
        SimpleCache<Integer> testCache = (SimpleCache<Integer>) SimpleCache.getCache();
        assertNull(testCache.put("Id", 123));
        logger.info("Number of elements in cache = " + testCache.size());
    }

    public void testGet() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertEquals("Mickey", testCache.get("name"));
        logger.info("Number of elements in cache = " + testCache.size());
    }

    public void testRemove() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertEquals("Mickey", testCache.remove("name"));
        logger.info("Number of elements in cache = " + testCache.size());
    }

    @Test
    public void testPutNegative() {
        SimpleCache<String> testCache = (SimpleCache<String>) SimpleCache.getCache();
        assertNull(testCache.put("name", "Mickey"));
        assertNull(testCache.get("name1"));
        logger.info("Number of elements in cache = " + testCache.size());
    }
}
