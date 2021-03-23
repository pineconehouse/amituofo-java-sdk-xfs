package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.item;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2.B2FileSystemPreference;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.backblaze.b2.client.contentSources.B2ContentSource;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2UploadFileRequest;
import com.backblaze.b2.client.structures.B2UploadFileRequest.Builder;
import com.backblaze.b2.util.B2ExecutorUtils;

public class B2ContentWriter extends ContentWriterBase<B2FileItem> {
	private InterruptableInputStream sourcein = null;

	public B2ContentWriter(B2FileItem file) {
		super(file);
	}

	@Override
	public void abort() {
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
		try {
			upload(sourceItem.getContent(), (long) sourceItem.getSize(), progressListener);
		} catch (B2Exception e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		try {
			upload(in, length, progressListener);
		} catch (B2Exception e) {
			throw new ServiceException(e);
		}
	}

	private void upload(InputStream in, long length, final ProgressListener<ReadProgressEvent, Integer> progressListener) throws B2Exception {
		if (in == null) {
			return;
		}

		if (progressListener != null) {
			// final LongCounter lastTimeReadBytes = new LongCounter();
			// builder.setListener(new B2UploadListener() {
			//
			// @Override
			// public void progress(B2UploadProgress progress) {
			// long readbytes = progress.getBytesSoFar()-lastTimeReadBytes.i;
			// System.out.println(progress.getState() + " " + progress.getLength() + " " + progress.getBytesSoFar()+" "+readbytes);
			// progressListener.progressChanged(ReadProgressEvent.BYTE_READING_EVENT, (int) readbytes);
			// }
			// });
			sourcein = new ProgressInputStream(in, progressListener);
		} else {
			sourcein = new InterruptableInputStream(in);
		}

		Builder builder = B2UploadFileRequest.builder(fileitem.getItemspace().getBucketId(), fileitem.getPath(), "application/octet-stream", new B2ContentSource() {

			@Override
			public Long getSrcLastModifiedMillisOrNull() throws IOException {
				return null;
			}

			@Override
			public String getSha1OrNull() throws IOException {
				return null;
			}

			@Override
			public long getContentLength() throws IOException {
				return length;
			}

			@Override
			public InputStream createInputStream() throws IOException, B2Exception {
				return sourcein;
			}
		});

		B2UploadFileRequest request = builder.build();

		B2FileSystemPreference preference = ((B2FileSystemPreference) fileitem.getOperationPreference());

		boolean forceLarge = preference.isEnabledMultipartUpload() && length > preference.getMinMultipartUploadFileSize();
		if (forceLarge || fileitem.getB2Client().getFilePolicy().shouldBeLargeFile(length)) {
			ExecutorService executor = Executors.newFixedThreadPool(preference.getMultipartThreadCount(), B2ExecutorUtils.createThreadFactory("xfs-%d"));
			fileitem.getB2Client().uploadLargeFile(request, executor);
		} else {
			fileitem.getB2Client().uploadSmallFile(request);
		}

		fileitem.setSize(length);
	}

}
