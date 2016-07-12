package com.todimala.projects.simpleCache;

import java.util.Calendar;

import rx.Observable;
import rx.Subscription;

public class CacheObject<T> {

	private T value;
	private Calendar lastAccessed;
	private Long duration;
	public Observable<Long> observable;
	public Subscription timerSubscription;
	
	public CacheObject(T val) {
		this.value = val;
		this.duration = (long)3600.0; // One hour in seconds
		this.lastAccessed = Calendar.getInstance();
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
}
