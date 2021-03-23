package com.amituofo.xfs.service;

public class ItemspaceSummary {
	private long fileItemCount = -1;
//	private long folderItemCount = -1;
	private long totalCapacityBytes = -1;
	private long usedCapacityBytes = -1;

	public ItemspaceSummary(long fileItemCount, long totalCapacityBytes, long usedCapacityBytes) {
		super();
		this.fileItemCount = fileItemCount;
//		this.folderItemCount = folderItemCount;
		this.totalCapacityBytes = totalCapacityBytes;
		this.usedCapacityBytes = usedCapacityBytes;
	}

	public long getFileItemCount() {
		return fileItemCount;
	}

//	public long getFolderItemCount() {
//		return folderItemCount;
//	}

	public long getTotalCapacityBytes() {
		return totalCapacityBytes;
	}

	public long getUsedCapacityBytes() {
		return usedCapacityBytes;
	}

}
