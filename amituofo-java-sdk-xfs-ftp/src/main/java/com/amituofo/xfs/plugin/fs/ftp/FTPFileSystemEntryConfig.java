package com.amituofo.xfs.plugin.fs.ftp;

import javax.net.ssl.TrustManager;

import org.apache.commons.net.util.TrustManagerUtils;

import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.xfs.config.RemoteEntryConfig;
import com.amituofo.xfs.plugin.fs.ftp.define.FileType;
import com.amituofo.xfs.plugin.fs.ftp.define.Protocol;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class FTPFileSystemEntryConfig extends RemoteEntryConfig {
	public static final String SYSTEM_NAME = "File Transfer Protocol (FTP)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP03858729";

	public static final String ANONYMOUS_LOGIN = "ANONYMOUS_LOGIN";
	// private static final String LOCAL_PASSIVE_MODE = "LOCAL_PASSIVE_MODE";
	private static final String LIST_HIDDEN_FILES = "LIST_HIDDEN_FILES";
	// private static final String CHARSET = "CHARSET";
	private static final String CONTROL_ENCODING = "CONTROL_ENCODING";
	// private static final String REMOTE_ENCODING = "REMOTE_ENCODING";
	// private static final String LOCAL_ENCODING = "LOCAL_ENCODING";
	private static final String OPTS_UTF8_SUPPORT = "OPTS_UTF8_SUPPORT";
	private static final String LISTING_COMMAND = "LISTING_COMMAND";
	private static final String ACCOUNT = "ACCOUNT";
	private static final String LOGIN_COMMANDS = "LOGIN_COMMANDS";
	private static final String BUFFER_SIZE_IN_KB = "BUFFER_SIZE_IN_KB";
	private static final String LOCAL_DATA_TRANSFER_MODE = "LOCAL_DATA_TRANSFER_MODE";

	private static final String PROTOCOL = "PROTOCOL";
	private static final String DEFAULT_CONNECTION_POOL_SIZE = "DEFAULT_CONNECTION_POOL_SIZE";
	private static final String FILE_TYPE = "FILE_TYPE";

	private ListingCommand lcmd = null;
	private TrustManager trustManager;

	public static enum ListingCommand {
		LIST, MLSD
	};

	public static enum DataTransferMode {
		PASSIVE, ACTIVE
	};

	public FTPFileSystemEntryConfig() {
		this("");
	}

	public FTPFileSystemEntryConfig(String name) {
		super(FTPFileSystemEntryConfig.class, SYSTEM_ID, name, URL_ROOT_PATH, URL_PS, 21);

		this.setLocalDataTransferMode(DataTransferMode.PASSIVE);
		// this.setPassiveMode(true);
		// this.setCharset("utf-8");
		String systemDefaultEncoding = System.getProperty("sun.jnu.encoding");
		if (StringUtils.isNotEmpty(systemDefaultEncoding)) {
			this.setControlEncoding(systemDefaultEncoding);
		}

		setEnableUTF8Support(true);

		// this.setConnectionTimeout(5000);
		setTrustManager(TrustManagerUtils.getAcceptAllTrustManager());
	}

	@Override
	public void setHost(String domain) {
		super.setHost(domain);
		int port = URLUtils.getRequestHostPort(domain, this.getPort());
		if (StringUtils.isNotEmpty(domain)) {
			String tmp = domain.toLowerCase();
			if (tmp.startsWith("ftp://")) {
				// this.setUseSSL(false);
			} else if (tmp.startsWith("ftps://")) {
				// this.setUseSSL(true);
			}
		}

		super.setPort(port);
	}

	public String getLoginUser() {
		if (isAnonymousLogin()) {
			return "anonymous";
		}

		return super.getUser();
	}

	public String getLoginPassword() throws ServiceException {
		if (isAnonymousLogin()) {
			return "anonymous";
		}

		return config.getSensitiveString(PASSWORD);
	}

	public String getUser() {
		if (this.isAnonymousLogin()) {
			return "anonymous";
		}

		return super.getUser();
	}

	public String getPassword() throws ServiceException {
		if (this.isAnonymousLogin()) {
			return "";
		}

		return super.getPassword();
	}

	public boolean isAnonymousLogin() {
		return config.getBoolean(ANONYMOUS_LOGIN);
	}

	public void setLocalDataTransferMode(DataTransferMode mode) {
		if (mode == null) {
			mode = DataTransferMode.PASSIVE;
		}
		config.set(LOCAL_DATA_TRANSFER_MODE, mode.toString());
	}

	public DataTransferMode getLocalDataTransferMode() {
		String mode = config.getString(LOCAL_DATA_TRANSFER_MODE);
		if (StringUtils.isNotEmpty(mode)) {
			return DataTransferMode.valueOf(mode);
		}
		return DataTransferMode.PASSIVE;
	}

	// public boolean isPassiveMode() {
	// return config.getBoolean(LOCAL_PASSIVE_MODE);
	// }

	public boolean isListHiddenFiles() {
		return config.getBoolean(LIST_HIDDEN_FILES);
	}

	public boolean isEnableUTF8Support() {
		return config.getBoolean(OPTS_UTF8_SUPPORT);
	}

	public void setEnableUTF8Support(boolean enable) {
		config.set(OPTS_UTF8_SUPPORT, enable);
	}

	public void setAnonymousLogin(boolean enable) {
		config.set(ANONYMOUS_LOGIN, enable);
	}

	// public void setPassiveMode(boolean enable) {
	// config.set(LOCAL_PASSIVE_MODE, enable);
	// }

	public void setListHiddenFiles(boolean enable) {
		config.set(LIST_HIDDEN_FILES, enable);
	}

	// public void setCharset(String charset) {
	// config.set(CHARSET, charset);
	// }
	//
	public void setControlEncoding(String encoding) {
		config.set(CONTROL_ENCODING, encoding);
	}

	//
	// public String getCharset() {
	// return config.getString(CHARSET);
	// }
	//
	public String getControlEncoding() {
		return config.getString(CONTROL_ENCODING);
	}

	// public String getLocalEncoding() {
	// return config.getString(LOCAL_ENCODING);
	// }
	//
	// public void setLocalEncoding(String encoding) {
	// config.set(LOCAL_ENCODING, encoding);
	// }

	// public String getRemoteEncoding() {
	// return config.getString(REMOTE_ENCODING);
	// }
	//
	// public void setRemoteEncoding(String encoding) {
	// config.set(REMOTE_ENCODING, encoding);
	// }

	// @Override
	// protected void validate() throws InvalidParameterException {
	// // if (!this.isAnonymousLogin()) {
	// // ValidUtils.errorIfEmpty(config.getString(USER), "User name must be specificed!");
	// // ValidUtils.errorIfEmpty(config.getString(PASSWORD), "Password must be specificed!");
	// // }
	// }

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new FTPFileSystemEntry(this, (FTPFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		FTPFileSystemPreference p = new FTPFileSystemPreference();
		// p.setLocalEncoding(getLocalEncoding());
		// p.setRemoteEncoding(getRemoteEncoding());
		p.setControlEncoding(this.getControlEncoding());
		return p;
	}

	public void setAccount(String account) {
		config.set(ACCOUNT, account);
	}

	public void setListingCommand(ListingCommand cmd) {
		if (cmd == null) {
			cmd = ListingCommand.LIST;
		}
		config.set(LISTING_COMMAND, cmd.toString());
		this.lcmd = cmd;
	}

	public String getAccount() {
		return config.getString(ACCOUNT);
	}

	public ListingCommand getListingCommand() {
		if (lcmd == null) {
			String cmd = config.getString(LISTING_COMMAND);
			if (StringUtils.isNotEmpty(cmd)) {
				lcmd = ListingCommand.valueOf(cmd);
			} else {
				lcmd = ListingCommand.LIST;
			}
		}

		return lcmd;
	}

	public void setLoginCommands(String cmds) {
		config.set(LOGIN_COMMANDS, cmds);
	}

	public String getLoginCommands() {
		return config.getString(LOGIN_COMMANDS);
	}

	public String[] getLoginCommandRows() {
		String[] logincmds;
		String cmds = config.getString(LOGIN_COMMANDS);
		if (StringUtils.isNotEmpty(cmds)) {
			cmds = cmds.replace("\r", "");
			logincmds = cmds.split("\n");
		} else {
			logincmds = new String[0];
		}
		return logincmds;
	}

	public void setBufferSizeInKb(int value) {
		config.set(BUFFER_SIZE_IN_KB, value);
	}

	public int getBufferSizeInKb() {
		return config.getInteger(BUFFER_SIZE_IN_KB, 1024);
	}

	public void setProtocol(Protocol protocol) {
		config.set(PROTOCOL, protocol == null ? Protocol.NO_ENCRYPTION.name() : protocol.name());
	}

	public Protocol getProtocol() {
		return Protocol.valueOf(config.getString(PROTOCOL, Protocol.NO_ENCRYPTION.name()));
	}

	public int getDefaultConnectionPoolSize() {
		return config.getInteger(DEFAULT_CONNECTION_POOL_SIZE, 2);
	}

	public void setDefaultConnectionPoolSize(int size) {
		config.set(DEFAULT_CONNECTION_POOL_SIZE, size);
	}

	public FileType getDefaultFileType() {
		return FileType.valueOf(config.getString(FILE_TYPE, FileType.BINARY.name()));
	}

	public void setDefaultFileType(FileType type) {
		config.set(FILE_TYPE, type == null ? FileType.BINARY.name() : type.name());
	}

	public void setTrustManager(TrustManager trustManager) {
		this.trustManager = trustManager;
	}

	public TrustManager getTrustManager() {
		return this.trustManager;
	}

}
