package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.io.InputStream;

import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadata;
import com.hitachivantara.hcp.standard.model.metadata.HCPMetadataSummary;
import com.hitachivantara.hcp.standard.model.request.impl.GetMetadataRequest;

public class HCPVersionMetadataItem extends HCPMetadataItem {
	private String versionId;

	public HCPVersionMetadataItem(HCPBucketspace namespace, String key, String versionId, HCPMetadataSummary metaSummary) {
		super(namespace, key, metaSummary);
		this.versionId = versionId;
	}

	public void write(String metaName, InputStream in) throws ServiceException {
		throw new ServiceException("Unsupport modify or append metadata to history version!");
	}

	@Override
	public InputStream getContent() throws ServiceException {
		HCPMetadata meta;
		try {
			meta = getHcpClient().getMetadata(new GetMetadataRequest(getKey(), metaSummary.getName()).withVersionId(versionId));
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (HSCException e) {
			throw new ServiceException(e);
		}

		InputStream in = meta.getContent();

		return in;
	}
	
	@Override
	public boolean delete() throws ServiceException {
		throw new ServiceException("Unsupport delete metadata from history version!");
	}
}
