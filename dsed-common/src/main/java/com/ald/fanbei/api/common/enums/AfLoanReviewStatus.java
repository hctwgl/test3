/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年3月28日下午2:39:54
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfLoanReviewStatus {
	
	REFUSE("风控拒绝审核不通过"),
	AGREE("风控审核同意"),
	FBAGREE("爱上街审核同意"),
	FBREFUSE("爱上街平台审核拒绝");
    
    public String desz;


    AfLoanReviewStatus(String desz) {
        this.desz = desz;
    }

}
