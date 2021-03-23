package com.amituofo.xfs.config;

import com.amituofo.common.define.Protocol;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.URLUtils;

public abstract class HTTPEntryConfig extends RemoteEntryConfig {
	public static final String USE_SSL = "USE_SSL";
	public static final String IGNORE_SSL_CERTIFICATION = "IGNORE_SSL_CERTIFICATION";

	public HTTPEntryConfig(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, String rootPath) {
		super(connConfigClass, fileSystemId, name, rootPath, URL_PS, 80);
		this.setIgnoreSSLCertification(true);
	}

	public Protocol getProtocol() {
		return config.getBoolean(USE_SSL) ? Protocol.HTTPS : Protocol.HTTP;
	}

	public String getHttpHostUrl() {
		String url = getProtocol().toString() + "://" + getHost();
		int port = getPort();
		if (port != 80) {
			return url + ":" + port;
		} else {
			return url;
		}
	}

	@Override
	public void setHost(String domain) {
		super.setHost(domain);
		int port = URLUtils.getRequestHostPort(domain, this.getPort());
		if (StringUtils.isNotEmpty(domain)) {
			String tmp = domain.toLowerCase();
			if (tmp.startsWith("http://")) {
				this.setUseSSL(false);
			} else if (tmp.startsWith("https://")) {
				this.setUseSSL(true);
			}
		}

		super.setPort(port);
	}

	public boolean isUseSSL() {
		return config.getBoolean(USE_SSL);
	}

	public boolean isIgnoreSSLCertification() {
		return config.getBoolean(IGNORE_SSL_CERTIFICATION);
	}

	public void setUseSSL(boolean useSSL) {
		config.set(USE_SSL, useSSL);
		if (useSSL) {
			this.setPort(443);
		}
	}

	public void setIgnoreSSLCertification(boolean ignore) {
		config.set(IGNORE_SSL_CERTIFICATION, ignore);
	}

}
