package com.tcloudata.spider;

import com.tcloudata.spider.dao.MysqlUtil;
import com.tcloudata.spider.jdbc.NewHouseHtmlJdbcTemplate;
import com.tcloudata.spider.pipeline.NewHouseHtmlDaoPipeline;
import com.tcloudata.spider.scheduler.RedisScheduler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class NewHouseHtmlSpider implements PageProcessor {

    private String cityUrl = "";
    private Site site = Site.me().setCycleRetryTimes(5).setRetryTimes(5).setSleepTime(1000).setTimeOut(3 * 60 * 1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8").setDomain("anjuke.com");

    public NewHouseHtmlSpider(String cityUrl) {
        this.cityUrl = cityUrl;
        site.setDomain(cityUrl.replaceAll("http://", "").replaceAll("/loupan/", ""));
    }

    public NewHouseHtmlSpider() {
    }

    @Override
    public void process(Page page) {
        System.out.println(page.getUrl().toString());
        List<String> links;
        if (cityUrl.isEmpty()) {
            links = page.getHtml().links().regex("http://\\w+.fang.anjuke.com/loupan/.*").all();
        } else {
            String urlReg = cityUrl + ".*";
            links = page.getHtml().links().regex(urlReg).all();
        }
        for (String link : links) {
            String targetUrl = link.contains("#") ? link.replaceAll("#.*", "") : link;

            if (targetUrl.matches(".*fang\\.anjuke\\.com/loupan/[a-zA-Z]+/p[0-9]+/") ||
                    targetUrl.matches(".*fang\\.anjuke\\.com/loupan/[a-zA-Z]+/") ||
                    targetUrl.matches(".*fang\\.anjuke\\.com/loupan/\\d+\\.html")) {

                if (targetUrl.matches(".*fang\\.anjuke\\.com/loupan/\\d+\\.html")) {
                    targetUrl = targetUrl.replaceAll("/loupan/", "/loupan/canshu-");
                }
                page.addTargetRequest(targetUrl);
            }

        }
        if (cityUrl.isEmpty()) {
            links = page.getHtml().links().regex("http://\\w+.anjuke.com/?").all();
            page.addTargetRequests(links);
            links = page.getHtml().links().regex("http://\\w+.fang.anjuke.com/?").all();
            page.addTargetRequests(links);
        }
        if (!page.getUrl().toString().contains("/loupan/canshu-")) {
            page.setSkip(true);
        }

        //参考价格、楼盘地址、最新开盘时间、交房时间、装修标准、产权等信息
        page.putField("title", page.getHtml().xpath("//div[@class=\"lp-tit\"]/h1/text()").toString());
        String city = page.getHtml().xpath("//div[@class=\"crumb-item fl\"]/a[1]/text()").toString();
        if (city != null && !city.isEmpty()) city = city.trim().replace("安居客", "");
        page.putField("city", city);

        if (page.getResultItems().get("title") == null || page.getResultItems().get("city") == null) {
            //skip this page
            page.setSkip(true);
        }
        List<Selectable> canList = page.getHtml().xpath("//div[@class=\"can-left\"]/div[@class=\"can-item\"]").nodes();
        for (Selectable canItem : canList) {
            String info = canItem.xpath("//div[@class=\"can-head\"]").toString();
            if (info.contains("基本信息")) {
                page.putField("basic", canItem.toString());
                continue;
            }
            if (info.contains("销售信息")) {
                page.putField("sale", canItem.toString());
                continue;
            }
            if (info.contains("小区情况") || info.contains("建筑规划")) {
                page.putField("detail", canItem.toString());
                continue;
            }
        }
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
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
                        //.addUrl("http://www.anjuke.com/sy-city.html")
                        .scheduler(redisScheduler)
                        .addPipeline(new ConsolePipeline())
                        .addPipeline(new NewHouseHtmlDaoPipeline(jdbcTemplate))
                        .run();
                MysqlUtil.It().getJtAnjuke().update("UPDATE city SET new_status=2 WHERE city=?", city);
            }
        }

    }
}
