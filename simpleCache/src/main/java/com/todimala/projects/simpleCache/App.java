package com.todimala.projects.simpleCache;

import org.apache.log4j.Logger;

/**
 * simple Cache!
 *
 */
public class App 
{
    final static Logger logger = Logger.getLogger(App.class);
    
    public static void main( String[] args )
    {
        // simple Test 
        App.test();
    }
    
    public static void test() {
        SimpleCache<String> myCache = new SimpleCache<String>();
        myCache.put("name", "Mickey", (long)4.0);
        logger.info("Before: The name value in cache is : " + myCache.get("name"));
        try {
            Thread.sleep(360000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("After: The name value in cache is : " + myCache.get("name"));
    }
}
