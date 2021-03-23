package com.amituofo.xfs.plugin.fs.cifs.item;

import static com.hierynomus.mssmb2.SMB2ShareAccess.ALL;
import static java.util.EnumSet.of;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.common.kit.thread.Interrupter;
import com.amituofo.common.util.StreamUtils;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

public class SmbContentWriter extends ContentWriterBase<SmbFileItem> {
	private Interrupter interrupter = new Interrupter();
	// private InterruptableInputStream sourcein = null;
	// private File file = null;
	// private OutputStream out;

	public SmbContentWriter(SmbFileItem file) {
		super(file);
	}

	@Override
	public void abort() throws ServiceException {
		// interrupter.setInterrupted(true);
		// if (sourcein != null && file != null) {
		// try {
		// sourcein.abort();
		// sourcein.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// try {
		// out.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// file.closeNoWait();
		// file = null;
		// }
		// }
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
		// if (interrupter.isInterrupted()) {
		// return;
		// }

		DiskShare diskShare = fileitem.getDiskShare();
		File file=null;
		InputStream sourcein;
		try {
			if (progressListener != null) {
				sourcein = new ProgressInputStream(in, progressListener);
			} else {
				sourcein = in;
				// sourcein = new InterruptableInputStream(in);
			}

			// Set<AccessMask> accessMask = new HashSet<AccessMask>();
			// accessMask.add(AccessMask.GENERIC_ALL);
			// Set<FileAttributes> attributes = null;
			// Set<SMB2ShareAccess> shareAccesses = new HashSet<SMB2ShareAccess>();
			// shareAccesses.add(SMB2ShareAccess.FILE_SHARE_WRITE);
			// SMB2CreateDisposition createDisposition = SMB2CreateDisposition.FILE_CREATE;
			// Set<SMB2CreateOptions> createOptions = null;
			// file = diskShare.openFile(fileitem.getPath(), accessMask, attributes, shareAccesses, createDisposition, createOptions);
			file = diskShare.openFile(fileitem.getPath(), of(AccessMask.GENERIC_WRITE), null, ALL, SMB2CreateDisposition.FILE_CREATE, null);
			OutputStream out = file.getOutputStream();
			long outlen = StreamUtils.inputStream2OutputStream(sourcein, true, out, true, interrupter);
			fileitem.setSize(outlen);
			fileitem.setLastUpdateTime(System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		} finally {
			if (file != null) {
				file.flush();
				file.close();
			}
		}
	}

}
