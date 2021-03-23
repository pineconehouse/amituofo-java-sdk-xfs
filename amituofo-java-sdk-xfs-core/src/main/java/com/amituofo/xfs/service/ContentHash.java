package com.amituofo.xfs.service;

public class ContentHash {
	String algorithm;
	String hash;

	public ContentHash(String algorithm, String hash) {
		super();
		this.algorithm = algorithm;
		this.hash = hash == null ? "" : hash;
	}

	public ContentHash(String eTagOrMD5Hash) {
		this("MD5", eTagOrMD5Hash);
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public String getHashCode() {
		return hash;
	}

}
