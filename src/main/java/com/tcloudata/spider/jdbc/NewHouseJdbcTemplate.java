package com.tcloudata.spider.jdbc;

import com.tcloudata.spider.bean.NewHouseBean;
import com.tcloudata.spider.dao.NewHouseDao;
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
public class NewHouseJdbcTemplate implements NewHouseDao {
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource ds) {
        this.dataSource = ds;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void add(NewHouseBean bean) {
        String qSql = "SELECT COUNT(1) FROM new_house WHERE md5=? ";
        int counter = jdbcTemplate.queryForObject(qSql, Integer.class, bean.getMd5());
        if (counter > 0) {
            String upSql = "UPDATE new_house SET title=?,city=?,area=?,price=?,address=?,opentime=?,deliveredtime=?,decoration=?,property=? WHERE md5=?";
            jdbcTemplate.update(upSql,
                    bean.getTitle(), bean.getCity(), bean.getArea(), bean.getPrice(), bean.getAddress(), bean.getOpenTime(),
                    bean.getDeliveredTime(), bean.getDecoration(), bean.getProperty(), bean.getMd5());
            System.out.println("Update Recode : " + bean.toString());
        } else {
            String inSql = "INSERT INTO new_house (title,city,area,price,address,opentime,deliveredtime,decoration,property,md5) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            jdbcTemplate.update(inSql,
                    bean.getTitle(), bean.getCity(), bean.getArea(), bean.getPrice(), bean.getAddress(), bean.getOpenTime(),
                    bean.getDeliveredTime(), bean.getDecoration(), bean.getProperty(), bean.getMd5());
            System.out.println("Insert Recode : " + bean.toString());
        }


    }
}
