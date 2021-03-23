package com.amituofo.xfs.plugin.fs.objectstorage.hcp;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.EntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;
import com.amituofo.xfs.service.ProxyStatus;
import com.hitachivantara.core.http.Protocol;
import com.hitachivantara.core.http.client.ClientConfiguration;

public class HCPFileSystemEntryConfig extends OSDFileSystemEntryConfig {
	public static final String SYSTEM_NAME = "Hitachi Content Platform (HCP)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP08934870";

	public static final String HCP_VERSION = "HCP_VERSION";
	public static final String SHOW_DELETED_OBJECTS = "SHOW_DELETED_OBJECTS";
	private static final String ENABLE_PURGE_ON_DELETION = "ENABLE_PURGE_ON_DELETION";

	public static final String TENANT = "TENANT";
	public static final String DOMAIN = "DOMAIN";
	public static final String AUTHENTICATION_TYPE = "AUTHENTICATION_TYPE";
	// public static final String ENDPOINT = "ENDPOINT";

	public static enum AuthenticationType {
		Local, Active_Directory, Anonymous
	}

	public static final String[] HCP_VERSIONS = new String[] { "Auto detect",
			"Hitachi Content Platform 7.5.x",
			"Hitachi Content Platform 8.0.x",
			"Hitachi Content Platform 8.1.x or later" };

	public HCPFileSystemEntryConfig() {
		this("");
	}

	public HCPFileSystemEntryConfig(String name) {
		super(HCPFileSystemEntryConfig.class, SYSTEM_ID, name, URL_ROOT_PATH);
	}

	public HCPFileSystemEntryConfig(HCPFileSystemEntryConfig config) {
		this(config.getName());
		super.config.setSimpleConfiguration(config.getSimpleConfiguration());
		super.config.set(FILE_SYSTEM_ID, SYSTEM_ID);
	}

	public HCPFileSystemEntryConfig(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, String rootPath) {
		super(connConfigClass, fileSystemId, name, rootPath);
	}

	public String getNamespace() {
		return super.getBucketName();
	}

	public void setNamespace(String bucketName) {
		super.setBucketName(bucketName);
	}

	public int getHCPVersion() {
		return config.getInteger(HCP_VERSION);
	}

	public void setHCPVersion(int selectedVersion) {
		config.set(HCP_VERSION, selectedVersion);
	}

	public boolean isShowDeletedObjects() {
		return config.getBoolean(SHOW_DELETED_OBJECTS);
	}

	public boolean isPurgeDeletionEnabled() {
		return config.getBoolean(ENABLE_PURGE_ON_DELETION);
	}

	public void setShowDeletedObjects(boolean show) {
		config.set(SHOW_DELETED_OBJECTS, show);
	}

	public void setEnablePurgeDeletion(boolean selected) {
		config.set(ENABLE_PURGE_ON_DELETION, selected);
	}

	public String getTenant() {
		return config.getString(TENANT);
	}

	public String getDomain() {
		return config.getString(DOMAIN);
	}

	public String getEndpoint() {
		// if (StringUtils.isEmpty(super.getEndpoint())) {
		return this.getTenant() + "." + this.getDomain();
		// }

		// return super.getEndpoint();
	}

	@Override
	public String getHost() {
		return this.getNamespace() + "." + getEndpoint();
	}

	public void setTenant(String tenant) {
		config.set(TENANT, tenant);
	}

	public void setDomain(String domain) {
		config.set(DOMAIN, domain);
	}

	public void setAuthenticationType(AuthenticationType authType) {
		config.set(AUTHENTICATION_TYPE, authType.name());
	}

	public AuthenticationType getAuthenticationType() {
		String authtype = config.getString(AUTHENTICATION_TYPE);
		if (authtype == null || authtype.length() == 0) {
			return AuthenticationType.Local;
		}
		return AuthenticationType.valueOf(authtype);
	}

	public ClientConfiguration getClientConfiguration() {
		ClientConfiguration clientConfig = new ClientConfiguration();

		clientConfig.setProtocol(this.isUseSSL() ? Protocol.HTTPS : Protocol.HTTP);

		if (this.getConnectionTimeout() > 0) {
			// request timeout 导致传输大文件超时后失败
			// clientConfig.setRequestTimeout(this.getConnectionTimeout());
			clientConfig.setConnectTimeout(this.getConnectionTimeout());
		}

		if (ProxyStatus.DISABLED != this.getProxyStatus()) {
			if (ProxyStatus.USE_PRIVATE_SETTING == this.getProxyStatus()) {
				clientConfig.setProxy(this.getProxyHost(), this.getProxyPort());
				// 强制配置为忽略认证，否则错误
				clientConfig.ignoreHostnameVerification();
				clientConfig.ignoreSslVerification();
				this.setIgnoreSSLCertification(true);
				if (this.isProxyAuthenticationRequired()) {
					clientConfig.setProxyUsername(this.getProxyUsername());
					clientConfig.setProxyPassword(this.getProxyPassword());
				}
			} else if (ProxyStatus.USE_GLOBAL_SETTING == this.getProxyStatus()) {
				// 强制配置为忽略认证，否则错误
				this.setIgnoreSSLCertification(true);
				clientConfig.ignoreHostnameVerification();
				clientConfig.ignoreSslVerification();
				// String host = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyHost") :
				// System.getProperty("http.proxyHost"));
				// String port = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPort") :
				// System.getProperty("http.proxyPort"));
				// String usr = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyUser") :
				// System.getProperty("http.proxyUser"));
				// String pwd = (this.getProtocol() == Protocol.HTTPS ? System.getProperty("https.proxyPassword") :
				// System.getProperty("http.proxyPassword"));
				//
				// if (StringUtils.isNotEmpty(host) && StringUtils.isNotEmpty(port)) {
				// clientConfig.setProxy(host, Integer.parseInt(port));
				//
				// if (StringUtils.isNotEmpty(usr) && StringUtils.isNotEmpty(pwd)) {
				// clientConfig.setProxyUsername(usr);
				// clientConfig.setProxyPassword(pwd);
				// }
				// }
			}
		}
		return clientConfig;
	}

	@Override
	protected void validate() throws InvalidParameterException {
		super.validate();
		ValidUtils.invalidIfEmpty(config.getString(HCP_VERSION), "Hcp version must be specificed!");
		ValidUtils.invalidIfEmpty(this.getNamespace(), "Bucket/Namespace name must be specificed!");
		ValidUtils.invalidIfEqualsIgnoreCase(this.getTenant(), "default", "Default tenant unsupport by this version of xfs!");
		ValidUtils.invalidIfEqualsIgnoreCase(this.getNamespace(), "default", "Default namespace unsupport by this version of xfs!");

		// if(this.getAuthenticationType() == AuthenticationType.Active_Directory) {
		//
		// }
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new HCPFileSystemEntry(this, (HCPFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new HCPFileSystemPreference(this.isPurgeDeletionEnabled(), this.isShowDeletedObjects());
	}

}
