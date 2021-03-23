package com.amituofo.xfs.plugin.fs.objectstorage.mqe;

import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDItemBase;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPClientFactory;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemEntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.mqe.item.MQEQueryRequest;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;
import com.hitachivantara.hcp.query.api.HCPQuery;
import com.hitachivantara.hcp.query.model.request.ObjectBasedQueryRequest;
import com.hitachivantara.hcp.query.model.request.QueryRequest;

public class MQEFileSystemEntry extends FileSystemEntryBase<MQEFileSystemEntryConfig, MQEFileSystemPreference, MQEQueryRequest> {
	private HCPQuery hcpQueryClient;
	private HCPFileSystemEntry hcpEntry;

	public MQEFileSystemEntry(MQEFileSystemEntryConfig entryConfig, MQEFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected MQEQueryRequest createDefaultItemspace() throws ServiceException {
		try {
			HCPFileSystemEntryConfig hcpEntryConfig = entryConfig;

			hcpEntry = entryConfig.createHCPFileSystemEntry();
			hcpEntry.open();

			this.hcpQueryClient = HCPClientFactory.getInstance().getHCPQuery(
					com.hitachivantara.core.http.Protocol.valueOf(hcpEntryConfig.getProtocol()),
					// hcpEntryConfig.getNamespace(),
					hcpEntryConfig.getEndpoint(),
					hcpEntryConfig.getAccesskey(),
					hcpEntryConfig.getSecretkey(),
					hcpEntryConfig.getClientConfiguration(),
					false);
		} catch (ServiceException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}

		QueryRequest queryRequest = ((MQEFileSystemEntryConfig) entryConfig).getQueryRequest();
		return createMQEQueryRequest(queryRequest);
		// MQEQueryRequest request = new MQEQueryRequest(this, hcpEntry, hcpQueryClient, queryRequest);
		// return request;
	}

	public MQEQueryRequest createMQEQueryRequest(QueryRequest query) throws ServiceException {
		MQEQueryRequest request = new MQEQueryRequest(this, hcpEntry, hcpQueryClient, query);

		return request;
	}

	public MQEQueryRequest createMQEQueryRequest(String query) throws ServiceException {
		ObjectBasedQueryRequest basedQueryRequest = MQEFileSystemEntryConfig.createBasedQueryRequest(query, 1000);
		return createMQEQueryRequest(basedQueryRequest);
	}

	@Override
	public char getSeparatorChar() {
		return OSDItemBase.SEPARATOR_CHAR;
	}

	@Override
	public boolean hasFeature(int featureId) {
		return false;
	}

	@Override
	protected List<MQEQueryRequest> listAccessibleItemspaces() throws ServiceException {
		return null;
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void deleteItemSpace(String name) throws ServiceException {
		// TODO Auto-generated method stub
		
	}

}
