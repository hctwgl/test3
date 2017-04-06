package com.ald.fanbei.api.biz.bo;

import java.util.HashMap;

/**
 * 
 * @类描述： 通讯录封装Bo
 * 
 * @author huyang 2017年4月5日上午11:31:50
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskAddressListDetailBo extends HashMap<String, String> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String nickname;// 真实姓名
    private String phone;// 手机号码
    private String tradeNo;// 明细流水号

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        this.put("nickname", nickname);
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.put("phone", phone.replaceAll("\"", ""));
    }

    public String getTradeNo() {
        return tradeNo;
    }

    public void setTradeNo(String tradeNo) {
        this.tradeNo = tradeNo;
        this.put("tradeNo", tradeNo);
    }

}
