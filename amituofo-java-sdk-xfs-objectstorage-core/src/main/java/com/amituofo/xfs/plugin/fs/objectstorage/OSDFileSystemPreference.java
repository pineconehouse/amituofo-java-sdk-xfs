package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.xfs.service.impl.FileSystemPreferenceBase;

public class OSDFileSystemPreference extends FileSystemPreferenceBase {
	private boolean contentVerify;
	private boolean enabledMultipartUpload;
	private int multipartThreadCount;
	private long enableMultipartUploadWhenFileSizeLargerThan;
	private long multipartSize;// 1024 * 1024 * 100;// 100MB
	private boolean listByVersions;

	public OSDFileSystemPreference() {
		contentVerify = true;
		enabledMultipartUpload = false;
		multipartThreadCount = 6;
		enableMultipartUploadWhenFileSizeLargerThan = Long.MAX_VALUE;
		multipartSize = 100 * 1024 * 1024L;// 1024 * 1024 * 100;// 100MB
		listByVersions = false;
		// contentVerify = Config.getInstance().getBoolean(ConfigKeys.OSD_VALIDATE_UPLOAD_CONTENT);
		// enabledMultipartUpload = Config.getInstance().getBoolean(ConfigKeys.OSD_ENABLE_MULTIPART_UPLOAD);
		// multipartThreadCount = Config.getInstance().getInt(ConfigKeys.OSD_MAX_MULTIPART_UPLOAD_THREADS);
		// enableMultipartUploadWhenFileSizeLargerThan =
		// Config.getInstance().getLong(ConfigKeys.OSD_ENABLE_MULTIPART_UPLOAD_WHEN_FILE_SIZE_LARGER_THAN_xMB) * 1024 * 1024L;
		// multipartSize = Config.getInstance().getLong(ConfigKeys.OSD_MULTIPART_UPLOAD_PART_SIZE_xMB) * 1024 * 1024L;// 1024 * 1024 * 100;// 100MB
	}

	public boolean listByVersions() {
		return listByVersions;
	}

	public void setListByVersions(boolean listByVersions) {
		this.listByVersions = listByVersions;
	}

	public boolean isVerifyPutObjectContent() {
		return contentVerify;
	}

	public void setVerifyPutObjectContent(boolean contentVerify) {
		this.contentVerify = contentVerify;

	}

	public boolean isEnabledMultipartUpload() {
		return enabledMultipartUpload;
	}

	public void setEnabledMultipartUpload(boolean enabledMultipartUpload) {
		this.enabledMultipartUpload = enabledMultipartUpload;
	}

	public int getMultipartThreadCount() {
		return multipartThreadCount;
	}

	public void setMultipartThreadCount(int multipartThreadCount) {
		this.multipartThreadCount = multipartThreadCount;
	}

	public long getMinMultipartUploadFileSize() {
		return enableMultipartUploadWhenFileSizeLargerThan;
	}

	public void setMinMultipartUploadFileSize(long enableMultipartUploadWhenFileSizeLargerThan) {
		this.enableMultipartUploadWhenFileSizeLargerThan = enableMultipartUploadWhenFileSizeLargerThan;
	}

	public long getMultipartSize() {
		return multipartSize;
	}

	public void setMultipartSize(long multipartSize) {
		this.multipartSize = multipartSize;
	}

}
