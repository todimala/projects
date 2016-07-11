package com.todimala.projects.simpleCache;

/**
 * simple Cache!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        SimpleCache<String> myCache = new SimpleCache<String>();
        myCache.put("name", "Mickey");
        System.out.println("The name value in cache is : " + myCache.get("name"));
    }
}
