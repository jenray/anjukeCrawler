package com.tcloudata.spider;

import com.tcloudata.spider.dao.MysqlUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by jenray on 16/8/8.
 */
public class CitySpider {
    public static void main(String[] args) {

        try {
            Document document = Jsoup.connect("http://www.anjuke.com/sy-city.html")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                    .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                    .timeout(10000)
                    .get();
            HashSet<String> cityUrlSet = new HashSet<String>();
            String url = "";
            for (Element ddItem : document.getElementsByTag("dd")) {
                for (Element aItem : ddItem.getElementsByTag("a")) {
                    url = aItem.attr("href");
                    if (url.endsWith("/")) url = url.substring(0, url.length() - 1);
                    System.out.println(aItem.text() + "\t" + url);
                    cityUrlSet.add(url);
                    System.out.println(cityUrlSet.size());
                }
            }

            String qSql = "SELECT COUNT(1) FROM city WHERE city=? ";
            String upSql = "UPDATE city SET new_url=?,old_url=?,new_status=0,old_status=0 WHERE city=?";
            String inSql = "INSERT INTO city (city,new_url,old_url) VALUES (?, ?, ?)";
            for (String cityUrl : cityUrlSet) {
                try {
                    document = Jsoup.connect(cityUrl)
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0")
                            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                            .header("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                            .timeout(10000)
                            .get();
                    String newUrl = "", oldUrl = "", city = "";

                    city = document.title().replaceAll("房产网.*", "");
                    for (Element aItem : document.getElementById("glbNavigation").getElementsByTag("a")) {
                        if (aItem.attr("href").matches(".*fang.anjuke.com/loupan/?")) {
                            newUrl = aItem.attr("href");

                        } else if (aItem.attr("href").matches(".*anjuke.com/community/?")) {
                            oldUrl = aItem.attr("href");

                        }
                    }

                    System.out.println(city + "\t\t[新盘]\t" + newUrl + "\t\t[小区]\t" + oldUrl);
                    int counter = MysqlUtil.It().getJtAnjuke().queryForObject(qSql, Integer.class, city);
                    if (counter > 0) {
                        MysqlUtil.It().getJtAnjuke().update(upSql,
                                newUrl, oldUrl, city);
                    } else {
                        MysqlUtil.It().getJtAnjuke().update(inSql,
                                city, newUrl, oldUrl);
                    }
                    Thread.sleep(500);
                } catch (Exception ex) {
                    System.out.println(cityUrl);
                    ex.printStackTrace();
                    continue;
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
