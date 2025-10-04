package com.Practise.AWS.AWS.Controllers;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.Practise.AWS.AWS.Services.StorageService;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.DeleteObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedDeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/aws")
public class StorageController {

	@Value("${aws.s3.bucket}")
	private String bucketName;

	@Autowired
	S3Presigner presigner;
	@Autowired
	StorageService storageService;
	/***********************S3 Bucket*******************/
	/**************************************************/
	@PostMapping("/upload")
	public ResponseEntity<String> downloadFile(@RequestParam(value = "file") MultipartFile file) {
		/*** Using Presigned Url ***/
//		PutObjectRequest objectRequest=PutObjectRequest.builder()
//				.bucket(bucketName)
//				.key(file.getOriginalFilename())
//				.build();
//		PutObjectPresignRequest   presignRequest=  PutObjectPresignRequest.builder()
//				.signatureDuration(Duration.ofMinutes(1)).putObjectRequest(objectRequest).build();
//		PresignedPutObjectRequest request=presigner.presignPutObject(presignRequest);
//		String presignedUrl = request.url().toString();
//		return ResponseEntity.ok(presignedUrl);
		return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.OK);
	}

	@GetMapping("/download/{fileName}")
	public ResponseEntity<ByteArrayResource> getMethodName(@PathVariable String fileName) {
		byte[] data=storageService.downloadFile(fileName);
		ByteArrayResource resource=new ByteArrayResource(data);
		return ResponseEntity.
				ok().contentLength(data.length)
				.header("content-type", "application/octet-Stream")
				.header("content-disposition","application"+fileName)
				.body(resource);	
		
		
		/*** Using Presigned Url We can fetch the file from s3 **/
//		GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//				.bucket(bucketName)
//				.key(fileName)
//				.responseContentDisposition("attachment; filename=\"" + fileName + "\"")
//				.build();
//
//		GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
//				.signatureDuration(Duration.ofMinutes(1)).getObjectRequest(getObjectRequest).build();
//
//		PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
//		String presignedUrl = presignedRequest.url().toString();
//
//		return ResponseEntity.ok(presignedUrl);

	}

	@DeleteMapping("/delete/{fileName}")
	public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
		
//		DeleteObjectRequest objectRequest=DeleteObjectRequest.builder()
//				.bucket(bucketName)
//				.key(fileName)
//				.build();
//		DeleteObjectPresignRequest presignRequest=DeleteObjectPresignRequest.builder()
//				.signatureDuration(Duration.ofMinutes(1))
//				.deleteObjectRequest(objectRequest)
//				.build();
//		PresignedDeleteObjectRequest request=presigner.presignDeleteObject(presignRequest);
//		String presignedUrl = request.url().toString();
//		return ResponseEntity.ok(presignedUrl);
		return new ResponseEntity<>(storageService.deleteFile(fileName), HttpStatus.OK);
	}
	
	/********************************************************/
	/********************************************************/
	
	
	
	/******************SNS**************************/
	/**********************************************/
	
	
	/*****SNS ----Email *****/
	@GetMapping("/addNotification/{email}")
	public ResponseEntity<String>  addNotification(@PathVariable String email){
		return new ResponseEntity<>(storageService.addNotification(email),HttpStatus.OK);
	}
	
	@GetMapping("/sendNotification")
	public ResponseEntity<String> sendNotification(){
		return new ResponseEntity<>(storageService.sendNotification(),HttpStatus.OK);
	}
	
	/******  SNS----SMS  *****/
	
	@PostMapping("/sendsms/{phoneNumber}")
	public ResponseEntity<String>  sendSms(@PathVariable String phoneNumber){
		return new ResponseEntity<>(storageService.sendSMS(phoneNumber),HttpStatus.OK);
	}

}
