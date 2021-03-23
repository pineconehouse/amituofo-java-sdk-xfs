package com.amituofo.xfs.service;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public interface FileSystemConfig extends Serializable {
	public static final char URL_PS = '/';
	public static final char FILE_PS = File.separatorChar;
	public static final String URL_ROOT_PATH = "/";

	String getFileSystemId();

	String getFileSystemVersion();

	List<String> getSupportSystems();

	char getPathSeparator();

	FileSystemType getFileSystemType();

}
