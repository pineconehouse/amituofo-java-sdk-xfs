package com.amituofo.xfs.service;

import java.io.InputStream;

import com.amituofo.common.ex.ServiceException;

public interface FileItem extends Item {

	InputStream getContent() throws ServiceException;

	ContentWriter getContentWriter();

	ContentHash getContentHash();

	void setContentHash(ContentHash contentHash);

	void upateProperties() throws ServiceException;

	default ItemType getType() {
		return ItemType.File;
	}

	default boolean isDirectory() {
		return false;
	}

	default boolean isFile() {
		return true;
	}

	default boolean isStreaming() {
		return true;
	}

	// String getURL();
}
