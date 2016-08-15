package com.tcloudata.spider.bean;

/**
 * Created by jenray on 16/8/2.
 */
public class NewHouseHtmlBean {
    private String title;
    private String city;
    private String basic;
    private String sale;
    private String detail;
    private String md5;

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBasic() {
        return basic;
    }

    public void setBasic(String basic) {
        this.basic = basic;
    }

    public String getSale() {
        return sale;
    }

    public void setSale(String sale) {
        this.sale = sale;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "NewHouseHtmlBean{" +
                "title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", basic='" + basic + '\'' +
                ", sale='" + sale + '\'' +
                ", detail='" + detail + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
