package com.amituofo.xfs.plugin.fs.webdav.item;

import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemEntry;
import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemspaceBase;

public class WebDavItemspace extends ItemspaceBase<WebDavFileSystemEntry, WebDavFileSystemPreference> {
	private String name;

	public WebDavItemspace(WebDavFileSystemEntry fileSystemEntry, String name) {
		super(fileSystemEntry);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		return new WebDavFolderItem(this, fullpath);
	}

	@Override
	public FileItem newFileItemInstance(String fullpath) {
		return new WebDavFileItem(this, fullpath);
	}

	@Override
	protected FolderItem createRootFolder() {
		WebDavFolderItem folder = (WebDavFolderItem) newFolderItemInstance("/");
		// xxx
		// System.out.println(entry.getHadoopFileSystem().getWorkingDirectory());
		// System.out.println(entry.getHadoopFileSystem().getHomeDirectory());
		// folder.setFileStatus(entry.getHadoopFileSystem().getFileStatus());
		folder.setName("/");
		// folder.setPath("/");
		// folder.setFileStatus(entry.getHadoopFileSystem().getFileStatus(new Path("/")));
		return folder;
	}

}
