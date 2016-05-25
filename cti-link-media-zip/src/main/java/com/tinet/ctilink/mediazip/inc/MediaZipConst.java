package com.tinet.ctilink.mediazip.inc;

public class MediaZipConst {
	public static String S3_BUCKET = "s3_bucket";
	public static String AWS_CREDENTIAL = "aws_credential";
	public static String AWS_TTS_CACHE_URL_POSTFIX = "s3-website.cn-north-1.amazonaws.com.cn";
	public static String LOCAL_UPLOAD_PATH = "/usr/local/media-zip-upload";
	public static int MAX_FILE_SIZE = 1024*1024*100; //100MB 约104分钟
	public static String PROPERTY_AWS_MEDIA_ZIP_SQS_URL = "aws.mediaZip.sqs.url";
}
