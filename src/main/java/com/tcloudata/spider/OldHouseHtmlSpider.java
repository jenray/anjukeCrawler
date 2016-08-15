package com.tcloudata.spider;

import com.tcloudata.spider.dao.MysqlUtil;
import com.tcloudata.spider.jdbc.NewHouseJdbcTemplate;
import com.tcloudata.spider.jdbc.OldHouseHtmlJdbcTemplate;
import com.tcloudata.spider.pipeline.NewHouseHtmlDaoPipeline;
import com.tcloudata.spider.pipeline.OldHouseHtmlDaoPipeline;
import com.tcloudata.spider.scheduler.RedisScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class OldHouseHtmlSpider implements PageProcessor {

    private String cityUrl = "";
    private Site site = Site.me().setCycleRetryTimes(5).setRetryTimes(5).setSleepTime(10000).setTimeOut(3 * 60 * 1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8").setDomain("anjuke.com");

    public OldHouseHtmlSpider(String cityUrl) {
        this.cityUrl = cityUrl;
        site.setDomain(cityUrl.replaceAll("http://", "").replaceAll("/community/", ""));
    }

    public OldHouseHtmlSpider() {
    }

    @Override
    public void process(Page page) {
        System.out.println(page.getUrl().toString() + "\t" + (new Date()).toLocaleString());
        site.setSleepTime(10000);
        List<String> links;
        if (cityUrl.isEmpty()) {
            links = page.getHtml().links().regex("http://\\w+.anjuke.com/community/.*").all();
        } else {
            String urlReg = cityUrl + ".*";
            links = page.getHtml().links().regex(urlReg).all();
        }
        //List<String> links = page.getHtml().links().regex("http://zhuzhou.anjuke.com/community/.*").all();
        //List<String> links = page.getHtml().links().regex("http://\\w+.anjuke.com/community/.*").all();
        for (String link : links) {
            String realUrl = link.contains("#") ? link.replaceAll("#.*", "") : link;
            if (realUrl.matches(".*/community/[a-zA-Z]+/?") ||
                    realUrl.matches(".*/community/[a-zA-Z]+/p[0-9]+/?") ||
                    realUrl.matches(".*/community/view/[0-9]+/?")) {
                realUrl = realUrl.endsWith("/") ? realUrl.substring(0, realUrl.length() - 1) : realUrl;
                page.addTargetRequest(realUrl);
            }
        }
        if (cityUrl.isEmpty()) {
            links = page.getHtml().links().regex("http://\\w+.anjuke.com/?").all();
            page.addTargetRequests(links);
            links = page.getHtml().links().regex("http://\\w+.anjuke.com/community/?").all();
            page.addTargetRequests(links);
        }

        if (!page.getUrl().toString().contains("community/view/")) {
            page.setSkip(true);
        }
        //参考价格、楼盘地址、最新开盘时间、交房时间、装修标准、产权等信息
        String title = page.getHtml().xpath("//div[@class=\"comm-title clearfix\"]/h1/text()").toString();
        if (title != null && !title.isEmpty()) title = title.trim();
        page.putField("title", title);
        String city = page.getHtml().xpath("//div[@class=\"p_1180 p_crumbs\"]/a[2]/text()").toString();
        if (city != null && !city.isEmpty()) city = city.trim().replace("小区", "");
        page.putField("city", city);
        if (title == null || city == null) {
            //skip this page
            page.setSkip(true);
            if (page.getUrl().toString().contains("community/view/")) {
                site.setSleepTime(3600000);
            }
        }

        String basic = page.getHtml().xpath("//div[@class=\"border-info comm-basic clearfix\"]").toString();
        if (basic != null && !basic.isEmpty()) basic = basic.trim();
        page.putField("basic", basic);
        String detail = page.getHtml().xpath("//div[@class=\"border-info comm-detail\"]").toString();
        if (detail != null && !detail.isEmpty()) detail = detail.trim();
        page.putField("detail", detail);

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
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
            }
        }

    }
}
