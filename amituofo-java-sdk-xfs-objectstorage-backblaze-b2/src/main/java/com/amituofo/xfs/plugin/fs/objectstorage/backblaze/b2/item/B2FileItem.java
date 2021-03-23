package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.item;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import com.amituofo.common.api.ObjectHandler;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDVersionFileItem;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemProperties;
import com.backblaze.b2.client.contentHandlers.B2ContentSink;
import com.backblaze.b2.client.contentSources.B2Headers;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2CopyFileRequest;
import com.backblaze.b2.client.structures.B2DownloadAuthorization;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2GetDownloadAuthorizationRequest;
import com.backblaze.b2.client.structures.B2GetUploadUrlRequest;
import com.backblaze.b2.client.structures.B2HideFileRequest;
import com.backblaze.b2.client.structures.B2ListFileVersionsRequest;
import com.backblaze.b2.client.structures.B2UploadUrlResponse;

public class B2FileItem extends B2Item implements OSDFileItem, OSDVersionFileItem {
//	protected String etag;

	public B2FileItem(B2Bucketspace bucket, String key) {
		super(bucket, key);
	}

	@Override
	public InputStream getContent() throws ServiceException {
		final InputStream[] contentin = new InputStream[1];
		try {
			B2ContentSink handler = new B2ContentSink() {

				@Override
				public void readContent(B2Headers responseHeaders, InputStream in) throws B2Exception, IOException {
					contentin[0] = in;
				}
			};

			getB2Client().downloadById(getFileId(), handler);
		} catch (B2Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		return contentin[0];
	}

	@Override
	public ContentWriter getContentWriter() {
		return new B2ContentWriter(this);
	}

	@Override
	public void rename(String newname) throws ServiceException {
		try {
			ValidUtils.invalidIfEmpty(newname, "A new name must be specified!");
			ValidUtils.invalidIfEqual(newname, this.getName(), "New name must be different with current name!");
		} catch (InvalidParameterException e) {
			throw new ServiceException(e);
		}

		B2FileItem cloneItem = (B2FileItem) this.clone();

		cloneItem.setName(newname);

		cloneItem.copy(this);

		if (cloneItem.exists()) {
			this.delete();
		}
	}

	public URL generateDownloadUrl(Date expiration) throws ServiceException {
		String url;
		try {
			int validDurationInSeconds = (int) (expiration.getTime() - System.currentTimeMillis()) / 1000;
			B2GetDownloadAuthorizationRequest request = B2GetDownloadAuthorizationRequest.builder(getItemspace().getBucketId(), this.getPath(), validDurationInSeconds).build();
			B2DownloadAuthorization downloadauth = getB2Client().getDownloadAuthorization(request);

			url = URLUtils.catPath(itemspace.getFileSystemEntry().getAccountAuthorization().getDownloadUrl(), "file/" + this.getItemspace().getName());
			url = URLUtils.catPath(url, this.getPath());
			url += "?Authorization=" + downloadauth.getAuthorizationToken();
		} catch (B2Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void listVersions(ObjectHandler<Integer, OSDVersionFileItem> event) {
		try {
			B2ListFileVersionsRequest request = B2ListFileVersionsRequest.builder(itemspace.getBucketId()).setStartFileName(this.getPath()).setPrefix(this.getPath()).build();
			Iterator<B2FileVersion> it = getB2Client().fileVersions(request).iterator();
			while (it.hasNext()) {
				B2FileVersion version = it.next();
				// System.out.println(version.toString());
				event.handle(ItemEvent.ITEM_FOUND, (OSDVersionFileItem) createItem(version));
			}

			event.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			event.exceptionCaught(null, e);
		}
	}

	@Override
	public void copy(OSDFileItem source) throws ServiceException {
		B2FileItem sourceFile = (B2FileItem) source;
		String targetBucketId = this.getItemspace().getBucketId();
		String targetKey = URLUtils.catPath(this.getParent().getPath(), this.getName());
		B2CopyFileRequest request = B2CopyFileRequest.builder(sourceFile.getFileId(), targetKey).setDestinationBucketId(targetBucketId).build();
		try {
			getB2Client().copySmallFile(request);
		} catch (B2Exception e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public String getVersionId() {
		return getFileId();
	}

	@Override
	public String getETag() {
		return this.getContentHash().getHashCode();
	}

	@Override
	public String getKey() {
		return this.getPath();
	}

	@Override
	public void setKey(String key) {
		this.setPath(key);
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			getB2Client().deleteFileVersion(getPath(), getFileId());
			return true;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean exists() {
		try {
			upateProperties();
			return true;
		} catch (Exception e) {
			// throw new ServiceException(e);
			return false;
		}
	}

	public void hide() throws ServiceException {
		try {
			getB2Client().hideFile(getItemspace().getBucketId(), this.getPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void upateProperties() throws ServiceException {
		B2FileVersion info;
		try {
			if (StringUtils.isNotEmpty(super.fileId)) {
				info = getB2Client().getFileInfo(super.fileId);
			} else {
				info = getB2Client().getFileInfoByName(this.getItemspace().getName(), this.getPath());
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}

		this.setSize(info.getContentLength());
		this.setLastUpdateTime(info.getUploadTimestamp());
		super.fileId = info.getFileId();
	}

	@Override
	public String getURL() {
		// Friendly URL:https://f000.backblazeb2.com/file/namespace1/fyqnicm2387910-p3_1516953614940.shtml
		// S3 URL:https://namespace1.s3.us-west-000.backblazeb2.com/fyqnicm2387910-p3_1516953614940.shtml
		// Native
		// URL:https://f000.backblazeb2.com/b2api/v1/b2_download_file_by_id?fileId=4_zf11e1311412be7327458041d_f100a9ba5d3d5f5ce_d20201120_m070325_c000_v0001075_t0051

		String url;
		try {
			url = URLUtils.catPath(itemspace.getFileSystemEntry().getAccountAuthorization().getDownloadUrl(), "file/" + this.getItemspace().getName());
		} catch (ServiceException e) {
			e.printStackTrace();
			OSDFileSystemEntryConfig config = (OSDFileSystemEntryConfig) this.getFileSystemEntry().getEntryConfig();
			url = (config.getProtocol().name().toLowerCase() + "://f000.backblazeb2.com/file/") + this.itemspace.getName() + "/";
		}
		url = URLUtils.catPath(url, this.getPath());

		return url;
	}

	@Override
	public ItemProperties getProperties() throws ServiceException {
		ItemProperties p = super.getProperties();

		B2FileVersion info;
		try {
			if (StringUtils.isNotEmpty(super.fileId)) {
				info = getB2Client().getFileInfo(super.fileId);
			} else {
				info = getB2Client().getFileInfoByName(this.getItemspace().getName(), this.getPath());
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		}
		p.add("extend:Action", StringUtils.nullToString(info.getAction(), "-"));
		p.add("extend:ContentMD5", StringUtils.nullToString(info.getContentMd5(), "-"));
		p.add("extend:ContentSha1", StringUtils.nullToString(info.getContentSha1(), "-"));
		p.add("extend:FileId", StringUtils.nullToString(info.getFileId(), "-"));
		p.add("extend:ContentType", StringUtils.nullToString(info.getContentType(), "-"));
		p.add("extend:ContentLength", StringUtils.nullToString(info.getContentLength(), "-"));
		p.add("extend:LargeFileSha1OrNull", StringUtils.nullToString(info.getLargeFileSha1OrNull(), "-"));
		p.add("extend:UploadTimestamp", StringUtils.nullToString(info.getUploadTimestamp(), "-"));

		Map<String, String> more = info.getFileInfo();
		if (more != null) {
			Iterator<String> it = more.keySet().iterator();
			while (it.hasNext()) {
				String name = (String) it.next();
				String value = more.get(name);
				p.add("extend:" + name, StringUtils.nullToString(value, "-"));
			}
		}
		return p;
	}

	@Override
	public String getOwner() {
		return null;
	}

}
