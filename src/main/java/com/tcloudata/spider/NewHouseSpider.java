package com.tcloudata.spider;

import com.tcloudata.spider.jdbc.NewHouseJdbcTemplate;
import com.tcloudata.spider.scheduler.RedisScheduler;
import org.jsoup.Jsoup;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

/**
 * Hello world!
 */
public class NewHouseSpider implements PageProcessor {


    private Site site = Site.me().setCycleRetryTimes(5).setRetryTimes(5).setSleepTime(1000).setTimeOut(3 * 60 * 1000)
            .setUserAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
            .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
            .setCharset("UTF-8").setDomain("anjuke.com");


    @Override
    public void process(Page page) {
        System.out.println(page.getUrl().toString());
        //List<String> links = page.getHtml().links().regex("http://xiang.fang.anjuke.com/loupan/.*").all();
        List<String> links = page.getHtml().links().regex("http://\\w+.fang.anjuke.com/loupan/.*").all();
        for (String link : links) {
            String realUrl = link.contains("#") ? link.replaceAll("#.*", "") : link;
            if (realUrl.matches(".*fang\\.anjuke\\.com/loupan/[a-zA-Z]+/") ||
                    realUrl.matches(".*fang\\.anjuke\\.com/loupan/[a-zA-Z]+/p[0-9]+/") ||
                    realUrl.matches(".*fang\\.anjuke\\.com/loupan/\\d+\\.html")) {
                if (realUrl.matches(".*fang\\.anjuke\\.com/loupan/\\d+\\.html")) {
                    realUrl = realUrl.replaceAll("/loupan/", "/loupan/canshu-");
                }
                page.addTargetRequest(realUrl);
            }
        }

        links = page.getHtml().links().regex("http://\\w+.anjuke.com/?").all();
        page.addTargetRequests(links);
        links = page.getHtml().links().regex("http://\\w+.fang.anjuke.com/?").all();
        page.addTargetRequests(links);

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
        String area = page.getHtml().xpath("//div[@class=\"crumb-item fl\"]/a[3]/text()").toString();
        if (area != null && !area.isEmpty()) area = area.trim().replace("楼盘", "");
        page.putField("area", area);

        List<Selectable> canList = page.getHtml().xpath("//div[@class=\"can-left\"]/div[@class=\"can-item\"]").nodes();
        for (Selectable canItem : canList) {
            String info = canItem.xpath("//div[@class=\"can-head\"]").toString();
            if (info.contains("基本信息") || info.contains("销售信息") || info.contains("小区情况") || info.contains("建筑规划")) {
                List<Selectable> items = canItem.xpath("//ul[@class=\"list\"]/li").nodes();
                for (Selectable item : items) {
                    String name = item.xpath("//div[@class=\"name\"]/text()").toString();
                    String des = "";
                    if (name != null && !name.isEmpty()) {
                        name = name.trim();
                        //des = item.xpath("//div[@class=\"des\"]/text()").toString().replaceAll("\\[.*?\\]", "").trim();
                        des = item.xpath("//div[@class=\"des\"]").toString();
                        if (des == null || des.isEmpty()) continue;
                        des = Jsoup.parse(des).text().replaceAll("\\[.*?\\]", "").trim();
                    }
                    if (name.equals("参考单价")) {
                        page.putField("price", des);
                        continue;
                    }

                    if (name.equals("楼盘地址")) {
                        page.putField("address", des);
                        continue;
                    }

                    if (name.equals("开盘时间")) {
                        page.putField("opentime", des);
                        continue;
                    }

                    if (name.equals("交房时间")) {
                        page.putField("deliveredtime", des);
                        continue;
                    }

                    if (name.equals("装修标准")) {
                        page.putField("decoration", des);
                        continue;
                    }

                    if (name.equals("产权年限")) {
                        page.putField("property", des);
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return site;

    }

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
        NewHouseJdbcTemplate newHouseJdbcTemplate = (NewHouseJdbcTemplate) context.getBean("newHouseJdbcTemplate");

        RedisScheduler redisScheduler = new RedisScheduler("redis://192.168.2.43:6379/14");
        Spider.create(new NewHouseSpider())
                .addPipeline(new ConsolePipeline())
                .addUrl("http://xiang.fang.anjuke.com")
                .scheduler(redisScheduler)
                //.addUrl("http://www.anjuke.com/sy-city.html")
                //.addPipeline(new NewHouseDaoPipeline(newHouseJdbcTemplate))
                .run();
    }
}
