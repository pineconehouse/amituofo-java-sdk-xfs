package com.amituofo.xfs.plugin.fs.memory;

import java.lang.reflect.Constructor;
import java.util.List;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystem;
import com.amituofo.xfs.plugin.fs.memory.filesystem.MemoryFileSystemException;
import com.amituofo.xfs.plugin.fs.memory.item.MemoryItemBase;
import com.amituofo.xfs.plugin.fs.memory.item.MemoryItemspace;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;

public class MemoryFileSystemEntry extends FileSystemEntryBase<MemoryFileSystemEntryConfig, MemoryFileSystemPreference, MemoryItemspace> {
	private MemoryFileSystem memoryFileSystem;

	public MemoryFileSystemEntry(MemoryFileSystemEntryConfig entryConfig, MemoryFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected MemoryItemspace createDefaultItemspace() throws ServiceException {
		String spacename = "MemorySpace-1";

		Class<? extends MemoryFileSystem> fsc = entryConfig.getMemoryFileSystem();
		try {
			Constructor<? extends MemoryFileSystem> c = fsc.getConstructor(MemoryFileSystemEntry.class);
			memoryFileSystem = c.newInstance(this);
			memoryFileSystem.init();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}

		MemoryItemspace root = new MemoryItemspace(this, spacename);
		return root;
	}

	@Override
	protected List<MemoryItemspace> listAccessibleItemspaces() throws ServiceException {
		return null;
	}

	@Override
	public char getSeparatorChar() {
		return MemoryItemBase.SEPARATOR_CHAR;
	}

	@Override
	public void close() throws ServiceException {
		super.close();
		try {
			memoryFileSystem.close();
		} catch (MemoryFileSystemException e) {
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
		// TODO Auto-generated method stub
	}

	@Override
	public void modifyItemSpace(ItemspaceConfig itemspaceConfig) throws ServiceException {
		
	}
	
	@Override
	public void deleteItemSpace(String name) throws ServiceException {
		// TODO Auto-generated method stub
	}

	public MemoryFileSystem getDefaultMemoryFileSystem() {
		return memoryFileSystem;
	}

}
