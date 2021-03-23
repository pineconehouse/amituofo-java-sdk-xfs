package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2;

import java.util.List;
import java.util.Map;

import com.amituofo.xfs.service.ItemspaceConfig;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2CorsRule;
import com.backblaze.b2.client.structures.B2CreateBucketRequest;
import com.backblaze.b2.client.structures.B2LifecycleRule;
import com.backblaze.b2.client.structures.B2UpdateBucketRequest;

public class B2BucketspaceConfig extends ItemspaceConfig {
	private B2CreateBucketRequest createRequest;
	private B2UpdateBucketRequest updateRequest;

	public B2BucketspaceConfig(String bucketName, B2UpdateBucketRequest updateRequest) {
		super(bucketName);
		this.updateRequest = updateRequest;
	}

	public B2BucketspaceConfig(String bucketName, B2CreateBucketRequest createRequest) {
		super(bucketName);
		this.createRequest = createRequest;
	}

	public static B2BucketspaceModifyConfigBuilder updateBuilder(B2Bucket bucket) {
		B2BucketspaceModifyConfigBuilder builder = new B2BucketspaceModifyConfigBuilder(bucket);
		return builder;
	}

	public static B2BucketspaceCreateConfigBuilder createBuilder(String bucketName, B2BucketType bucketType) {
		B2BucketspaceCreateConfigBuilder builder = new B2BucketspaceCreateConfigBuilder(bucketName, bucketType);
		return builder;
	}

	public B2CreateBucketRequest getCreateRequest() {
		return createRequest;
	}

	public B2UpdateBucketRequest getUpdateRequest() {
		return updateRequest;
	}

	public B2BucketType getBucketType() {
		if (createRequest != null)
			return B2BucketType.valueOf(createRequest.getBucketType());
		if (updateRequest != null)
			return B2BucketType.valueOf(updateRequest.getBucketType());
		return null;
	}

	public Map<String, String> getBucketInfo() {
		if (createRequest != null)
			return createRequest.getBucketInfo();
		if (updateRequest != null)
			return updateRequest.getBucketInfo();
		return null;
	}

	public List<B2CorsRule> getCorsRules() {
		if (createRequest != null)
			return createRequest.getCorsRules();
		if (updateRequest != null)
			return updateRequest.getCorsRules();
		return null;
	}

	public List<B2LifecycleRule> getLifecycleRules() {
		if (createRequest != null)
			return createRequest.getLifecycleRules();
		if (updateRequest != null)
			return updateRequest.getLifecycleRules();
		return null;
	}

}
