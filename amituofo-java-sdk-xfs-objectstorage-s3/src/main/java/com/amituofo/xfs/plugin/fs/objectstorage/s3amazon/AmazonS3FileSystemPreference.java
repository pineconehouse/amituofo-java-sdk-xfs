package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon;

import com.amazonaws.regions.Regions;
import com.amituofo.xfs.plugin.fs.objectstorage.s3common.BasicS3FileSystemPreference;

public class AmazonS3FileSystemPreference extends BasicS3FileSystemPreference {
	private Regions region;

	public AmazonS3FileSystemPreference(Regions region) {
		super(region.name());
		this.region = region;
	}

	public Regions getRegion() {
		return region;
	}

}
