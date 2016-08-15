package com.tcloudata.spider.scheduler;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.ResourceBundle;

/**
 * Created by Jenray on 2014/4/23.
 */
public class RedisUtil {

    private RedisTemplate rtOldHtml = null;

    private static RedisUtil it = new RedisUtil();

    private RedisUtil() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        rtOldHtml = (RedisTemplate) applicationContext.getBean("redisOldHtmlTemplate");


    }

    public static RedisUtil It() {
        return it;
    }

    public RedisTemplate getRtOldHtml() {
        return rtOldHtml;
    }

}
