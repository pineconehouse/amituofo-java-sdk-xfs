package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2;

import java.util.List;
import java.util.Map;

import com.amituofo.common.ex.InvalidConfigException;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.xfs.service.ItemspaceConfigBuilder;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2CorsRule;
import com.backblaze.b2.client.structures.B2LifecycleRule;
import com.backblaze.b2.client.structures.B2UpdateBucketRequest;

public class B2BucketspaceModifyConfigBuilder implements ItemspaceConfigBuilder<B2BucketspaceConfig> {
	private B2UpdateBucketRequest.Builder builder;
	private B2Bucket bucket;

	public B2BucketspaceModifyConfigBuilder(B2Bucket bucket) {
		this.bucket = bucket;
		builder = B2UpdateBucketRequest.builder(bucket);
	}

	@Override
	public B2BucketspaceConfig bulid() throws InvalidConfigException {
		return new B2BucketspaceConfig(bucket.getBucketName(), builder.build());
	}

	public B2BucketspaceModifyConfigBuilder withBucketType(B2BucketType bucketType) {
		if (bucketType == null) {
			bucketType = B2BucketType.allPrivate;
		}

		builder.setBucketType(bucketType.name());
		return this;
	}

	public B2BucketspaceModifyConfigBuilder withBucketInfo(Map<String, String> bucketInfo) {
		builder.setBucketInfo(bucketInfo);
		return this;
	}

	public B2BucketspaceModifyConfigBuilder withCorsRules(List<B2CorsRule> corsRules) {
		builder.setCorsRules(corsRules);
		return this;
	}

	public B2BucketspaceModifyConfigBuilder withLifecycleRules(List<B2LifecycleRule> lifecycleRules) {
		builder.setLifecycleRules(lifecycleRules);
		return this;
	}
}
