package com.todimala.projects.simpleCache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class SimpleCache<V> implements Cache<String, V>{

    static final Logger logger = Logger.getLogger(SimpleCache.class);
    ConcurrentHashMap<String, CacheObject<V>> cacheRepo;
    
    private Observable<Long> cacheTuneObservable;
    private Subscription cacheTuneSubscription;
    
    public static <V> SimpleCache<?> getCache() {
    	return SingletonHolder.cacheInstance;
    }
    
    private static class SingletonHolder {
    	private static final SimpleCache cacheInstance = new SimpleCache();
    }

    private SimpleCache() {
        cacheRepo = new ConcurrentHashMap<>();
        addCacheTuneSubscription();
    }
    
    public Observable<Long> getCacheTuneObservable() {
        return cacheTuneObservable;
    }

    public void setCacheTuneObservable(Observable<Long> cacheTuneObservable) {
        this.cacheTuneObservable = cacheTuneObservable;
    }

    public Subscription getCacheTuneSubscription() {
        return cacheTuneSubscription;
    }

    public void setCacheTuneSubscription(Subscription cacheTuneSubscription) {
        this.cacheTuneSubscription = cacheTuneSubscription;
    }

    @Override
    public V put(String key, V value) {
        CacheObject<V> valueObj = new CacheObject<>(value);
        CacheObject<V> returnObj = cacheRepo.put(key, valueObj);
        valueObj.setTimerSubscription(addCacheEntryExpiryTimer(key, valueObj));
        if (returnObj != null)
            return returnObj.getValue();
        return null; 
    }

    @Override
    public V put(String key, V value, Long d) {
        CacheObject<V> valueObj = new CacheObject<>(value);
        if (d > 0)
            valueObj.setDuration(d);
        CacheObject<V> returnObj = cacheRepo.put(key, valueObj);
        valueObj.setTimerSubscription(addCacheEntryExpiryTimer(key, valueObj));
        if (returnObj != null)
            return returnObj.getValue();
        return null; 
    }

    @Override
    public V get(String key) {
        CacheObject<V> returnObj = cacheRepo.get(key);
        if (returnObj != null) {
            logger.debug("Cancelled timer"); 
            returnObj.getTimerSubscription().unsubscribe();
            returnObj.setTimerSubscription(addCacheEntryExpiryTimer(key, returnObj));
            return returnObj.getValue();
        }
        return null;
    }

    @Override
    public V remove(String key) {
        CacheObject<V> returnObj = cacheRepo.remove(key);
        if (returnObj != null) 
            return returnObj.getValue();
        return null;
    }
    
    private Subscription addCacheEntryExpiryTimer(final String key, final CacheObject<V> valueObj) {
        if (valueObj == null) 
            return null;
        Subscription timerSubscription;
        valueObj.setObservable(Observable.timer(valueObj.getDuration(), TimeUnit.SECONDS));
        Subscriber<Long> mySubscriber = new CacheEntryExpirySubscriber<>(key, valueObj);
        timerSubscription = valueObj.getObservable().subscribe(mySubscriber);
        logger.debug("Setting timer"); 
        return timerSubscription;
    }
    
    private boolean addCacheTuneSubscription() {
        long interval = Long.parseLong(
                SimpleCacheProperties.appProps.getProperty(
                        SimpleCacheProperties.CACHE_TUNE_FREQ_INSEC));
        this.setCacheTuneObservable(Observable.interval(interval, TimeUnit.SECONDS));
        Subscriber<Long> mySubscriber = new PeriodicCacheStoreExpirySubscriber<>(this.cacheRepo);
        this.setCacheTuneSubscription(this.getCacheTuneObservable().subscribe(mySubscriber));
        logger.debug("Setting Cache tune interval handler"); 
        return true;
    }
    
    /*
     * A class for handling of when a cache entry expires
     */
    private class CacheEntryExpirySubscriber<Long> extends Subscriber<Long> {
        
        String key;
        CacheObject<V> valueObj;
        
        CacheEntryExpirySubscriber(String key, CacheObject<V> valueObj) {
            this.key = key;
            this.valueObj = valueObj;
        }
        
        @Override
        public void onNext(Long l) {
            String policy = SimpleCacheProperties.appProps.getProperty(SimpleCacheProperties.CACHE_EXPIRATION_ACTION);
            logger.info(String.format("Timer expired: applying policy, %s=%s, on the cached object.",
                    SimpleCacheProperties.CACHE_EXPIRATION_ACTION, policy));
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
            logger.debug("Completed the Cache expiry timer handler");
        }

        @Override
        public void onError(Throwable e) { 
            logger.error("Internal error ocured while processing the event on timer");
        }
    }
    
    /*
     * A class for periodically cleaning up all the expired cache entries
     * in the cache store.
     */
    private class PeriodicCacheStoreExpirySubscriber<Long> extends Subscriber<Long> {
        
        String cacheTunePolicy;
        ConcurrentHashMap<String, CacheObject<V>> cacheStore;
        
        PeriodicCacheStoreExpirySubscriber(ConcurrentHashMap<String, CacheObject<V>> cache) {
            this.cacheStore = cache;
            cacheTunePolicy = 
                    SimpleCacheProperties.appProps.getProperty(SimpleCacheProperties.CACHE_TUNE_POLICY);
        }
        
        @Override
        public void onNext(Long l) {
            logger.info("Running periodic CacheStore cleanup.");
            cacheStore.forEach( (k, v) -> {
            	if (v.isExpired()) {
            		logger.info(String.format("Deleting expired cache entry: Key = %s and value = %s", 
            			k, v.getValue()));
            		cacheStore.remove(k);
            	}
            });
        }

        @Override
        public void onCompleted() { 
            logger.debug("Completed the Cache tuning handler");
        }

        @Override
        public void onError(Throwable e) { 
            logger.error("Internal error ocured while processing the Cache tuner event");
        }
    }
}
