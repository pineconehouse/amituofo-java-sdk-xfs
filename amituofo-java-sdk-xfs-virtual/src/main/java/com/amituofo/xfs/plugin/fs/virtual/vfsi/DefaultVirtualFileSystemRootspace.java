package com.amituofo.xfs.plugin.fs.virtual.vfsi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.amituofo.common.util.DigestUtils;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.vfs.JdbcVirtualFSIConnPool;
import com.amituofo.xfs.service.ItemType;

public abstract class DefaultVirtualFileSystemRootspace implements VirtualFileSystemRootspace {
	private long fileIdSeq = 1L;
	Map<String, VirtualFSI> map = new HashMap<String, VirtualFSI>();
	protected final VirtualFileSystemEntry fileSystemEntry;
	protected final String ROOT_PATH;
	protected final String spacename;

	// private Timer cleanService;

	public DefaultVirtualFileSystemRootspace(VirtualFileSystemEntry fileSystemEntry, String spacename) {
		this.fileSystemEntry = fileSystemEntry;
		this.spacename = spacename;
		this.ROOT_PATH = String.valueOf(fileSystemEntry.getSeparatorChar());

		// cleanService = new Timer();
		// cleanService.schedule(new TimerTask() {
		//
		// @Override
		// public void run() {
		// synchronized (map) {
		// List<VirtualFSI> tobeRemoved = new ArrayList<VirtualFSI>();
		// Collection<VirtualFSI> fsis = map.values();
		// for (VirtualFSI virtualFSI : fsis) {
		// long lastUpdatetime = virtualFSI.getLastUpdatetime();
		// if ((System.currentTimeMillis() - lastUpdatetime) > 1000 * 60) {
		// tobeRemoved.add(virtualFSI);
		// }
		// }
		//
		// for (VirtualFSI virtualFSI : tobeRemoved) {
		// virtualFSI.close();
		// map.remove(virtualFSI.getFileSystemId());
		// }
		// }
		// }
		// }, 1000 * 30, 1000 * 15);

	}

	@Override
	public void close() {
		synchronized (map) {
			// cleanService.cancel();
			Collection<VirtualFSI> fsis = map.values();
			for (VirtualFSI memVirtualFSI : fsis) {
				memVirtualFSI.close();
			}
			map.clear();

			String dbname = fileSystemEntry.getName();
			JdbcVirtualFSIConnPool.close(dbname);
		}
	}

	private String getFolderPath(String fullPath, ItemType type) {
		String folderPath;
		if (type == ItemType.File) {
			int endIndex = fullPath.lastIndexOf(fileSystemEntry.getSeparatorChar());
			if (endIndex > 0) {
				folderPath = fullPath.substring(0, endIndex);
			} else {
				folderPath = ROOT_PATH;
			}
		} else {
			folderPath = fullPath;
		}

		return folderPath;
	}

	@Override
	public String toFolderId(String folderPath) {
		String unionPath = folderPath;
		if (!ROOT_PATH.equals(unionPath)) {
			while (unionPath.charAt(0) == fileSystemEntry.getSeparatorChar()) {
				unionPath = unionPath.substring(1);
			}
			while (unionPath.charAt(unionPath.length() - 1) == fileSystemEntry.getSeparatorChar()) {
				unionPath = unionPath.substring(0, unionPath.length() - 1);
			}
		}
		unionPath = unionPath.toLowerCase();

		String dbid = DigestUtils.calcMD5ToHex(unionPath);
		// String dbid = StringUtils.encodeBase64String(unionPath);
		return dbid;
	}

	@Override
	public VirtualFSI getVirtualFSI(String fullPath, ItemType type) {
		// if (StringUtils.isEmpty(fullPath)) {
		// fullPath = String.valueOf(memFileSystemEntry.getSeparatorChar());
		// }

		synchronized (map) {
			String folderPath = getFolderPath(fullPath, type);
			String folderId = toFolderId(folderPath);

			VirtualFSI vf = map.get(folderId);
			if (vf == null) {
				// String location = memFileSystemEntry.getVirtualFileSystemLocation().getAbsolutePath();
				// vf = new H2VirtualFSI(memFileSystemEntry, location, dbid, folderPath);

//				String dbname = fileSystemEntry.getName();
				vf = createVirtualFSI(folderId, fileSystemEntry, folderPath);

//				vf = new MysqlVirtualFSI(folderId, fileSystemEntry, folderPath, dbname, JdbcVirtualFSIConnPool.getDataSource(dbname));
				// vf = new JimfsVirtualFSI(memFileSystemEntry, folderPath);
				try {
					vf.init();
					map.put(folderId, vf);
				} catch (VirtualFileSystemException e) {
					e.printStackTrace();
				}
			}
			return vf;
		}
	}

	protected abstract VirtualFSI createVirtualFSI(String folderId, VirtualFileSystemEntry fileSystemEntry2, String folderPath);

	@Override
	public boolean cleanAndDeleteVirtualFSI(String folderPath) {
		String dbid = toFolderId(folderPath);
		VirtualFSI vf = map.remove(dbid);
		if (vf != null) {
			try {
				return vf.remove();
			} catch (VirtualFileSystemException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public VirtualFSI removeVirtualFSICache(String folderPath) {
		String dbid = toFolderId(folderPath);
		return map.remove(dbid);
	}

	@Override
	public long generateFileId() {
		return fileIdSeq++;
	}

}
