package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.io.InputStream;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.service.ContentWriter;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummary;
import com.hitachivantara.hcp.standard.model.request.impl.DeleteMetadataRequest;
import com.hitachivantara.hcp.standard.model.request.impl.GetMetadataRequest;
import com.hitachivantara.hcp.standard.model.request.impl.PutMetadataRequest;

public class HCPMetadataItem extends HCPFileItem {
	protected HCPMetadataSummary metaSummary;

	public HCPMetadataItem(HCPBucketspace rootitem, String key, HCPMetadataSummary metaSummary) {
		super(rootitem, key);
		this.metaSummary = metaSummary;
	}

	@Override
	public String getName() {
		return metaSummary.getName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public Long getSize() {
		return metaSummary.getSize();
	}

	public HCPMetadataSummary getMetadataSummary() {
		return metaSummary;
	}

	public void write(String metaName, InputStream in) throws ServiceException {
		try {
			getHcpClient().putMetadata(new PutMetadataRequest(getKey(), metaName, in));
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}
	}
	
	@Override
	public ContentWriter getContentWriter() {
		return new HCPMetadataContentWriter(this);
	}

	@Override
	public InputStream getContent() throws ServiceException {
		if (this.isFile()) {

			HCPMetadata meta;
			try {
				meta = getHcpClient().getMetadata(new GetMetadataRequest(getKey(), metaSummary.getName()));
			} catch (InvalidResponseException e) {
				throw new ServiceException(e.getMessage(), e);
			} catch (HSCException e) {
				throw new ServiceException(e);
			}

			InputStream in = meta.getContent();

			return in;
		}

		return null;
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			boolean deleted = getHcpClient().deleteMetadata(new DeleteMetadataRequest(getKey(), metaSummary.getName()));

			return deleted;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof HCPMetadataItem)) {
			return false;
		}

		return ((HCPMetadataItem) obj).getName().equals(this.getName());
	}

}
