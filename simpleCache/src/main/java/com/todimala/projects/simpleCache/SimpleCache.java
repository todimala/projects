package com.todimala.projects.simpleCache;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

public class SimpleCache<V> implements Cache<String, V>{

    static final Logger logger = Logger.getLogger(SimpleCache.class);
    ConcurrentHashMap<String, CacheObject<V>> cacheRepoMap;
    
    private Observable<Long> cacheTuneObservable;
    private Subscription cacheTuneSubscription;
    
    SimpleCache() {
        cacheRepoMap = new ConcurrentHashMap<>();
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
        CacheObject<V> returnObj = cacheRepoMap.put(key, valueObj);
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
        CacheObject<V> returnObj = cacheRepoMap.put(key, valueObj);
        valueObj.setTimerSubscription(addCacheEntryExpiryTimer(key, valueObj));
        if (returnObj != null)
            return returnObj.getValue();
        return null; 
    }

    @Override
    public V get(String key) {
        CacheObject<V> returnObj = cacheRepoMap.get(key);
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
        CacheObject<V> returnObj = cacheRepoMap.remove(key);
        if (returnObj != null) 
            return returnObj.getValue();
        return null;
    }
    
    private Subscription addCacheEntryExpiryTimer(final String key, final CacheObject<V> valueObj) {
        if (valueObj == null) 
        	return null;
        Subscription timerSubscription;
        valueObj.setObservable(Observable.timer(valueObj.getDuration(), TimeUnit.SECONDS));
        Subscriber<Long> mySubscriber = new TimerSubscriber<>(key, valueObj);
        timerSubscription = valueObj.getObservable().subscribe(mySubscriber);
        logger.debug("Setting timer"); 
        return timerSubscription;
    }
    
    private boolean addCacheTuneSubscription() {
        long interval = Long.parseLong(
        		SimpleCacheProperties.appProps.getProperty(
        				SimpleCacheProperties.CACHE_TUNE_FREQ_INSEC));
        this.setCacheTuneObservable(Observable.interval(interval, TimeUnit.SECONDS));
        Subscriber<Long> mySubscriber = new CacheTunerSubscriber<>();
        this.setCacheTuneSubscription(this.getCacheTuneObservable().subscribe(mySubscriber));
        logger.debug("Setting Cache tune interval handler"); 
        return true;
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
            logger.debug("Completed the Cache expiry timer handler");
        }

        @Override
        public void onError(Throwable e) { 
            logger.error("Internal error ocured while processing the event on timer");
        }
    }
    
    private class CacheTunerSubscriber<Long> extends Subscriber<Long> {
        
    	String cacheTunePolicy;
    	
        CacheTunerSubscriber() {
        	cacheTunePolicy = 
        			SimpleCacheProperties.appProps.getProperty(SimpleCacheProperties.CACHE_TUNE_POLICY);
        }
        
        @Override
        public void onNext(Long l) {
        	logger.info("Implementing Cache tuning policy " + cacheTunePolicy);
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
