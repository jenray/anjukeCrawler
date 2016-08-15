package com.tcloudata.spider.pipeline;

import com.tcloudata.spider.jdbc.NewHouseJdbcTemplate;

import com.tcloudata.spider.bean.NewHouseBean;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.security.MessageDigest;

/**
 * Created by jenray on 16/8/2.
 */

public class NewHouseDaoPipeline implements Pipeline {
    private NewHouseJdbcTemplate newHouseJdbcTemplate;

    public NewHouseDaoPipeline(NewHouseJdbcTemplate newHouseJdbcTemplate) {
        this.newHouseJdbcTemplate = newHouseJdbcTemplate;
    }

    public static String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();

        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    @Override
    public void process(ResultItems resultItems, Task task) {

        NewHouseBean bean = new NewHouseBean();
        String title = resultItems.get("title");
        bean.setTitle(title);
        String city = resultItems.get("city");
        bean.setCity(city);
        String area = resultItems.get("area");
        bean.setArea(area);
        String price = resultItems.get("price");
        bean.setPrice(price);
        String address = resultItems.get("address");
        bean.setAddress(address);
        String opentime = resultItems.get("opentime");
        bean.setOpenTime(opentime);
        String deliveredtime = resultItems.get("deliveredtime");
        bean.setDeliveredTime(deliveredtime);
        String decoration = resultItems.get("decoration");
        bean.setDecoration(decoration);
        String property = resultItems.get("property");
        bean.setProperty(property);
        String md5 = MD5(city + "-" + title);
        bean.setMd5(md5);
        newHouseJdbcTemplate.add(bean);
        System.out.println(bean.toString());

    }
}