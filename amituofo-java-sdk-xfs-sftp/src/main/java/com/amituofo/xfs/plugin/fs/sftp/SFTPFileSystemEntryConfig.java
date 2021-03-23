package com.amituofo.xfs.plugin.fs.sftp;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.config.RemoteEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class SFTPFileSystemEntryConfig extends RemoteEntryConfig {
	public static final String SYSTEM_NAME = "SSH File Transfer Protocol (SFTP)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP03858730";

	private static final String PRIVATE_KEY_FLIE_PASSPHRASE = "PRIVATE_KEY_FLIE_PASSPHRASE";
	private static final String PRIVATE_KEY_FLIE = "PRIVATE_KEY_FLIE";
	private static final String FILENAME_ENCODING = "FILENAME_ENCODING";

	public SFTPFileSystemEntryConfig() {
		this("");
	}

	public SFTPFileSystemEntryConfig(String name) {
		super(SFTPFileSystemEntryConfig.class, SYSTEM_ID, name, URL_ROOT_PATH, URL_PS, 22);

		// this.setPassiveMode(true);
		// this.setCharset("utf-8");
		// String systemDefaultEncoding = System.getProperty("sun.jnu.encoding");
		// if (StringUtils.isNotEmpty(systemDefaultEncoding)) {
		// this.setControlEncoding(systemDefaultEncoding);
		// }

		// setEnableUTF8Support(true);

//		this.setConnectionTimeout(5000);
	}

	public String getLoginUser() {
		// if (isAnonymousLogin()) {
		// return "anonymous";
		// }

		return super.getUser();
	}

	public String getLoginPassword() throws ServiceException {
		// if (isAnonymousLogin()) {
		// return "anonymous";
		// }

		return super.getPassword();
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new SFTPFileSystemEntry(this, (SFTPFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		SFTPFileSystemPreference p = new SFTPFileSystemPreference();
		// p.setLocalEncoding(getLocalEncoding());
		// p.setRemoteEncoding(getRemoteEncoding());
		// p.setControlEncoding(this.getControlEncoding());
		return p;
	}

	public String getPrivateKeyflie() {
		return config.getString(PRIVATE_KEY_FLIE);
	}

	public String getPassphrase() throws ServiceException {
		return config.getSensitiveString(PRIVATE_KEY_FLIE_PASSPHRASE);
	}

	public void setPassphrase(String passphrase) throws ServiceException {
		config.setSensitive(PRIVATE_KEY_FLIE_PASSPHRASE, passphrase);
	}

	public void setPrivateKeyflie(String keyflie) {
		config.set(PRIVATE_KEY_FLIE, keyflie);
	}

	public void setFilenameEncoding(String encoding) {
		config.set(FILENAME_ENCODING, encoding);
	}

	public String getFilenameEncoding() {
		String encoding = config.getString(FILENAME_ENCODING);
		if (StringUtils.isEmpty(encoding)) {
			String systemDefaultEncoding = System.getProperty("sun.jnu.encoding");
			if (StringUtils.isNotEmpty(systemDefaultEncoding)) {
				encoding = systemDefaultEncoding.toUpperCase();
			} else {
				encoding = "UTF-8";
			}
		}

		return encoding;
	}

}
