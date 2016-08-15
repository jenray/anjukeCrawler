package com.tcloudata.spider;

import com.tcloudata.spider.dao.MysqlUtil;
import com.tcloudata.spider.jdbc.NewHouseHtmlJdbcTemplate;
import com.tcloudata.spider.jdbc.NewHouseJdbcTemplate;
import com.tcloudata.spider.jdbc.OldHouseHtmlJdbcTemplate;
import com.tcloudata.spider.pipeline.NewHouseDaoPipeline;
import com.tcloudata.spider.pipeline.NewHouseHtmlDaoPipeline;
import com.tcloudata.spider.pipeline.OldHouseHtmlDaoPipeline;
import com.tcloudata.spider.scheduler.RedisScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;

import java.util.List;
import java.util.Map;

/**
 * Created by jenray on 16/8/9.
 */
public class Start {
    public static void main(String[] args) {
        if (args.length > 0) {
            ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
            String spider = args[0];
            if (spider.equalsIgnoreCase("new_html")) {
                NewHouseHtmlJdbcTemplate jdbcTemplate = (NewHouseHtmlJdbcTemplate) context.getBean("newHouseHtmlJdbcTemplate");

                RedisScheduler redisScheduler = new RedisScheduler("redis://192.168.2.43:6379/14");
                String qSql = "SELECT city,new_url FROM city WHERE new_status=0";
                List<Map<String, Object>> cityUrlMapList = MysqlUtil.It().getJtAnjuke().queryForList(qSql);
                for (Map<String, Object> cityUrlMap : cityUrlMapList) {
                    String city = cityUrlMap.get("city").toString(),
                            cityUrl = cityUrlMap.get("new_url").toString();
                    System.out.println(city + "\t" + cityUrl);
                    if (cityUrl != null && cityUrl.matches("http://\\w+.fang.anjuke.com/loupan/.*")) {
                        MysqlUtil.It().getJtAnjuke().update("UPDATE city SET new_status=1 WHERE city=?", city);
                        Spider.create(new NewHouseHtmlSpider(cityUrl))
                                .addUrl(cityUrl)
                                .scheduler(redisScheduler)
                                .addPipeline(new ConsolePipeline())
                                .addPipeline(new NewHouseHtmlDaoPipeline(jdbcTemplate))
                                .run();
                        MysqlUtil.It().getJtAnjuke().update("UPDATE city SET new_status=2 WHERE city=?", city);
                    } else {
                        MysqlUtil.It().getJtAnjuke().update("UPDATE city SET new_status=2 WHERE city=?", city);
                    }
                }
            } else if (spider.equalsIgnoreCase("old_html")) {
                OldHouseHtmlJdbcTemplate jdbcTemplate = (OldHouseHtmlJdbcTemplate) context.getBean("oldHouseHtmlJdbcTemplate");

                RedisScheduler redisScheduler = new RedisScheduler("redis://192.168.2.43:6379/15");
                String qSql = "SELECT city,old_url FROM city WHERE old_status=0";
                List<Map<String, Object>> cityUrlMapList = MysqlUtil.It().getJtAnjuke().queryForList(qSql);
                for (Map<String, Object> cityUrlMap : cityUrlMapList) {
                    String city = cityUrlMap.get("city").toString(),
                            cityUrl = cityUrlMap.get("old_url").toString();
                    System.out.println(city + "\t" + cityUrl);
                    if (cityUrl != null && cityUrl.matches("http://\\w+.anjuke.com/community/.*")) {
                        MysqlUtil.It().getJtAnjuke().update("UPDATE city SET old_status=1 WHERE city=?", city);
                        Spider.create(new OldHouseHtmlSpider(cityUrl))
                                .addUrl(cityUrl)
                                .addPipeline(new ConsolePipeline())
                                //.addUrl("http://www.anjuke.com/sy-city.html")
                                .scheduler(redisScheduler)
                                .addPipeline(new OldHouseHtmlDaoPipeline(jdbcTemplate))
                                .run();
                        MysqlUtil.It().getJtAnjuke().update("UPDATE city SET old_status=2 WHERE city=?", city);
                    } else {
                        MysqlUtil.It().getJtAnjuke().update("UPDATE city SET old_status=2 WHERE city=?", city);
                    }
                }
            } else {
                NewHouseJdbcTemplate newHouseJdbcTemplate = (NewHouseJdbcTemplate) context.getBean("newHouseJdbcTemplate");

                RedisScheduler redisScheduler = new RedisScheduler("redis://192.168.2.43:6379/13");
                Spider.create(new NewHouseSpider())
                        .addPipeline(new ConsolePipeline())
                        .scheduler(redisScheduler)
                        .addUrl("http://www.anjuke.com/sy-city.html")
                        .addPipeline(new NewHouseDaoPipeline(newHouseJdbcTemplate))
                        .run();
            }
        } else {
            System.out.println("please add args : new new_html old old_html");
        }
    }

}
