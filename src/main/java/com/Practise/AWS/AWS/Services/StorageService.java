package com.Practise.AWS.AWS.Services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.Random;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.amazonaws.services.s3.model.S3Object;
//import com.amazonaws.services.s3.model.S3ObjectInputStream;
//import com.amazonaws.util.IOUtils;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.SnsException;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.utils.IoUtils;

@Service
@Slf4j
public class StorageService {

	@Value("${aws.s3.bucket}")
	private String bucketName;
	
	@Value("${aws.sns.topic-arn}")
	private String topic_arn;
	
	@Autowired
	S3Client s3Client;
	
	@Autowired
	S3Presigner presigner;
	
	@Autowired
	SnsClient snsClient;

	/*** Uploading a File **/
	public String uploadFile(MultipartFile file) {
		/**1.Way **/
//		File fileObj=convertMultipartFileToFile(file);
//		String fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();
//		s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
//		fileObj.delete();
		
		File fileObj=convertMultipartFileToFile(file);
		String fileName=System.currentTimeMillis()+"_"+file.getOriginalFilename();
		PutObjectRequest objectRequest=PutObjectRequest
				.builder() 
				.bucket(bucketName)
				.key(fileName)
				.build();
	    s3Client.putObject(objectRequest, RequestBody.fromFile(fileObj));
	    fileObj.delete();
		return "File is uploaded successfully"+" "+file.getOriginalFilename();
	}
	
	
	private File convertMultipartFileToFile(MultipartFile file) {
		File convertedFile=new File(file.getOriginalFilename());
		try(FileOutputStream fos=new FileOutputStream(convertedFile)) {
			fos.write(file.getBytes());			
		}catch (Exception e) {
			log.error("Error Converting Multipart file to file"+e);
		}
		return convertedFile;
	}
	
	/** Downloading a File ***/
	public byte[] downloadFile(String fileName) {
		
		/***1.This is one way for downloading a file from AWS  using SDK1 Version using this dependency
		 * <dependency>-->
		<!--			<groupId>com.amazonaws</groupId>-->
		<!--			<artifactId>aws-java-sdk-s3</artifactId>-->
		<!--			<version>1.12.681</version>  Use latest stable version -->
		<!--		</dependency>-->***/
//		S3Object s3Object=s3Client.getObject(new GetObjectRequest(bucketName, fileName));
//		S3ObjectInputStream inputStream=s3Object.getObjectContent();
//		try {
//			byte[] content=IOUtils.toByteArray(inputStream);
//			return content;
//		}catch(Exception e) {
//			 e.printStackTrace();
//		}
//		return null;
		
		/***2.This sis second way for downloading a file from AWS using SDK version2 
		 * <dependency>
			<groupId>software.amazon.awssdk</groupId>
			<artifactId>s3</artifactId>
			<version>2.25.11</version> <!-- latest version as of now -->
		</dependency> ***/
		GetObjectRequest getObjectRequest=GetObjectRequest
				.builder().bucket(bucketName).key(fileName).build();

		try (ResponseInputStream<GetObjectResponse>  responStream=s3Client.getObject(getObjectRequest)){
			byte[] content=IoUtils.toByteArray(responStream);
			return content;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
		
		
	}
	
	/*** Deleting a file **/
	public String deleteFile(String fileName) {
		
		/**1.Way ***/
//		s3Client.deleteObject(bucketName, fileName);
//		return "File is deleted successfully";
		
		
		/**2.Way **/
		DeleteObjectRequest objectRequest=DeleteObjectRequest.
				builder()
				.bucket(bucketName)
				.key(fileName)
				.build();
		s3Client.deleteObject(objectRequest);
		return "File is deleted successfully";
	}
	
	
	/***** SNS*******/
	public String addNotification(String email) {
		SubscribeRequest request=SubscribeRequest.builder()
				.topicArn(topic_arn)
				.protocol("email")
				.endpoint(email)
				.build();
		snsClient.subscribe(request);
		return "Subscription request is pending.To confirm the subscription,check your email."+"\t"+email;
	
		
	}
	
	/****SNS   Protocol-EMAIL ******/
	public String sendNotification() {
		PublishRequest publishRequest=PublishRequest.builder()
				.topicArn(topic_arn)
				.message(buildEmailBody())
				.subject("Network connection issue")
				.build();
		snsClient.publish(publishRequest);
		return "Notification Was Send Successfully";
		
	}
	
	private String buildEmailBody() {
		return "Dear Employee,\n\n"
		         + "We are currently experiencing a network connectivity issue that may impact access to certain systems and services.\n\n"
		         + "Our IT team is actively working to identify and resolve the issue as quickly as possible. We will keep you updated on the progress and notify you once normal service has been restored.\n\n"
		         + "We appreciate your patience and understanding.\n\n"
		         + "Best regards,\n"
		         + "IT Support Team";	
  }
	
	/****SNS   Protocol-SMS ******/
	
	
	private String generateOtp() {
		Random random=new Random();
		int otp=100000+random.nextInt(900000);
		return String.valueOf(otp);
	}
	
	public String sendSMS(String phoneNumber) {	
		String otp=generateOtp();
		String message="Your otp is:"+"\t"+otp;	
		try {
		PublishRequest publishRequest=PublishRequest.builder()
				.message(message)
				.phoneNumber(phoneNumber)
				.build();
		 snsClient.publish(publishRequest);
		   return "Otp sent to"+"\t"+phoneNumber+":OTP"+"\t"+otp;
		}catch (SnsException e) {
			return "Failed to send sms"+e.awsErrorDetails().errorMessage();
		}		
				
	}
}
