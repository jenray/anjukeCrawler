package com.tcloudata.spider.pipeline;

import com.tcloudata.spider.bean.OldHouseHtmlBean;
import com.tcloudata.spider.jdbc.OldHouseHtmlJdbcTemplate;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * Created by jenray on 16/8/2.
 */

public class OldHouseHtmlDaoPipeline implements Pipeline {
    private OldHouseHtmlJdbcTemplate jdbcTemplate;

    public OldHouseHtmlDaoPipeline(OldHouseHtmlJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        OldHouseHtmlBean bean = new OldHouseHtmlBean();
        String title = resultItems.get("title");
        bean.setTitle(title);
        String city = resultItems.get("city");
        bean.setCity(city);
        String basic = resultItems.get("basic");
        bean.setBasic(basic);
        String detail = resultItems.get("detail");
        bean.setDetail(detail);
        String md5 = NewHouseDaoPipeline.MD5(city + "-" + title);
        bean.setMd5(md5);
        jdbcTemplate.add(bean);
        System.out.println(bean.toString());

    }
}