package com.amituofo.xfs.plugin.fs.objectstorage;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.EntryConfig;
import com.amituofo.xfs.config.HTTPEntryConfig;

public abstract class OSDFileSystemEntryConfig extends HTTPEntryConfig {
	public static final String NAMESPACE = "NAMESPACE";
//	public static final String AK = "USER";
//	public static final String SK = "PASSWORD";

	// private RootItem DEFAULT_SECTION = new DefaultSection(URL_ROOT_PATH);

	public OSDFileSystemEntryConfig(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, String rootPath) {
		super(connConfigClass, fileSystemId, name, rootPath);
	}

	public abstract String getEndpoint();

	public String getBucketName() {
		return config.getString(NAMESPACE);
	}

	public String getAccesskey() {
		return super.getUser();
	}

	public String getSecretkey() throws ServiceException {
		return super.getPassword();
	}

	public void setBucketName(String bucketName) {
		config.set(NAMESPACE, bucketName);
	}

	public void setAccesskey(String userInBase64) {
		super.setUser(userInBase64);
	}

	public void setSecretkey(String passwordInMd5) throws ServiceException {
		super.setPassword(passwordInMd5);
	}

	@Override
	protected void validate() throws InvalidParameterException {
		super.validate();
		ValidUtils.invalidIfEmpty(this.getAccesskey(), "Accesskey must be specificed!");
		try {
			ValidUtils.invalidIfEmpty(this.getSecretkey(), "Secretkey must be specificed!");
		} catch (Exception e) {
			// e.printStackTrace();
			throw new InvalidParameterException(e);
		}
	}

}
