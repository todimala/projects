package com.todimala.projects.simpleCache;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class SimpleCache<V> implements Cache<String, V>{

    static final Logger logger = Logger.getLogger(SimpleCache.class);
    HashMap<String, CacheObject<V>> cacheRepoMap; 
    
    SimpleCache() {
        cacheRepoMap = new HashMap<>();
    }
    
    @Override
    public V put(String key, V value) {
        CacheObject<V> valueObj = new CacheObject<>(value);
        CacheObject<V> returnObj = cacheRepoMap.put(key, valueObj);
        valueObj.timerSubscription = addCacheEntryExpiryTimer(key, valueObj);
        if (returnObj != null)
            return returnObj.getValue();
        return null; 
    }

    @Override
    public V put(String key, V value, Long d) {
        CacheObject<V> valueObj = new CacheObject<>(value);
        if (d > 0)
            valueObj.setDuration(d);
        CacheObject<V> returnObj = cacheRepoMap.put(key, valueObj);
        valueObj.timerSubscription = addCacheEntryExpiryTimer(key, valueObj);
        if (returnObj != null)
            return returnObj.getValue();
        return null; 
    }

    @Override
    public V get(String key) {
        CacheObject<V> returnObj = cacheRepoMap.get(key);
        if (returnObj != null) {
            logger.debug("Cancelled timer"); 
            returnObj.timerSubscription.unsubscribe();
            returnObj.timerSubscription = addCacheEntryExpiryTimer(key, returnObj);
            return returnObj.getValue();
        }
        return null;
    }

    @Override
    public V remove(String key) {
        CacheObject<V> returnObj = cacheRepoMap.remove(key);
        if (returnObj != null) 
            return returnObj.getValue();
        return null;
    }
    
    private Subscription addCacheEntryExpiryTimer(final String key, final CacheObject<V> valueObj) {
        Subscription timerSubscription;
        valueObj.observable = Observable.timer(valueObj.getDuration(), TimeUnit.SECONDS);
        Subscriber<Long> mySubscriber = new TimerSubscriber<>(key, valueObj);
        timerSubscription = valueObj.observable.subscribe(mySubscriber);
        logger.debug("Setting timer"); 
        return timerSubscription;
    }
    
    private class TimerSubscriber<Long> extends Subscriber<Long> {
        
        String key;
        CacheObject<V> valueObj;
        
        TimerSubscriber(String key, CacheObject<V> valueObj) {
            this.key = key;
            this.valueObj = valueObj;
        }
        
        @Override
        public void onNext(Long l) {
            logger.info(String.format("Timer expired: applying policy, %s, on the cached object.",
                    SimpleCacheProperties.CACHE_EXPIRATION_ACTION));
            String policy = SimpleCacheProperties.appProps.getProperty(SimpleCacheProperties.CACHE_EXPIRATION_ACTION);
            if ((null != policy) && policy.matches("delete")) {
                remove(key);
            } else if ((null != policy) && policy.matches("mark")) {
                valueObj.setExpired(true);
            } else {
                logger.error(String.format("Unknown %s value: %s",
                        SimpleCacheProperties.CACHE_EXPIRATION_ACTION, policy));
            }
        }

        @Override
        public void onCompleted() { 
            logger.debug("");
        }

        @Override
        public void onError(Throwable e) { 
            logger.error("Internal error ocured while processing the event on timer");
        }
    }
}
