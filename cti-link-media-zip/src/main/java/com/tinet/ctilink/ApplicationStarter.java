package com.tinet.ctilink;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.tinet.ctilink.mediazip.inc.MediaZipConst;
import com.tinet.ctilink.mediazip.inc.MediaZipMacro;
import com.tinet.ctilink.scheduler.RedisTaskScheduler;
import com.tinet.ctilink.scheduler.TaskSchedulerGroup;
import com.tinet.ctilink.util.PropertyUtil;

/**
 * 应用程序启动器
 * 
 * @author Jiangsl
 *
 */
@Component
public class ApplicationStarter implements ApplicationListener<ContextRefreshedEvent> {
	private static Logger logger = LoggerFactory.getLogger(ApplicationStarter.class);

	
	@Override
	public void onApplicationEvent(final ContextRefreshedEvent event) {
		
		// 设置JVM的DNS缓存时间
		// http://docs.amazonaws.cn/AWSSdkDocsJava/latest/DeveloperGuide/java-dg-jvm-ttl.html
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");
		MediaZipMacro.AWS_MEDIA_ZIP_SQS_URL = PropertyUtil.getProperty(MediaZipConst.PROPERTY_AWS_MEDIA_ZIP_SQS_URL);
		
		System.out.println("SQS URL:" + MediaZipMacro.AWS_MEDIA_ZIP_SQS_URL);
		logger.info("cti-link-media-zip启动成功");
		System.out.println("cti-link-media-zip启动成功");
	}
}