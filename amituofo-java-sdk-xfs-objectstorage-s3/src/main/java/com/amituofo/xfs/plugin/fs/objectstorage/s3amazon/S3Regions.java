package com.amituofo.xfs.plugin.fs.objectstorage.s3amazon;

import com.amazonaws.regions.Regions;

public class S3Regions {
	public final static S3Regions[] S3REGIONS;

	static {
		Regions[] REGIONS = Regions.values();
		S3REGIONS = new S3Regions[REGIONS.length];

		for (int i = 0; i < REGIONS.length; i++) {
			Regions region = REGIONS[i];
			S3REGIONS[i] = new S3Regions(region);
		}
	}

	private final Regions region;

	public S3Regions(Regions region) {
		this.region = region;
	}

	@Override
	public String toString() {
		return region.getDescription();// + " [" + region.name() + "]";
	}

	public Regions region() {
		return region;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((region == null) ? 0 : region.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		S3Regions other = (S3Regions) obj;
		if (region != other.region)
			return false;
		return true;
	}

}
