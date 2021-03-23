package com.amituofo.xfs.plugin.fs.local;

import com.amituofo.xfs.service.impl.FileSystemPreferenceBase;

public class LocalFileSystemPreference extends FileSystemPreferenceBase {

//	private boolean showHidden = true;
	private LocalFileSystemView fileSystemView = LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW;

	public LocalFileSystemPreference(LocalFileSystemView fileSystemView) {
		super();
//		this.showHidden = showHidden;
		this.fileSystemView = (fileSystemView == null ? LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW : fileSystemView);
	}

	// public LocalFileSystemPreference(boolean showHidden) {
	// this.showHidden = showHidden;
	// }

//	public boolean isShowHidden() {
//		return showHidden;
//	}
//
//	public void setShowHidden(boolean showHidden) {
//		this.showHidden = showHidden;
//	}

	public LocalFileSystemView getFileSystemView() {
		return fileSystemView;
	}

	public void setFileSystemView(LocalFileSystemView fileSystemView) {
		this.fileSystemView = (fileSystemView == null ? LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW : fileSystemView);
	}

}
