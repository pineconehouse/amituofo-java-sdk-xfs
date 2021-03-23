package com.amituofo.xfs.plugin.fs.objectstorage.hcp.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import com.amituofo.common.api.ProgressListener;
import com.amituofo.common.define.ReadProgressEvent;
import com.amituofo.common.ex.HSCException;
import com.amituofo.common.ex.ServiceException;
import com.amituofo.common.kit.io.InterruptableInputStream;
import com.amituofo.common.kit.io.ProgressInputStream;
import com.amituofo.xfs.plugin.fs.local.item.LocalFileItem;
import com.amituofo.xfs.plugin.fs.objectstorage.hcp.HCPFileSystemPreference;
import com.amituofo.xfs.service.ContentWriterBase;
import com.amituofo.xfs.service.FileItem;
import com.hitachivantara.hcp.common.ex.InvalidResponseException;
import com.hitachivantara.hcp.standard.api.MultipartUpload;
import com.hitachivantara.hcp.standard.model.PutObjectResult;
import com.hitachivantara.hcp.standard.model.request.impl.MultipartUploadRequest;
import com.hitachivantara.hcp.standard.model.request.impl.PutObjectRequest;
import com.hitachivantara.hcp.standard.util.multipartupload.MulitipartUploadException;
import com.hitachivantara.hcp.standard.util.multipartupload.MulitipartUploadException.Stage;
import com.hitachivantara.hcp.standard.util.multipartupload.MulitipartUploaderExecutor;
import com.hitachivantara.hcp.standard.util.multipartupload.UploadEventHandler;

public class HCPContentWriter extends ContentWriterBase<HCPFileItem> {
	private MulitipartUploaderExecutor exec = null;
	private InterruptableInputStream sourcein = null;

	public HCPContentWriter(HCPFileItem file) {
		super(file);
	}

	@Override
	public void abort() throws ServiceException {
		if (exec != null) {
			try {
				exec.abortMultipartUpload();
			} catch (InvalidResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (HSCException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			exec = null;
		}

		if (sourcein != null) {
			try {
				sourcein.abort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void write(InputStream in, long length) throws ServiceException {
		write(in, length, null);
	}

	@Override
	public void write(FileItem sourceItem) throws ServiceException {
		write(sourceItem, null);
	}

	@Override
	public void write(FileItem sourceItem, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		try {
			upload(sourceItem, progressListener);
		} catch (ServiceException e) {
			throw e;
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	@Override
	public void write(InputStream in, long length, ProgressListener<ReadProgressEvent, Integer> progressListener) throws ServiceException {
		try {
			upload(in, progressListener);
		} catch (InvalidResponseException e) {
			throw new ServiceException(e.getMessage(), e);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private void upload(FileItem sourceItem,
			ProgressListener<ReadProgressEvent, Integer> progressListener) throws IOException, InvalidResponseException, HSCException, ServiceException {
		if (sourceItem instanceof LocalFileItem) {
			File file = ((LocalFileItem) sourceItem).getFile();
			// If config > 5MB
			HCPFileSystemPreference preference = fileitem.getItemspace().getFileSystemPreference();
			if (preference.isEnabledMultipartUpload() && file.length() > preference.getMinMultipartUploadFileSize()) {
				multipartUpload(file, progressListener);
			} else {
				upload(new FileInputStream(file), progressListener);
			}
		} else {
			upload(sourceItem.getContent(), progressListener);
		}
	}

	private void upload(InputStream in, ProgressListener<ReadProgressEvent, Integer> progressListener) throws InvalidResponseException, HSCException {
		if (in == null) {
			return;
		}

		try {
			if (progressListener != null) {
//				request.withProgressListener(progressListener);
				sourcein = new ProgressInputStream(in, progressListener);
			} else {
				sourcein = new InterruptableInputStream(in);
			}

			HCPFileSystemPreference preference = fileitem.getItemspace().getFileSystemPreference();
			PutObjectRequest request = new PutObjectRequest(fileitem.getKey(), sourcein).withVerifyContent(preference.isVerifyPutObjectContent());

			PutObjectResult result = fileitem.getHcpClient().putObject(request);
			// TODO 无大小信息，Bug
			fileitem.upateProperties(result);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			}
		}
	}

	private void multipartUpload(File file, ProgressListener<ReadProgressEvent, Integer> progressListener) throws InvalidResponseException, HSCException, IOException {

		HCPFileSystemPreference preference = fileitem.getItemspace().getFileSystemPreference();
		MultipartUploadRequest request = new MultipartUploadRequest(fileitem.getKey());
		MultipartUpload api = fileitem.getHcpClient().getMultipartUpload(request);
		// ==========================================================================================================================
		exec = new MulitipartUploaderExecutor(api, fileitem.getKey(), file, preference.getMultipartSize());
		// 开始上传（这里使用10个线程上传,文件被分为10片）
		exec.multiThreadUpload(preference.getMultipartThreadCount(),
				/**
				 * 分片上传事件处理
				 * 
				 * @author sohan
				 *
				 */
				new UploadEventHandler() {
					private final PrintStream log = System.out;

					@Override
					public void init(String bucketName, String objectPath, String uploadId) {
						log.println("Step 1: Initialize [" + objectPath + "] [" + uploadId + "]");
					}

					@Override
					public void beforePartUpload(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, long startTime) {
						log.println("Step 2: Upload parts... [" + objectPath + "] [" + uploadId + "] " + partNumber + " " + uploadPartsize);
					}

					@Override
					public void caughtException(Stage stage, MulitipartUploadException e) {
						log.println("Step " + stage + ": Error [" + e.getKey() + "] [" + e.getUploadId() + "] " + e.getPartNumber() + " " + e.getUploadPartsize());
						e.printStackTrace();

						// **此处可以记录失败分片以备后期重传此断点分片**
						// Do something
					}

					@Override
					public void afterPartUpload(String bucketName, String objectPath, String uploadId, int partNumber, long uploadPartsize, long startTime, long endTime) {

						log.println("Step 2: Upload parts OK ["
								+ objectPath
								+ "] ["
								+ uploadId
								+ "] "
								+ partNumber
								+ " "
								+ uploadPartsize
								+ "\t用时:"
								+ (((double) (endTime - startTime)) / 1000)
								+ " sec");
					}

					@Override
					public void complete(String bucketName, String objectPath, String uploadId, Long uploadedSize, long startTime, long endTime) {
						log.println("Step 3: Complete... [" + objectPath + "] [" + uploadId + "]");
						
//						try {
//							fileitem.upateProperties();
//						} catch (ServiceException e) {
//							e.printStackTrace();
//						}

						// 通過計算两侧文件的MD5验证上传数据是否正确
						// **此处验证为验证上传正确性代码-实际开发无需此操作**
						// try {
						// HCPObject s3Object = hcpNamespace.getObject(objectPath);
						// InputStream in = s3Object.getContent();
						// byte[] orginalFileMd5;
						// orginalFileMd5 = DigestUtils.calcMD5(tobeUploadFile);
						// byte[] objectFromHCPMd5 = DigestUtils.calcMD5(in);
						// in.close();
						//
						// boolean equals = Arrays.equals(orginalFileMd5, objectFromHCPMd5);
						// // assertTrue(equals == true);
						// if (equals)
						// System.out.println("***Upload " + objectPath + " Successfully!***");
						// } catch (Exception e1) {
						// e1.printStackTrace();
						// }

					}
				},

				progressListener);
		// =========================================================================================================================

	}

}
