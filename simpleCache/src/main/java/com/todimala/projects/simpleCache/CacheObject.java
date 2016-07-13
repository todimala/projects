package com.todimala.projects.simpleCache;

import java.util.Calendar;

import rx.Observable;
import rx.Subscription;

public class CacheObject<T> {

    private T value;
    private Calendar lastAccessed;
    private Long duration;
    private boolean expired;
    public Observable<Long> observable;
    public Subscription timerSubscription;
    
    public CacheObject(T val) {
        this.value = val;
        this.duration = Long.getLong(
                            SimpleCacheProperties.appProps.getProperty(
                                    SimpleCacheProperties.CACHE_DURATION_INSEC));
        this.lastAccessed = Calendar.getInstance();
        this.setExpired(false);
    }

    public T getValue() {
        this.lastAccessed = Calendar.getInstance();
        return value;
    }

    public void setValue(T value) {
        this.lastAccessed = Calendar.getInstance();
        this.value = value;
    }

    public Calendar getLastAccessed() {
        return lastAccessed;
    }

    protected void setLastAccessed(Calendar lastAccessed) {
        this.lastAccessed = lastAccessed;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
