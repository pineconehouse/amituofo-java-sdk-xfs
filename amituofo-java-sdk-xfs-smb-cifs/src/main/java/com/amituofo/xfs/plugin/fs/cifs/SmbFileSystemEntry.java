package com.amituofo.xfs.plugin.fs.cifs;

import java.io.IOException;
import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.cifs.item.SmbItemBase;
import com.amituofo.xfs.plugin.fs.cifs.item.SmbSharespace;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;
import com.hierynomus.mserref.NtStatus;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.SmbConfig;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

public class SmbFileSystemEntry extends FileSystemEntryBase<SmbFileSystemEntryConfig, SmbFileSystemPreference, SmbSharespace> {

	private Connection connection;
	private Session session;

	public SmbFileSystemEntry(SmbFileSystemEntryConfig entryConfig, SmbFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected SmbSharespace createDefaultItemspace() throws ServiceException {
		SmbConfig config;
		// config = SmbConfig.builder().withSigningRequired(false).build();
		config = SmbConfig.createDefaultConfig();
		try {
			AuthenticationContext ac;
			SMBClient client = new SMBClient(config);
			connection = client.connect(entryConfig.getHost());
			// ac = AuthenticationContext.guest();//.anonymous();
			ac = new AuthenticationContext(entryConfig.getUser(), entryConfig.getPassword().toCharArray(), entryConfig.getDomain());
			session = connection.authenticate(ac);
			DiskShare share = (DiskShare) session.connectShare(entryConfig.getSharename());
			SmbSharespace root = new SmbSharespace(this, entryConfig.getSharename(), share);
			return root;
		} catch (SMBApiException e) {
			if (e.getStatus() == NtStatus.STATUS_LOGON_FAILURE || e.getStatus() == NtStatus.STATUS_LOGON_TYPE_NOT_GRANTED || e.getStatus() == NtStatus.STATUS_ACCESS_DENIED) {
				throw new ServiceException("Authentication failed! Please confirm user name and password!", e);
			}
			if (e.getStatus() == NtStatus.STATUS_BAD_NETWORK_NAME) {
				throw new ServiceException("Sharename incorrent!", e);
			}
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e1) {
			throw new ServiceException(e1);
		}
	}

	@Override
	protected List<SmbSharespace> listAccessibleItemspaces() throws ServiceException {
		return null;
	}

	@Override
	public char getSeparatorChar() {
		return SmbItemBase.SEPARATOR_CHAR;
	}

	@Override
	public void close() throws ServiceException {
		super.close();

		if (session != null) {
			try {
				session.close();
				session = null;
			} catch (IOException e) {
				throw new ServiceException(e);
			}
		}
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (IOException e) {
				throw new ServiceException(e);
			}
		}
		
	}

	@Override
	public boolean hasFeature(int featureId) {
		return false;
	}

	@Override
	public void createItemSpace(ItemspaceConfig config) throws ServiceException {
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void deleteItemSpace(String name) throws ServiceException {
	}

}
