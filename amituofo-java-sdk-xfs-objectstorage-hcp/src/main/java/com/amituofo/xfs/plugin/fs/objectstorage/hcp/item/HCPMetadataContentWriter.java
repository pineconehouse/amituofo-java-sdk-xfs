package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.model.request.impl.PutMetadataRequest;

public class HCPMetadataContentWriter extends ContentWriterBase<HCPMetadataItem> {
	InputStream originalIn = null;

	public HCPMetadataContentWriter(HCPMetadataItem file) {
		super(file);
	}

	@Override
	public void abort() throws ServiceException {
		if (originalIn != null) {
			try {
				originalIn.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			originalIn = null;
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
			setMetadata(sourceItem.getContent(), progressListener);
		} catch (ServiceException e) {
			throw e;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		try {
			setMetadata(in, progressListener);
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private void setMetadata(InputStream in,
			ProgressListener<ReadProgressEvent, Integer> progressListener) throws IOException, InvalidResponseException, HSCException, ServiceException {

		if (fileitem instanceof HCPVersionMetadataItem) {
			throw new ServiceException("Metadata can not set in old version! " + fileitem.getKey() + " version:" + ((HCPVersionMetadataItem) fileitem).getVersionId());
		}

		try {
			this.originalIn = in;
			fileitem.getHcpClient().putMetadata(new PutMetadataRequest(fileitem.getKey(), fileitem.getName(), in));
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}

	}

}
