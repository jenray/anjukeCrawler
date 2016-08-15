package com.tcloudata.spider.dao;

import com.tcloudata.spider.bean.NewHouseHtmlBean;

import javax.sql.DataSource;

/**
 * Created by jenray on 16/8/2.
 */
public interface NewHouseHtmlDao {
    void setDataSource(DataSource ds);

    void add(NewHouseHtmlBean bean);
}
