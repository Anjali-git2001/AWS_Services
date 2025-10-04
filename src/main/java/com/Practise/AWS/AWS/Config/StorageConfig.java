package com.Practise.AWS.AWS.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sns.SnsClient;

//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.AWSStaticCredentialsProvider;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Configuration
public class StorageConfig {
	@Value("${aws.accessKey}")
	private String accessKey;
	
	@Value("${aws.secretKey}")
	private String secretKey;
	
	@Value("${aws.region}")
	private String region;
	
	/** 1.Way ***/
//	@Bean
//	public AmazonS3 generateS3Client() {	
//		AWSCredentials awsCredentials=new BasicAWSCredentials(accessKey, secretKey);
//		return AmazonS3ClientBuilder.standard().
//				withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).withRegion(region).build();
//	
//
//	}
	
	/**2.Way**/
	@Bean
	public S3Client generateS3Client1() {
		AwsBasicCredentials awsCredentials=AwsBasicCredentials.create(accessKey, secretKey);
		return S3Client.builder().
				region(Region.of(region)).
				credentialsProvider(StaticCredentialsProvider.create(awsCredentials)).build();
	}
	
	@Bean
	public S3Presigner s3Presigner() {
	    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
	    return S3Presigner.builder()
	            .region(Region.of(region))
	            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
	            .build();
	}
	
	
	@Bean
	public  SnsClient  snsClient() {
	    AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
	    return  SnsClient.builder()
	    		.region(Region.of(region))
	    		.credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
	    		.build();

	}

}
