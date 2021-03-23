package com.amituofo.xfs.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.service.Bookmark;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemType;

public abstract class EntryConfigBase implements EntryConfig {
	public static final String FILE_SYSTEM_ID = "_<_FILE_SYSTEM_ID_>_";
	public static final String FILE_SYSTEM_VERSION = "_<_FILE_SYSTEM_VERSION_>_";
	public static final String SUPPORT_SYSTEMS = "_<_SUPPORT_SYSTEMS_>_";

	public static final String NAME = "_<_NAME_>_";
	public static final String DESCRIPTION = "_<_DESCRIPTION_>_";

	public static final String ROOT_PATH = "_<_ROOT_PATH_>_";
	public static final String PATH_SEPARATOR = "_<_PATH_SEPARATOR_>_";
	public static final String FILE_SYSTEM_TYPE = "_<_FILE_SYSTEM_TYPE_>_";

	public static final String BOOKMARKS = "_<_BOOKMARKS_>_";

	protected final SimpleConfiguration config;

	public EntryConfigBase(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, FileSystemType fileSystemType, String rootPath, char pathSeparator) {
		config = new SimpleConfiguration(connConfigClass);
		config.set(FILE_SYSTEM_ID, fileSystemId);
		config.set(NAME, name);

		config.set(ROOT_PATH, rootPath);
		config.set(PATH_SEPARATOR, pathSeparator);
		config.set(FILE_SYSTEM_TYPE, fileSystemType.name());
	}

	public EntryConfigBase(EntryConfig config) {
		this.config = ((EntryConfigBase) config).config.clone();
	}

	public EntryConfig clone() {
		EntryConfig sc = null;
		try {
			sc = this.getClass().newInstance();
			sc.setSimpleConfiguration(config.clone());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sc;

	}

	//// public abstract FileSystemEntry createFileSystemEntry();
	//
	// public abstract FileSystemEntry createFileSystemEntry(FileSystemPreference perference);
	//
	// public abstract FileSystemPreference createPreference();

	protected abstract void validate() throws InvalidParameterException;

	@Override
	public FileSystemEntry createFileSystemEntry() {
		return createFileSystemEntry(createPreference());
	}
	// @Override
	// protected EntryConfig clone() throws CloneNotSupportedException {
	// EntryConfig config = this.clone();
	//
	// return config;
	// }

	public void validateConfig() throws InvalidParameterException {
		config.validateConfig();

		ValidUtils.invalidIfEmpty(getFileSystemId(), "File system entry name must be specificed!");
		ValidUtils.invalidIfEmpty(getName(), "Entry name could not be empty!");

		validate();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof EntryConfigBase)) {
			return false;
		}

		EntryConfigBase a = (EntryConfigBase) obj;
		EntryConfigBase b = this;

		return a.config.equals(b.config);
	}

	@Override
	public String getFileSystemId() {
		return config.getString(FILE_SYSTEM_ID);
	}

	@Override
	public String getFileSystemVersion() {
		return config.getString(FILE_SYSTEM_VERSION);
	}

	@Override
	public List<String> getSupportSystems() {
		return config.getStringList(SUPPORT_SYSTEMS);
	}

	@Override
	public String getRootPath() {
		return config.getString(ROOT_PATH);
	}

	@Override
	public char getPathSeparator() {
		return config.getChar(PATH_SEPARATOR);
	}

	@Override
	public FileSystemType getFileSystemType() {
		return FileSystemType.valueOf(config.getString(FILE_SYSTEM_TYPE));
	}

	public String getId() {
		return config.getConfigurationID();
	}

	public String getName() {
		return config.getString(NAME);
	}

	public void setName(String name) {
		config.set(NAME, name);
	}

	public void setDescription(String description) {
		config.set(DESCRIPTION, description);
	}

	public String getDescription() {
		return config.getString(DESCRIPTION);
	}

	public String getConfigurationID() {
		return config.getConfigurationID();
	}

	public char getChar(String key) {
		return config.getChar(key);
	}

	public String getString(String key) {
		return config.getString(key);
	}

	public boolean getBoolean(String key) {
		return config.getBoolean(key);
	}

	public Integer getInteger(String key) {
		return config.getInteger(key);
	}

	public Long getLong(String key) {
		return config.getLong(key);
	}

	public List<String> getStringList(String key) {
		return config.getStringList(key);
	}

	@Override
	public List<Bookmark> getBookmarks() {
		List<String> arraybookmarks = getStringList(BOOKMARKS);
		return parseBookmarks(arraybookmarks);
	}

	@Override
	public void setBookmarks(List<Bookmark> bookmarks) {
		if (bookmarks == null) {
			return;
		}
		config.set(BOOKMARKS, bookmarkToArrays(bookmarks));
	}

	public static List<Bookmark> parseBookmarks(List<String> arraybookmarks) {
		final String SP = "%#|#%";
		List<Bookmark> list = new ArrayList<Bookmark>();
		if (arraybookmarks != null) {
			for (String strbm : arraybookmarks) {
				if (strbm.length() > SP.length()) {
					// int spIndex = strbm.indexOf(SP);
					String[] vs = strbm.split(String.valueOf((char) (30)));
					if (vs.length == 3) {
						String title = vs[0];
						String rootitem = vs[1];
						String path = vs[2];
						// Bookmark bm = new Bookmark(vs[0], vs[2]);
						// String title = strbm.substring(0, spIndex);
						// String path = strbm.substring(spIndex + SP.length());
						// URI uri;
						// try {
						// uri = new URI(strbm.substring(spIndex + SP.length()));
						// } catch (URISyntaxException e) {
						// e.printStackTrace();
						// continue;
						// }
						Bookmark bm = new Bookmark(title, rootitem, path);
						list.add(bm);
					}
				}
			}
		}
		return list;
	}

	public static String[] bookmarkToArrays(List<Bookmark> bookmarks) {
		if (bookmarks == null) {
			return null;
		}
		// public final static String RS = String.valueOf((char) (30));

		String[] arraybookmarks = new String[bookmarks.size()];
		for (int i = 0; i < arraybookmarks.length; i++) {
			Bookmark bookmark = bookmarks.get(i);
			arraybookmarks[i] = (bookmark.getTitle() + ((char) (30)) + bookmark.getSpacename() + ((char) (30)) + bookmark.getPath());
		}

		return arraybookmarks;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	@Override
	public SimpleConfiguration getSimpleConfiguration() {
		return config;
	}

	@Override
	public void setSimpleConfiguration(SimpleConfiguration config) {
		this.config.setSimpleConfiguration(config);
	}

	@Override
	public void setSimpleConfiguration(Map<String, Object> configMap) {
		this.config.setSimpleConfiguration(configMap);
	}

}
