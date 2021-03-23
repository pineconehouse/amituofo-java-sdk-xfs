package com.amituofo.xfs.plugin.fs.sftp.item;

import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.api.IOCloseListener;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.CloseHandleableInputStream;
import com.amituofo.common.util.StreamUtils;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemType;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpATTRS;

public class SFTPFileItem extends SFTPItemBase implements FileItem {

	public SFTPFileItem(SFTPRootspace itemspace) {
		super(itemspace);
	}

	@Override
	public InputStream getContent() throws ServiceException {
		ChannelSftp ftpClient = null;
		InputStream in = null;
		try {
			ftpClient = pool.borrowObject();
			in = ftpClient.get(this.getEncodedActualPath());

			if (in != null) {
				final ChannelSftp xftpClient = ftpClient;
				// ftp链接的回收需要等到数据全部被读取完毕
				final CloseHandleableInputStream cin = new CloseHandleableInputStream(in, new IOCloseListener() {

					@Override
					public void closed() throws IOException {
//						System.out.println("completePendingCommand...S" + xftpClient);
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
		return new SFTPContentWriter(this);
	}

	@Override
	public boolean delete() throws ServiceException {
		ChannelSftp ftpClient = null;
		try {
			ftpClient = pool.borrowObject();

			String key = (String) this.getEncodedActualPath();
			ftpClient.rm(key);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}

		return true;
	}

//	@Override
//	public boolean delete(ItemHandler event) {
//		boolean deleted = false;
//		ChannelSftp ftpClient = null;
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
		ChannelSftp ftpClient = null;
		try {
			ftpClient = pool.borrowObject();
			SftpATTRS attr = ftpClient.stat(this.getEncodedActualPath());
			if (attr != null) {
				this.setSize(attr.getSize());
				this.setLastUpdateTime(Long.valueOf(attr.getMTime()));
			}
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			pool.returnObject(ftpClient);
		}
	}

}
