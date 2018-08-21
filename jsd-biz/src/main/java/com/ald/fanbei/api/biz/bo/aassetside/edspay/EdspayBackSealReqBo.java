package com.ald.fanbei.api.biz.bo.aassetside.edspay;

import java.io.Serializable;

/**
 * @author chengkang 2017年11月29日 14:29:12
 * @类现描述：钱包平台退回债权请求实体
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class EdspayBackSealReqBo implements Serializable {

    private static final long serialVersionUID = 4347678991772430075L;

    private String edspayUserCardId;

    private String userType;//用户类型 1：公司 2：个人 3：钱包用户

    private String realName;//用户姓名

    private String mobile;//用户手机号

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEdspayUserCardId() {
        return edspayUserCardId;
    }

    public void setEdspayUserCardId(String edspayUserCardId) {
        this.edspayUserCardId = edspayUserCardId;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

}
