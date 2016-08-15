package com.tcloudata.spider.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Created by Jenray on 2014/6/5.
 */
public class MysqlUtil {

    private static MysqlUtil it = new MysqlUtil();
    private JdbcTemplate jtAnjuke = null;

    private MysqlUtil() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("beans.xml");
        jtAnjuke = new JdbcTemplate((DataSource) applicationContext.getBean("dataSourceAnjuke"));

    }

    public static MysqlUtil It() {
        return it;
    }

    public JdbcTemplate getJtAnjuke() {
        return jtAnjuke;
    }
}
