package com.azurequeue.test.util;

import java.util.Base64;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.models.QueueStorageException;
import com.google.gson.Gson;

import entity.Details;

public class AzureMessageQueue {
	
	public static void addQueueMessage(String connectStr, String queueName, Details message) throws QueueStorageException {
		  
        // Instantiate a QueueClient which will be
        // used to create and manipulate the queue
        QueueClient queueClient = new QueueClientBuilder()
                                    .connectionString(connectStr)
                                    .queueName(queueName)
                                    .buildClient();

        // Add a message to the queue
        Gson gSon = new Gson();
        String messageToSend = gSon.toJson(message);
        System.out.println("Adding message " + messageToSend + " to queue");
        String encodedMsg = Base64.getEncoder().encodeToString(messageToSend.getBytes());
        queueClient.sendMessage(encodedMsg);
    
}

}
