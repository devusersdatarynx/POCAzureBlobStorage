package com.myqueuetrigger.test.AzureQueueTrigger;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.BlobInput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;


public class QueueTriggerTest {
//	
	@FunctionName("queueprocessor")
	public void run(
	   @QueueTrigger(name = "msg",
	                  queueName = "queuetest",
	                  dataType = "",
	                  connection = "AzureWebJobsStorage") Details message,
	    final ExecutionContext executionContext,
	    @BlobInput(
			      name = "file", 
			      dataType = "binary", connection = "AzureWebJobsStorage",
			      path = "{Path}") byte[] content
	) {
	    executionContext.getLogger().info(message.getPath() + " :: " + content);
	    
	    CloudStorageAccount storageAccount = null;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container=null;
		
	    try {
			
//	    	String connectStr = "DefaultEndpointsProtocol=https;AccountName=javaqueuetest;AccountKey=TfawBoaRxjbhIUEqW4WLiltXdT5m3h8wmPJInqep7tgKPHfPQQd8tZiiPMAMRsIBeZqw4w8KE5buNFaHlshfdw==;EndpointSuffix=core.windows.net";
				
	    	String connectStr = "DefaultEndpointsProtocol=https;AccountName=51aa39044cb847dfa2d8;AccountKey=1LYPYvKAnECs97aHckJntU8si6QKKvJEUMBqkObhsE8+VC0iQeqvsASBsnMizfvY4jhj1qK2qVroK7X4CgPASQ==;EndpointSuffix=core.windows.net";
	    	
				 //unique name of the container
				 String containerName = "output";

				  
					// Parse the connection string and create a blob client to interact with Blob storage
					storageAccount = CloudStorageAccount.parse(connectStr);
					blobClient = storageAccount.createCloudBlobClient();
					container = blobClient.getContainerReference(containerName);
					
					container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
					
					InputStream targetStream = new ByteArrayInputStream(content);
					 
					 executionContext.getLogger().info("QueueTriger Input Stream : " + targetStream);
					 
			         ZipInputStream zipIn = new ZipInputStream(targetStream);
			         ZipEntry zipEntry = zipIn.getNextEntry();
			         while(zipEntry != null) {
			        	 executionContext.getLogger().info("ZipEntry Name: " + zipEntry.getName());
			        	 
			        	 
			        	//Getting a blob reference
						CloudBlockBlob blob = container.getBlockBlobReference(zipEntry.getName());

			        	
			        	 ByteArrayOutputStream outputB = new ByteArrayOutputStream();
			             byte[] buf = new byte[1024];
			             int n;
			             while ((n = zipIn.read(buf, 0, 1024)) != -1) {
			                 outputB.write(buf, 0, n);
			             }
			        	
			    			
			    		 // Upload to container
			             ByteArrayInputStream inputS = new ByteArrayInputStream(outputB.toByteArray());
			             
			             blob.upload(inputS, inputS.available());
			    		 
			    		 executionContext.getLogger().info("QueueTriger DONE: ");
			    		 
			        	 zipEntry = zipIn.getNextEntry();
			         }
			         zipIn.close();
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

