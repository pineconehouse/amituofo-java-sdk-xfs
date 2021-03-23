package com.amituofo.xfs.plugin.fs.ftp.item;

import java.io.UnsupportedEncodingException;

import org.apache.commons.net.ftp.FTPClient;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.plugin.fs.ftp.FTPClientPool;
import com.amituofo.xfs.plugin.fs.ftp.FTPFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystem;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemBase;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemInstanceCreator;

public abstract class FTPItemBase extends ItemBase<FTPRootspace> implements FileSystem {
	public static final char SEPARATOR_CHAR = '/';
	public static final String OS_DEFAULT_ENCODING = System.getProperty("sun.jnu.encoding");

	protected final FTPClientPool pool;

	// private FolderItem[] roots = null;

	public FTPItemBase(FTPRootspace itemspace) {
		super(itemspace);
		this.pool = itemspace.getClientPool();
	}

	@Override
	public char getPathSeparator() {
		return SEPARATOR_CHAR;
	}

	public String encodeFilename(String name) throws UnsupportedEncodingException {
		String lenc = itemspace.getFileSystemPreference().getControlEncoding();
		if (StringUtils.isNotEmpty(lenc)) {
			return new String(name.getBytes(lenc), "ISO-8859-1");
			// return new String(name.getBytes(OS_DEFAULT_ENCODING), "ISO-8859-1");
		}

		return new String(name.getBytes(), "ISO-8859-1");
	}

	public String getEncodedActualPath() throws UnsupportedEncodingException {
		return encodeFilename(this.actualPath);
	}

	public String getEncodedName() throws UnsupportedEncodingException {
		return encodeFilename(this.getName());
	}

	// protected FTPItemBase toItem(FTPFile ftpFile) {
	// FTPItemBase item;
	// if (ftpFile.isFile()) {
	// item = ((RootItemBase)rootitem).newFileItemInstance(this, ftpFile.getName());
	//// item = new FTPFileItem(rootitem, (FTPFileSystemEntry) fileSystemEntry, preference, pool);
	// } else {
	// item = new FTPFolderItem(rootitem, (FTPFileSystemEntry) fileSystemEntry, preference, pool);
	// }
	//
	// // try {
	// // String[] charsets = new String[] { "utf-8", "iso-8859-1", "CESU-8", "GBK", "UTF-16BE", "UTF-16LE", "unicode", null };//
	// // Charset.availableCharsets().keySet().toArray(new
	// // // String[Charset.availableCharsets().size()]);
	// // for (String charset : charsets) {
	// // System.out.println(charset
	// // + " -> "
	// // + new String(ftpFile.getName().getBytes(charset), "UTF-8")
	// // + "\t"
	// // + new String(ftpFile.getName().getBytes(charset))
	// // + "\t"
	// // + new String(ftpFile.getName().getBytes(charset), "GBK")
	// // + "\t"
	// // + new String(ftpFile.getName().getBytes(charset), "iso-8859-1"));
	// // }
	// // } catch (Exception e) {
	// // }
	//
	//// item.setName(ftpFile.getName());
	// // item.setData(hcpObjectEntry.getKey());
	//// item.setPath(URLUtils.catPath(this.getPath(), item.getName()));
	// // item.setParent(this.getParent());
	//
	// // item.setCatelog(namespace);
	// item.setSize(ftpFile.getSize());
	// item.setLastUpdateTime(ftpFile.getTimestamp().getTimeInMillis());
	// // item.setCreateTime();
	//
	// return item;
	// }

	// protected FolderItem toItem(String folderPath) {
	// FolderItem item = new FTPFolderItem(rootitem, (FTPFileSystemEntry) fileSystemEntry, preference, pool);
	//
	//// String targetKey = folderPath.replace('\\', '/');
	// ((ItemInnerFunc)item).setName(URLUtils.getLastNameFromPath(folderPath));
	// ((ItemInnerFunc)item).setPath(folderPath);
	// // item.setParent(this.getParent());
	//
	// return item;
	// }

	// @Override
	// public ItemLocation getLocationType() {
	// return ItemLocation.Remote;
	// }

	// @Override
	// public FolderItem getParent() {
	// String key = this.getPath();
	// if ("/".equals(key)) {
	// return null;
	// }
	//
	// try {
	// // String name = this.getName();
	// //
	// // int li = key.lastIndexOf(name);
	// //
	// // String parentKey = key.substring(0, (li - 1 > 0 ? li - 1 : li));
	//
	// String parentKey = URLUtils.getRequestBasePath(key);
	//
	// return toItem(parentKey);
	// } catch (Exception e) {
	// // throw new ServiceException(e);
	// return null;
	// }
	// }

