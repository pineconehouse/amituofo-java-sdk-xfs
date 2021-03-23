package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.common.api.ObjectHandler;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.FileItem;

public interface OSDFileItem extends OSDItem, FileItem, OSDFileItemProperties {
	void listVersions(ObjectHandler<Integer, OSDVersionFileItem> event);// throws ServiceException;

	String getURL();

//	@Override
//	default ContentHash getContentHash() {
//		return null;
//	}
//
//	@Override
//	default void setContentHash(ContentHash contentHash) {
//		 disable modify content hash
//	}
	
}
