package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.util.List;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFolderItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.ObjectEntryIterator;
import com.hitachivantara.hcp.standard.api.event.ObjectDeletingListener;
import com.hitachivantara.hcp.standard.define.NextAction;
import com.hitachivantara.hcp.standard.define.ObjectState;
import com.hitachivantara.hcp.standard.model.HCPObjectEntry;
import com.hitachivantara.hcp.standard.model.HCPObjectEntrys;
import com.hitachivantara.hcp.standard.model.HCPObjectSummary;
import com.hitachivantara.hcp.standard.model.request.impl.CheckObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.CopyObjectRequest;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteDirectoryRequest;
import com.hitachivantara.hcp.standard.model.request.impl.ListDirectoryRequest;

public class HCPFolderItem extends HCPItemBase implements OSDFolderItem {
	
	public HCPFolderItem(HCPBucketspace namespace, String key) {
		super(namespace, key);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		try {

			HCPObjectEntrys result = null;
			result = getHcpClient().listDirectory(new ListDirectoryRequest(this.getPath()).withDeletedObject(itemspace.isShowDeletedObjects()));

			ObjectEntryIterator it = result.iterator();
			List<HCPObjectEntry> list;
			// int totalcount = 0;
			int c = 50;
			while ((list = it.next(c)) != null) {
				// totalcount += list.size();
				// print.addRow("-" + list.size() + "-----------------------------------------------");
				for (int i = 0; i < list.size(); i++) {
					HCPObjectEntry hcpObjectEntry = list.get(i);

					// if (filter != null && filter.getType() == ItemType.Directory) {
					// if (hcpObjectEntry.getType() != ObjectType.directory) {
					// continue;
					// }
					// } else {
					//
					// }

					// print.addRow((ii++) + "\t" + hcpObjectEntry.getType() + "\t" + hcpObjectEntry.getKey() + "\t" +
					// hcpObjectEntry.getName() + "\t" + hcpObjectEntry.getSize());

					Item item;
					switch (hcpObjectEntry.getType()) {
						case object:
							if(hcpObjectEntry.getState() == ObjectState.deleted) {
								item = (itemspace).newDeletedFileItemInstance(hcpObjectEntry.getKey());
							} else {
								item = ((ItemInstanceCreator)itemspace).newFileItemInstance(hcpObjectEntry.getKey());
							}
							// item = new HCPFileItem(preference, (HCPFileSystemEntry)fileSystemEntry, getHcpClient(), namespaceSetting);
							HCPFileItem.setHCPItemProperties((HCPFileItem)item, hcpObjectEntry);
							break;
						case directory:
							item = ((ItemInstanceCreator)itemspace).newFolderItemInstance(hcpObjectEntry.getKey());
							// item = new HCPFolderItem(preference, (HCPFileSystemEntry)fileSystemEntry, getHcpClient(), namespaceSetting);
							HCPFolderItem.setHCPItemProperties((HCPFolderItem)item, hcpObjectEntry);
							break;
						default:
							item = ((ItemInstanceCreator)itemspace).newFileItemInstance(hcpObjectEntry.getKey());
							// item = new HCPFileItem(preference, (HCPFileSystemEntry)fileSystemEntry, getHcpClient(), namespaceSetting);
							HCPFileItem.setHCPItemProperties((HCPFileItem)item, hcpObjectEntry);
					}


					if (filter != null && !filter.accept(item)) {
						continue;
					}

					HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (ef == HandleFeedback.interrupted) {
						it.abort();
						handler.handle(ItemEvent.EXEC_END, null);
						return;
					}
				}
			}

			it.close();
			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			handler.exceptionCaught(this, e);
		}
	}

//	@Override
//	public boolean deleteEmptyFolder() throws ServiceException {
//		try {
//			return getHcpClient().deleteObject(this.getPath());
//		} catch (InvalidResponseException e) {
//			throw new ServiceException(e.getMessage(), e);
//		} catch (HSCException e) {
//			throw new ServiceException(e);
//		}
//	}

