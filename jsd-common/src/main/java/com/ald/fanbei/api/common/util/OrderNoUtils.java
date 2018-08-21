package com.ald.fanbei.api.common.util;
 
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
 
/**
 * 类OrderNoUtils.java的实现描述： 自动生成有规则的订单号(或编号) 
 * @author chengkang 2017年6月6日 上午9:07:09
 */
public class OrderNoUtils {
    protected final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
 
    static OrderNoUtils orderNoUtils = new OrderNoUtils();
 
    public static OrderNoUtils getInstance() {
        if (orderNoUtils == null) {
            orderNoUtils = new OrderNoUtils();
            return orderNoUtils;
        } else {
            return orderNoUtils;
        }
    }
 
    /**
     * 产生唯一 的序列号。
     * 
     * @return
     */
    public String getSerialNumber() {
        int hashCode = UUID.randomUUID().toString().hashCode();
        if (hashCode < 0) {
            hashCode = -hashCode;
        }
        return sdf.format(new Date()).substring(2, 8) + String.format("%010d", hashCode);
    }
}