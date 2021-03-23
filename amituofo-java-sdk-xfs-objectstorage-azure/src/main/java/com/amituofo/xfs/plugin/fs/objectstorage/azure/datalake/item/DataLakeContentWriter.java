package com.amituofo.xfs.plugin.fs.objectstorage.azure.datalake.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.xfs.plugin.fs.local.item.LocalFileItem;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.azure.storage.common.ParallelTransferOptions;
import com.azure.storage.common.ProgressReceiver;
import com.azure.storage.file.datalake.DataLakeFileClient;

public class DataLakeContentWriter extends ContentWriterBase<DataLakeFileItem> {
	private InterruptableInputStream sourcein = null;

	public DataLakeContentWriter(DataLakeFileItem file) {
		super(file);
	}

	@Override
	public void abort() throws ServiceException {
		if (sourcein != null) {
			try {
				sourcein.abort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void write(InputStream in, long length) throws ServiceException {
		write(in, length, null);
	}

	@Override
	public void write(FileItem sourceItem) throws ServiceException {
		write(sourceItem, null);
	}

	@Override
	public void write(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		upload(sourceItem, progressListener);
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		upload(in, length, progressListener);
	}

	private void upload(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		if (sourceItem instanceof LocalFileItem) {
			File file = ((LocalFileItem) sourceItem).getFile();
			// If config > 5MB
			// HCPFileSystemPreference preference = fileitem.getItemspace().getFileSystemPreference();
			// if (preference.isEnabledMultipartUpload() && file.length() > preference.getMinMultipartUploadFileSize()) {
			// multipartUpload(file, progressListener);
			// } else {
			// upload(file.getPath(), file.length(), progressListener);
			FileInputStream in;
			try {
				in = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				throw new ServiceException(e);
			}
			upload(in, file.length(), progressListener);
			// }
		} else {
			upload(sourceItem.getContent(), sourceItem.getSize(), progressListener);
		}
	}

	// private void upload(File file, ProgressListener<ReadProgressEvent, Integer> progressListener) throws InvalidResponseException,
	// HSCException {
	// BlobClient client = fileitem.getBlobContainerClient().getBlobClient(fileitem.getPath());
	// client.uploadFromFileWithResponse(
	// new BlobUploadFromFileOptions(file.getPath()).setParallelTransferOptions(new ParallelTransferOptions().setProgressReceiver(new
	// ProgressReceiver() {
	//
	// @Override
	// public void reportProgress(long bytesTransferred) {
	// progressListener.progressChanged(ReadProgressEvent.BYTE_READING_EVENT, (int) bytesTransferred);
	//
	// }
	// })),
	// null,
	// null);
	// progressListener.progressChanged(ReadProgressEvent.BYTE_READ_END_EVENT, 0);
	// }

//	private void upload(String filepath, long length, final ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
//		try {
//
//			DataLakeFileClient client = fileitem.getContainerClient().getFileClient(fileitem.getPath());
//			if (progressListener != null) {
//				client.uploadFromFile(filepath, new ParallelTransferOptions().setProgressReceiver(new ProgressReceiver() {
//
//					@Override
//					public void reportProgress(long bytesTransferred) {
//						// System.out.println(bytesTransferred);
//						// 不被调用？？？
//						progressListener.progressChanged(ReadProgressEvent.BYTE_READING_EVENT, (int) bytesTransferred);
//					}
//				}), null, null, null, null);
//				progressListener.progressChanged(ReadProgressEvent.BYTE_READ_END_EVENT, (int) 0);
//			} else {
//				client.uploadFromFile(filepath, true);
//			}
//
//			fileitem.setSize(length);
//		} catch (Exception e) {
//			throw new ServiceException(e);
//		}
//	}

	private void upload(InputStream in, long length, final ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		if (in == null) {
			return;
		}

		try {
			if (progressListener != null) {
				sourcein = new ProgressInputStream(in, progressListener);
			} else {
				sourcein = new InterruptableInputStream(in);
			}

			DataLakeFileClient client = fileitem.getContainerClient().getFileClient(fileitem.getPath());
			client.create(true);
			// 0 size 文件不予写入
			if (length > 0) {
				client.append(sourcein, 0, length);
				client.flush(length);
			}
			fileitem.setSize(length);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

}
