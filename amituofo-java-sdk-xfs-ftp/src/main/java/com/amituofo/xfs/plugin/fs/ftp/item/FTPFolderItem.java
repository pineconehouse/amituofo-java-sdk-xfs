package com.amituofo.xfs.plugin.fs.ftp.item;

import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPListParseEngine;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.ftp.FTPFileSystemEntryConfig.ListingCommand;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.ItemspaceBase;

public class FTPFolderItem extends FTPItemBase implements FolderItem {

	public FTPFolderItem(FTPRootspace itemspace) {
		super(itemspace);
	}

	@Override
	public void list(ItemFilter filter, ItemHandler handler) {
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			// FTPFile[] fs = ftpClient.mlistDir();
			// FTPFile[] fs = ftpClient.listFiles("/");
			// FTPFile[] ds = ftpClient.listDirectories();
			// String[] ns = ftpClient.listNames();
			final FTPListParseEngine engine;
			ListingCommand lcmd = itemspace.getFileSystemEntry().getEntryConfig().getListingCommand();
			if (lcmd == ListingCommand.MLSD) {
				engine = ftpClient.initiateMListParsing(getEncodedActualPath());
			} else {
				engine = ftpClient.initiateListParsing(getEncodedActualPath());
			}
			while (engine.hasNext()) {
				FTPFile[] files = engine.getNext(50); // "page size" you want
				// while (files != null && files.length != 0) {
				for (FTPFile ftpFile : files) {
					if (ftpFile == null) {
						continue;
					}

					// if (filter != null && filter.getType() == ItemType.Directory) {
					// if (ftpFile.isFile()) {
					// continue;
					// }
					// } else {
					//
					// }

					// print.addRow((ii++) + "\t" + hcpObjectEntry.getType() + "\t" + hcpObjectEntry.getKey() + "\t" +
					// hcpObjectEntry.getName() + "\t" + hcpObjectEntry.getSize());

					if (isXFile(ftpFile)) {
						continue;
					}

					Item item = toItem(ftpFile);

					if (filter != null && !filter.accept(item)) {
						continue;
					}

					HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (ef == HandleFeedback.interrupted) {
						handler.handle(ItemEvent.EXEC_END, null);
						return;
					}
				}

				// files = engine.getNext(50); // "page size" you want
				// }
			}

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			handler.exceptionCaught(this, e);
		} finally {
			pool.returnObject(ftpClient);
		}
	}

	@Override
	public void listFolders(final ItemFilter filter, ItemHandler handler) {
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			FTPFile[] dirs = ftpClient.listDirectories(getEncodedActualPath());
			for (FTPFile ftpFile : dirs) {
				if (ftpFile == null) {
					continue;
				}

				if (isXFile(ftpFile)) {
					continue;
				}

				Item item = toItem(ftpFile);

				if (filter != null && !filter.accept(item)) {
					continue;
				}

				HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
				if (ef == HandleFeedback.interrupted) {
					handler.handle(ItemEvent.EXEC_END, null);
					return;
				}
			}

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			handler.exceptionCaught(this, e);
		} finally {
			pool.returnObject(ftpClient);
		}
	}

	private boolean isXFile(FTPFile ftpFile) {
		String name = ftpFile.getName();

		if (name.length() < 3) {
			if (".".equals(name) || "..".equals(name)) {
				return true;
			}
		}

		return false;
	}

	// @Override
	// public boolean deleteEmptyFolder() throws ServiceException {
	// FTPClient ftpClient = null;
	// try {
	// ftpClient = pool.borrowObject();
	//
	// return ftpClient.removeDirectory(this.getEncodedActualPath());
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// } finally {
	// pool.returnObject(ftpClient);
	// }
	// }

	@Override
	public boolean delete() throws ServiceException {
		// return delete(null);
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();

			return ftpClient.removeDirectory(this.getEncodedActualPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}
	}

	@Override
	public boolean delete(ItemHandler handler) {
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();

			boolean result = false;
			result = deleteAllFiles(ftpClient, this, handler);

			return result;
		} catch (Exception e) {
			if (handler != null) {
				handler.exceptionCaught(this, e);
			}
			return false;
		} finally {
			pool.returnObject(ftpClient);
		}
	}

	private boolean deleteAllFiles(final FTPClient ftpClient, FTPFolderItem fileItem, final ItemHandler handler) {
		// 递归查询本目录下的文件
		fileItem.list(new ItemHandler() {
			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (eventType == ItemEvent.ITEM_FOUND) {
					// 如果是文件夹递归查询
					if (data.isDirectory()) {
						deleteAllFiles(ftpClient, (FTPFolderItem) data, handler);
					} else {
						// 如果是文件直接删除
						deleteFile(ftpClient, (FTPFileItem) data, handler);
					}
				}

				return null;
			}

			@Override
			public void exceptionCaught(Item data, Throwable e) {
				if (handler != null) {
					handler.exceptionCaught(data, e);
				}
			}
		});

		// 到达此处代表此目录下已没有文件，直接删除此目录，如果有文件会返回false
		boolean deleted = deleteDir(ftpClient, fileItem, handler);

		return deleted;
	}

	private boolean deleteDir(FTPClient ftpClient, FTPFolderItem fileItem, ItemHandler handler) {
		boolean deleted = false;
		try {
			deleted = ftpClient.removeDirectory(fileItem.getEncodedActualPath());

			if (handler != null) {
				handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, fileItem);
			}

		} catch (IOException e) {
			if (handler != null) {
				handler.exceptionCaught(fileItem, e);
			}
		}

		return deleted;
	}

	private boolean deleteFile(FTPClient ftpClient, FTPFileItem fileItem, ItemHandler handler) {
		boolean deleted = false;
		try {
			deleted = ftpClient.deleteFile(fileItem.getEncodedActualPath());

			if (handler != null) {
				handler.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, fileItem);
			}
		} catch (IOException e) {
			if (handler != null) {
				handler.exceptionCaught(fileItem, e);
			}
		}

		return deleted;
	}

	// @Override
	// public boolean createDirectory(String name) throws ServiceException {
	// FTPClient ftpClient = null;
	// try {
	// ftpClient = pool.borrowObject();
	//
	// return ftpClient.makeDirectory(encodeFilename(URLUtils.catPath(this.getPath(), name)));
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// } finally {
	// pool.returnObject(ftpClient);
	// }
	// // if (!deleted) {
	// // throw new ServiceException("Unable to make directory " + name);
	// // }
	// }

	@Override
	public boolean createDirectory() throws ServiceException {
		boolean isOK = false;
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();

			isOK = ftpClient.makeDirectory(getEncodedActualPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}

		return isOK;
	}

	@Override
	public void rename(String newname) throws ServiceException {
		try {
			ValidUtils.invalidIfEmpty(newname, "A new name must be specified!");
			ValidUtils.invalidIfEqual(newname, this.getName(), "New name must be different with current name!");
		} catch (InvalidParameterException e) {
			throw new ServiceException(e);
		}

		String key = this.getPath();
		if ("/".equals(key)) {
			throw new ServiceException("Unable to rename root folder.");
		}

		super.rename(newname);
	}

	protected Item toItem(FTPFile ftpFile) {
		Item item;
		if (ftpFile.isFile()) {
			item = ((ItemspaceBase) itemspace).newFileItemInstance(this, ftpFile.getName());
			// item = new FTPFileItem(rootitem, (FTPFileSystemEntry) fileSystemEntry, preference, pool);
		} else {
			item = ((ItemspaceBase) itemspace).newFolderItemInstance(this, ftpFile.getName());
			// item = new FTPFolderItem(rootitem, (FTPFileSystemEntry) fileSystemEntry, preference, pool);
		}

		// try {
		// String[] charsets = new String[] { "utf-8", "iso-8859-1", "CESU-8", "GBK", "UTF-16BE", "UTF-16LE", "unicode", null };//
		// Charset.availableCharsets().keySet().toArray(new
		// // String[Charset.availableCharsets().size()]);
		// for (String charset : charsets) {
		// System.out.println(charset
		// + " -> "
		// + new String(ftpFile.getName().getBytes(charset), "UTF-8")
		// + "\t"
		// + new String(ftpFile.getName().getBytes(charset))
		// + "\t"
		// + new String(ftpFile.getName().getBytes(charset), "GBK")
		// + "\t"
		// + new String(ftpFile.getName().getBytes(charset), "iso-8859-1"));
		// }
		// } catch (Exception e) {
		// }

		// item.setName(ftpFile.getName());
		// item.setData(hcpObjectEntry.getKey());
		// item.setPath(URLUtils.catPath(this.getPath(), item.getName()));
		// item.setParent(this.getParent());

		// item.setCatelog(namespace);
		((ItemHiddenFunction) item).setSize(ftpFile.getSize());
		((ItemHiddenFunction) item).setLastUpdateTime(ftpFile.getTimestamp().getTimeInMillis());
		// item.setCreateTime();

		return item;
	}

}
