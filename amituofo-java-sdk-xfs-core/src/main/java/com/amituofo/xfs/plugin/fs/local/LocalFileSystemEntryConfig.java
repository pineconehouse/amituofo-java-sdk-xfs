package com.amituofo.xfs.plugin.fs.local;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.config.EntryConfigBase;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.amituofo.xfs.service.FileSystemType;
import com.amituofo.xfs.util.FileSystemUtils;

public class LocalFileSystemEntryConfig extends EntryConfigBase {
	public static final String SYSTEM_NAME = "Local File System";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP08398913";

//	public final static LocalFileSystemEntryConfig SYSTEM_VIEW_DRIVER1 = new LocalFileSystemEntryConfig(
//			SYSTEM_NAME,
//			FileSystemUtils.getDefaultAvailableRoot(),
//			LocalFileSystemView.USER_FILE_SYSTEM_VIEW);
	public final static LocalFileSystemEntryConfig SYSTEM_VIEW_HOME = new LocalFileSystemEntryConfig(
			SYSTEM_NAME,
			FileSystemUtils.getHomeDirectory(),
			LocalFileSystemView.USER_FILE_SYSTEM_VIEW);
	public final static LocalFileSystemEntryConfig DEFAULT_VIEW_DRIVER1 = new LocalFileSystemEntryConfig(
			SYSTEM_NAME,
			FileSystemUtils.getDefaultAvailableRoot(),
			LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW);
//	public final static LocalFileSystemEntryConfig DEFAULT_VIEW_HOME = new LocalFileSystemEntryConfig(
//			SYSTEM_NAME,
//			FileSystemUtils.getHome(),
//			LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW);

	public static final String SHOW_HIDDEN_FILE = "SHOW_HIDDEN_FILE";
	public static final String LOCAL_FILE_SYSTEM_VIEW = "LOCAL_FILE_SYSTEM_VIEW";
	private File rootFile;

	public LocalFileSystemEntryConfig() {
		this("", URL_ROOT_PATH);
	}

	public LocalFileSystemEntryConfig(String rootPath) {
		this(rootPath, rootPath, LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW);
	}

	public LocalFileSystemEntryConfig(String name, String rootPath) {
		this(name, rootPath, LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW);
	}

	public LocalFileSystemEntryConfig(String name, String rootPath, LocalFileSystemView view) {
		super(LocalFileSystemEntryConfig.class, SYSTEM_ID, name, FileSystemType.Local, rootPath, FILE_PS);
		this.setFileSystemView(view);
	}
	

	public LocalFileSystemEntryConfig(String name, File rootFile, LocalFileSystemView view) {
		super(LocalFileSystemEntryConfig.class, SYSTEM_ID, name, FileSystemType.Local, rootFile.getPath(), FILE_PS);
		this.setFileSystemView(view);
		this.rootFile = rootFile;
	}
	
	public File getRootFile() {
		if (rootFile == null) {
			if (getFileSystemView() == LocalFileSystemView.USER_FILE_SYSTEM_VIEW) {
				// BUG 无法创建win32 folder
				rootFile = FileSystemView.getFileSystemView().createFileObject(this.getRootPath());
			} else {
				rootFile = new File(this.getRootPath());
			}
		}
		return rootFile;
	}

//	@Override
//	public FileSystemEntry createFileSystemEntry() {
//		return createFileSystemEntry((LocalFileSystemPreference) createPreference());
//	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new LocalFileSystemEntry(this, (LocalFileSystemPreference)perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new LocalFileSystemPreference(this.getFileSystemView());
	}

	// public boolean isShowHidden() {
	// return config.getBoolean(SHOW_HIDDEN_FILE);
	// }
	//
	// public void setShowHidden(boolean show) {
	// config.set(SHOW_HIDDEN_FILE, show);
	// }

	@Override
	public char getPathSeparator() {
		return File.separatorChar;
	}

	public LocalFileSystemView getFileSystemView() {
		String view = config.getString(LOCAL_FILE_SYSTEM_VIEW);
		if (StringUtils.isEmpty(view)) {
			return LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW;
		}

		return LocalFileSystemView.valueOf(view);
	}

	public void setFileSystemView(LocalFileSystemView fileSystemView) {
		config.set(LOCAL_FILE_SYSTEM_VIEW, fileSystemView == null ? LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW.toString() : fileSystemView.toString());
	}

	@Override
	protected void validate() throws InvalidParameterException {

	}

//	@Override
//	public RootItem getDefaultSection() {
//		return DEFAULT_SECTION;
//	}

}
