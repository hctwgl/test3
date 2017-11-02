package com.ald.fanbei.api.biz.bo.worm;

import java.io.Serializable;

/**
 * @ClassName SpecificationBo.
 * @desc
 * 第三方自建商城规则参数BO
 * @author <a href="hantao@edspay.com">hantao</a>
 * @version V1.0
 * @date 2017/10/16 1:31
 */
public class SpecificationBo implements Serializable {

    private static final long serialVersionUID = -5651313181510893404L;

    private String pecificationKey; //规则参数key

    private String specificationValue; //规则参数值

    private String businessName; //商家名称

    public String getPecificationKey() {
        return pecificationKey;
    }

    public void setPecificationKey(String pecificationKey) {
        this.pecificationKey = pecificationKey;
    }

    public String getSpecificationValue() {
        return specificationValue;
    }

    public void setSpecificationValue(String specificationValue) {
        this.specificationValue = specificationValue;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }
}
