# Azure function to unzip files on azure
## Prerequisites
1.	Install [Azure functions core tool](https://docs.microsoft.com/en-us/azure/azure-functions/functions-run-local#v2). 
2.	Install [Azure CLI](https://docs.microsoft.com/en-us/cli/azure/install-azure-cli).
3.	Java developer kit version 8 or 11.
4.	Apache Maven version 3.0 or above.

## Prerequisite check
1.	In a terminal or command window, run func --version to check that the Azure Functions Core Tools are version 3.x.
2.	Run az --version to check that the Azure CLI version is 2.4 or later.
3.	Run az login to sign in to Azure and verify an active subscription.

## Introduction 
This is sample application to show uploading zip file to azure blob storage and publish message on azure queue, once message is received starts unzipping of file. The message is a simple java Bean class which has ID and Path of the zip file on azure platform. 

It has two applications, Publisher and reciever. Publisher is responsible for upload and publish message on queue. Teceiver is reponsible for listen to the queue and gets triggered when message enters the queue, once message is received it will locate the path of zip file in the message and unzips file from the path to the defined container.

Receiver application works on Queue Trigger and needs to be deployed on azure storage account.

## Steps to run
* Clone the repository to your local machine
* Install all the Prerequisites
* Change the POM.xml and connectionStr as per your azure blob storage account (For more details refer "Azure Unzip with Java details.docx" file in the repository)
* Deploy receiver(Queue Listener) function to azure using command prompt ```mvn install package azure-functions:deploy```
* Run publisher application on your local machine
* Uplaod a zip file and test 
* For more details please refer "Azure Unzip with Java details.docx" file in the repository
