package com.amituofo.xfs.plugin.fs.objectstorage.azure.blobs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

import com.azure.core.http.rest.PagedIterable;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobItem;
import com.azure.storage.blob.models.BlobItemProperties;
import com.azure.storage.blob.models.BlobListDetails;
import com.azure.storage.blob.models.BlobType;
import com.azure.storage.blob.models.ListBlobsOptions;
import com.azure.storage.blob.options.BlobBeginCopyOptions;
import com.azure.storage.common.StorageSharedKeyCredential;

public class TestAzureBlobs {

	public TestAzureBlobs() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws IOException, InterruptedException {
//		String connectStr="DefaultEndpointsProtocol=https;AccountName=song99;AccountKey=o+F3ZgyRHsHOm5GcOYlcaYUq2ipwcwFNET5u4Z6lZFgULXYTnUXzGbokuY6XTBNtKCbWMSa8jjT/H9o2y1nwqA==;EndpointSuffix=core.windows.net";
//		// TODO Auto-generated method stub
//		// Create a BlobServiceClient object which will be used to create a container client
//		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();
//
//		//Create a unique name for the container
		String containerName = "quickstartblobs1";
//
//		// Create the container and return a container client object
//		BlobContainerClient containerClient = blobServiceClient.createBlobContainer(containerName);
		//--------------------------------------------
		
	    String accountName="song";
		String accountKey="npwT4tinlqbrbncwfS6U+w8t29c+VB4btCYMW1Y6boHtD6vj0tl3WJ+DgnidJhiVoZrQ/ml+NFPLsgTSB2r7EA==";
		/*
         * Use your Storage account's name and key to create a credential object; this is used to access your account.
         */
        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

        /*
         * From the Azure portal, get your Storage account blob service URL endpoint.
         * The URL typically looks like this:
         */
        String endpoint = String.format(Locale.ROOT, "https://%s.blob.core.windows.net", accountName);

        /*
         * Create a BlobServiceClient object that wraps the service endpoint, credential and a request pipeline.
         */
        BlobServiceClient storageClient = new BlobServiceClientBuilder().endpoint(endpoint).credential(credential).buildClient();
        BlobContainerClient containerClient = storageClient.getBlobContainerClient(containerName);
		//--------------------------------------------
        
        BlobClient blobClient = containerClient.getBlobClient("test/LGPL2.txt3");
        //https://song99.blob.core.windows.net/quickstartblobs1/LGPL2.txt
//        blobClient.copyFromUrl("https://song99.blob.core.windows.net/quickstartblobs1/LGPL2.txt");
//        blobClient.beginCopy(new BlobBeginCopyOptions("https://song99.blob.core.windows.net/quickstartblobs1/LGPL2.txt"));
        blobClient.beginCopy(new BlobBeginCopyOptions("https://song99.blob.core.windows.net/quickstartblobs1/LGPL2.txt"));
       Thread.sleep(3000);
        System.exit(0);
		//--------------------------------------------
        
//		PagedIterable<BlobItem> itemversions = containerClient
//				.listBlobs(new ListBlobsOptions().setPrefix("f1/f2/test2.txt").setDetails(new BlobListDetails().setRetrieveVersions(true)), null);
		PagedIterable<BlobItem> itemversions = containerClient
				.listBlobs(new ListBlobsOptions().setDetails(new BlobListDetails().setRetrieveDeletedBlobs(true)), null);
		for (Iterator<BlobItem> it = itemversions.iterator(); it.hasNext();) {
			BlobItem blobItem = (BlobItem) it.next();
			System.out.println("\t" + blobItem.getName() + "\t" + blobItem.getVersionId() + "\t"+blobItem.getProperties().getContentLength());

		}
		//--------------------------------------------
        
//        System.exit(0);
		System.out.println("\nListing folder blobs...");
//		PagedIterable<BlobItem> items = containerClient.listBlobs(new ListBlobsOptions().setPrefix("f1/"),"f1/", null);
		PagedIterable<BlobItem> items = containerClient.listBlobsByHierarchy("");
		
		for (Iterator<BlobItem> it = items.iterator(); it.hasNext();) {
			BlobItem blobItem = (BlobItem) it.next();
			 BlobItemProperties prop = blobItem.getProperties();
				BlobType tp = prop.getBlobType();
				
				Long size = prop.getContentLength();
				System.out.println("\t" + blobItem.getName());
				
		}
		
//		System.exit(0);
		//--------------------------------------------
		
		
		// Create a local file in the ./data/ directory for uploading and downloading
		String localPath = "c:\\temp\\";
		String fileName = "quickstart" + java.util.UUID.randomUUID() + ".txt";
		File localFile = new File(localPath + fileName);

		// Write text to the file
		FileWriter writer = new FileWriter(localFile, true);
		writer.write("Hello, World!");
		writer.close();

		// Get a reference to a blob
		blobClient = containerClient.getBlobClient(fileName);

		System.out.println("\nUploading to Blob storage as blob:\n\t" + blobClient.getBlobUrl());

		// Upload the blob
//		blobClient.uploadFromFile(filePath, new ParallelTransferOptions().setProgressReceiver(new ProgressReceiver() {
//			
//			@Override
//			public void reportProgress(long bytesTransferred) {
//				// TODO Auto-generated method stub
//				
//			}
//		}), null, null, null, null, null);
//		
//		blobClient.uploadFromFileWithResponse(new BlobUploadFromFileOptions(filePath)
//                .setParallelTransferOptions(new ParallelTransferOptions().setProgressReceiver(new ProgressReceiver() {
//        			
//        			@Override
//        			public void reportProgress(long bytesTransferred) {
//        				// TODO Auto-generated method stub
//        				
//        			}
//        		})), null, null);
		blobClient.uploadFromFile(localPath + fileName);
//		blobClient.beginCopy(sourceUrl, pollInterval)
		
		//--------------------------------------------
		
		System.out.println("\nListing blobs...");

		// List the blob(s) in the container.
		for (BlobItem blobItem : containerClient.listBlobs()) {
		    System.out.println("\t" + blobItem.getName());
		}
		
//		System.out.println("\nListing folder blobs...");
//
//		PagedIterable<BlobItem> items = containerClient.listBlobsByHierarchy("/f1/");
//		
//		for (Iterator<BlobItem> it = items.iterator(); it.hasNext();) {
//			BlobItem item = (BlobItem) it.next();
//			 System.out.println("\t" + item.getName());
//		}
		
		
		//--------------------------------------------
		
		// Download the blob to a local file
		// Append the string "DOWNLOAD" before the .txt extension so that you can see both files.
		String downloadFileName = fileName.replace(".txt", "DOWNLOAD.txt");
//		File downloadedFile = new File(localPath + downloadFileName);

		System.out.println("\nDownloading blob to\n\t " + localPath + downloadFileName);

		blobClient.downloadToFile(localPath + downloadFileName);
	}

}
