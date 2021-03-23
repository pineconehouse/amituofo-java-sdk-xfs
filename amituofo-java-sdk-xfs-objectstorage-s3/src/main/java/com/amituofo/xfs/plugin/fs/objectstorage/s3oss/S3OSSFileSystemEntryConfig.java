package com.amituofo.xfs.plugin.fs.objectstorage.s3oss;

import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.CompatibleS3FileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class S3OSSFileSystemEntryConfig extends CompatibleS3FileSystemEntryConfig {
	public static final String SYSTEM_NAME = "S3 Compatible Storage Cloud Service (OSS)";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP938572619";

	public static final String OSS_PROVIDER_NAME = "OSS_PROVIDER_NAME";
	public static final String ENDPOINT_TYPE = "ENDPOINT_TYPE";
	public static final String ENDPOINT_ID = "ENDPOINT_ID";
	public static final String CUSTOM_ENDPOINT = "CUSTOM_ENDPOINT";

	// public static final String FORCE_GLOBAL_BUCKET_ACCESS = "FORCE_GLOBAL_BUCKET_ACCESS";

	public S3OSSFileSystemEntryConfig() {
		this("");
	}

	public S3OSSFileSystemEntryConfig(String name) {
		super(S3OSSFileSystemEntryConfig.class, SYSTEM_ID, name, "");
		this.setUseSSL(true);
	}

	// public boolean isForceGlobalBucketAccess() {
	// return config.getBoolean(FORCE_GLOBAL_BUCKET_ACCESS);
	// }
	//
	// public void setForceGlobalBucketAccess(boolean forceGlobalBucketAccess) {
	// config.set(FORCE_GLOBAL_BUCKET_ACCESS, forceGlobalBucketAccess);
	// }

	public S3OSSProvider getOSSProvider() {
		String ossName = config.getString(OSS_PROVIDER_NAME);
		if (StringUtils.isNotEmpty(ossName)) {
			return S3OSSProviderManagement.getOSSProvider(ossName);
		}

		return S3OSSProviderManagement.getOSSProviders()[0];
	}

	@Override
	public String getHost() {
		String host = super.getHost();
		if(StringUtils.isEmpty(host)) {
			String endpoint = this.getEndpoint();
			if(getProtocol()==com.amituofo.common.define.Protocol.HTTPS) {
				host=endpoint.substring(8);
			} else {
				host=endpoint.substring(7);
			}
			
			super.setHost(host);
		}
		
		return host;
	}

	public S3OSSEndpoint getOSSEndpoint() {
		String ossName = config.getString(OSS_PROVIDER_NAME);
		if (StringUtils.isNotEmpty(ossName)) {
			S3OSSProvider ossProvider = S3OSSProviderManagement.getOSSProvider(ossName);
			if (ossProvider != null) {
				String type = config.getString(ENDPOINT_TYPE);
				S3OSSEndpoint[] endpoints = ossProvider.getEndpoints(type);

				String endpointName = config.getString(ENDPOINT_ID);

				for (S3OSSEndpoint ossEndpoint : endpoints) {
					if (endpointName.equalsIgnoreCase(ossEndpoint.getName())) {
						return ossEndpoint;
					}
				}
			}
		}

		return S3OSSProviderManagement.getOSSProviders()[0].getDefaultEndpoint();
	}

	public void setOSSEndpoint(S3OSSEndpoint s3endpoint) {
		config.set(OSS_PROVIDER_NAME, s3endpoint.getOssProvider().getName());
		config.set(ENDPOINT_TYPE, s3endpoint.getEndpointCategory());
		config.set(ENDPOINT_ID, s3endpoint.getName());
		super.setEndpoint(s3endpoint.getEndpoint());
	}

	public void setCustomEndpoint(boolean enable) {
		config.set(CUSTOM_ENDPOINT, enable);
	}

	public boolean isCustomEndpoint() {
		return config.getBoolean(CUSTOM_ENDPOINT);
	}

	@Override
	public String getEndpoint() {
		String endpoint = super.getEndpoint();
		if (StringUtils.isEmpty(endpoint)) {
			return "";
		}

		endpoint = endpoint.toLowerCase();
		if (!endpoint.startsWith("http")) {
			com.amituofo.common.define.Protocol protocol = getProtocol();

			return protocol.name().toLowerCase() + "://" + endpoint;
		}

		return endpoint;
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new S3OSSFileSystemEntry(this, (S3OSSFileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new S3OSSFileSystemPreference(this.getEndpoint());
	}

}
