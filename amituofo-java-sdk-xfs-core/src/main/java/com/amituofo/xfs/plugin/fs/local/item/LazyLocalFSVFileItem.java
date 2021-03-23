package com.amituofo.xfs.plugin.fs.local.item;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

public class LazyLocalFSVFileItem extends LazyLocalFileItem {
//	private final static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	public LazyLocalFSVFileItem(LocalDriver itemspace, File file) {
		super(itemspace, file);
	}

//	@Override
//	public FolderItem[] getRoots() throws ServiceException {
//		File[] viewRoots = fileSystemView.getRoots();
//		File[] drivers = File.listRoots();
//		LocalFolderItem[] roots = new LazyLocalFolderItem[drivers.length + viewRoots.length];
//		int i = 0;
//		for (; i < viewRoots.length; i++) {
//			File viewRoot = viewRoots[i];
//			RootItem rootitem = ItemUtils.toSection(viewRoot, false);
//			LazyLocalFSVFolderItem root = new LazyLocalFSVFolderItem(rootitem, (LocalFileSystemEntry) fileSystemEntry, preference, viewRoot);
//			root.setName(viewRoot.getName());
//			// root.setData(driver);
//			root.setLastUpdateTime(null);
//			root.setCreateTime(null);
//			root.setPath(viewRoot.getPath());
//			root.setSize(null);
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
	
//	@Override
//	public Item clone() {
//		LazyLocalFSVFileItem clone = new LazyLocalFSVFileItem(itemspace, (LocalFileSystemEntry) fileSystemEntry, preference, file);
//		//		clone.setParent(this.getParent());
////		clone.setCatelog(this.getCatelog());
//		// clone.setName(this.getName());
//		// clone.setActualPath(this.getActualPath());
//		// // clone.setData(this.getData());
//		// clone.setSize(this.getSize());
//		// clone.setCreateTime(this.getCreateTime());
//		// clone.setLastUpdateTime(this.getLastUpdateTime());
//		return clone;
//	}
	
//	@Override
//	public String getName() {
////		System.out.println("-------------------------------------------------");
////		System.out.println("file="+file);
////		System.out.println("isDrive="+fileSystemView.isDrive(file));
////		System.out.println("isFileSystem="+fileSystemView.isFileSystem(file));
////		System.out.println("isFileSystemRoot="+fileSystemView.isFileSystemRoot(file));
////		System.out.println("isRoot="+fileSystemView.isRoot(file));
////		System.out.println("isTraversable="+fileSystemView.isTraversable(file));
////		System.out.println("isComputerNode="+fileSystemView.isComputerNode(file));
////		System.out.println("getSystemTypeDescription="+fileSystemView.getSystemTypeDescription(file));
////		System.out.println("getSystemDisplayName="+fileSystemView.getSystemDisplayName(file));
//		String val = super.name;
//		if (val == null) {
//			val = fileSystemView.getSystemDisplayName(file);
//			//Will be empty when root D:\\ C:\\
//			if (StringUtils.isEmpty(val)) {
//				val = file.getPath();
//			}
//			super.setName(val);
//		}
//
//		return val;
//	}

}
