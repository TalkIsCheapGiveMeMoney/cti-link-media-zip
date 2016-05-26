package com.tinet.ctilink.mediazip.inc;

public class MediaZipConst {
	public static String AWS_CREDENTIAL = "aws_credential";
	public static String AWS_MP3_URL_POSTFIX = "s3-website.cn-north-1.amazonaws.com.cn";
	public static String LOCAL_UPLOAD_PATH = "/var/local/media-zip-upload";
	public static String MONITOR_PATH = "/var/local/voices/monitor";
	public static int MAX_FILE_SIZE = 1024*1024*100; //100MB 约104分钟
	//public static String PROPERTY_AWS_MEDIA_ZIP_SQS_URL = "aws.mediaZip.sqs.url";
	public static String PROPERTY_AWS_MEDIA_S3_BUCKET = "aws.mediaZip.s3.bucket";
}
