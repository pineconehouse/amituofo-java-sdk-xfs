package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.item;

import java.util.Iterator;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDBucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemInstanceCreator;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.B2FileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.B2FileSystemPreference;
import com.amituofo.xfs.service.ContentHash;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;
import com.amituofo.xfs.service.ItemspaceBase;
import com.amituofo.xfs.service.ListOption;
import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2ListFileVersionsRequest;

public class B2Bucketspace extends ItemspaceBase<B2FileSystemEntry, B2FileSystemPreference> implements OSDBucketspace, OSDItemInstanceCreator {
	protected final B2StorageClient b2Client;
	protected B2Bucket bucket;

	public B2Bucketspace(B2FileSystemEntry entry, B2StorageClient b2Client, B2Bucket bucket) {
		super(entry);
		this.b2Client = b2Client;
		this.bucket = bucket;
	}

	public B2Bucket getBucket() {
		return bucket;
	}

	public void setBucket(B2Bucket bucket) {
		this.bucket = bucket;
	}

	@Override
	public String getName() {
		return bucket.getBucketName();
	}

	public B2StorageClient getB2Client() {
		return b2Client;
	}

	@Override
	public void list(final ListOption listOption, final ItemHandler handler) throws ServiceException {
		// this.getRootFolder().list(handler);
		try {
			ItemFilter filter = listOption.getFilter();
			B2StorageClient b2Client = getB2Client();

			com.backblaze.b2.client.structures.B2ListFileVersionsRequest.Builder builder = B2ListFileVersionsRequest.builder(bucket.getBucketId());
			if (StringUtils.isNotEmpty(listOption.getPrefix())) {
				builder.setWithinFolder(listOption.getPrefix());
			}

			B2ListFileVersionsRequest request = builder.build();

			Iterator<B2FileVersion> it = b2Client.fileVersions(request).iterator();
			while (it.hasNext()) {
				Item item = createItem(listOption.getPrefix(), it.next());

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				HandleFeedback result = handler.handle(ItemEvent.ITEM_FOUND, item);
				if (result == HandleFeedback.interrupted) {
					return;
				}
			}
		} catch (B2Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			handler.handle(ItemEvent.EXEC_END, null);
		}
	}

	protected Item createItem(String parentDir, B2FileVersion file) {
		String fullpath = file.getFileName();
		String filename = URLUtils.getLastNameFromPath(fullpath);

		Item item;
		if (file.isFolder()) {
			item = ((ItemInstanceCreator) this).newFolderItemInstance(fullpath);
		} else {
			item = ((ItemInstanceCreator) this).newFileItemInstance(fullpath);
			((ItemHiddenFunction) item).setSize(file.getContentLength());
			((B2FileItem) item).setContentHash(new ContentHash(file.getContentMd5()));
		}
		((ItemHiddenFunction) item).setName(filename);
		((ItemHiddenFunction) item).setLastUpdateTime(file.getUploadTimestamp());
		((ItemHiddenFunction) item).setCreateTime(file.getUploadTimestamp());

		((B2Item) item).fileId = file.getFileId();
		return item;
	}

	@Override
	public FolderItem newFolderItemInstance(String fullpath) {
		return new B2FolderItem(this, fullpath);
	}

	@Override
	public FileItem newFileItemInstance(String fullpath) {
		return new B2FileItem(this, fullpath);
	}

	@Override
	public OSDVersionFileItem newVersionFileItemInstance(String fullpath, String versionId) {
		return new B2FileItem(this, fullpath);
	}

	@Override
	public String getEndpoint() {
		return entry.getEntryConfig().getEndpoint();
	}

	public String getBucketId() {
		return bucket.getBucketId();
	}

}
