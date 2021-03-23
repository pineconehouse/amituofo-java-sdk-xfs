package com.amituofo.xfs.plugin.fs.objectstorage.azure.common;

import java.util.Locale;

public enum AzureStorageServiceKind {

	BlobService("blob"), FileService("file"), QueueService("queue"), TableService("table"), DataLakeStorage("dfs"), StaticWebsite("web");

	private String defaultEndpoint1;
	private String defaultEndpoint2;

	AzureStorageServiceKind(String service) {
		this.defaultEndpoint1 = "https://%s." + service + ".core.windows.net";
		this.defaultEndpoint2 = "https://%s-secondary." + service + ".core.windows.net";
	}

	public String getPrimaryEndpointUrl(String accountName) {
		return String.format(Locale.ROOT, defaultEndpoint1, accountName);
	}

	public String getSecondaryEndpointUrl(String accountName) {
		return String.format(Locale.ROOT, defaultEndpoint2, accountName);
	}
}
