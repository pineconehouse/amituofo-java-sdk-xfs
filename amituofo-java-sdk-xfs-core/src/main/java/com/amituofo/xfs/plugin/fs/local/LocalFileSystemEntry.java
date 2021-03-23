package com.amituofo.xfs.plugin.fs.local;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.local.item.LocalDriver;
import com.amituofo.xfs.plugin.fs.local.item.LocalFolderItem;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemFeatures;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;

public class LocalFileSystemEntry extends FileSystemEntryBase<LocalFileSystemEntryConfig, LocalFileSystemPreference, LocalDriver> {
	public final static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	public LocalFileSystemEntry(LocalFileSystemEntryConfig entryConfig) {
		super(entryConfig);
	}

	public LocalFileSystemEntry(LocalFileSystemEntryConfig entryConfig, LocalFileSystemPreference perference) {
		super(entryConfig, perference);
	}

	@Override
	public FileSystemEntry open() {
		try {
			return super.open();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected LocalDriver createDefaultItemspace() {
		File file = entryConfig.getRootFile();
		LocalDriver defaultItemspace = new LocalDriver(this, new LocalFileSystemPreference(entryConfig.getFileSystemView()), file);
		return defaultItemspace;
	}

	// @Override
	// public RootItem getRoot(String rootId) {
	// RootItem rootitem = new LocalRootItem(this, new File(entryConfig.getRootPath()));
	// return rootitem;
	// }

	@Override
	protected List<LocalDriver> listAccessibleItemspaces() throws ServiceException {
		List<LocalDriver> list = new ArrayList<LocalDriver>();

		// if (preference.getFileSystemView() == LocalFileSystemView.USER_FILE_SYSTEM_VIEW) {
		File[] viewRoots = new File[] { fileSystemView.getHomeDirectory() };

		for (int i = 0; i < viewRoots.length; i++) {
			File viewRoot = viewRoots[i];
			LocalDriver rootitem = new LocalDriver(this, new LocalFileSystemPreference(LocalFileSystemView.USER_FILE_SYSTEM_VIEW), viewRoot);
			list.add(rootitem);
		}
		// }

		File[] drivers = File.listRoots();
		for (int i = 0; i < drivers.length; i++) {
			File driver = drivers[i];
			LocalDriver rootitem = new LocalDriver(this, new LocalFileSystemPreference(LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW), driver);
			list.add(rootitem);
		}

		return list;
	}

	// @Override
	// public FileItem linkFile(String spaceId, String fullpath) {
	// File file = new File(fullpath);
	// if (spaceId == null) {
	// rootitem = getDefaultRoot();
	// }
	//
	// LocalFileItem root;
	// if (preference.getFileSystemView() == LocalFileSystemView.DEFAULT_FILE_SYSTEM_VIEW) {
	// root = new LazyLocalFileItem(rootitem, this, preference, file);
	// } else {
	// root = new LazyLocalFSVFileItem(rootitem, this, preference, file);
	// }
	//
	// return root;
	// }
	//
	// @Override
	// public FolderItem linkFolder(String spaceId, String fullpath) throws ServiceException {
	// return newFolderItemInstance(rootitem, fullpath);
	// }

	@Override
	public char getSeparatorChar() {
		return LocalFolderItem.SEPARATOR_CHAR;
	}

	@Override
	public boolean hasFeature(int featureId) {
		if (featureId == FileSystemFeatures.AUTO_CREATE_FOLDER) {
			return false;
		}

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

}
