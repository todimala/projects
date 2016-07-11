package com.todimala.projects.simpleCache;

import java.util.Calendar;

public class CacheObject<T> {

	private T value;
	private Calendar lastAccessed;
	
	public CacheObject(T val) {
		this.value = val;
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
	
}
