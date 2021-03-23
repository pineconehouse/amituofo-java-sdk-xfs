package com.amituofo.xfs.plugin.fs.objectstorage.s3common;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.config.EntryConfig;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;

public abstract class BasicS3FileSystemEntryConfig extends OSDFileSystemEntryConfig {
	public static final String ENDPOINT = "ENDPOINT";
	public static final String PATH_STYLE_ACCESS = "PATH_STYLE_ACCESS";
//	public static final String DECODE_PATH_ENABLED = "DECODE_PATH_ENABLED";

	public BasicS3FileSystemEntryConfig(Class<? extends EntryConfig> connConfigClass, String fileSystemId, String name, String rootPath) {
		super(connConfigClass, fileSystemId, name, rootPath);
	}

	public String getEndpoint() {
		return config.getString(ENDPOINT);
	}

	public void setEndpoint(String endpoint) {
		if (StringUtils.isNotEmpty(endpoint)) {
			endpoint = endpoint.trim();
			config.set(ENDPOINT, endpoint);
		} else {
			config.set(ENDPOINT, "");
		}
		super.setHost(endpoint);
	}

	public boolean isPathStyleAccessEnabled() {
		return config.getBoolean(PATH_STYLE_ACCESS);
	}

	public void setPathStyleAccessEnabled(boolean enable) {
		config.set(PATH_STYLE_ACCESS, enable);
	}

//	public boolean isDecodePathEnabled() {
//		return config.getBoolean(DECODE_PATH_ENABLED);
//	}
//
//	public void setDecodePathEnabled(boolean enabled) {
//		config.set(DECODE_PATH_ENABLED, enabled);
//	}

	@Override
	protected void validate() throws InvalidParameterException {
		super.validate();
		ValidUtils.invalidIfEmpty(this.getEndpoint(), "Endpoint must be specificed!");
	}

}
