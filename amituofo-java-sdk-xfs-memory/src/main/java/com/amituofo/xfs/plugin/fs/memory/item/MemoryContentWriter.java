package com.amituofo.xfs.plugin.fs.memory.item;

import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.common.kit.thread.Interrupter;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystem;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;

public class MemoryContentWriter extends ContentWriterBase<MemoryFileItem> {
	private InterruptableInputStream sourcein = null;

	public MemoryContentWriter(MemoryFileItem file) {
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
		write(sourceItem.getContent(), -1, null);
	}

	@Override
	public void write(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		write(sourceItem.getContent(), -1, progressListener);
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		MemoryFileSystem memfs = fileitem.getMemoryFileSystem();
		try {
			long outlen;
			if (progressListener != null) {
				sourcein = new ProgressInputStream(in, progressListener);
			} else {
				sourcein = new InterruptableInputStream(in);
			}
			outlen = memfs.write(fileitem.getPath(), sourcein, length);

			fileitem.setSize(outlen);
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
