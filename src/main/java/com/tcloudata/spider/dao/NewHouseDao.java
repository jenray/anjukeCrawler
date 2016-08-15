package com.tcloudata.spider.dao;

import com.tcloudata.spider.bean.NewHouseBean;

import javax.sql.DataSource;

/**
 * Created by jenray on 16/8/2.
 */
public interface NewHouseDao {
    void setDataSource(DataSource ds);

    void add(NewHouseBean bean);
}
