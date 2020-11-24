package com.azurequeue.test.controller;

//Include the following imports to use queue APIs
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;

//Include follwing to import Azure blob storage API's 
import com.azure.storage.blob.*;
import java.io.*;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/zipfile")
public class BlobController {
	
	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		
		String connectStr = "DefaultEndpointsProtocol=https;AccountName=javaqueuetest;AccountKey=qj/dMEAzDH7IYh58mWMSFglyHCDxnL/UuCmERTQ1uNXzXuhCD+WZ78taksHGOfqvEbrSBSdtAMEaFSpl2UU8Iw==;EndpointSuffix=core.windows.net";
		
		// Create a BlobServiceClient object which will be used to create a container client
		BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr).buildClient();

		//Create a unique name for the container
		String containerName = "zipfiles";

		// Create the container and return a container client object
		BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
		
		// Get a reference to a blob
		BlobClient blobClient = containerClient.getBlobClient(file.getOriginalFilename());
		
		// Upload to container
		blobClient.upload(file.getInputStream(), file.getSize(), true);
		
		addQueueMessage(connectStr, "queuetest", blobClient.getContainerName()+"/"+blobClient.getBlobName());
		
		
		return "Done";
	}
	
	public static void addQueueMessage(String connectStr, String queueName, String messageText) {
	    try {
		        // Instantiate a QueueClient which will be
		        // used to create and manipulate the queue
		        QueueClient queueClient = new QueueClientBuilder()
		                                    .connectionString(connectStr)
		                                    .queueName(queueName)
		                                    .buildClient();
		
		        System.out.println("Adding message to the queue: " + messageText);
		
		        // Add a message to the queue
		        queueClient.sendMessage(messageText);
		    }  catch (QueueStorageException e) {
		        // Output the exception message and stack trace
		        System.out.println(e.getMessage());
		        e.printStackTrace();
		    }
     }

}
