package com.amituofo.xfs.util;

import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;

public class FolderItemStatistic extends ItemStatistic {

	private FolderItem folderitem;
	private FolderItemStatistic parent;

	public FolderItemStatistic(FolderItemStatistic parent, FolderItem thisfolderitem) {
		super();
		this.parent = parent;
		this.folderitem = thisfolderitem;
	}

	public FolderItemStatistic(FolderItemStatistic parent, FolderItem thisfolderitem, Item item) {
		super(item);
		this.parent = parent;
		this.folderitem = thisfolderitem;
	}

	public FolderItemStatistic(FolderItemStatistic parent, FolderItem thisfolderitem, ItemStatistic summary) {
		super(summary);
		this.parent = parent;
		this.folderitem = thisfolderitem;
	}

	public FolderItem getFolderItem() {
		return folderitem;
	}

	public FolderItemStatistic getParent() {
		return parent;
	}
	
//	public void updateParent() {
//		FolderItemStatistic parentStatistic = this.parent;
//		while(parentStatistic!=null) {
//			parentStatistic.
//		}
//	}

}
