package com.amituofo.xfs.plugin.fs.memory.filesystem;

import java.io.InputStream;

import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.Property;

public interface MemoryFileSystem {

	void init() throws MemoryFileSystemException;

	void close() throws MemoryFileSystemException;

	void newFile(String filepath) throws MemoryFileSystemException;

	void newDirectory(String filepath) throws MemoryFileSystemException;

	void newDirectorys(String filepath) throws MemoryFileSystemException;

	boolean delete(String filepath) throws MemoryFileSystemException;

	boolean rename(String filepath, String newfilepath) throws MemoryFileSystemException;

	boolean exist(String filepath) throws MemoryFileSystemException;

	void list(String filepath, ItemFilter filter, ItemHandler handler) throws MemoryFileSystemException;

	InputStream read(String filepath, long offset, long length) throws MemoryFileSystemException;

	// void write(String filepath, long offset, InputStream in, long length) throws MemoryFileSystemException;
	long write(String filepath, InputStream in, long length) throws MemoryFileSystemException;

	Property<Object>[] getSystemAttribute(String filepath) throws MemoryFileSystemException;

	long getSize(String filepath) throws MemoryFileSystemException;

	long getLastModifiedTime(String filepath) throws MemoryFileSystemException;

}
