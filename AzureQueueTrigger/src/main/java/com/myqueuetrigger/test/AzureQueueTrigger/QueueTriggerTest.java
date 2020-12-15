package com.myqueuetrigger.test.AzureQueueTrigger;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.RetryExponentialRetry;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;


public class QueueTriggerTest {
	
	@FunctionName("queueprocessor")
	public void run(
	   @QueueTrigger(name = "msg",
	                  queueName = "queuetest",
	                  dataType = "",
	                  connection = "AzureWebJobsStorage") Details message,
	    final ExecutionContext executionContext
//	    @BlobInput(
//			      name = "file", 
//			      dataType = "binary", connection = "AzureWebJobsStorage",
//			      path = "{Path}") byte[] content
	) {
		
		System.gc();
		
		executionContext.getLogger().info("PATH: " + message.getPath());
		
		// starting time 
        long start = System.currentTimeMillis();
		
        final long MEGABYTE = 1024L * 1024L;
		
		long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
		
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now(); 
		
		executionContext.getLogger().info("START TIME: " + dtf.format(now)); 
		
		 MemoryMXBean memorymbean = ManagementFactory.getMemoryMXBean();
		    MemoryUsage usage = memorymbean.getHeapMemoryUsage();
		    executionContext.getLogger().info("INIT HEAP: " + usage.getInit()/(1024*1024));
		    executionContext.getLogger().info("MAX HEAP: " + usage.getMax()/(1024*1024));
		    executionContext.getLogger().info("USE HEAP: " + usage.getUsed()/(1024*1024));
		    executionContext.getLogger().info("\nFull Information:");
		    executionContext.getLogger().info("Heap Memory Usage: "
		            + memorymbean.getHeapMemoryUsage());
		    executionContext.getLogger().info("Non-Heap Memory Usage: "
		            + memorymbean.getNonHeapMemoryUsage());
	    
	    CloudStorageAccount storageAccount = null;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container=null;
		
	    try {
			
//	    	String connectStr = "DefaultEndpointsProtocol=https;AccountName=javaqueuetest;AccountKey=TfawBoaRxjbhIUEqW4WLiltXdT5m3h8wmPJInqep7tgKPHfPQQd8tZiiPMAMRsIBeZqw4w8KE5buNFaHlshfdw==;EndpointSuffix=core.windows.net";
				
	    	String connectStr = "DefaultEndpointsProtocol=https;AccountName=51aa39044cb847dfa2d8;AccountKey=1LYPYvKAnECs97aHckJntU8si6QKKvJEUMBqkObhsE8+VC0iQeqvsASBsnMizfvY4jhj1qK2qVroK7X4CgPASQ==;EndpointSuffix=core.windows.net";
	    	
				 //unique name of the container
				 String containerName = "output";
				 
				// Config to upload file size > 1MB in chunks
					int deltaBackoff = 2;
					int maxAttempts = 2;
					BlobRequestOptions blobReqOption = new BlobRequestOptions();
		            blobReqOption.setSingleBlobPutThresholdInBytes(1024 * 1024); // 1MB
		            blobReqOption.setRetryPolicyFactory(new RetryExponentialRetry(deltaBackoff, maxAttempts));

				  
					// Parse the connection string and create a blob client to interact with Blob storage
					storageAccount = CloudStorageAccount.parse(connectStr);
					blobClient = storageAccount.createCloudBlobClient();
					blobClient.setDefaultRequestOptions(blobReqOption);
					container = blobClient.getContainerReference(containerName);
					
					container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
                    
			         ZipInputStream zipIn = new ZipInputStream(message.getPath().toURL().openStream());
			         
			         ZipEntry zipEntry = zipIn.getNextEntry();
			         while(zipEntry != null) {
			        	executionContext.getLogger().info("ZipEntry name: " + zipEntry.getName());
			        	 
			        	 
			        	//Getting a blob reference
						CloudBlockBlob blob = container.getBlockBlobReference(zipEntry.getName());
			        	
			        	 ByteArrayOutputStream outputB = new ByteArrayOutputStream();
			             byte[] buf = new byte[1024 * 64];
			             int n;
			             while ((n = zipIn.read(buf, 0, 1024 * 64)) != -1) {
			                 outputB.write(buf, 0, n);
			                 System.out.print(".");
			             }
			    			
			    		 // Upload to container
			             ByteArrayInputStream inputS = new ByteArrayInputStream(outputB.toByteArray());
			             
			             blob.setStreamWriteSizeInBytes(256 * 1024); // 256K
			             
			             blob.upload(inputS, inputS.available());
			    		 
			             executionContext.getLogger().info("ZipEntry name: " + zipEntry.getName() + " extracted");
			             zipIn.closeEntry();
			        	 zipEntry = zipIn.getNextEntry();
			         }
			         zipIn.close();
			         
			         executionContext.getLogger().info("FILE EXTRACTION FINISHED");
			         
			         LocalDateTime now2 = LocalDateTime.now();  
					   System.out.println(dtf.format(now2));  
					   
					   long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();
					
					// ending time 
			        long end = System.currentTimeMillis(); 
			        executionContext.getLogger().info("File extraction takes " + 
			                                    (end - start)/1000.0 + "sec. Memory used : "  + ((afterUsedMem-beforeUsedMem)/MEGABYTE) + "MB"); 
					
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

