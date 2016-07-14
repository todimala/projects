package com.todimala.projects.simpleCache;

import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

public final class SimpleCacheProperties {

    private SimpleCacheProperties(){}
    static final Logger logger = Logger.getLogger(App.class);
    static Properties appProps;
    static String CACHE_EXPIRATION_ACTION = "cache.expiration.action";
    static String CACHE_DURATION_INSEC = "cache.duration.insec";
    static String CACHE_TUNE_FREQ_INSEC = "cache.tune.frequency.insec";
    static String CACHE_TUNE_POLICY = "cache.tuning.policy";
    
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
            logger.info("Property: CACHE_EXPIRATION_ACTION = " + appProps.getProperty(CACHE_EXPIRATION_ACTION));
            logger.info("Property: CACHE_DURATION_INSEC = " + appProps.getProperty(CACHE_DURATION_INSEC));
            logger.info("Property: CACHE_TUNE_FREQ_INSEC = " + appProps.getProperty(CACHE_TUNE_FREQ_INSEC));
            logger.info("Property: CACHE_TUNE_POLICY = " + appProps.getProperty(CACHE_TUNE_POLICY));
        } catch (Exception iox) {
            
        }
    }
}
