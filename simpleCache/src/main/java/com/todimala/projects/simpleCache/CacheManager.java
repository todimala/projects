package com.todimala.projects.simpleCache;

import org.apache.log4j.Logger;

/**
 * A Simple Cache Manager
 *
 */
public class CacheManager implements Runnable
{
	private static final Logger LOGGER = Logger.getLogger(CacheManager.class);
	private static final CacheManager MANAGER = new CacheManager();
	static SimpleCache<Object> myCache;
    
    private CacheManager() { }
    
    public static CacheManager getCacheManager() {
    	return MANAGER;
    }
    
    public static void main( String[] args )
    {
    	CacheManager manager = getCacheManager();
    	manager.run();
    }
    
    public static void initCache() {
        myCache = (SimpleCache<Object>) SimpleCache.getCache();
        myCache.put("name", "Mickey");
        LOGGER.info("Before: The name value in cache is : " + myCache.get("name").toString());
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("After: The name value in cache is : " + myCache.get("name").toString());
    }

	@Override
	public void run() {
        initCache();
		while (true) {
			try {
				Thread.sleep(5000);
			} catch(Exception ex) {
				LOGGER.error("Exception is::: " + ex);
			}
		}
	}
}
