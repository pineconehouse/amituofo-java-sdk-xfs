package com.amituofo.xfs.plugin.fs.hdfs.item;

import com.amituofo.xfs.plugin.fs.hdfs.HadoopHDFileSystemEntry;
import com.amituofo.xfs.plugin.fs.hdfs.HadoopHDFileSystemPreference;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.ItemspaceBase;

public class HadoopHDFSItemspace extends ItemspaceBase<HadoopHDFileSystemEntry, HadoopHDFileSystemPreference> {
	private String name;

	public HadoopHDFSItemspace(HadoopHDFileSystemEntry fileSystemEntry, String name) {
		super(fileSystemEntry);
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		return new HadoopHDFSFolderItem(entry.getHadoopFileSystem(), this, fullpath);
	}

	@Override
	public FileItem newFileItemInstance(String fullpath) {
		return new HadoopHDFSFileItem(entry.getHadoopFileSystem(), this, fullpath);
	}

	@Override
	protected FolderItem createRootFolder() {
		HadoopHDFSFolderItem folder = (HadoopHDFSFolderItem) newFolderItemInstance("/");
		// xxx
		// System.out.println(entry.getHadoopFileSystem().getWorkingDirectory());
		// System.out.println(entry.getHadoopFileSystem().getHomeDirectory());
		// folder.setFileStatus(entry.getHadoopFileSystem().getFileStatus());
		folder.setName("/");
//		folder.setPath("/");
		// folder.setFileStatus(entry.getHadoopFileSystem().getFileStatus(new Path("/")));
		return folder;
	}

}
