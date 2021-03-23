package com.amituofo.xfs.plugin.fs.hdfs.item;

import java.io.InputStream;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.common.kit.thread.Interrupter;
import com.amituofo.common.util.StreamUtils;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;

public class HadoopHDFSContentWriter extends ContentWriterBase<HadoopHDFSFileItem> {
	private Interrupter interrupter = new Interrupter();

	public HadoopHDFSContentWriter(HadoopHDFSFileItem file) {
		super(file);
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
		FileSystem memfs = fileitem.getHadoopFileSystem();
		try {
			FSDataOutputStream out;
			InputStream sourcein;
			if (progressListener != null) {
				// out = memfs.create(fileitem.getFilePath(), true, 1024 * 10, new Progressable() {
				//
				// @Override
				// public void progress() {
				// System.out.println("1");
				// progressListener.progressChanged(ReadProgressEvent.BYTE_READING_EVENT, 1);
				// }
				// });
				sourcein = new ProgressInputStream(in, progressListener);
				out = memfs.create(fileitem.getFilePath(), true);
			} else {
				sourcein = in;
				out = memfs.create(fileitem.getFilePath(), true);
			}

			long outlen = StreamUtils.inputStream2OutputStream(sourcein, true, out, true, interrupter);
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
