package com.amituofo.xfs.plugin.fs.objectstorage.s3compatible;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.Bucket;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.util.StringUtils;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemEntry;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.S3ClientFactory;
import com.amituofo.xfs.plugin.fs.objectstorage.s3compatible.item.CompatibleS3Bucketspace;

public class CompatibleS3FileSystemEntry extends BasicS3FileSystemEntry<CompatibleS3FileSystemEntryConfig, CompatibleS3FileSystemPreference, CompatibleS3Bucketspace> {

	public CompatibleS3FileSystemEntry(CompatibleS3FileSystemEntryConfig entryConfig, CompatibleS3FileSystemPreference preference) {
		super(entryConfig, preference);
	}

	@Override
	protected AmazonS3 createClient(
			Protocol awsProtocol,
			ClientConfiguration clientConfig,
			CompatibleS3FileSystemEntryConfig s3EntryConfig) throws ServiceException {

		if (StringUtils.isNotEmpty(s3EntryConfig.getSignerType())) {
			clientConfig.setSignerOverride(s3EntryConfig.getSignerType());
		}

		AmazonS3 hcpClient = S3ClientFactory.getInstance().getS3Client(awsProtocol,
				s3EntryConfig.isIgnoreSSLCertification(),
				s3EntryConfig.getEndpoint(),
				s3EntryConfig.getAccesskey(),
				s3EntryConfig.getSecretkey(),
				s3EntryConfig.isPathStyleAccessEnabled(),
				clientConfig);
		return hcpClient;
	}

	// @Override
	// public void close() throws ServiceException {
	// S3ClientFactory.getInstance().clearCache(CompatibleS3FileSystemEntryConfig.SYSTEM_ID);
	// }

	@Override
	protected CompatibleS3Bucketspace createS3BucketItem(
			BasicS3FileSystemEntry<CompatibleS3FileSystemEntryConfig, CompatibleS3FileSystemPreference, CompatibleS3Bucketspace> s3DefaultFileSystemEntry,
			AmazonS3 s3Client,
			Bucket bucket) {
		return new CompatibleS3Bucketspace((CompatibleS3FileSystemEntry) s3DefaultFileSystemEntry, s3Client, bucket);
	}

}
