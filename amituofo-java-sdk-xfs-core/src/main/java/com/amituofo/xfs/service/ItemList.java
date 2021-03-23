package com.amituofo.xfs.service;

import java.util.Iterator;
import java.util.List;

public interface ItemList<T> {
	void add(T item);
	
	long size();
	
	Iterator<T> iterator();

	void updateStatus(T item, int status);
	
	void release();
	
	void clear();

//	List<T> toList();
}
