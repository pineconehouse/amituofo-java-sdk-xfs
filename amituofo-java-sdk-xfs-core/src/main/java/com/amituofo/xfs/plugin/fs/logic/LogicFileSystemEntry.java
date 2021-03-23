package com.amituofo.xfs.plugin.fs.logic;

import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.logic.item.LogicItemspace;
import com.amituofo.xfs.service.FileSystemConfig;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.Itemspace;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;

public class LogicFileSystemEntry extends FileSystemEntryBase<LogicFileSystemEntryConfig, LogicFileSystemPreference, Itemspace> {

	private ItemLister lister;

	public LogicFileSystemEntry(LogicFileSystemEntryConfig entryConfig, LogicFileSystemPreference preference, ItemLister lister) {
		super(entryConfig, preference);
		this.lister = lister;
	}

	@Override
	protected Itemspace createDefaultItemspace() throws ServiceException {
		return new LogicItemspace(this, lister);
	}

	@Override
	public char getSeparatorChar() {
		return FileSystemConfig.URL_PS;
	}

	@Override
	public boolean hasFeature(int featureId) {
		return false;
	}

	@Override
	protected List<Itemspace> listAccessibleItemspaces() throws ServiceException {
		return null;
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
