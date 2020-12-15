package com.azurequeue.test.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.OperationContext;
import com.microsoft.azure.storage.RetryExponentialRetry;
import com.microsoft.azure.storage.blob.BlobContainerPublicAccessType;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

@Configuration
public class AzureConnection {
	
	@Value("${azure.uploadContainerName}")
	private String containerName;
	
	@Bean
    public CloudBlobContainer getCloudBlobContainer() throws Exception {
		
		CloudStorageAccount storageAccount = null;
		CloudBlobClient blobClient = null;
		CloudBlobContainer container=null;
		
		try {
            String connectStr = "DefaultEndpointsProtocol=https;AccountName=51aa39044cb847dfa2d8;AccountKey=1LYPYvKAnECs97aHckJntU8si6QKKvJEUMBqkObhsE8+VC0iQeqvsASBsnMizfvY4jhj1qK2qVroK7X4CgPASQ==;EndpointSuffix=core.windows.net";
				
			// Config to upload file size > 1MB in chunks
			int deltaBackoff = 2;
			int maxAttempts = 2;
			BlobRequestOptions blobReqOption = new BlobRequestOptions();
            blobReqOption.setSingleBlobPutThresholdInBytes(1024 * 1024); // 1MB
            blobReqOption.setRetryPolicyFactory(new RetryExponentialRetry(deltaBackoff, maxAttempts));
            blobReqOption.setConcurrentRequestCount(4);
			
			// Parse the connection string and create a blob client to interact with Blob storage
			storageAccount = CloudStorageAccount.parse(connectStr);
			blobClient = storageAccount.createCloudBlobClient();
			blobClient.setDefaultRequestOptions(blobReqOption);
			container = blobClient.getContainerReference(containerName);
			
			// Create container in not exists
			container.createIfNotExists(BlobContainerPublicAccessType.CONTAINER, new BlobRequestOptions(), new OperationContext());
			
			return container;
		} catch (Exception e) {
			throw e;
		}
	}

}
