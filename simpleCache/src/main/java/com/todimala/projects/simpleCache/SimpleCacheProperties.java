package com.todimala.projects.simpleCache;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public class SimpleCacheProperties {

	final static Logger logger = Logger.getLogger(App.class);
	static Properties appProps;
	static String EXPIRATION_POLICY= "expirationPolicy";
	
	static {
		String configFilename = "simpleCache.properties";
		appProps = new Properties();
    	InputStream configReadStream = null;
    	try {	
    		configReadStream = App.class.getClassLoader().getResourceAsStream(configFilename);
    		if (configReadStream == null) {
    			logger.error("Could not find properties file " + configFilename + " in the classpath");
    		}
    		
    		//load the properties file
    		appProps.load(configReadStream);
    		logger.info("Property: expirationPolicy = " + appProps.getProperty("expirationPolicy"));
    	} catch (Exception iox) {
    		
    	}
	}
}
