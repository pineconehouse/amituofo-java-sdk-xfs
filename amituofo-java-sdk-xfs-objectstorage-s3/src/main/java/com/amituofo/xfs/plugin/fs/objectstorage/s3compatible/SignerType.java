package com.amituofo.xfs.plugin.fs.objectstorage.s3compatible;

import com.amituofo.common.util.StringUtils;

public enum SignerType {
	Default("Default", null), SignatureVersion4("Signature Version 4", "AWSS3V4SignerType"), SignatureVersion2("Signature Version 2", "S3SignerType");

	private String title;
	private String signerType;

	SignerType(String title, String signerType) {
		this.title = title;
		this.signerType = signerType;
	}

	public String getSignerType() {
		return signerType;
	}

	@Override
	public String toString() {
		return title;
	}
	
	public static SignerType valueOfSignerType(String signerType ) {
		if(StringUtils.isEmpty(signerType)) {
			return Default;
		}
		
		if(SignatureVersion4.signerType.equals(signerType)) {
			return SignatureVersion4;
		}
		
		return SignatureVersion2;
	}
	
}
