package com.todimala.projects.simpleCache;

import java.util.Calendar;

import org.apache.log4j.Logger;

import rx.Observable;
import rx.Subscription;

public class CacheObject<T> {

    static final Logger logger = Logger.getLogger(CacheObject.class);
    private T value;
    private double numAccess;
    private Calendar lastAccessed;
    private long duration;
    private boolean expired;
    private Observable<Long> expiryObservable;
    private Subscription expirySubscription;
    
    public CacheObject(T val) {
        this.value = val;
        this.numAccess = 0;
        this.duration = Long.parseLong(
                SimpleCacheProperties.appProps.getProperty(
                SimpleCacheProperties.CACHE_DURATION_INSEC));
        this.lastAccessed = Calendar.getInstance();
        this.setExpired(false);
    }

    public T getValue() {
        this.lastAccessed = Calendar.getInstance();
        this.numAccess++;
        return value;
    }

    public void setValue(T value) {
        this.lastAccessed = Calendar.getInstance();
        this.value = value;
    }

    public double getNumAccess() {
        return numAccess;
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

    public Observable<Long> getObservable() {
        return expiryObservable;
    }

    public void setObservable(Observable<Long> observable) {
        this.expiryObservable = observable;
    }

    public Subscription getTimerSubscription() {
        return expirySubscription;
    }

    public void setTimerSubscription(Subscription timerSubscription) {
        this.expirySubscription = timerSubscription;
    }
}
