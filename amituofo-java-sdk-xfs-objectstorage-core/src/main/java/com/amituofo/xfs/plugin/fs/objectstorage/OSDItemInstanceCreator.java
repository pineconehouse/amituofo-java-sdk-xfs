package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.xfs.service.ItemInstanceCreator;

public interface OSDItemInstanceCreator extends ItemInstanceCreator {
	OSDVersionFileItem newVersionFileItemInstance(String key, String versionId);

}
