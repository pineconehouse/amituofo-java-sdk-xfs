package com.amituofo.xfs.plugin.fs.objectstorage.s3oss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class S3OSSProvider {
	private String provider;
	private String providerName;
	private Map<String, List<S3OSSEndpoint>> supportEndpoints = new HashMap<String, List<S3OSSEndpoint>>();

	public S3OSSProvider(String provider, String providerName) {
		this.provider = provider;
		this.providerName = providerName;
	}

	public S3OSSEndpoint[] getEndpoints(String type) {
		List<S3OSSEndpoint> endpoints = supportEndpoints.get(type);
		if (endpoints == null) {
			endpoints = new ArrayList<S3OSSEndpoint>();
			supportEndpoints.put(type, endpoints);
		}
		return endpoints.toArray(new S3OSSEndpoint[endpoints.size()]);
	}

	public void addEndpoint(String type, S3OSSEndpoint s3endpoint) {
		List<S3OSSEndpoint> endpoints = supportEndpoints.get(type);
		if (endpoints == null) {
			endpoints = new ArrayList<S3OSSEndpoint>();
			supportEndpoints.put(type, endpoints);
		}

		endpoints.add(s3endpoint);
	}

	public String toString() {
		return providerName;
	}

	public String getName() {
		return provider;
	}

	public String[] getEndpointTypes() {
		return supportEndpoints.keySet().toArray(new String[supportEndpoints.size()]);
	}

	public S3OSSEndpoint getDefaultEndpoint() {
		List<S3OSSEndpoint> list = supportEndpoints.get("Default Endpoint");
		if (list != null) {
			return list.get(0);
		}

		String[] types = getEndpointTypes();
		if (types.length > 0) {
			list = supportEndpoints.get(types[0]);
			if (list != null) {
				return list.get(0);
			}
		}

		return null;
	}

}
