/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 * @类描述：
 * @author suweili 2017年2月22日上午10:46:14
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfResourceSecType {
	ResourceValue1MainImage("MAIN_IMAGE", "主图"),
	ResourceValue1OtherImage("OTHER_IMAGE", "副图"),
	
	creditScoreAmount("CREDIT_SCORE_AMOUNT","征信等级"),
	H5_URL("H5_URL", "跳转普通H5"),
	GOODS_ID("GOODS_ID", "跳转商品详情"),
	MODEL_URL("MODEL_URL", "本地模板H5");
	

	 private String    code;

   private String name;
   AfResourceSecType(String code, String name) {
       this.code = code;
       this.name = name;
   }
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
