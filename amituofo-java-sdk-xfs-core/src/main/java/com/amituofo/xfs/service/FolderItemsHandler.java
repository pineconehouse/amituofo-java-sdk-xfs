package com.amituofo.xfs.service;

import java.util.List;

import com.amituofo.common.define.HandleFeedback;

public interface FolderItemsHandler {
	void exceptionCaught(Item data, Throwable e);

	HandleFeedback handle(Integer event, FolderItem parentFolder, List<FileItem> files, List<FileItem> unHandledFileItems);
}
