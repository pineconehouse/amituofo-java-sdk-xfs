package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;

public class LazyLocalFSVFolderItem extends LazyLocalFolderItem {
	private final static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	public LazyLocalFSVFolderItem(LocalDriver itemspace, File file) {
		super(itemspace, file);
	}

//	@Override
//	public FolderItem[] getRoots() throws ServiceException {
//		File[] viewRoots = new File[] { fileSystemView.getHomeDirectory() };
//		File[] drivers = File.listRoots();
//		LocalFolderItem[] roots = new LazyLocalFolderItem[drivers.length + viewRoots.length];
//		int i = 0;
//		for (; i < viewRoots.length; i++) {
//			File viewRoot = viewRoots[i];
//			RootItem rootitem = ItemUtils.toSection(viewRoot, false);
//			LazyLocalFSVFolderItem root = new LazyLocalFSVFolderItem(rootitem, (LocalFileSystemEntry) fileSystemEntry, preference, viewRoot);
//			root.setName(viewRoot.getName());
//			// root.setData(driver);
//			// root.setLastUpdateTime(null);
//			// root.setCreateTime(null);
//			root.setPath(viewRoot.getPath());
//			// root.setSize(null);
//			// root.setType(ItemType.Directory);
//			// root.setParent(null);
//
//			roots[i] = root;
//		}
//
//		i = 0;
//		for (; i < drivers.length; i++) {
//			File driver = drivers[i];
//			RootItem rootitem = ItemUtils.toSection(driver, true);
//			LazyLocalFolderItem root = new LazyLocalFolderItem(rootitem, (LocalFileSystemEntry) fileSystemEntry, preference, driver);
//			root.setName(driver.getPath());
//			// root.setData(driver);
//			// root.setLastUpdateTime(null);
//			// root.setCreateTime(null);
//			root.setPath(driver.getPath());
//			// root.setSize(null);
//			// root.setParent(null);
//
//			roots[i + viewRoots.length] = root;
//		}
//
//		return roots;
//	}

	@Override
	public String getName() {
		// System.out.println("-------------------------------------------------");
		// System.out.println("file="+file);
		// System.out.println("isDrive="+fileSystemView.isDrive(file));
		// System.out.println("isFileSystem="+fileSystemView.isFileSystem(file));
		// System.out.println("isFileSystemRoot="+fileSystemView.isFileSystemRoot(file));
		// System.out.println("isRoot="+fileSystemView.isRoot(file));
		// System.out.println("isTraversable="+fileSystemView.isTraversable(file));
		// System.out.println("isComputerNode="+fileSystemView.isComputerNode(file));
		// System.out.println("getSystemTypeDescription="+fileSystemView.getSystemTypeDescription(file));
		// System.out.println("getSystemDisplayName="+fileSystemView.getSystemDisplayName(file));

		String val = super.name;
		if (val == null) {
			val = fileSystemView.getSystemDisplayName(file);
			// Will be empty when root D:\\ C:\\
			if (StringUtils.isEmpty(val)) {
				val = file.getPath();
			}
			super.setName(val);
		}

		return val;
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		// -------------------------------------------
		File[] files = fileSystemView.getFiles(file, false);
		for (File file : files) {
			Item item = itemspace.newItemInstance(file);
			
			if (filter != null && !filter.accept(item)) {
				continue;
			}

			HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
			if (ef == HandleFeedback.interrupted) {
				handler.handle(ItemEvent.EXEC_END, null);
				return;
			}
		}
		// -------------------------------------------

		handler.handle(ItemEvent.EXEC_END, null);
	}
	
	@Override
	public String getExt() {
		int i = getPath().lastIndexOf('.');
		if (i != -1) {
			return getPath().substring(i + 1);
		}
		
		getName();
		return super.getExt();
	}

//	@Override
//	public Item clone() {
//		LazyLocalFSVFolderItem clone = new LazyLocalFSVFolderItem(itemspace, (LocalFileSystemEntry) fileSystemEntry, preference, file);
//		// clone.setParent(this.getParent());
//		// clone.setCatelog(this.getCatelog());
//		// clone.setName(this.getName());
//		// clone.setActualPath(this.getActualPath());
//		// // clone.setData(this.getData());
//		// // clone.setType(this.getType());
//		// clone.setSize(this.getSize());
//		// clone.setCreateTime(this.getCreateTime());
//		// clone.setLastUpdateTime(this.getLastUpdateTime());
//		return clone;
//	}

//	@Override
//	protected Item toItem(File file) {
//		LocalItemBase item;
//		if (file.isDirectory()) {
//			item = new LazyLocalFSVFolderItem(itemspace, (LocalFileSystemEntry) fileSystemEntry, preference, file);
//		} else {
//			item = new LazyLocalFSVFileItem(itemspace, (LocalFileSystemEntry) fileSystemEntry, preference, file);
//		}
//
//		// item.setName(file.getName());
//		// item.setSize(file.length());
//		// item.setLastUpdateTime(file.lastModified());
//		// item.setCreateTime(null);
//		// item.setActualPath(file.getPath());
//		// item.setData(file);
//		// item.setParent(null);
//
//		return item;
//	}
}
