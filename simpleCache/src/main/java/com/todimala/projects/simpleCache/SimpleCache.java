package com.todimala.projects.simpleCache;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

public class SimpleCache<V> implements Cache<String, V>{

	final static Logger logger = Logger.getLogger(SimpleCache.class);
	HashMap<String, CacheObject<V>> _cache; 
	
	SimpleCache() {
		_cache = new HashMap<String, CacheObject<V>>();
	}
	
	public V put(String key, V value) {
		//return this.put(key, value, (long)2.0);
		CacheObject<V> valueObj = new CacheObject<V>(value);
		CacheObject<V> returnObj = (CacheObject<V>) _cache.put(key, valueObj);
		valueObj.timerSubscription = addCacheEntryExpiryTimer(key, valueObj);
		if (returnObj != null) return returnObj.getValue();
		return null; 
	}

	public V put(String key, V value, Long d) {
		CacheObject<V> valueObj = new CacheObject<V>(value);
		if (d > 0) valueObj.setDuration(d);
		CacheObject<V> returnObj = (CacheObject<V>) _cache.put(key, valueObj);
		valueObj.timerSubscription = addCacheEntryExpiryTimer(key, valueObj);
		if (returnObj != null) return returnObj.getValue();
		return null; 
	}

	public V get(String key) {
		CacheObject<V> returnObj = _cache.get(key);
		if (returnObj != null) {
			logger.debug("Cancelled timer"); 
			returnObj.timerSubscription.unsubscribe();
			returnObj.timerSubscription = addCacheEntryExpiryTimer(key, returnObj);
			return returnObj.getValue();
		}
		return null;
	}

	public V remove(String key) {
		CacheObject<V> returnObj = _cache.remove(key);
		if (returnObj != null) return returnObj.getValue();
		return null;
	}
	
	private Subscription addCacheEntryExpiryTimer(final String key, final CacheObject<V> valueObj) {
		Subscription timerSubscription = null;
		valueObj.observable = Observable.timer(valueObj.getDuration(), TimeUnit.SECONDS);
		Subscriber<Long> mySubscriber = new Subscriber<Long>() {
		    @Override
		    public void onNext(Long l) {
		    	logger.info(String.format("Timer expired: applying policy, %s, on the cached object.",
		    			SimpleCacheProperties.EXPIRATION_POLICY));
		    	String policy = SimpleCacheProperties.appProps.getProperty(SimpleCacheProperties.EXPIRATION_POLICY);
		    	if ((null != policy) && policy.matches("delete")) {
		    		remove(key);
		    	} else if ((null != policy) && policy.matches("expire")) {
		    		valueObj.setExpired(true);
		    	} else {
		    		logger.error(String.format("Unknown %s value: %s",
		    				SimpleCacheProperties.EXPIRATION_POLICY, policy));
		    	}
		    }

		    @Override
		    public void onCompleted() { }

		    @Override
		    public void onError(Throwable e) { 
		    	logger.error("Internal error ocured while processing the event on timer");
		    }
		};
		timerSubscription = valueObj.observable.subscribe(mySubscriber);
		logger.debug("Setting timer"); 
		return timerSubscription;
	}
}
