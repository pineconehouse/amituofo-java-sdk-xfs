package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.Item;

public interface OSDItem extends Item {
	void copy(OSDFileItem source) throws ServiceException;
}
