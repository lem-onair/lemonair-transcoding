package com.hanghae.lemonairtranscoding.service;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsService {
	private final AmazonS3 amazonsS3SyncClient;
	private final S3AsyncClient amazonS3AsyncClient;

	@Value("${ffmpeg.output.directory}")
	private String outputPath;

	@Value("${aws.s3.bucket}")
	private String bucket;

	private final String PREFIX_M3U8 = "m3u8-";

	public String uploadToS3(String filePath) {
		String key = filePath.substring(outputPath.length() + 1).replaceAll("\\\\", "/");
		if (key.startsWith(PREFIX_M3U8)) {
			key = key.substring(PREFIX_M3U8.length());
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setCacheControl("max-age=0, no-cache, no-store, must-revalidate");
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, new File(filePath)).withMetadata(metadata);
			amazonsS3SyncClient.putObject(putObjectRequest);
		}
		else{
			PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, key, new File(filePath));
			amazonsS3SyncClient.putObject(putObjectRequest);
		}

		String uploadedUrl = amazonsS3SyncClient.getUrl(bucket, key).toString();
		log.info(uploadedUrl);
		return uploadedUrl;
	}


	private String getS3UploadKey(String filePath){
		String key = filePath.substring(outputPath.length() + 1).replaceAll("\\\\", "/");
		if (key.startsWith(PREFIX_M3U8)) {
			key = key.substring(PREFIX_M3U8.length());
		}
		return key;
	}


}
