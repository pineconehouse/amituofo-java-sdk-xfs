package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.s3amazon.item.AmazonS3Bucketspace;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.S3ClientFactory;
import com.amituofo.xfs.service.ItemspaceConfig;

public class AmazonS3FileSystemEntry extends BasicS3FileSystemEntry<AmazonS3FileSystemEntryConfig, AmazonS3FileSystemPreference, AmazonS3Bucketspace> {
	public AmazonS3FileSystemEntry(AmazonS3FileSystemEntryConfig entryConfig, AmazonS3FileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected AmazonS3 createClient(
			Protocol awsProtocol,
			ClientConfiguration clientConfig,
			AmazonS3FileSystemEntryConfig s3EntryConfig) throws ServiceException {

		AmazonS3 hcpClient = S3ClientFactory.getInstance().getS3Client(awsProtocol,
				s3EntryConfig.isIgnoreSSLCertification(),
				s3EntryConfig.getRegion(),
				s3EntryConfig.getAccesskey(),
				s3EntryConfig.getSecretkey(),
				s3EntryConfig.isForceGlobalBucketAccessEnabled(),
				s3EntryConfig.isPathStyleAccessEnabled(),
				s3EntryConfig.isAccelerateModeEnabled(),
				s3EntryConfig.isDualstackEnabled(),
				s3EntryConfig.isPayloadSigningEnabled(),
//				s3EntryConfig.isUseArnRegionEnabled(),
//				s3EntryConfig.isRegionalUsEast1Endpoint(),
				clientConfig);
		return hcpClient;
	}

	@Override
	protected AmazonS3Bucketspace createS3BucketItem(
			BasicS3FileSystemEntry<AmazonS3FileSystemEntryConfig, AmazonS3FileSystemPreference, AmazonS3Bucketspace> s3DefaultFileSystemEntry,
			AmazonS3 s3Client,
			Bucket bucket) {
		return new AmazonS3Bucketspace((AmazonS3FileSystemEntry) s3DefaultFileSystemEntry, s3Client, bucket);
	}



}
