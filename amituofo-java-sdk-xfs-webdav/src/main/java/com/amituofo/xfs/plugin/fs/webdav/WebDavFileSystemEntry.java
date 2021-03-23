package com.amituofo.xfs.plugin.fs.webdav;

import java.io.IOException;
import java.net.ProxySelector;
import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.webdav.item.WebDavItemBase;
import com.amituofo.xfs.plugin.fs.webdav.item.WebDavItemspace;
import com.amituofo.xfs.plugin.fs.webdav.util.SardineExtend;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

public class WebDavFileSystemEntry extends FileSystemEntryBase<WebDavFileSystemEntryConfig, WebDavFileSystemPreference, WebDavItemspace> {
	private Sardine sardine;

	public WebDavFileSystemEntry(WebDavFileSystemEntryConfig entryConfig, WebDavFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected WebDavItemspace createDefaultItemspace() throws ServiceException {
//		ProxySelector proxy = ProxySelector.getDefault();
		sardine = SardineFactory.begin(entryConfig.getUser(), entryConfig.getPassword());
//		sardine = new SardineExtend(entryConfig);
		WebDavItemspace root = new WebDavItemspace(this, "/");
		return root;
	}

	@Override
	protected List<WebDavItemspace> listAccessibleItemspaces() throws ServiceException {
		return null;
	}

	@Override
	public char getSeparatorChar() {
		return WebDavItemBase.SEPARATOR_CHAR;
	}

	@Override
	public void close() throws ServiceException {
		super.close();
		try {
			sardine.shutdown();
		} catch (IOException e) {
			e.printStackTrace();
			throw new ServiceException(e);
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

	public Sardine getSardine() {
		return sardine;
	}

}
