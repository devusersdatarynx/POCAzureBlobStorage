package com.azurequeue.test.controller;

//Include the following imports to use queue APIs
import com.azure.storage.queue.*;
import com.azure.storage.queue.models.*;
import com.google.gson.Gson;

import entity.Details;

//Include follwing to import Azure blob storage API's 
import com.azure.storage.blob.*;
import java.io.*;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/zipfile")
public class BlobController {
	
	 @Autowired
	 RestTemplate restTemplate;
	
	@PostMapping("/upload")
	public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
		
		try {
			String connectStr = "DefaultEndpointsProtocol=https;AccountName=51aa39044cb847dfa2d8;AccountKey=1LYPYvKAnECs97aHckJntU8si6QKKvJEUMBqkObhsE8+VC0iQeqvsASBsnMizfvY4jhj1qK2qVroK7X4CgPASQ==;EndpointSuffix=core.windows.net";
			
//			String connectStr = "DefaultEndpointsProtocol=https;AccountName=queuelistener573996;AccountKey=4hGXZopXoN+8n3z42w+IPC2GDUAjRblMFfbETrJhIuG0I+KrrqJFYghad1BjtjGJxOYODzMBSkpXWoRUmjkZSg==;EndpointSuffix=core.windows.net";
			
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
			
			Details d = new Details("123", blobClient.getContainerName()+"/"+blobClient.getBlobName());
			
			addQueueMessage(connectStr, "queuetest", d);
			
			return "Done";
			
		} catch (Exception e) {
			return e.getMessage();
		}
		
	}
	
	public static void addQueueMessage(String connectStr, String queueName, Details message) throws QueueStorageException {
	  
		        // Instantiate a QueueClient which will be
		        // used to create and manipulate the queue
		        QueueClient queueClient = new QueueClientBuilder()
		                                    .connectionString(connectStr)
		                                    .queueName(queueName)
		                                    .buildClient();
		
		        System.out.println("Adding message to the queue: " + message);
		
		        // Add a message to the queue
		        Gson gSon = new Gson();
		        String messageToSend = gSon.toJson(message);
		        System.out.println(messageToSend);
		        String encodedMsg = Base64.getEncoder().encodeToString(messageToSend.getBytes());
		        queueClient.sendMessage(encodedMsg);
		    
     }

}
