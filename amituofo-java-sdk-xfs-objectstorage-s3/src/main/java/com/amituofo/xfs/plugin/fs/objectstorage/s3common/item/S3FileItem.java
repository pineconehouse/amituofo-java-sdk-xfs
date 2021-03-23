package com.amituofo.xfs.plugin.fs.objectstorage.s3common.item;

import java.net.URL;
import java.util.Date;
import java.util.List;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.Tag;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.xfs.plugin.fs.objectstorage.OSDFileItem;

public interface S3FileItem extends OSDFileItem {
	URL generatePresignedUrl(HttpMethod method, Date expiration, ResponseHeaderOverrides responseHeader) throws ServiceException;

	void putMetadata(ObjectMetadata metadata) throws ServiceException;

	ObjectMetadata getMetadata() throws ServiceException;

	void setObjectTagging(List<Tag> tags) throws ServiceException;

	List<Tag> getObjectTagging() throws ServiceException;
}
