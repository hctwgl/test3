package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;

import java.math.BigDecimal;

public class JsdBorrowCashDto extends JsdBorrowCashDo {
    private BigDecimal shouldRepaySum;		//应还总额
    private String company;//清算公司
    private String productType;//产品类型
    private String productName;//产品名称

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getShouldRepaySum() {
        return shouldRepaySum;
    }

    public void setShouldRepaySum(BigDecimal shouldRepaySum) {
        this.shouldRepaySum = shouldRepaySum;
    }
}
