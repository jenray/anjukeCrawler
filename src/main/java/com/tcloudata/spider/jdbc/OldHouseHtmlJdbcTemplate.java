package com.tcloudata.spider.jdbc;

import com.tcloudata.spider.bean.OldHouseHtmlBean;
import com.tcloudata.spider.dao.MysqlUtil;
import com.tcloudata.spider.dao.OldHouseHtmlDao;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by jenray on 16/8/2.
 */
public class OldHouseHtmlJdbcTemplate implements OldHouseHtmlDao {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void add(OldHouseHtmlBean bean) {
        String qSql = "SELECT COUNT(1) FROM old_house_html WHERE md5=? ";
        int counter = jdbcTemplate.queryForObject(qSql, Integer.class, bean.getMd5());
        if (counter > 0) {
            String upSql = "UPDATE old_house_html SET title=?,city=?,basic=?,detail=? WHERE md5=?";
            jdbcTemplate.update(upSql,
                    bean.getTitle(), bean.getCity(), bean.getBasic(), bean.getDetail(), bean.getMd5());
            System.out.println("Update Recode : " + bean.toString());
        } else {
            String inSql = "INSERT INTO old_house_html (title,city,basic,detail,md5) VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(inSql,
                    bean.getTitle(), bean.getCity(), bean.getBasic(), bean.getDetail(), bean.getMd5());
            System.out.println("Insert Recode : " + bean.toString());
        }


    }
}