	// public boolean deleteVersion() throws ServiceException {
	// String key = (String) this.getActualPath();
	// try {
	// return getHcpClient().deleteObject(new DeleteObjectRequest(key).withVersionId(this.getSummary().getVersionId()));
	// } catch (InvalidResponseException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (HSCException e) {
	// throw new ServiceException(e);
	// }
	// }

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		String sourceNamespaceName = ((HCPFileItem)source).getHcpClient().getNamespace();
		String sourceKey = source.getPath();
		String targetNamespaceName = this.getHcpClient().getNamespace();
		String targetKey = URLUtils.catPath(this.getPath(), source.getName());
		CopyObjectRequest request = new CopyObjectRequest()
				//
				.withSourceKey(sourceKey)
				//
				.withSourceNamespace(sourceNamespaceName)
				//
				.withTargetKey(targetKey)
				//
				.withTargetNamespace(targetNamespaceName)
				//
				.withCopyingMetadata(true)
				//
				.withCopyingOldVersion(false);
		
		if (source instanceof OSDVersionFileItem) {
			request.withSourceVersion(source.getVersionId());
		}

		try {
			getHcpClient().copyObject(request);
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete() {
		return delete(null);
	}

	@Override
	public boolean delete(final ItemHandler handler) {
		String key = (String) this.getPath();
		DeleteDirectoryRequest req = new DeleteDirectoryRequest(key).withDeleteContainedObjects(true).withPurge(itemspace.isEnablePurgeDeletion());
		req.withDeleteListener(new ObjectDeletingListener() {

			@Override
			public NextAction afterDeleting(HCPObjectSummary objectEntry, boolean deleted) {
				if (handler != null) {
					HCPFolderItem item = (HCPFolderItem)((ItemInstanceCreator)itemspace).newFolderItemInstance(objectEntry.getKey());
					item = HCPFolderItem.setHCPItemProperties(item, objectEntry);
					HandleFeedback ef = handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, item);
					if (ef == HandleFeedback.interrupted) {
//						handler.handle(ItemEvent.EXEC_END, null);
						return NextAction.stop;
					}
				}

				return null;
			}

			@Override
			public NextAction beforeDeleting(HCPObjectSummary objectEntry) {
				return null;

			}
		});

		try {
			boolean deleted = getHcpClient().deleteDirectory(req);

			return deleted;
		} catch (Exception e) {
			if (handler != null) {
				handler.exceptionCaught(this, e);
			}
			return false;
		}
	}

//	@Override
//	public boolean createDirectory(String name) throws ServiceException {
//		try {
//			return getHcpClient().createDirectory(URLUtils.catPath(this.getPath(), name));
//		} catch (InvalidResponseException e) {
//			throw new ServiceException(e.getMessage(), e);
//		} catch (HSCException e) {
//			throw new ServiceException(e);
//		}
//	}

	@Override
	public boolean createDirectory() throws ServiceException {
		try {
			return getHcpClient().createDirectory(this.getPath());
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void rename(String newname) throws ServiceException {
		// TODO Auto-generated method stub
		throw new ServiceException("Unsupport operation!");
	}

	@Override
	public boolean exists() throws ServiceException {
		String key = getPath();
		if (key.length() == 0 || "/".equals(key)) {
			// Root path
			return true;
		}

		try {
			return getHcpClient().doesObjectExist(new CheckObjectRequest(key));
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}

	public static HCPFolderItem setHCPItemProperties(HCPFolderItem item, HCPObjectSummary hcpObjectEntry) {
		item.setName(hcpObjectEntry.getName());
		item.setPath(hcpObjectEntry.getKey());
		// item.setSize(hcpObjectEntry.getSize());
		item.setLastUpdateTime(hcpObjectEntry.getIngestTime());
		// item.setCreateTime(hcpObjectEntry.getIngestTime());

		return item;
	}

	public static HCPFolderItem setHCPItemProperties(HCPFolderItem item, HCPObjectEntry hcpObjectEntry) {
		item.setName(hcpObjectEntry.getName());
//		item.setPath(hcpObjectEntry.getKey());
		item.setLastUpdateTime(hcpObjectEntry.getIngestTime());
		// item.setCreateTime(hcpObjectEntry.getIngestTime());
		// item.setSummary(hcpObjectEntry);
		item.state = hcpObjectEntry.getState();

		return item;
	}
}
