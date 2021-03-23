package com.amituofo.xfs.plugin.fs.virtual.item;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.value.Counter;
import com.amituofo.xfs.plugin.fs.virtual.vfs.JdbcVirtualFSI;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFSI;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemException;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemType;

public class VirtualFolderItem extends VirtualItemBase implements FolderItem {

	public VirtualFolderItem(VirtualItemspace itemspace, VirtualFSI virtualFSI) {// , int deepLevel,
		super(itemspace, virtualFSI);
		this.setName(virtualFSI.getFolderName());
		this.setPath(virtualFSI.getFolderPath());
		// this.setCreateTime(System.currentTimeMillis());
		// this.setLastUpdateTime(System.currentTimeMillis());
	}

	// @Override
	// public Item clone() {
	// VirtualFolderItem clone = new VirtualFolderItem(rootitem, fileSystemEntry, preference, virtualFSI);// , currentDeepLevel, false);
	// return clone;
	// }

	
	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		try {
			virtualFSI.list(filter, handler);
		} catch (VirtualFileSystemException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean delete() throws ServiceException {
		return delete(null);
	}

	@Override
	public boolean delete(ItemHandler handler) {
		// try {
		// boolean deleted = ((VirtualItemBase) this.getParent()).getVirtualFSI().delete(this.getName(), ItemType.Directory);
		// if (handler != null) {
		// handler.handle(ItemEvent.ITEM_DELETED, this);
		// }
		// return deleted;
		// } catch (VirtualFileSystemException e) {
		// e.printStackTrace();
		// return false;
		// }

		try {
			boolean deleted = removeSubs(this, handler);
			if (handler != null) {
				handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, this);
			}
			
			return deleted;
		} catch (Exception e) {
			// e.printStackTrace();
			if (handler != null) {
				handler.exceptionCaught(this, e);
			}
			return false;
		}
	}

	private boolean removeSubs(VirtualFolderItem folder, final ItemHandler handler) throws VirtualFileSystemException {
		// final List<VirtualFolderItem> subfolders = new ArrayList<VirtualFolderItem>();
		final Counter failed = new Counter();
		folder.getVirtualFSI().listFolders(null, new ItemHandler() {

			@Override
			public void exceptionCaught(Item data, Throwable e) {
			}

			@Override
			public HandleFeedback handle(Integer meta, Item obj) {
				if (obj != null) {
					VirtualFolderItem subfolder = (VirtualFolderItem) obj;
					// subfolders.add(subfolder);
					try {
						if (!removeSubs(subfolder, handler)) {
							failed.i++;
							return HandleFeedback.interrupted;
						}
					} catch (VirtualFileSystemException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		});

		// int c = 0;
		// for (VirtualFolderItem virtualFolderItem : subfolders) {
		// System.out.println("-Debug- delete folder " + folder.getPath());
		// if (((JdbcVirtualFSI) virtualFolderItem.getVirtualFSI()).remove()) {
		// c++;
		// }
		// }
		// return c == subfolders.size();
		// return folder.delete();
		if (failed.i == 0) {
//			System.out.println("-Debug- delete folder " + folder.getPath());
			boolean deleted = ((JdbcVirtualFSI) folder.getVirtualFSI()).remove();
			
			if (deleted && handler != null) {
				handler.handle(ItemEvent.ITEM_DELETED, this);
			}
			
			return deleted;
		}
		return false;
	}

//	@Override
//	public boolean deleteEmptyFolder() throws ServiceException {
//		return delete(null);
//	}

	@Override
	public boolean createDirectory(String name) throws ServiceException {
		try {
			// String folderPath = URLUtils.catPath(this.getPath(), name, fileSystemEntry.getSeparatorChar());
			virtualFSI.newDirectory(name);
			return true;
		} catch (VirtualFileSystemException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean createDirectory() throws ServiceException {
		try {
			((VirtualItemBase) this.getParent()).getVirtualFSI().newDirectory(this.getName());
			return true;
		} catch (VirtualFileSystemException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		if ("/".equals(this.getPath())) {
			return true;
		}

		try {
			return ((VirtualItemBase) this.getParent()).getVirtualFSI().exist(this.getName(), ItemType.Directory);
		} catch (VirtualFileSystemException e) {
			// e.printStackTrace();
			throw new ServiceException(e);
		}
	}


}
