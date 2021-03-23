package com.amituofo.xfs.service.impl;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import com.amituofo.xfs.service.ItemList;

public class StackFileItemList<T> implements ItemList<T>, Serializable {
	protected final Stack<T> list = new Stack<T>();

	public StackFileItemList() {
	}

	public StackFileItemList(List<T> list) {
		this.list.addAll(list);
	}

	@Override
	public void add(T item) {
//		list.add(item);
		list.push(item);
	}

	@Override
	public long size() {
		return list.size();
	}

	@Override
	public Iterator<T> iterator() {
		return list.iterator();
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
