package com.amituofo.xfs.util;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import com.amituofo.common.util.FileUtils;

public class FileSystemUtils {

	public FileSystemUtils() {
		// TODO Auto-generated constructor stub
	}

	public static File[] getDrivers() {
		File[] roots = File.listRoots();

		return roots;
	}

//	public static Item[] getLocalDrivers() {
//		File[] drivers = getDrivers();
//		Item[] roots = new Item[drivers.length];
//		for (int i = 0; i < drivers.length; i++) {
//			File driver = drivers[i];
//
//			LocalFolderItem root = new LocalFolderItem(rootitem, LOCAL_FILE_SYSTEM_ENTRY, LOCAL_FILE_SYSTEM_PREFERENCE, driver);
//			root.setName(driver.getPath());
////			root.setData(driver);
//			root.setLastUpdateTime(null);
//			root.setCreateTime(null);
//			root.setPath(driver.getPath());
////			root.setSize(0L);
////			root.setType(ItemType.Directory);
////			root.setParent(null);
//
//			roots[i] = root;
//		}
//
//		return roots;
//	}
	
	public static File getHomeDirectory() {
		File home = FileSystemView.getFileSystemView().getHomeDirectory();

		return home;
	}
	
	public static String getHome() {
//		File s = FileSystemView.getFileSystemView().getDefaultDirectory();
//		System.out.println("defaultD="+s);
		File home = FileSystemView.getFileSystemView().getHomeDirectory();
//		System.out.println("home="+home);
		if (FileUtils.isFileExist(home)) {
			return home.getAbsolutePath();
		}

		return getDefaultAvailableRoot();
	}

	public static String getDefaultAvailableRoot() {
		File[] roots = File.listRoots();
		if (roots != null && roots.length > 0) {
			for (File root : roots) {
				try {
					if (root.getUsableSpace() > 0) {
						return root.getPath();
					}
				} catch (Exception e) {
					// Donothing;
				}
			}
		}

		return File.separator;
	}
}
