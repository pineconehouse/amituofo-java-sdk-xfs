package com.amituofo.xfs.plugin.fs.cifs.item;

import static com.hierynomus.msdtyp.AccessMask.FILE_LIST_DIRECTORY;
import static com.hierynomus.msdtyp.AccessMask.FILE_READ_ATTRIBUTES;
import static com.hierynomus.msdtyp.AccessMask.FILE_READ_EA;
import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OPEN;
import static com.hierynomus.mssmb2.SMB2ShareAccess.ALL;
import static java.util.EnumSet.of;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.ItemspaceBase;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msdtyp.FileTime;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2CreateOptions;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.smbj.share.Directory;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

public class SmbFolderItem extends SmbItemBase implements FolderItem {
	public SmbFolderItem(SmbSharespace itemspace, String filepath) {
		super(itemspace);
		this.setPath(filepath);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		Directory dir = null;
		try {
			dir = getDiskShare().openDirectory(this.getPath(), of(FILE_LIST_DIRECTORY, FILE_READ_ATTRIBUTES, FILE_READ_EA), null, ALL, FILE_OPEN, null);

			Iterator<FileIdBothDirectoryInformation> it = dir.iterator();
			while (it.hasNext()) {
				FileIdBothDirectoryInformation fileinfo = (FileIdBothDirectoryInformation) it.next();

				String nm = fileinfo.getFileName();
				if (nm.length() <= 2 && (".".equals(nm) || "..".equals(nm))) {
					continue;
				}

				Item item = toItem(fileinfo);

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				handler.handle(ItemEvent.ITEM_FOUND, item);
			}

			handler.handle(ItemEvent.EXEC_END, null);

		} catch (Exception e) {
			e.printStackTrace();
			handler.exceptionCaught(null, e);
		} finally {
			if (dir != null) {
				dir.close();
			}
		}

		// try {
		// List<FileIdBothDirectoryInformation> files = diskShare.list(this.getPath());
		// for (FileIdBothDirectoryInformation fileinfo : files) {
		// Item item = toItem(fileinfo);
		//
		// if (filter != null && !filter.accept(item)) {
		// continue;
		// }
		//
		// handler.handle(ItemEvent.ITEM_FOUND, item);
		// }
		//
		// handler.handle(ItemEvent.EXEC_END, null);
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// handler.exceptionCaught(null, e);
		// }
	}

	private Item toItem(FileIdBothDirectoryInformation fileStatus) {
		Item item;
		String name = fileStatus.getFileName();
		String fullpath;
		if (getPath().length() == 0) {
			fullpath = name;
		} else {
			fullpath = getPath() + getFileSystemEntry().getSeparatorChar() + name;
		}
//		System.out.println(fileStatus.getFileAttributes()+"\t"+name+"\t");
//		/0x00010000=16
		if ((fileStatus.getFileAttributes() & 16) == 16) {
			item = ((ItemspaceBase) itemspace).newFolderItemInstance(fullpath);
		} else {
			item = ((ItemspaceBase) itemspace).newFileItemInstance(fullpath);
			((ItemHiddenFunction) item).setSize(fileStatus.getEndOfFile());
		}
		
		((ItemHiddenFunction) item).setName(name);

		FileTime time = fileStatus.getLastWriteTime();
		if (time != null) {
			((ItemHiddenFunction) item).setLastUpdateTime(time.toEpochMillis());
		}
		time = fileStatus.getCreationTime();
		if (time != null) {
			((ItemHiddenFunction) item).setCreateTime(time.toEpochMillis());
		}
		time = fileStatus.getLastAccessTime();
		if (time != null) {
			((ItemHiddenFunction) item).setLastAccessTime(time.toEpochMillis());
		}

		return item;
	}
	

	@Override
	public void rename(String newfilename) throws ServiceException {
		Directory dir = null;
		try {
			String newfilepath;

			String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), null);
			if (parentPath == null) {
				newfilepath = newfilename;
			} else {
				newfilepath = URLUtils.catPath(parentPath, newfilename, itemspace.getFileSystemEntry().getSeparatorChar());
			}
			dir = getDiskShare().openDirectory(this.getPath(), of(AccessMask.DELETE), null, ALL, FILE_OPEN, null);
			dir.rename(newfilepath);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			if (dir != null) {
				dir.close();
			}
		}
	}


	@Override
	public boolean exists() throws ServiceException {
		if (StringUtils.isEmpty(this.getPath())) {
			return true;
		}

		try {
			return getDiskShare().folderExists(this.getPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			getDiskShare().rmdir(this.getPath(), true);
			return true;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean delete(ItemHandler handler) {
		try {
			getDiskShare().rmdir(this.getPath(), true);
			if (handler != null) {
				handler.handle(ItemEvent.ITEM_DELETED, this);
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			if (handler != null) {
				handler.exceptionCaught(this, e);
			}
			return false;
		}
	}

	// @Override
	// public boolean deleteEmptyFolder() throws ServiceException {
	// return delete(null);
	// }

	@Override
	public boolean createDirectory() throws ServiceException {
		try {
			getDiskShare().mkdir(this.getPath());
			return true;
		} catch (SMBApiException e) {
			e.printStackTrace();
			return false;
		}
	}

}
