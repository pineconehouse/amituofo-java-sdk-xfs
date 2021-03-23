package com.amituofo.xfs.plugin.fs.objectstorage.mqe.item;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.MQEFileSystemEntryConfig;
import com.amituofo.xfs.service.Item;
import com.hitachivantara.hcp.query.define.Operation;
import com.hitachivantara.hcp.query.model.ObjectSummary;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.PutObjectResult;

public class MQEFileItem extends HCPFileItem {
	// protected final HCPFileItem hcpFileItem;
	// ObjectSummary summary;
	public MQEFileItem(HCPBucketspace namespace, String key) {
		super(namespace, key);
	}

	@Override
	public String getSystemName() {
		return MQEFileSystemEntryConfig.SYSTEM_NAME;
	}

	@Override
	public int getStatus() {
		Operation op = ((ObjectSummary) getSummary()).getOperation();
		// if (op == Operation.NOT_FOUND || op == Operation.DELETED) {
		// return Item.ITEM_STATUS_DELETED;
		// }

		if (op != Operation.CREATED) {
			return Item.ITEM_STATUS_DELETED;
		}
		return 0;
	}

	@Override
	public void upateProperties() throws ServiceException {

		// 不允许更新属性，因为类型不同会导致问题
//		super.upateProperties();
	}

	@Override
	public HCPObjectSummary updateSummary() throws ServiceException {
		// 不允许更新属性，因为类型不同会导致问题
//		return super.updateSummary();
		return null;
	}

	@Override
	public HCPObjectSummary updateSummary(PutObjectResult result) throws ServiceException {
		// 不允许更新属性，因为类型不同会导致问题
//		return super.updateSummary(result);
		return null;
	}

	@Override
	public void upateProperties(PutObjectResult result) {
		// 不允许更新属性，因为类型不同会导致问题
//		super.upateProperties(result);
	}
	
	
}
