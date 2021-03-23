package com.amituofo.xfs.plugin.fs.objectstorage.azure.common;

import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileSystemEntryConfig;

public abstract class AzureStorageFileSystemEntryConfig extends OSDFileSystemEntryConfig {
	private static final String AUTHENTICATE_TYPE = "AUTHENTICATE_TYPE";
	private static final String ENDPOINT = "ENDPOINT";
	// private static final String AZURE_STORAGE_SERVICE_TYPE = "AZURE_STORAGE_SERVICE_TYPE";
	private static final String ENDPOINT_TYPE = "ENDPOINT_TYPE";

	public AzureStorageFileSystemEntryConfig(String systemid, Class<? extends AzureStorageFileSystemEntryConfig> connConfigClass) {
		this(systemid, connConfigClass, "");
	}

	public AzureStorageFileSystemEntryConfig(String systemid, Class<? extends AzureStorageFileSystemEntryConfig> connConfigClass, String name) {
		super(connConfigClass, systemid, name, URL_ROOT_PATH);
		// super.config.set(AZURE_STORAGE_SERVICE_TYPE, serviceType.name());
	}

	public AzureStorageFileSystemEntryConfig(AzureStorageFileSystemEntryConfig config, Class<? extends AzureStorageFileSystemEntryConfig> connConfigClass) {
		this(config.getFileSystemId(), connConfigClass, config.getName());
		super.config.setSimpleConfiguration(config.getSimpleConfiguration());
		super.config.set(FILE_SYSTEM_ID, config.getFileSystemId());
		// super.config.set(AZURE_STORAGE_SERVICE_TYPE, serviceType.name());
	}

	// public AzureFileSystemEntryConfig(Class<? extends EntryConfig> connConfigClass,
	// String fileSystemId,
	// String name,
	// FileSystemType fileSystemType,
	// String rootPath,
	// char pathSeparator) {
	// super(connConfigClass, fileSystemId, name, fileSystemType, rootPath, pathSeparator);
	// }

	public abstract AzureStorageServiceKind getAzureStorageServiceType();
	// {
	// String st = config.getString(AZURE_STORAGE_SERVICE_TYPE);
	// return AzureStorageServiceType.valueOf(st);
	// }

	@Override
	protected void validate() throws InvalidParameterException {
		super.validate();
		ValidUtils.invalidIfEmpty(this.getContainerName(), "Container name must be specificed!");
	}

	public EndpointKind getEndpointType() {
		String at = config.getString(ENDPOINT_TYPE);
		if (at == null) {
			return EndpointKind.PrimaryEndpoint;
		}

		return EndpointKind.valueOf(at);
	}

	public void setEndpointType(EndpointKind endpointType) {
		config.set(ENDPOINT_TYPE, endpointType.name());
	}

	public String getEndpoint() {
		EndpointKind endpointType = getEndpointType();
		switch (endpointType) {
			case SecondaryEndpoint:
				String accountName1 = this.getAccountName();
				AzureStorageServiceKind st1 = getAzureStorageServiceType();
				return st1.getSecondaryEndpointUrl(accountName1);
			case CustomEndpoint:
				String forceEndpoint = config.getString(ENDPOINT);
				return forceEndpoint;
			case PrimaryEndpoint:
			default:
				String accountName = this.getAccountName();
				AzureStorageServiceKind st = getAzureStorageServiceType();
				return st.getPrimaryEndpointUrl(accountName);
		}
	}

	public void setEndpoint(String endpoint) {
		config.set(ENDPOINT, endpoint);
		super.setHost(getEndpoint());
	}

	// public String getEndpoint(String accountName) {
	// String endpointurl;
	// String forceEndpoint = getEndpoint();
	// if (forceEndpoint != null) {
	// endpointurl = forceEndpoint;
	// } else {
	// AzureStorageServiceType st = getAzureStorageServiceType();
	// endpointurl = isUseSecondEndpoint() ? st.getSecondaryEndpointUrl(accountName) : st.getPrimaryEndpointUrl(accountName);
	// }
	// return endpointurl;
	// }

	public String getContainerName() {
		return super.getBucketName();
	}

	public void setContainerName(String containerName) {
		super.setBucketName(containerName);
	}

	public void setAccountName(String accountName) {
		super.setAccesskey(accountName);
	}

	public String getAccountName() {
		return super.getAccesskey();
	}

	// @Override
	// public String getEndpoint() {
	// String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", getAccountName());
	// return endpoint;
	// }

	public AuthenticateType getAuthenticateType() {
		String at = config.getString(AUTHENTICATE_TYPE);
		if (at == null) {
			return AuthenticateType.STORAGE_SHARED_KEY_CREDENTIAL;
		}

		return AuthenticateType.valueOf(at);
	}

	public void setAuthenticateType(AuthenticateType credentialType) {
		if (StringUtils.isEmpty(getAccountName())) {
			try {
				super.setSecretkey("powered-by-rison-han");
			} catch (ServiceException e) {
			}
		}
		config.set(AUTHENTICATE_TYPE, credentialType.name());
	}

}
