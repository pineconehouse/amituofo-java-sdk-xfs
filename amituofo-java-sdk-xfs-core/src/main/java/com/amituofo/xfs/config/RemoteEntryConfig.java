package com.amituofo.xfs.config;

import java.net.Proxy.Type;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.service.FileSystemType;
import com.amituofo.xfs.service.ProxyStatus;

public abstract class RemoteEntryConfig extends EntryConfigBase {
	public static final String CONNECTION_TIMEOUT = "_<_CONNECTION_TIMEOUT_>_";
	public static final String PROXY_TYPE = "PROXY_TYPE";
	public static final String PROXY_STATUS = "PROXY_STATUS";
	public static final String PROXY_HOST = "PROXY_HOST";
	public static final String PROXY_PORT = "PROXY_PORT";
	public static final String PROXY_AUTHENTICATION_REQUIRED = "PROXY_AUTHENTICATION_REQUIRED";
	public static final String PROXY_USERNAME = "PROXY_USERNAME";
	public static final String PROXY_PASSWORD = "PROXY_PASSWORD";

	public static final String HOST = "HOST";
	public static final String PORT = "PORT";
	public static final String HOME = "HOME";
	public static final String USER = "USER";
	public static final String PASSWORD = "PASSWORD";

	private int defaultPort = -1;

	public RemoteEntryConfig(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, String rootPath, char pathSeparator, int defaultPort) {
		super(connConfigClass, fileSystemId, name, FileSystemType.Remote, rootPath, pathSeparator);
		this.setPort(defaultPort);
		this.defaultPort = defaultPort;
	}

	public int getConnectionTimeout() {
		Integer timeout = config.getInteger(CONNECTION_TIMEOUT);
		if (timeout == null) {
			return -1;
		}

		return timeout;
	}

	public int getPort() {
		Integer port = config.getInteger(PORT);
		if (port == null) {
			return defaultPort;
		}

		return port;
	}

	public String getHost() {
		return config.getString(HOST);
	}

	public void setPort(int port) {
		config.set(PORT, port);
	}

	// public void setHost(String domain) {
	// config.set(HOST, domain);
	// }

	public void setHost(String domain) {
		if (StringUtils.isNotEmpty(domain)) {
			domain = domain.trim();
			String host = URLUtils.getRequestHostName(domain);
			config.set(HOST, host);
			int port = URLUtils.getRequestHostPort(domain, -1);
			if (port > 0) {
				this.setPort(port);
			}
		} else {
			config.set(HOST, "");
		}
	}

	public String getUser() {
		return config.getString(USER);
	}

	public String getPassword() throws ServiceException {
		return config.getSensitiveString(PASSWORD);
	}

	public String getDefaultHome() {
		return config.getString(HOME);
	}

	public void setDefaultHome(String namespace) {
		config.set(HOME, namespace);
	}

	public void setUser(String username) {
		config.set(USER, username);
	}

	public void setPassword(String password) throws ServiceException {
		config.setSensitive(PASSWORD, password);
	}

	public void setConnectionTimeout(int timeout) {
		config.set(CONNECTION_TIMEOUT, timeout);
	}

	public void setProxy(String host, int port) {
		config.set(PROXY_HOST, host);
		config.set(PROXY_PORT, port);
	}

	public void setProxyType(Type type) {
		config.set(PROXY_TYPE, type.name());
	}

	public void setProxyAuth(String username, String password) {
		if (StringUtils.isNotEmpty(username) && StringUtils.isNotEmpty(password)) {
			setProxyAuthenticationRequired(true);
			config.set(PROXY_USERNAME, username);
			config.set(PROXY_PASSWORD, password);
		}
	}

	public Type getProxyType() {
		String type = config.getString(PROXY_HOST);
		if (StringUtils.isNotEmpty(type)) {
			return Type.valueOf(type);
		}

		return null;
	}

	public String getProxyHost() {
		return config.getString(PROXY_HOST);
	}

	public int getProxyPort() {
		Integer port = config.getInteger(PROXY_PORT);
		if (port == null) {
			return -1;
		}
		return port.intValue();
	}

	public String getProxyUsername() {
		return config.getString(PROXY_USERNAME);
	}

	public String getProxyPassword() {
		return config.getString(PROXY_PASSWORD);
	}

	public Boolean isProxyAuthenticationRequired() {
		return config.getBoolean(PROXY_AUTHENTICATION_REQUIRED);
	}

	public void setProxyAuthenticationRequired(boolean enabled) {
		config.set(PROXY_AUTHENTICATION_REQUIRED, enabled);
	}

	public ProxyStatus getProxyStatus() {
		String status = config.getString(PROXY_STATUS);
		if (status == null) {
			return ProxyStatus.DISABLED;
		}
		return ProxyStatus.valueOf(status);
	}

	public void setProxyStatus(ProxyStatus status) {
		config.set(PROXY_STATUS, status.name());
	}

	@Override
	protected void validate() throws InvalidParameterException {
		ValidUtils.invalidIfEmpty(this.getHost(), "Host name or IP must be specificed!");
		ValidUtils.invalidIfLassThan(this.getPort(), 1L, "Port number must be specificed!");
	}

}
