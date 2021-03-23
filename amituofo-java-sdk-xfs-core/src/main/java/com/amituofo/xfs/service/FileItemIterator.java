package com.amituofo.xfs.service;

import java.util.Iterator;

public interface FileItemIterator<T extends Item> extends Iterator<T> {
	void release();
}
