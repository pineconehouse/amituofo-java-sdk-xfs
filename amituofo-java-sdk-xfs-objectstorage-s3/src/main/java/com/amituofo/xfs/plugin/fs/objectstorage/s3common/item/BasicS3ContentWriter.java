package com.amituofo.xfs.plugin.fs.objectstorage.s3common.item;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.transfer.PersistableTransfer;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.services.s3.transfer.internal.S3ProgressListener;
import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.local.item.LocalFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemPreference;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;

public class BasicS3ContentWriter extends ContentWriterBase<BasicS3FileItem> {

	private TransferManager tm;
	private Upload multipartUpload = null;

	public BasicS3ContentWriter(BasicS3FileItem file) {
		super(file);
	}

	@Override
	public void abort() {
		// TODO Auto-generated method stub
		if (tm != null) {
			// 导致整个链接断开！！！
			// tm.shutdownNow();
			tm.shutdownNow(false);

			if (multipartUpload != null) {
				multipartUpload.abort();
				multipartUpload = null;
			}

			tm = null;
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
		try {
			upload(sourceItem, progressListener);
		} catch (AmazonServiceException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ServiceException(e);
		} catch (InterruptedException e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		try {
			upload(in, length, progressListener);
		} catch (AmazonClientException e) {
			throw new ServiceException(e);
		} catch (InterruptedException e) {
			throw new ServiceException(e);
		}
	}

	private void upload(InputStream in, long length, final ProgressListener<ReadProgressEvent, Integer> progressListener) throws AmazonClientException, InterruptedException {
		if (in == null) {
			return;
		}

		try {
			PutObjectRequest request = new PutObjectRequest(fileitem.getBucketName(), fileitem.getPath(), in, null);

			// Dec 29, 2018 12:19:24 PM com.amazonaws.services.s3.AmazonS3Client putObject
			// WARNING: No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory
			// errors.
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentLength(length);
			request.withMetadata(metadata);

			// if (progressListener != null) {
			// request.setGeneralProgressListener(new com.amazonaws.event.ProgressListener() {
			//
			// @Override
			// public void progressChanged(ProgressEvent progressEvent) {
			// // System.out.println(progressEvent.getEventType() + " " + progressEvent.getBytes());
			// switch (progressEvent.getEventType()) {
			// case REQUEST_BYTE_TRANSFER_EVENT:
			// progressListener.progressChanged(ReadProgressEvent.BYTE_READING_EVENT, (int) progressEvent.getBytes());
			// break;
			// case TRANSFER_COMPLETED_EVENT:
			// progressListener.progressChanged(ReadProgressEvent.BYTE_READ_END_EVENT, null);
			// break;
			// }
			// }
			//
			// });
			// }

			putObject(request, progressListener);
		} finally {
			// AWS will close stream auto
			// try {
			// in.close();
			// } catch (IOException e) {
			// }
		}
	}

	private void putObject(PutObjectRequest request,
			final ProgressListener<ReadProgressEvent, Integer> progressListener) throws AmazonServiceException, AmazonClientException, InterruptedException {
		// TODO ????????????
		// Dec 29, 2018 12:19:24 PM com.amazonaws.services.s3.AmazonS3Client putObject
		// WARNING: No content length specified for stream data. Stream contents will be buffered in memory and could result in out of memory
		// errors.
		// ObjectMetadata metadata = new ObjectMetadata();
		// metadata.setContentLength(file.length());
		// request.withMetadata(metadata);

		if (progressListener != null) {
			request.setGeneralProgressListener(new com.amazonaws.event.ProgressListener() {

				@Override
				public void progressChanged(ProgressEvent progressEvent) {
					// System.out.println(progressEvent.getEventType() + " " + progressEvent.getBytes());
					switch (progressEvent.getEventType()) {
						case REQUEST_BYTE_TRANSFER_EVENT:
							progressListener.progressChanged(ReadProgressEvent.BYTE_READING_EVENT, (int) progressEvent.getBytes());
							break;
						case TRANSFER_COMPLETED_EVENT:
							progressListener.progressChanged(ReadProgressEvent.BYTE_READ_END_EVENT, null);
							break;
					}
				}

			});
		}

		tm = TransferManagerBuilder.standard().withS3Client(fileitem.getS3Client()).build();
		// TransferManager processes all transfers asynchronously,
		// so this call returns immediately.
		Upload upload = tm.upload(request);

		// Optionally, you can wait for the upload to finish before continuing.
		upload.waitForCompletion();
	}

	private void upload(FileItem sourceItem,
			ProgressListener<ReadProgressEvent, Integer> progressListener) throws IOException, ServiceException, AmazonClientException, InterruptedException {
		if (sourceItem instanceof LocalFileItem) {
			File file = ((LocalFileItem) sourceItem).getFile();
			// If config > 5MB
			if (((BasicS3FileSystemPreference) fileitem.getOperationPreference()).isEnabledMultipartUpload()
					&& file.length() > ((BasicS3FileSystemPreference) fileitem.getOperationPreference()).getMinMultipartUploadFileSize()) {
				multipartUpload(file, progressListener);
			} else {
				upload(file, progressListener);
			}
		} else {
			// TODO
			upload(sourceItem.getContent(), sourceItem.getSize(), progressListener);
		}
	}

	private void upload(File file, final ProgressListener<ReadProgressEvent, Integer> progressListener) throws AmazonClientException, InterruptedException {
		if (file == null) {
			return;
		}

		PutObjectRequest request = new PutObjectRequest(fileitem.getBucketName(), fileitem.getPath(), file);

		StorageClass storageClass = ((BasicS3FileSystemPreference) fileitem.getOperationPreference()).getDefaultStorageClass();
		if (storageClass != null) {
			request.withStorageClass(storageClass);
		}

		putObject(request, progressListener);
	}

	// TODO
	private void multipartUpload(File file, final ProgressListener<ReadProgressEvent, Integer> progressListener) throws AmazonServiceException, IOException {
		long PART_SIZE = ((BasicS3FileSystemPreference) fileitem.getOperationPreference()).getMultipartSize();
		long MULTIPART_COPY_THRESHOLD_SIZE=(long) ((BasicS3FileSystemPreference) fileitem.getOperationPreference()).getMinMultipartUploadFileSize();
		tm = TransferManagerBuilder.standard().withS3Client(fileitem.getS3Client())
				.withMultipartCopyThreshold(MULTIPART_COPY_THRESHOLD_SIZE)
				.withMultipartCopyPartSize(PART_SIZE)
				.withMinimumUploadPartSize(PART_SIZE)
				.withMinimumUploadPartSize(PART_SIZE).build();

		// final LongCounter filelength = new LongCounter(file.length());
		// TransferManager processes all transfers asynchronously,
		// so this call returns immediately.
		PutObjectRequest putObjectRequest = new PutObjectRequest(fileitem.getBucketName(), fileitem.getPath(), file);

		StorageClass storageClass = ((BasicS3FileSystemPreference) fileitem.getOperationPreference()).getDefaultStorageClass();
		if (storageClass != null) {
			putObjectRequest.withStorageClass(storageClass);
		}

		S3ProgressListener awsprogressListener = new S3ProgressListener() {

			@Override
			public void progressChanged(ProgressEvent progressEvent) {
				// System.out.println(progressEvent.getEventType() + " " + progressEvent.getBytes());
				switch (progressEvent.getEventType()) {
					case REQUEST_BYTE_TRANSFER_EVENT:
						int transferSize = (int) progressEvent.getBytes();
						// filelength.i -= transferSize;
						// 总是会大于实际文件大小一些？？！，此处处理避免多算长度
						// if (filelength.i > 0) {
						progressListener.progressChanged(ReadProgressEvent.BYTE_READING_EVENT, transferSize);
						// }
						break;
					case TRANSFER_COMPLETED_EVENT:
						progressListener.progressChanged(ReadProgressEvent.BYTE_READ_END_EVENT, null);
						break;
				}
				// System.out.println(progressEvent.getEventType() + " progressEvent.getBytes()=" + progressEvent.getBytes());
			}

			@Override
			public void onPersistableTransfer(PersistableTransfer persistableTransfer) {
				// TODO Auto-generated method stub
			}
		};

		multipartUpload = tm.upload(putObjectRequest, awsprogressListener);
		System.out.println("Object multipart upload started.  " + file + " to " + fileitem.getPath());

		// Optionally, wait for the upload to finish before continuing.
		try {
			multipartUpload.waitForCompletion();
		} catch (AmazonClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
