package com.amituofo.xfs.plugin.fs.sftp.item;

import com.amituofo.common.define.HandleFeedback;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.service.FolderItem;
import com.amituofo.xfs.service.Item;
import com.amituofo.xfs.service.ItemEvent;
import com.amituofo.xfs.service.ItemFilter;
import com.amituofo.xfs.service.ItemHandler;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemType;
import com.amituofo.xfs.service.ItemspaceBase;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import com.jcraft.jsch.SftpATTRS;

public class SFTPFolderItem extends SFTPItemBase implements FolderItem {

	public SFTPFolderItem(SFTPRootspace itemspace) {
		super(itemspace);
	}

	@Override
	public void list(final ItemFilter filter, final ItemHandler handler) {
		ChannelSftp ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			
			String actualPath = getEncodedActualPath();
//			SftpATTRS lists = ftpClient.lstat(getEncodedActualPath());
//			String realpath = ftpClient.realpath(actualPath);
//			String pwd = ftpClient.pwd();
//			ftpClient.cd(actualPath);
			
			ftpClient.ls(actualPath, new LsEntrySelector() {

				@Override
				public int select(LsEntry entry) {
					if (isXFile(entry)) {
						return LsEntrySelector.CONTINUE;
					}

					Item item = toItem(entry);

					if (filter != null && !filter.accept(item)) {
						return LsEntrySelector.CONTINUE;
					}

					HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
					if (ef == HandleFeedback.interrupted) {
						handler.handle(ItemEvent.EXEC_END, null);
						return LsEntrySelector.BREAK;
					}
					return 0;
				}
			});

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			handler.exceptionCaught(this, e);
		} finally {
			pool.returnObject(ftpClient);
		}
	}

	@Override
	public void listFolders(final ItemFilter filter, final ItemHandler handler) {
		ChannelSftp ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			ftpClient.ls(getEncodedActualPath(), new LsEntrySelector() {

				@Override
				public int select(LsEntry entry) {
					if (isXFile(entry)) {
						return LsEntrySelector.CONTINUE;
					}

					if (entry.getAttrs().isDir()) {
						Item item = toItem(entry);

						if (filter != null && !filter.accept(item)) {
							return LsEntrySelector.CONTINUE;
						}

						HandleFeedback ef = handler.handle(ItemEvent.ITEM_FOUND, item);
						if (ef == HandleFeedback.interrupted) {
							handler.handle(ItemEvent.EXEC_END, null);
							return LsEntrySelector.BREAK;
						}
					}
					return LsEntrySelector.CONTINUE;
				}
			});

			handler.handle(ItemEvent.EXEC_END, null);
		} catch (Exception e) {
			handler.exceptionCaught(this, e);
		} finally {
			pool.returnObject(ftpClient);
		}
	}

	private boolean isXFile(LsEntry ftpFile) {
		String name = ftpFile.getFilename();

		if (name.length() < 3) {
			if (".".equals(name) || "..".equals(name)) {
				return true;
			}
		}

		return false;
	}

	// @Override
	// public boolean deleteEmptyFolder() throws ServiceException {
	// ChannelSftp ftpClient = null;
	// try {
	// ftpClient = pool.borrowObject();
	//
	// ftpClient.rmdir(this.getEncodedActualPath());
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// } finally {
	// pool.returnObject(ftpClient);
	// }
	// return true;
	// }

	@Override
	public boolean delete() throws ServiceException {
		ChannelSftp ftpClient = null;
		try {
			ftpClient = pool.borrowObject();

			ftpClient.rmdir(this.getEncodedActualPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}
		return true;
	}

	@Override
	public boolean delete(ItemHandler handler) {
		ChannelSftp ftpClient = null;
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

	private boolean deleteAllFiles(final ChannelSftp ftpClient, SFTPFolderItem fileItem, final ItemHandler handler) {
		// 递归查询本目录下的文件
		fileItem.list(new ItemHandler() {
			@Override
			public HandleFeedback handle(Integer eventType, Item data) {
				if (eventType == ItemEvent.ITEM_FOUND) {
					// 如果是文件夹递归查询
					if (data.isDirectory()) {
						deleteAllFiles(ftpClient, (SFTPFolderItem) data, handler);
					} else {
						// 如果是文件直接删除
						deleteFile(ftpClient, (SFTPFileItem) data, handler);
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

	private boolean deleteDir(ChannelSftp ftpClient, SFTPFolderItem fileItem, ItemHandler handler) {
		try {
			ftpClient.rmdir(fileItem.getEncodedActualPath());

			handler.handle(ItemEvent.ITEM_DELETED, fileItem);

		} catch (Exception e) {
			if (handler != null) {
				handler.exceptionCaught(fileItem, e);
			}
			return false;
		}

		return true;
	}

	private boolean deleteFile(ChannelSftp ftpClient, SFTPFileItem fileItem, ItemHandler handler) {
		try {
			ftpClient.rm(fileItem.getEncodedActualPath());

			handler.handle(ItemEvent.ITEM_DELETED, fileItem);
		} catch (Exception e) {
			if (handler != null) {
				handler.exceptionCaught(fileItem, e);
			}
			return false;
		}

		return true;
	}

	// @Override
	// public boolean createDirectory(String name) throws ServiceException {
	// ChannelSftp ftpClient = null;
	// try {
	// ftpClient = pool.borrowObject();
	//
	// ftpClient.mkdir(encodeFilename(URLUtils.catPath(this.getPath(), name)));
	// } catch (Exception e) {
	// throw new ServiceException(e);
	// } finally {
	// pool.returnObject(ftpClient);
	// }
	// return false;
	// }

	@Override
	public boolean createDirectory() throws ServiceException {
		ChannelSftp ftpClient = null;
		try {
			ftpClient = pool.borrowObject();

			ftpClient.mkdir(getEncodedActualPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}

		return true;
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

	protected Item toItem(LsEntry ftpFile) {
		Item item;
		SftpATTRS attrs = ftpFile.getAttrs();
		if (attrs.isDir()) {
			item = ((ItemspaceBase) itemspace).newFolderItemInstance(this, ftpFile.getFilename());
		} else {
			item = ((ItemspaceBase) itemspace).newFileItemInstance(this, ftpFile.getFilename());
		}

		((ItemHiddenFunction) item).setPath(URLUtils.catPath(this.getPath(), item.getName()));
		((ItemHiddenFunction) item).setSize(attrs.getSize());
		((ItemHiddenFunction) item).setLastUpdateTime(Long.valueOf(attrs.getMTime()));
		// item.setCreateTime();

		return item;
	}

}
