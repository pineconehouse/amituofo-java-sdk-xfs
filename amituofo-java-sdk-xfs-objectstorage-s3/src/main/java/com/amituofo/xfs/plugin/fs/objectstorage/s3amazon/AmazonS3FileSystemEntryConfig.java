package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon;

import com.amazonaws.regions.Regions;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.common.util.ValidUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemEntryConfig;
import com.amituofo.xfs.service.FileSystemEntry;
import com.amituofo.xfs.service.FileSystemPreference;

public class AmazonS3FileSystemEntryConfig extends BasicS3FileSystemEntryConfig {
	public static final String SYSTEM_NAME = "Amazon S3 Storage";
	// !!!!!!!!!!!!! Never modify this ID; !!!!!!!!!!!!!
	public static final String SYSTEM_ID = "HSP70932487";

	public static final String REGION = "REGION";

	public static final String FORCE_GLOBAL_BUCKET_ACCESS = "FORCE_GLOBAL_BUCKET_ACCESS";
	public static final String ACCELERATE_MODE = "ACCELERATE_MODE";
	public static final String DUAL_STACK = "DUAL_STACK";
	public static final String PAYLOAD_SIGNING = "PAYLOAD_SIGNING";
	// public static final String USE_ARN_REGION = "USE_ARN_REGION";
	// public static final String REGIONAL_USEAST1_ENDPOINT = "REGIONAL_USEAST1_ENDPOINT";

	public static final Regions[] REGIONS = Regions.values();

	public static final String[] ACCOUNT_TYPES = new String[] { "Amazon S3 Storage" };

	public AmazonS3FileSystemEntryConfig() {
		this("");
	}

	public AmazonS3FileSystemEntryConfig(String name) {
		super(AmazonS3FileSystemEntryConfig.class, SYSTEM_ID, name, "");
		this.setUseSSL(true);
	}

	public boolean isForceGlobalBucketAccessEnabled() {
		return config.getBoolean(FORCE_GLOBAL_BUCKET_ACCESS);
	}

	public void setForceGlobalBucketAccessEnabled(boolean forceGlobalBucketAccess) {
		config.set(FORCE_GLOBAL_BUCKET_ACCESS, forceGlobalBucketAccess);
	}

	public boolean isAccelerateModeEnabled() {
		return config.getBoolean(ACCELERATE_MODE);
	}

	public void setAccelerateModeEnabled(boolean enable) {
		config.set(ACCELERATE_MODE, enable);
	}

	public boolean isDualstackEnabled() {
		return config.getBoolean(DUAL_STACK);
	}

	public void setDualstackEnabled(boolean enable) {
		config.set(DUAL_STACK, enable);
	}

	public boolean isPayloadSigningEnabled() {
		return config.getBoolean(PAYLOAD_SIGNING);
	}

	public void setPayloadSigningEnabled(boolean enable) {
		config.set(PAYLOAD_SIGNING, enable);
	}

	// public boolean isUseArnRegionEnabled() {
	// return config.getBoolean(USE_ARN_REGION);
	// }
	//
	// public void setUseArnRegionEnabled(boolean enable) {
	// config.set(USE_ARN_REGION, enable);
	// }
	//
	// public boolean isRegionalUsEast1Endpoint() {
	// return config.getBoolean(REGIONAL_USEAST1_ENDPOINT);
	// }
	//
	// public void setRegionalUsEast1Endpoint(boolean enable) {
	// config.set(REGIONAL_USEAST1_ENDPOINT, enable);
	// }

	public Regions getRegion() {
		String regionName = config.getString(REGION);
		if (StringUtils.isNotEmpty(regionName)) {
			return Regions.valueOf(regionName);
		}

		return Regions.DEFAULT_REGION;
	}

	public void setRegion(Regions regions) {
		config.set(REGION, regions.name());
		// super.setEndpoint(regions.name());
	}

	@Override
	public FileSystemEntry createFileSystemEntry(FileSystemPreference perference) {
		return new AmazonS3FileSystemEntry(this, (AmazonS3FileSystemPreference) perference);
	}

	@Override
	public FileSystemPreference createPreference() {
		return new AmazonS3FileSystemPreference(this.getRegion());
	}

	@Override
	protected void validate() throws InvalidParameterException {
		// DO not call super
		// super.validate();
		ValidUtils.invalidIfEmpty(this.getAccesskey(), "Accesskey must be specificed!");
		try {
			ValidUtils.invalidIfEmpty(this.getSecretkey(), "Secretkey must be specificed!");
		} catch (Exception e) {
			// e.printStackTrace();
			throw new InvalidParameterException(e);
		}
	}

}
