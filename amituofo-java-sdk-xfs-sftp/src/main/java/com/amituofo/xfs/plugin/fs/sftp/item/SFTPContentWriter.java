package com.amituofo.xfs.plugin.fs.sftp.item;

import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.jcraft.jsch.ChannelSftp;

public class SFTPContentWriter extends ContentWriterBase<SFTPFileItem> {
	private InterruptableInputStream sourcein = null;

	public SFTPContentWriter(SFTPFileItem file) {
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
		write(sourceItem.getContent(), -1);
	}

	@Override
	public void write(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		write(sourceItem.getContent(), -1, progressListener);
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

		ChannelSftp ftpClient = null;
		try {
			ftpClient = fileitem.pool.borrowObject();
			ftpClient.put(sourcein, fileitem.encodeFilename(targetPath));
		} catch (Exception e) {
			throw new ServiceException("Failed to upload file " + targetPath, e);
		} finally {
			try {
				sourcein.close();
			} catch (IOException e) {
			}
			// ftpClient.completePendingCommand();
			fileitem.pool.returnObject(ftpClient);

//			fileitem.setSize(Long.valueOf(in.available()));
			// XXX bug?
//			fileitem.upateProperties();
		}
	}

}
