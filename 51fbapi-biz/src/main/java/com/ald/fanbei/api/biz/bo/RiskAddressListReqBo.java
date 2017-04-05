package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 
 * @类描述： 用户通讯录同步请求Bo
 * @author huyang 2017年4月5日上午11:15:02
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskAddressListReqBo extends HashMap<String, String> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String orderNo;
    private String consumerNo;
    private String count;
    private String details;
    private String signInfo;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
        this.put("orderNo", orderNo);
    }

    public String getConsumerNo() {
        return consumerNo;
    }

    public void setConsumerNo(String consumerNo) {
        this.consumerNo = consumerNo;
        this.put("consumerNo", consumerNo);
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
        this.put("count", count);
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
        this.put("details", details);
    }

    public String getSignInfo() {
        return signInfo;
    }

    public void setSignInfo(String signInfo) {
        this.signInfo = signInfo;
        this.put("signInfo", signInfo);
    }

}