	// @Override
	// public FolderItem getParent() {
	// // a
	// // a/
	// // /a
	// // a/b/
	// // /a/b
	// // /a/b/
	// String parentPath = URLUtils.getParentPath(this.getPath());
	//
	// if (parentPath == null) {
	// return null;
	// }
	//
	// FolderItem item = ((ItemInstanceCreator)itemspace).newFolderItemInstance(parentPath);
	// ((ItemHiddenFunction)item).setName(URLUtils.getLastNameFromPath(parentPath));
	//
	// return item;
	// }

	// @Override
	// public FolderItem getRoot() throws ServiceException {
	// FolderItem root = super.getRoot();
	//
	// ((ItemInnerFunc)root).setName("/");
	//
	// return root;
	// }
	//
	// @Override
	// public FolderItem[] getRoots() throws ServiceException {
	// if (roots != null) {
	// return roots;
	// }
	//
	// FTPFolderItem item = new FTPFolderItem(rootitem, (FTPFileSystemEntry) fileSystemEntry, preference, pool);
	// item.setName("/");
	// // item.setData(hcpObjectEntry.getKey());
	// item.setPath("/");
	// // item.setParent(null);
	// // item.setType(ItemType.Directory);
	// // item.setCatelog(namespace);
	// item.setSize(null);
	// item.setLastUpdateTime(null);
	// item.setCreateTime(null);
	//
	// roots = new FTPFolderItem[] { item };
	// return roots;
	// }

	@Override
	public boolean isSame(Item item) {
		if (!(isFromSameSystem(item))) {
			return false;
		}

		// FTPItemBase item1 = (FTPItemBase) item;
		// if (!this.hcpClient.getNamespace().equalsIgnoreCase(item1.getHcpClient().getNamespace())) {
		// return false;
		// }

		if (!this.getPath().equals(item.getPath())) {
			return false;
		}

		if (this.getType() != item.getType()) {
			return false;
		}

		if (this.getSize() != item.getSize()) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isFromSameSystem(Item item) {
		if (!(item instanceof FTPItemBase)) {
			return false;
		}

		if (!(item.getSystemName().equals(this.getSystemName()))) {
			return false;
		}

		// Bug 没有判断是否来自同一个服务器
		// FtpFileItem item1 = (FtpFileItem) item;
		// if (!this.hcpClient.getEndpoint().equalsIgnoreCase(item1.getHcpClient().getEndpoint())) {
		// return false;
		// }
		return true;
	}

	@Override
	public String getSystemName() {
		return FTPFileSystemEntryConfig.SYSTEM_NAME;
	}

	// @Override
	// public String[] getSupportVersion() {
	// return new String[] { "x.x" };
	// }

	@Override
	public boolean exists() throws ServiceException {
//		String path = getPath();
//		if ("/".equals(path)) {
//			return true;
//		}

		FTPClient ftpClient = null;
		try {
			String path = this.getEncodedActualPath();
			if (FTPFileSystemEntryConfig.URL_ROOT_PATH.equals(path)) {
				return true;
			}

			ftpClient = pool.borrowObject();

			if (ftpClient.cwd(path) == 550) {
				// System.out.println("Directory Doesn't Exists");
				return false;
			} else if (ftpClient.cwd(path) == 250) {
				// System.out.println("Directory Exists");
				return true;
			}
			// ftpClient.getModificationTime(getActualPath())
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}
		return false;
	}

	@Override
	public void rename(String newname) throws ServiceException {
		FTPClient ftpClient = null;
		boolean result = false;
		try {
			ftpClient = pool.borrowObject();

			// ftpClient.changeWorkingDirectory(((FTPItemBase)this.getParent()).getEncodedActualPath());
			// result = ftpClient.rename(this.getEncodedName(), encodeFilename(newname));

			String from = this.getEncodedActualPath();
			String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), getFileSystemEntry().getRootPath());
			String to = encodeFilename(URLUtils.catPath(parentPath, newname, this.getPathSeparator()));
			result = ftpClient.rename(from, to);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}

		if (!result) {
			throw new ServiceException("Unable to rename file " + this.getPath());
		}
	}

}
