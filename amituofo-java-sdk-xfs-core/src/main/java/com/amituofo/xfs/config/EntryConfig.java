package com.amituofo.xfs.config;

import java.util.List;
import java.util.Map;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.xfs.service.Bookmark;
import com.amituofo.xfs.service.FileSystemConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public interface EntryConfig extends FileSystemConfig, SimpleConfigurationEntry {
	EntryConfig clone();

	FileSystemEntry createFileSystemEntry();

	FileSystemEntry createFileSystemEntry(FileSystemPreference perference);

	FileSystemPreference createPreference();

	void validateConfig() throws InvalidParameterException;

	String getRootPath();

	String getId();

	String getName();

	void setName(String name);

	void setDescription(String description);

	String getDescription();

	String getConfigurationID();

	char getChar(String key);

	String getString(String key);

	boolean getBoolean(String key);

	Integer getInteger(String key);

	Long getLong(String key);

//	String[] getStringArray(String key);
	List<String> getStringList(String key);

	String toString();

	SimpleConfiguration getSimpleConfiguration();

	void setSimpleConfiguration(SimpleConfiguration config);

	void setSimpleConfiguration(Map<String, Object> configMap);

	List<Bookmark> getBookmarks();

	void setBookmarks(List<Bookmark> bookmarks);

}
