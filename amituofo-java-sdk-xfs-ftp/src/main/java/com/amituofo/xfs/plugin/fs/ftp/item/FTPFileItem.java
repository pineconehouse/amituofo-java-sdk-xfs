package com.amituofo.xfs.plugin.fs.ftp.item;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.amituofo.common.api.IOCloseListener;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.CloseHandleableInputStream;
import com.amituofo.common.util.StreamUtils;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemType;

public class FTPFileItem extends FTPItemBase implements FileItem {

	public FTPFileItem(FTPRootspace itemspace) {
		super(itemspace);
	}

	@Override
	public InputStream getContent() throws ServiceException {
		FTPClient ftpClient = null;
		InputStream in = null;
		try {
			ftpClient = pool.borrowObject();
			in = ftpClient.retrieveFileStream(this.getEncodedActualPath());

			if (in != null) {
				final FTPClient xftpClient = ftpClient;
				// ftp链接的回收需要等到数据全部被读取完毕
				final CloseHandleableInputStream cin = new CloseHandleableInputStream(in, new IOCloseListener() {

					@Override
					public void closed() throws IOException {
//						System.out.println("completePendingCommand...S" + xftpClient);
						xftpClient.completePendingCommand();
//						System.out.println("completePendingCommand...E" + xftpClient);
						pool.returnObject(xftpClient);
					}
				});

				return cin;
			}

			return null;
		} catch (Exception e) {
			StreamUtils.close(in);
			throw new ServiceException(e);
		} finally {
			// 如果in是null代表
			if (in == null) {
				pool.returnObject(ftpClient);
				throw new ServiceException("Unable to retrieve file stream. " + this.getPath());
			}
		}
	}

	@Override
	public ContentWriter getContentWriter() {
		return new FTPContentWriter(this);
	}

//	@Override
//	public Item clone() {
//		FTPFileItem clone = new FTPFileItem((FTPRootspace)itemspace, (FTPFileSystemEntry) fileSystemEntry, preference, pool);
//		// clone.setParent(this.getParent());
////		clone.setCatelog(this.getCatelog());
//		clone.setName(this.getName());
//		clone.setPath(this.getPath());
//		// clone.setData(this.getData());
//		clone.setSize(this.getSize());
//		clone.setCreateTime(this.getCreateTime());
//		clone.setLastUpdateTime(this.getLastUpdateTime());
//		return clone;
//	}

	@Override
	public boolean delete() throws ServiceException {
		boolean deleted = false;
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();

			String key = (String) this.getEncodedActualPath();
			deleted = ftpClient.deleteFile(key);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}

		return deleted;
	}

//	@Override
//	public boolean delete(ItemHandler event) {
//		boolean deleted = false;
//		FTPClient ftpClient = null;
//		try {
//			ftpClient = pool.borrowObject();
//
//			String key = (String) this.getEncodedActualPath();
//			deleted = ftpClient.deleteFile(key);
//		} catch (Exception e) {
//			// throw new ServiceException(e);
//			if (event != null) {
//				event.exceptionCaught(this, e);
//			}
//			return false;
//		} finally {
//			pool.returnObject(ftpClient);
//		}
//
//		// if (!deleted) {
//		// throw new ServiceException("Unable to delete.");
//		// }
//
//		if (event != null) {
//			event.handle(deleted ? ItemEvent.ITEM_DELETED : ItemEvent.ITEM_DELETE_FAILED, this);
//		}
//
//		return deleted;
//	}

	@Override
	public void upateProperties() throws ServiceException {
		FTPClient ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			FTPFile[] files = ftpClient.listFiles(this.getEncodedActualPath());
			if (files != null && files.length > 0) {
				this.setSize(files[0].getSize());
				this.setLastUpdateTime(files[0].getTimestamp().getTimeInMillis());
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}
	}

}
