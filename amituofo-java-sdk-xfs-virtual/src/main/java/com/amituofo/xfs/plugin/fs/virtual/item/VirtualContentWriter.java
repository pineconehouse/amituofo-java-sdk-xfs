package com.amituofo.xfs.plugin.fs.virtual.item;

import java.io.InputStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.thread.Interrupter;
import com.amituofo.common.util.StreamUtils;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFSI;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.FolderItem;

public class VirtualContentWriter extends ContentWriterBase<VirtualFileItem> {
	protected final VirtualFSI memVirtualFSI;
	Interrupter interrupter = new Interrupter();
	private final static String FLAG = "1";

	public VirtualContentWriter(VirtualFileItem file, VirtualFSI memVirtualFSI) {
		super(file);
		this.memVirtualFSI = memVirtualFSI;
	}

	@Override
	public void abort() throws ServiceException {
		interrupter.setInterrupted(true);
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
		synchronized (FLAG) {
			// System.out.println(this+"");
			FolderItem parent = fileitem.getParent();
			mkdir(parent);
		}

		long len = StreamUtils.inputStream2None(in, progressListener, interrupter);
		try {
			// memVirtualFSI.newFile(fileitem.getName(), len, System.currentTimeMillis());
			memVirtualFSI.setFileLength(fileitem.getName(), len);
			fileitem.setSize(len);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	private void mkdir(FolderItem folder) throws ServiceException {
		if (!folder.exists()) {
			mkdir(folder.getParent());
			folder.createDirectory();
		}
	}

}
