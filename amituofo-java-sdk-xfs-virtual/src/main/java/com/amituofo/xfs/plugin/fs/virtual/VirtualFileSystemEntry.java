package com.amituofo.xfs.plugin.fs.virtual;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.RandomUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.virtual.item.VirtualItemBase;
import com.amituofo.xfs.plugin.fs.virtual.item.VirtualItemspace;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.MysqlVirtualFileSystemRootspace;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFSI;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemException;
import com.amituofo.xfs.plugin.fs.virtual.vfsi.VirtualFileSystemRootspace;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.Itemspace;
import com.amituofo.xfs.service.ItemspaceConfig;
import com.amituofo.xfs.service.impl.FileSystemEntryBase;
import com.amituofo.xfs.util.ItemStatistic;

public class VirtualFileSystemEntry extends FileSystemEntryBase<VirtualFileSystemEntryConfig, VirtualFileSystemPreference, VirtualItemspace> {
	private final static Map<String, VirtualFileSystemRootspace> vfsMap = new HashMap<String, VirtualFileSystemRootspace>();

	private VirtualFileSystemRootspace vfs = null;

	public VirtualFileSystemEntry(VirtualFileSystemEntryConfig entryConfig, VirtualFileSystemPreference preference) {
		super(entryConfig, preference);
	}

	private void init(String spacename) throws VirtualFileSystemException {
		String vfsId = entryConfig.getName();
		synchronized (vfsMap) {
			vfs = vfsMap.get(vfsId);
			if (vfs == null) {
				vfs = new MysqlVirtualFileSystemRootspace(this, spacename);
//				vfs = new MemoryFileSystemRootspace(this, spacename);
				vfs.init();
				vfsMap.put(vfsId, vfs);
			}
		}
	}

	@Override
	protected VirtualItemspace createDefaultItemspace() throws ServiceException {
		String spacename = "Space_0";
		try {
			init(spacename);
		} catch (VirtualFileSystemException e) {
			e.printStackTrace();
			throw new ServiceException(e);
		}

		// VirtualFSI rootFs = vfs.getVirtualFSI("/", ItemType.Directory);
		VirtualItemspace root = new VirtualItemspace(this, spacename);
		return root;
	}

	@Override
	protected List<VirtualItemspace> listAccessibleItemspaces() throws ServiceException {
		return null;
	}

	// @Override
	// public FileItem linkFile(RootItem rootitem, String fullpath) {
	// if (roots == null) {
	// init();
	// }
	//
	// String name = URLUtils.getLastNameFromPath(fullpath);
	// VirtualFSI fsi = vfs.getVirtualFSI(fullpath, ItemType.File);
	// VirtualFileItem file = new VirtualFileItem(entryConfig.getDefaultSection(), this, this.getPreference(), fsi, name);
	// return file;
	// }

	@Override
	public char getSeparatorChar() {
		return VirtualItemBase.SEPARATOR_CHAR;
	}

	@Override
	public void close() throws ServiceException {
		if (vfs != null) {
			vfs.close();
			String vfsId = entryConfig.getName();
			vfsMap.remove(vfsId);
		}
		super.close();
	}

	// @Override
	// public FolderItem linkFolder(RootItem rootitem, String fullpath) {
	// if (roots == null) {
	// init();
	// }
	//
	// VirtualFSI fsi = vfs.getVirtualFSI(fullpath, ItemType.Directory);
	// VirtualFolderItem file = new VirtualFolderItem(entryConfig.getDefaultSection(), this, this.getPreference(), fsi);
	// return file;
	// }

	@Override
	public boolean hasFeature(int featureId) {
		// TODO Auto-generated method stub
		return false;
	}

	public VirtualFileSystemRootspace getVirtualFileSystem() {
		return vfs;
	}

	public File getVirtualFileSystemLocation() {
		return getEntryConfig().getVirtualDataLocation();
	}

	public ItemStatistic generateFileItems() {
		ItemStatistic statistic = new ItemStatistic();
		try {
			getItemspaces();
			for (Itemspace memFolderItem : super.getItemspaces()) {
				statistic.sum(imagingVirtualFileItemsAtFolder(memFolderItem.getRootFolder().getPath(), entryConfig.getMaxFolderDeep()));
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return statistic;
	}

	private ItemStatistic imagingVirtualFileItemsAtFolder(String folderPath, int deepLevel) {
		ItemStatistic statistic=new ItemStatistic();
		VirtualFSI vf = vfs.getVirtualFSI(folderPath, ItemType.Directory);
		// MemFolderItem thisFolderItem = new MemFolderItem(this, vf);

		List<String> subFolders = new ArrayList<String>();

		int newDeepLevel = deepLevel - 1;
		if (newDeepLevel > 0) {
			int[] folderCountRange = getEntryConfig().getFolderCountRange();
			int count = RandomUtils.randomInt(folderCountRange[0], folderCountRange[1]);

			for (int i = 0; i < count; i++) {
				try {
					String subFoldername = RandomUtils.randomString(10);
					String subFolderpath = URLUtils.catPath(folderPath, subFoldername, getSeparatorChar());
					vf.newDirectory(subFoldername);
					statistic.sumFolderCount(1);
					subFolders.add(subFolderpath);
				} catch (VirtualFileSystemException e) {
					e.printStackTrace();
				}
			}
		}

		if (!folderPath.equals("/")) {
			int[] range = getEntryConfig().getFileCountRange();
			int count = RandomUtils.randomInt(range[0], range[1]);

			for (int i = 0; i < count; i++) {
				String filename = RandomUtils.randomString(10) + ".txt";
//				String filename = RandomUtils.randomChineseString(10) + ".txt";

				try {
//					filename = new String(filename.getBytes("ISO8859-1"));
					long[] sizeRange = getEntryConfig().getFilesizeInBytesRange();
					Long size = Long.valueOf(ThreadLocalRandom.current().nextLong(sizeRange[0], sizeRange[1]));
					vf.newFile(filename, size, System.currentTimeMillis());
					statistic.sumFileCount(1);
					statistic.sumFileSize(size);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		{
			for (String subFolderPath : subFolders) {
				statistic.sum(imagingVirtualFileItemsAtFolder(subFolderPath, --deepLevel));
			}
		}
		
		return statistic;
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
