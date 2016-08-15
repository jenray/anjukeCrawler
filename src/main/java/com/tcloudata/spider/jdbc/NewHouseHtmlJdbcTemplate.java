package com.tcloudata.spider.jdbc;

import com.tcloudata.spider.bean.NewHouseHtmlBean;
import com.tcloudata.spider.dao.NewHouseHtmlDao;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by jenray on 16/8/2.
 * private String title;
 * private String city;
 * private String area;
 * private String price;
 * private String address;
 * private String opentime;
 * private String deliveredtime;
 * private String decoration;
 * private String property;
 * private String md5;
 */
public class NewHouseHtmlJdbcTemplate implements NewHouseHtmlDao {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void add(NewHouseHtmlBean bean) {
        String qSql = "SELECT COUNT(1) FROM new_house_html WHERE md5=? ";
        int counter = jdbcTemplate.queryForObject(qSql, Integer.class, bean.getMd5());
        if (counter > 0) {
            String upSql = "UPDATE new_house_html SET title=?,city=?,basic=?,sale=?,detail=? WHERE md5=?";
            jdbcTemplate.update(upSql,
                    bean.getTitle(), bean.getCity(), bean.getBasic(), bean.getSale(), bean.getDetail(), bean.getMd5());
            System.out.println("Update Recode : " + bean.toString());
        } else {
            String inSql = "INSERT INTO new_house_html (title,city,basic,sale,detail,md5) VALUES (?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(inSql,
                    bean.getTitle(), bean.getCity(), bean.getBasic(), bean.getSale(), bean.getDetail(), bean.getMd5());
            System.out.println("Insert Recode : " + bean.toString());
        }


    }
}
