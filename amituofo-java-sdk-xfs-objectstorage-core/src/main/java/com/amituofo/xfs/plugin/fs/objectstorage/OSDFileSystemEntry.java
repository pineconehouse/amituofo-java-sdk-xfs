package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.xfs.service.FileSystemFeatures;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;

public abstract class OSDFileSystemEntry<CONFIG extends OSDFileSystemEntryConfig, PREFERENCE extends OSDFileSystemPreference, ITEMSPACE extends OSDBucketspace>
		extends FileSystemEntryBase<CONFIG, PREFERENCE, ITEMSPACE> {

	// @Override
	// public void close() {
	// // TODO Auto-generated method stub
	//
	// }

	public OSDFileSystemEntry(CONFIG entryConfig, PREFERENCE preference) {
		super(entryConfig, preference);
	}

	@Override
	public char getSeparatorChar() {
		return OSDItemBase.SEPARATOR_CHAR;
	}

	@Override
	public boolean hasFeature(int featureId) {
		if (featureId == FileSystemFeatures.AUTO_CREATE_FOLDER) {
			return true;
		}

		return false;
	}

}
