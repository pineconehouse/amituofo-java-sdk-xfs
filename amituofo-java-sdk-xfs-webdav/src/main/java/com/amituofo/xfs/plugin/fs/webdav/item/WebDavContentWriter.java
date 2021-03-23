package com.amituofo.xfs.plugin.fs.webdav.item;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.InputStreamEntity;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.common.kit.thread.Interrupter;
import com.amituofo.xfs.plugin.fs.webdav.WebDavFileSystemEntry;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;

public class WebDavContentWriter extends ContentWriterBase<WebDavFileItem> {
	private InterruptableInputStream sourcein = null;
	private Sardine sardine;

	public WebDavContentWriter(WebDavFileItem file) {
		super(file);
		sardine = ((WebDavFileSystemEntry) file.getFileSystemEntry()).getSardine();
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
		write(sourceItem.getContent(), -1, null);
	}

	@Override
	public void write(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		write(sourceItem.getContent(), -1, progressListener);
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		Sardine sardine = ((WebDavFileSystemEntry) fileitem.getFileSystemEntry()).getSardine();
		try {
			if (progressListener != null) {
				sourcein = new ProgressInputStream(in, progressListener);
			} else {
				sourcein = new InterruptableInputStream(in);
			}

			BufferedHttpEntity entity = new BufferedHttpEntity(new InputStreamEntity(sourcein, length));
			((SardineImpl) sardine).put(fileitem.getPathUrl(), entity, "binary/octet-stream", true);
			// sardine.put(fileitem.getPathUrl(), sourcein, "binary/octet-stream", false, length);
			// sardine.put(fileitem.getPathUrl(), sourcein, length);
			fileitem.setSize(length);
			fileitem.setLastUpdateTime(System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	// private void mkdir(FolderItem folder) throws ServiceException {
	// if (!folder.exists()) {
	// mkdir(folder.getParent());
	// folder.createDirectory();
	// }
	// }

}
