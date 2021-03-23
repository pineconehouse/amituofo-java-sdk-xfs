package com.amituofo.xfs.plugin.fs.cifs.item;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_OPEN;
import static com.hierynomus.mssmb2.SMB2ShareAccess.ALL;
import static java.util.EnumSet.of;

import java.io.IOException;
import java.io.InputStream;

import com.amituofo.common.api.IOCloseListener;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.CloseHandleableInputStream;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.service.ContentWriter;
import com.amituofo.xfs.service.FileItem;
import com.amituofo.xfs.service.ItemHiddenFunction;
import com.amituofo.xfs.service.ItemProperties;
import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msdtyp.FileTime;
import com.hierynomus.msfscc.fileinformation.FileAllInformation;
import com.hierynomus.mssmb2.SMBApiException;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

public class SmbFileItem extends SmbItemBase implements FileItem {
	public SmbFileItem(SmbSharespace itemspace, String filepath) {
		super(itemspace);
		this.setPath(filepath);
	}

	@Override
	public void upateProperties() {
		try {
			FileAllInformation info = getDiskShare().getFileInformation(this.getPath());
			((ItemHiddenFunction) this).setSize(info.getStandardInformation().getEndOfFile());

			((ItemHiddenFunction) this).setName(name);

			FileTime time = info.getBasicInformation().getLastWriteTime();
			if (time != null) {
				((ItemHiddenFunction) this).setLastUpdateTime(time.toEpochMillis());
			}
			time = info.getBasicInformation().getCreationTime();
			if (time != null) {
				((ItemHiddenFunction) this).setCreateTime(time.toEpochMillis());
			}
			time = info.getBasicInformation().getLastAccessTime();
			if (time != null) {
				((ItemHiddenFunction) this).setLastAccessTime(time.toEpochMillis());
			}

		} catch (SMBApiException e) {
			e.printStackTrace();
		}
	}

	@Override
	public InputStream getContent() throws ServiceException {
		try {
			final File file = getDiskShare().openFile(this.getPath(), of(AccessMask.GENERIC_READ), null, ALL, FILE_OPEN, null);
			return new CloseHandleableInputStream(file.getInputStream(), new IOCloseListener() {

				@Override
				public void closed() throws IOException {
					file.close();
				}
			});
		} catch (Exception e) {
			// e.printStackTrace();
			throw new ServiceException(e);
		}
	}

	@Override
	public void rename(String newfilename) throws ServiceException {
		File file = null;
		try {
			String newfilepath;

			String parentPath = URLUtils.getParentPath(this.getPath(), this.getPathSeparator(), null);
			if (parentPath == null) {
				newfilepath = newfilename;
			} else {
				newfilepath = URLUtils.catPath(parentPath, newfilename, itemspace.getFileSystemEntry().getSeparatorChar());
			}
			file = getDiskShare().openFile(this.getPath(), of(AccessMask.DELETE), null, ALL, FILE_OPEN, null);
			file.rename(newfilepath);
		} catch (Exception e) {
			throw new ServiceException(e);
		} finally {
			if (file != null) {
				file.close();
			}
		}
	}

	@Override
	public boolean delete() throws ServiceException {
		try {
			getDiskShare().rm(this.getPath());
			return true;
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public boolean exists() throws ServiceException {
		try {
			return getDiskShare().fileExists(this.getPath());
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public ContentWriter getContentWriter() {
		return new SmbContentWriter(this);
	}

}
