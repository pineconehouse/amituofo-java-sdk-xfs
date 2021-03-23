package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.item;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.value.Counter;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFolderItem;
import com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.B2FileSystemPreference;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2ListFileNamesRequest;
import com.backblaze.b2.client.structures.B2ListFileVersionsRequest;

public class B2FolderItem extends B2Item implements OSDFolderItem {
	public B2FolderItem(B2Bucketspace bucket, String key) {
		super(bucket, key);
	}

	@Override
	public boolean delete() {
		return delete(null);
	}

	@Override
	public boolean delete(final ItemHandler handler) {
		// final List<FolderItem> dirs = new ArrayList<FolderItem>();

		final Counter deleteFailed = new Counter();

		list(new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (ItemEvent.ITEM_FOUND == eventType) {
					if (data.isDirectory()) {
						// dirs.add((FolderItem) data);
					} else {
						boolean deleted = false;
						try {
							deleted = data.delete();
						} catch (ServiceException e) {
							if (handler != null) {
								handler.exceptionCaught(data, e);
							}
						}

						if (!deleted) {
							deleteFailed.i++;
						}
						if (handler != null) {
							HandleFeedback ef = handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, data);
							if (ef == HandleFeedback.interrupted) {
								// handler.handle(ItemEvent.EXEC_END, null);
								return HandleFeedback.interrupted;
							}
						}
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				if (handler != null) {
					handler.exceptionCaught(data, e);
				}
			}
		});

		return false;
	}

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		throw new ServiceException("Unsupport operation!");
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		Iterator<B2FileVersion> it;
		try {
			if (((B2FileSystemPreference) this.getOperationPreference()).listByVersions()) {
				B2ListFileVersionsRequest requestVersion = B2ListFileVersionsRequest.builder(itemspace.getBucketId()).setWithinFolder(this.getPath()).build();
				it = getB2Client().fileVersions(requestVersion).iterator();
			} else {
				B2ListFileNamesRequest request = B2ListFileNamesRequest.builder(itemspace.getBucketId()).setWithinFolder(this.getPath()).build();
				it = getB2Client().fileNames(request).iterator();
			}
		} catch (B2Exception e) {
			handler.exceptionCaught(this, e);
			return;
		}

		while (it.hasNext()) {
			Item item = createItem(it.next());

			if (filter != null && !filter.accept(item)) {
				continue;
			}

			HandleFeedback result = handler.handle(ItemEvent.ITEM_FOUND, item);
			if (result == HandleFeedback.interrupted) {
				return;
			}
		}

		handler.handle(ItemEvent.EXEC_END, null);
	}

	@Override
	public boolean exists() throws ServiceException {
		// 标准S3无法判断目录是否存在，HCP可以，所以此处会导致bug，强制所有目录存在
		return true;
	}

	// @Override
	// public boolean deleteEmptyFolder() throws ServiceException {
	// // try {
	// // getb2Client().deleteObject(this.getBucketName(), this.getPath());
	//
	// return true;
	// // } catch (B2Exception e) {
	// // throw new ServiceException(e.getMessage(), e);
	// // } catch (Exception e) {
	// // throw new ServiceException(e);
	// // }
	// }

	// @Override
	// public boolean createDirectory(String name) throws ServiceException {
	// this.linkFolder(name).linkFile(".bzEmpty").getContentWriter().write(new ByteArrayInputStream(new byte[0]), 0);
	//
	// return true;
	// }

	@Override
	public boolean createDirectory() throws ServiceException {
		this.linkFile(".bzEmpty").getContentWriter().write(new ByteArrayInputStream(new byte[0]), 0);

		return true;
	}

	@Override
	public void rename(String newname) throws ServiceException {
		throw new ServiceException("Unsupport operation!");
	}

}
