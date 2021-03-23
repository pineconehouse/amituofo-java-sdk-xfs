package com.amituofo.xfs.plugin.fs.objectstorage.mqe.item;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFolderItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemBase;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.item.HCPFolderItem;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.MQEFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.QueryStatusHandler;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemType;

public class MQEFolderItem extends ItemBase<MQEQueryRequest> implements OSDFolderItem {
	private int offset = -1;
	private int pageSize = -1;
	private QueryStatusHandler statushandler = null;

	public MQEFolderItem(MQEQueryRequest request) {
		super(request);
		super.setPath("");
		super.setName("");
	}

	@Override
	public String getSystemName() {
		return MQEFileSystemEntryConfig.SYSTEM_NAME;
	}

	public void list(int offset, int pageSize, ItemFilter filter, final ItemHandler itemhandler, final QueryStatusHandler statushandler) {
		MQEQueryRequest request = (MQEQueryRequest) itemspace;
		if (offset < 0 && pageSize < 0) {
			try {
				request.queryAll(itemhandler, statushandler);
			} catch (Exception e) {
				itemhandler.exceptionCaught(null, e);
			}
		} else {
			try {
				request.queryOffset(offset, pageSize, itemhandler, statushandler);
			} catch (Exception e) {
				e.printStackTrace();
				itemhandler.exceptionCaught(null, e);
			}
		}
	}

	@Override
	public void list(ItemFilter filter, final ItemHandler itemhandler) {
		list(offset, pageSize, filter, itemhandler, statushandler);
	}

	public void setStatusHandler(QueryStatusHandler statushandler) {
		this.statushandler = statushandler;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public char getPathSeparator() {
		return OSDItemBase.SEPARATOR_CHAR;
	}

	@Override
	public FolderItem getParent() {
		return null;
	}

	@Override
	public boolean isSame(Item item) {
		return false;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof HCPFolderItem) && !(item instanceof MQEFolderItem)) {
			return false;
		}

		// if (!(item.getSystemName().equals(this.getSystemName()))) {
		// return false;
		// }

		OSDFileSystemEntryConfig swdConfig = (OSDFileSystemEntryConfig) this.getFileSystemEntry().getEntryConfig();
		OSDFileSystemEntryConfig twdConfig = (OSDFileSystemEntryConfig) item.getFileSystemEntry().getEntryConfig();
		if (!swdConfig.getEndpoint().equalsIgnoreCase(twdConfig.getEndpoint()) && swdConfig.getAccesskey().equals(twdConfig.getAccesskey())) {
			return false;
		}

		return true;
	}

//	@Override
//	public String[] getSupportVersion() {
//		return null;
//	}

	@Override
	public boolean exists() throws ServiceException {
		// always true
		return true;
	}

	@Override
	public boolean delete(ItemHandler handler) {
		// Unsupport function
		return false;
	}

	@Override
	public boolean delete() throws ServiceException {
		// Unsupport function
		return false;
	}

	@Override
	public void rename(String newname) throws ServiceException {
		// Unsupport function
	}

	@Override
	public void listFolders(ItemFilter filter, ItemHandler handler) {
		// Unsupport function
	}

//	@Override
//	public boolean deleteEmptyFolder() throws ServiceException {
//		// Unsupport function
//		return false;
//	}

	@Override
	public boolean createDirectory(String name) throws ServiceException {
		// Unsupport function
		return false;
	}

	@Override
	public boolean createDirectory() throws ServiceException {
		// Unsupport function
		return false;
	}

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		// Unsupport function
	}

}
