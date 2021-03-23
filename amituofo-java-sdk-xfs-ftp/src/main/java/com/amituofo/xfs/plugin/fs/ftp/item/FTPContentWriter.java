package com.amituofo.xfs.plugin.fs.ftp.item;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;

public class FTPContentWriter extends ContentWriterBase<FTPFileItem> {
	private InterruptableInputStream sourcein = null;

	public FTPContentWriter(FTPFileItem file) {
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
		write(sourceItem.getContent(), sourceItem.getSize());
	}

	@Override
	public void write(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		write(sourceItem.getContent(), sourceItem.getSize(), progressListener);
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		upload(fileitem.getPath(), in, progressListener);
	}

	private void upload(String targetPath, InputStream in, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		if (in == null) {
			return;
		}

		if (progressListener != null) {
			sourcein = new ProgressInputStream(in, progressListener);
		} else {
			sourcein = new InterruptableInputStream(in);
		}

		boolean isOK = false;
		FTPClient ftpClient = null;
		try {
			ftpClient = fileitem.pool.borrowObject();
			isOK = ftpClient.storeFile(fileitem.encodeFilename(targetPath), sourcein);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			try {
				sourcein.close();
			} catch (IOException e) {
			}
			// ftpClient.completePendingCommand();
			fileitem.pool.returnObject(ftpClient);

			if (!isOK) {
				throw new ServiceException("Failed to upload file " + targetPath);
			}

			// fileitem.setSize(Long.valueOf(in.available()));
			// XXX bug?
			// fileitem.upateProperties();
		}
	}

}
