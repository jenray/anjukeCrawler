package com.tcloudata.spider.dao;

import com.tcloudata.spider.bean.OldHouseHtmlBean;

import javax.sql.DataSource;

/**
 * Created by jenray on 16/8/2.
 */
public interface OldHouseHtmlDao {
    void setDataSource(DataSource ds);

    void add(OldHouseHtmlBean bean);
}
