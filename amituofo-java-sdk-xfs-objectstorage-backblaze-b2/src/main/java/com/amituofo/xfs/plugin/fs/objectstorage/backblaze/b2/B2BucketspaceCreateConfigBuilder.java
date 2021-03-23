package com.amituofo.xfs.plugin.fs.objectstorage.backblaze.b2;

import java.util.List;
import java.util.Map;

import com.amituofo.common.ex.InvalidConfigException;
import com.amituofo.common.ex.InvalidParameterException;
import com.amituofo.xfs.service.impl.DefaultItemspaceConfigBuilder;
import com.backblaze.b2.client.structures.B2CorsRule;
import com.backblaze.b2.client.structures.B2CreateBucketRequest;
import com.backblaze.b2.client.structures.B2LifecycleRule;

public class B2BucketspaceCreateConfigBuilder extends DefaultItemspaceConfigBuilder<B2BucketspaceConfig> {
	private B2CreateBucketRequest.Builder builder;

	public B2BucketspaceCreateConfigBuilder(String bucketName, B2BucketType bucketType) {
		super(bucketName);
		builder = B2CreateBucketRequest.builder(bucketName, (bucketType == null ? B2BucketType.allPrivate.name() : bucketType.name()));
	}

	@Override
	public B2BucketspaceConfig bulid() throws InvalidConfigException {
		return new B2BucketspaceConfig(this.getItemspaceName(), builder.build());
	}

	public B2BucketspaceCreateConfigBuilder withBucketInfo(Map<String, String> bucketInfo) {
		builder.setBucketInfo(bucketInfo);
		return this;
	}

	public B2BucketspaceCreateConfigBuilder withCorsRules(List<B2CorsRule> corsRules) {
		builder.setCorsRules(corsRules);
		return this;
	}

	public B2BucketspaceCreateConfigBuilder withLifecycleRules(List<B2LifecycleRule> lifecycleRules) {
		builder.setLifecycleRules(lifecycleRules);
		return this;
	}

}
