package com.amituofo.xfs.plugin.fs.webdav;

import com.amituofo.xfs.config.HTTPEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class WebDavFileSystemEntryConfig extends HTTPEntryConfig {
	public static final String SYSTEM_NAME = "Web-based Distributed Authoring and Versioning File System (WebDav)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP957369395";

	public WebDavFileSystemEntryConfig() {
		super(WebDavFileSystemEntryConfig.class, SYSTEM_ID, "", URL_ROOT_PATH);
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new WebDavFileSystemEntry(this, (WebDavFileSystemPreference) perference);
	}

	@Override
	public WebDavFileSystemPreference createPreference() {
		return new WebDavFileSystemPreference();
	}

//	public ClientConfiguration getClientConfiguration() {
//		ClientConfiguration clientConfig = new ClientConfiguration();
//
//		clientConfig.setProtocol(this.isUseSSL() ? Protocol.HTTPS : Protocol.HTTP);
//
//		if (this.getConnectionTimeout() > 0) {
//			// request timeout 导致传输大文件超时后失败
//			// clientConfig.setRequestTimeout(this.getConnectionTimeout());
//			clientConfig.setConnectTimeout(this.getConnectionTimeout());
//		}
//
//		if (ProxyStatus.DISABLED != this.getProxyStatus()) {
//			if (ProxyStatus.USE_PRIVATE_SETTING == this.getProxyStatus()) {
//				clientConfig.setProxy(this.getProxyHost(), this.getProxyPort());
//				// 强制配置为忽略认证，否则错误
//				clientConfig.ignoreHostnameVerification();
//				clientConfig.ignoreSslVerification();
//				this.setIgnoreSSLCertification(true);
//				if (this.isProxyAuthenticationRequired()) {
//					clientConfig.setProxyUsername(this.getProxyUsername());
//					clientConfig.setProxyPassword(this.getProxyPassword());
//				}
//			} else if (ProxyStatus.USE_GLOBAL_SETTING == this.getProxyStatus()) {
//				// 强制配置为忽略认证，否则错误
//				this.setIgnoreSSLCertification(true);
//				clientConfig.ignoreHostnameVerification();
//				clientConfig.ignoreSslVerification();
//				// String host = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyHost") :
//				// System.getProperty("http.proxyHost"));
//				// String port = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPort") :
//				// System.getProperty("http.proxyPort"));
//				// String usr = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyUser") :
//				// System.getProperty("http.proxyUser"));
//				// String pwd = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPassword") :
//				// System.getProperty("http.proxyPassword"));
//				//
//				// if (StringUtils.isNotEmpty(host) && StringUtils.isNotEmpty(port)) {
//				// clientConfig.setProxy(host, Integer.parseInt(port));
//				//
//				// if (StringUtils.isNotEmpty(usr) && StringUtils.isNotEmpty(pwd)) {
//				// clientConfig.setProxyUsername(usr);
//				// clientConfig.setProxyPassword(pwd);
//				// }
//				// }
//			}
//		}
//		return clientConfig;
//	}

}
