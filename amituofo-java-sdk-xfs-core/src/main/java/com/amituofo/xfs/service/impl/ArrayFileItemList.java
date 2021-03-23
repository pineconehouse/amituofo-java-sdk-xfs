package com.amituofo.xfs.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.amituofo.xfs.service.ItemList;

public class ArrayFileItemList<T> implements ItemList<T>, Serializable {
	protected final List<T> list = new ArrayList<T>();

	public ArrayFileItemList() {
	}

	public ArrayFileItemList(List<T> list) {
		this.list.addAll(list);
	}

	@Override
	public void add(T item) {
		list.add(item);
	}

	@Override
	public long size() {
		return list.size();
	}

	@Override
	public Iterator<T> iterator() {
		return list.listIterator();
	}

	@Override
	public void updateStatus(T item, int status) {

	}

	@Override
	public void release() {
		list.clear();
	}

	@Override
	public void clear() {
		list.clear();
	}

//	@Override
//	public List<T> toList() {
//		return list;
//	}

}
