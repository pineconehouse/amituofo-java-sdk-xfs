package com.amituofo.xfs.plugin.fs.virtual.vfsi;

import java.util.concurrent.atomic.AtomicBoolean;

import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.virtual.VirtualFileSystemEntry;
import com.amituofo.xfs.plugin.fs.virtual.item.VirtualFolderItem;
import com.amituofo.xfs.plugin.fs.virtual.item.VirtualItemspace;

public abstract class DefaultVirtualFSI implements VirtualFSI {
//	protected VirtualFSI parentFSI;
	protected long lastUpdatetime = System.currentTimeMillis();

	protected final VirtualFileSystemRootspace virtualFileSystem;
	protected final VirtualFileSystemEntry fileSystemEntry;

	protected final String fsid;
	protected final String thisFolderPath;
	protected final String thisFolderName;
	protected VirtualFolderItem thisFolderItem;

	private final AtomicBoolean lock = new AtomicBoolean(false);
	
	public DefaultVirtualFSI(String fsid, VirtualFileSystemEntry virtualFileSystemEntry, String folderPath) {
		this.fsid = fsid;
		this.thisFolderPath = folderPath;
		this.thisFolderName = URLUtils.getLastNameFromPath(folderPath);
		this.fileSystemEntry = virtualFileSystemEntry;
		this.virtualFileSystem = virtualFileSystemEntry.getVirtualFileSystem();

//		try {
			this.thisFolderItem = new VirtualFolderItem((VirtualItemspace)virtualFileSystemEntry.getDefaultItemspace(), this);
//		} catch (ServiceException e) {
//			e.printStackTrace();
////			this.thisFolderItem = null;
//		}
	}

	@Override
	public String getFileSystemId() {
		return fsid;
	}

	protected long generateFileId() {
		return virtualFileSystem.generateFileId();
	}

	@Override
	public VirtualFolderItem getFolderItem() {
		return thisFolderItem;
	}

	@Override
	public String getFolderName() {
		return thisFolderName;
	}

	@Override
	public String getFolderPath() {
		return thisFolderPath;
	}

	@Override
	public long getLastUpdatetime() {
		return lastUpdatetime;
	}

	// @Override
	protected boolean lock() {
		// return lock.tryLock();
		synchronized (lock) {
			if (lock.get()) {
				return false;
			} else {
				lock.set(true);
				return true;
			}
		}
	}

	// @Override
	protected void unlock() {
		synchronized (lock) {
			// lock.unlock();
			lock.set(false);
		}
	}
}
