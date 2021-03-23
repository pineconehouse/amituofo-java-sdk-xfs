package com.amituofo.xfs.plugin.fs.objectstorage.s3oss;

public class S3OSSEndpoint {
	private String endpoint;
	private String endpointShort;
	private boolean supportHTTP;
	private boolean supportHTTPS;
	private S3OSSProvider ossProvider;
	private String endpointCategory;
	private String description;

	public S3OSSEndpoint(S3OSSProvider ossProvider, String endpointType, String endpointShort, String endpoint, String description, boolean supportHTTP, boolean supportHTTPS) {
		super();
		this.ossProvider = ossProvider;
		this.endpointCategory = endpointType;
		this.endpointShort = endpointShort;
		this.endpoint = endpoint;
		this.description = description;
		this.supportHTTP = supportHTTP;
		this.supportHTTPS = supportHTTPS;
	}

	public String toString() {
		return endpointShort + " [" + description + "]";
	}

	public S3OSSProvider getOssProvider() {
		return ossProvider;
	}

	public String getEndpointCategory() {
		return endpointCategory;
	}

	public String getName() {
		return endpointShort;
	}

	public String getDescription() {
		return description;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public String getEndpointShort() {
		return endpointShort;
	}

	public boolean isSupportHTTP() {
		return supportHTTP;
	}

	public boolean isSupportHTTPS() {
		return supportHTTPS;
	}

}
