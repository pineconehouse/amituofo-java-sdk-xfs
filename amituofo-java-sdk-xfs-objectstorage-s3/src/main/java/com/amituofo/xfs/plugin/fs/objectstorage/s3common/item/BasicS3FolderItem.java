package com.amituofo.xfs.plugin.fs.objectstorage.s3common.item;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.value.Counter;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemPreference;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFolderItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemPreference;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;

public abstract class BasicS3FolderItem<S3BUCKET extends BasicS3Bucketspace> extends BasicS3Item<S3BUCKET> implements OSDFolderItem {

	public BasicS3FolderItem(S3BUCKET bucket, String key) {
		super(bucket, key);
	}

	@Override
	public boolean delete() {
		return delete(null);
	}

	@Override
	public boolean delete(final ItemHandler handler) {
		final List<FolderItem> dirs = new ArrayList<FolderItem>();

		final Counter deleteFailed = new Counter();

		list(new ItemHandler() {

			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (ItemEvent.ITEM_FOUND == eventType) {
					if (data.isDirectory()) {
						dirs.add((FolderItem) data);
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

		// 如果没有删除失败的，换句话说都删除了目录空了 ，并且没有子目录就删除这个目录
		if (deleteFailed.i == 0 && dirs.isEmpty()) {
			getS3Client().deleteObject(this.getBucketName(), this.getPath());

			if (handler != null) {
				HandleFeedback ef = handler.handle(ItemEvent.ITEM_DELETED, this);
				// if (ef == EventFeedback.stop) {
				// handler.statusChanged(EventType.EXEC_END, null);
				// }
			}

			return true;
		} else {
			deleteFailed.i = 0;
			for (FolderItem folderItem : dirs) {
				boolean deleted = folderItem.delete(handler);

				if (!deleted) {
					deleteFailed.i++;
				}
			}

			// 子目录都删除了
			if (deleteFailed.i == 0) {
				getS3Client().deleteObject(this.getBucketName(), this.getPath());

				if (handler != null) {
					handler.handle(ItemEvent.ITEM_DELETED, this);
				}

				return true;
			} else {
				// 还有子目录这个目录也删不掉，直接false
				if (handler != null) {
					handler.handle(ItemEvent.ITEM_DELETE_FAILED, this);
				}
			}
		}

		return false;
	}

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		String sourceBucketName = source.getItemspace().getName();
		String sourceKey = source.getPath();
		String targetBucketName = this.getItemspace().getName();
		String targetKey = URLUtils.catPath(this.getPath(), source.getName());
		CopyObjectRequest request = new CopyObjectRequest(sourceBucketName, sourceKey, targetBucketName, targetKey);

		if (source instanceof OSDVersionFileItem) {
			request.setSourceVersionId(source.getVersionId());
		}

		try {
			getS3Client().copyObject(request);
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		// 罗列指定目录中的所有对象
		// Request HCP to list all the objects in this folder.
		// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
		if (((OSDFileSystemPreference) this.getOperationPreference()).listByVersions()) {
			listVersions(filter, handler);
		} else {
			listObjects(filter, handler);
		}
	}

	private void listVersions(ItemFilter filter, ItemHandler handler) {
		try {
			ListVersionsRequest request = new ListVersionsRequest().withBucketName(getBucketName()).withDelimiter("/");
			if (this.getPath().length() > 0 && !this.getPath().equals("/")) {
				String dirkey = this.getPath();
				if (dirkey.charAt(0) == SEPARATOR_CHAR) {
					dirkey = dirkey.substring(1);
				}
				request.withPrefix(dirkey);
			}

			if (((BasicS3FileSystemPreference) this.getOperationPreference()).isDecodePathEnabled() == false) {
				request.withEncodingType("url");
			}
			VersionListing objlisting = getS3Client().listVersions(request);
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

			List<String> dirs = objlisting.getCommonPrefixes();
			List<S3VersionSummary> objs = objlisting.getVersionSummaries();
			while (!dirs.isEmpty() || !objs.isEmpty()) {
				// Printout objects
				final int size = dirs.size();
				if (size > 0) {
					String dir = dirs.get(0);
					// Ignore root /
					if (!"/".equals(dir)) {
						FolderItem item = createDirItem(dir);

						if (filter == null || filter.accept(item)) {
							handler.handle(ItemEvent.ITEM_FOUND, item);
						}
					}

					for (int i = 1; i < size; i++) {
						dir = dirs.get(i);

						FolderItem item = createDirItem(dir);

						if (filter != null && !filter.accept(item)) {
							continue;
						}

						HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
						if (ef == HandleFeedback.interrupted) {
							handler.handle(ItemEvent.EXEC_END, null);
							return;
						}
					}
				}

				for (S3VersionSummary s3ObjectSummary : objs) {
					// System.out.println(++i + "\t" + s3ObjectSummary.getSize() + "\t" + s3ObjectSummary.getETag() + "\t" + s3ObjectSummary.getKey());
					if (s3ObjectSummary.getKey().endsWith("/")) {
						continue;
					}

					Item item = createItem(s3ObjectSummary);

					if (filter != null && !filter.accept(item)) {
						continue;
					}

					HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (ef == HandleFeedback.interrupted) {
						handler.handle(ItemEvent.EXEC_END, null);
						return;
					}
				}

				// // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				VersionListing nextObjlisting = getS3Client().listNextBatchOfVersions(objlisting);
				objlisting = nextObjlisting;
				// // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				dirs = nextObjlisting.getCommonPrefixes();
				objs = nextObjlisting.getVersionSummaries();
			}

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (

		Exception e) {
			handler.exceptionCaught(this, e);
		}
	}

	private void listObjects(ItemFilter filter, ItemHandler handler) {
		try {
			// 罗列指定目录中的所有对象
			// Request HCP to list all the objects in this folder.
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
			ListObjectsRequest request = new ListObjectsRequest().withBucketName(getBucketName()).withDelimiter("/");
			String dirkey = this.getPath();
			if (dirkey.length() > 0 && !dirkey.equals("/")) {
				if (dirkey.charAt(0) == SEPARATOR_CHAR) {
					dirkey = dirkey.substring(1);
				}
				request.withPrefix(dirkey);
			}

			if (((BasicS3FileSystemPreference) this.getOperationPreference()).isDecodePathEnabled() == false) {
				request.withEncodingType("url");
			}

			ObjectListing objlisting = getS3Client().listObjects(request);
			// =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*

			List<String> dirs = objlisting.getCommonPrefixes();
			List<S3ObjectSummary> objs = objlisting.getObjectSummaries();
			while (!dirs.isEmpty() || !objs.isEmpty()) {
				// Printout objects
				for (String dir : dirs) {
					FolderItem item = createDirItem(dir);
					// System.out.println(dir+"\t"+item.getPath()+"\t"+item.getPath());

					if (filter != null && !filter.accept(item)) {
						continue;
					}

					HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (ef == HandleFeedback.interrupted) {
						handler.handle(ItemEvent.EXEC_END, null);
						return;
					}
				}

				for (S3ObjectSummary s3ObjectSummary : objs) {
					// System.out.println(++i + "\t" + s3ObjectSummary.getSize() + "\t" + s3ObjectSummary.getETag() + "\t" + s3ObjectSummary.getKey());
					if (s3ObjectSummary.getKey().endsWith("/")) {
						continue;
					}

					Item item = createItem(s3ObjectSummary);

					if (filter != null && !filter.accept(item)) {
						continue;
					}

					HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (ef == HandleFeedback.interrupted) {
						handler.handle(ItemEvent.EXEC_END, null);
						return;
					}
				}

				// // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				ObjectListing nextObjlisting = getS3Client().listNextBatchOfObjects(objlisting);
				objlisting = nextObjlisting;
				// // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
				dirs = nextObjlisting.getCommonPrefixes();
				objs = nextObjlisting.getObjectSummaries();
			}

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			handler.exceptionCaught(this, e);
		}
	}

	// @Override
	// public void list(ItemFilter filter, EventHandler<EventType, Item> handler) throws ServiceException {
	//
	// try {
	// // 罗列指定目录中的所有对象
	// // Request HCP to list all the objects in this folder.
	// // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	// ListObjectsV2Request request = new ListObjectsV2Request().withBucketName(bucketName).withDelimiter("/");
	// if (!this.getActualPath().equals("/")) {
	// request.withPrefix(this.getActualPath());
	// }
	//
	// // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	// ListObjectsV2Result objlisting = null;
	// String token = null;
	// do {
	// objlisting = getS3Client().listObjectsV2(request);
	//
	// List<String> dirs = objlisting.getCommonPrefixes();
	// List<S3ObjectSummary> objs = objlisting.getObjectSummaries();
	// // Printout objects
	//
	// if (filter == null || filter.getType() != ItemType.File) {
	// for (String dir : dirs) {
	// S3FolderItem item = toDirItem(dir);
	//
	// if (handler != null) {
	// EventFeedback ef = handler.statusChanged(EventType.ITEM_FOUND, item);
	// if (ef == EventFeedback.stop) {
	// handler.statusChanged(EventType.EXEC_END, null);
	// return;
	// }
	// }
	// }
	// }
	//
	// if (filter == null || filter.getType() != ItemType.Directory) {
	// for (S3ObjectSummary s3ObjectSummary : objs) {
	// System.out.println(
	// objlisting.getNextContinuationToken() + "\t" + s3ObjectSummary.getSize() + "\t" + s3ObjectSummary.getETag() + "\t" +
	// s3ObjectSummary.getKey());
	// if (s3ObjectSummary.getKey().endsWith("/")) {
	// continue;
	// }
	//
	// S3ItemBase item = toItem(s3ObjectSummary);
	//
	// if (handler != null) {
	// EventFeedback ef = handler.statusChanged(EventType.ITEM_FOUND, item);
	// if (ef == EventFeedback.stop) {
	// handler.statusChanged(EventType.EXEC_END, null);
	// return;
	// }
	// }
	// }
	// }
	//
	// // // =*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*
	// token = objlisting.getNextContinuationToken();
	// request.setContinuationToken(token);
	// request.setStartAfter(objlisting.getStartAfter());
	// // request.setStartAfter(objs.get(objs.size() - 1).getKey());
	// } while (token != null && token.length() > 0);
	//
	// if (handler != null) {
	// handler.statusChanged(EventType.EXEC_END, null);
	// }
	// } catch (AmazonServiceException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// }
	// }

	@Override
	public boolean exists() throws ServiceException {
		// 标准S3无法判断目录是否存在，HCP可以，所以此处会导致bug，强制所有目录存在
		return true;

		// String key = getPath();
		// if (key.length() == 0 || "/".equals(key)) {
		// // Root path
		// return true;
		// }
		//
		// try {
		// // 标准S3无法判断目录是否存在，HCP可以，所以此处会导致bug
		// return getS3Client().doesObjectExist(this.bucketName, this.getPath());
		// } catch (Exception e) {
		// throw new ServiceException(e);
		// }
	}

	// @Override
	// public boolean deleteEmptyFolder() throws ServiceException {
	// try {
	// getS3Client().deleteObject(this.getBucketName(), this.getPath());
	//
	// return true;
	// } catch (AmazonServiceException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// }
	// }

	// @Override
	// public boolean createDirectory(String name) throws ServiceException {
	// try {
	// // create meta-data for your folder and set content-length to 0
	// ObjectMetadata metadata = new ObjectMetadata();
	// metadata.setContentLength(0);
	//
	// // create empty content
	// InputStream emptyContent = new ByteArrayInputStream(new byte[0]);
	//
	// // create a PutObjectRequest passing the folder name suffixed by /
	// String dirKey = URLUtils.catPath(this.getPath(), name);
	// PutObjectRequest putObjectRequest = new PutObjectRequest(getBucketName(), URLUtils.catPath(dirKey, "/"), emptyContent, metadata);
	//
	// // send request to S3 to create folder
	// getS3Client().putObject(putObjectRequest);
	// } catch (AmazonServiceException e) {
	// throw new ServiceException(e.getMessage(), e);
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// }
	//
	// return true;
	// }

	@Override
	public boolean createDirectory() throws ServiceException {
		try {
			// create meta-data for your folder and set content-length to 0
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(0);

			// create empty content
			InputStream emptyContent = new ByteArrayInputStream(new byte[0]);

			// create a PutObjectRequest passing the folder name suffixed by /
			String dirKey = this.getPath();
			PutObjectRequest putObjectRequest = new PutObjectRequest(getBucketName(), URLUtils.catPath(dirKey, "/"), emptyContent, metadata);

			// send request to S3 to create folder
			getS3Client().putObject(putObjectRequest);
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		return true;
	}

	@Override
	public void rename(String newname) throws ServiceException {
		throw new ServiceException("Unsupport operation!");
	}

}
