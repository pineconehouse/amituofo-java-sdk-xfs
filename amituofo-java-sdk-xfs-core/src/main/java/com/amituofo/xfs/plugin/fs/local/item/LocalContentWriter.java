package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.common.kit.io.RandomFileWriter;
import com.amituofo.common.util.StreamUtils;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;

public class LocalContentWriter extends ContentWriterBase<LocalFileItem> {
	private RandomFileWriter fw = null;
	private int bufferSize = RandomFileWriter.DEFAULT_BUFFER_SIZE;

	public LocalContentWriter(LocalFileItem file) {
		super(file);
		
//		file.getFileSystemEntry().getPreference().get
	}

	@Override
	public void abort() {
		if (fw != null) {
			fw.interrupt();
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
		try {
			if (progressListener != null) {
				ProgressInputStream pin = new ProgressInputStream(in, progressListener);
				writeFile(pin);
			} else {
				writeFile(in);
			}
			
			// 清空元数据
			fileitem.setSize(null);
			fileitem.setLastUpdateTime(null);
			fileitem.setCreateTime(null);
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	private void writeFile(InputStream in) throws IOException {
		File target = fileitem.getFile();

		// FileUtils.copyStreamToFile(in, target, bufferSize);
		fw = new RandomFileWriter(target, 0, in);
		fw.setBufferSize(bufferSize);
		fw.write();

		// AsynchronousFileWriter fw = new AsynchronousFileWriter(target, 0, 0, srcIn);
		//// fw.setBufferSize(partBufferSize);
		//// fw.setListener(listener);
		// fw.waitForComplete(true);
		// fw.write();

		// return fw.getWriteLength();

		// byte[] b = new byte[1024*4];
		// int n = srcIn.read(b);
		// while(n>0) {
		// n = srcIn.read(b);
		// }
	}

}
