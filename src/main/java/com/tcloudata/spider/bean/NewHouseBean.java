package com.tcloudata.spider.bean;

/**
 * Created by jenray on 16/8/2.
 */
public class NewHouseBean {
    private String title;
    private String city;
    private String area;
    private String price;
    private String address;
    private String opentime;
    private String deliveredtime;
    private String decoration;
    private String property;
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

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOpenTime() {
        return opentime;
    }

    public void setOpenTime(String opentime) {
        this.opentime = opentime;
    }

    public String getDeliveredTime() {
        return deliveredtime;
    }

    public void setDeliveredTime(String deliveredtime) {
        this.deliveredtime = deliveredtime;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "NewHouseBean{" +
                "title='" + title + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", price='" + price + '\'' +
                ", address='" + address + '\'' +
                ", opentime='" + opentime + '\'' +
                ", deliveredtime='" + deliveredtime + '\'' +
                ", decoration='" + decoration + '\'' +
                ", property='" + property + '\'' +
                ", md5='" + md5 + '\'' +
                '}';
    }
}
