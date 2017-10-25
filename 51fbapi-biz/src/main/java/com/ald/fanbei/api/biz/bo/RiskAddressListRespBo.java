package com.ald.fanbei.api.biz.bo;

/**
 * 
 * @类描述：用户通讯录同步返回Bo
 * @author huyang 2017年4月5日上午11:22:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class RiskAddressListRespBo extends RiskRespBo {

    private String succCount;
    private String failCount;

    public String getSuccCount() {
        return succCount;
    }

    public void setSuccCount(String succCount) {
        this.succCount = succCount;
    }

    public String getFailCount() {
        return failCount;
    }

    public void setFailCount(String failCount) {
        this.failCount = failCount;
    }

}
