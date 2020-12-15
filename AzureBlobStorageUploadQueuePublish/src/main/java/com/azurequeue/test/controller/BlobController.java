package com.azurequeue.test.controller;


import com.azurequeue.test.util.AzureConnection;
import com.azurequeue.test.util.AzureMessageQueue;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import entity.Details;
import io.swagger.annotations.ApiOperation;

import java.io.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/zipfile")
public class BlobController {
	
	@Value("${azure.connectionStr}")
    private String connectStr;
	
	private AzureConnection azureConnection;
	
	@Autowired
	public BlobController(AzureConnection azureConnection) {
		
		this.azureConnection = azureConnection;
	}
	
	@ApiOperation(value = "Uploads zip file and publishes message on azure queue")
	@PostMapping("/uploadAndPublishMessageOnQueue")
	public ResponseEntity<String> uploadFileAndPublishMessageOnQueue(HttpServletRequest request) throws IOException {
		
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		if (!isMultipart) {
           
            return new ResponseEntity<String>("Invalid file", HttpStatus.BAD_REQUEST);
        }
		
		// Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload();
		
		try {
			
			CloudBlobContainer container = azureConnection.getCloudBlobContainer();
			
			FileItemIterator iter = upload.getItemIterator(request);
			
            while (iter.hasNext()) {
            	
                FileItemStream item = iter.next();
                
                //Getting a blob reference
				CloudBlockBlob blob = container.getBlockBlobReference(item.getName());
				
    			blob.setStreamWriteSizeInBytes(256 * 1024); // 256K of a block
    			
    			// Upload to container
    			blob.upload(item.openStream(), -1);
    			
    			Details d = new Details("123", blob.getUri());
    			
    			// Adds message to azure queue
    			AzureMessageQueue.addQueueMessage(connectStr, "queuetest", d);
            } 
            
            return new ResponseEntity<String>("Finished", HttpStatus.OK);
			
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_GATEWAY);
		}
		
	}

}
