package com.amituofo.xfs.plugin.fs.objectstorage.azure.common;

public enum AuthenticateType {
	STORAGE_SHARED_KEY_CREDENTIAL("Storage Shared Key Credential"), AZURE_IDENTITY("Azure Identity");

	private String title;

	AuthenticateType(String name) {
		this.title = name;
	}

	public String toString() {
		return title;
	}
}