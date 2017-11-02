/**
 * 
 */
package com.ald.fanbei.api.common.enums;

/**
 *@类现描述：
 *@author chenjinhu 2017年6月4日 下午3:49:14
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public enum AfGameChanceType {
	 INVITE("I", "邀请次数"), 
	 DAY("D", "每天次数");
   
   private String code;
   private String name;


   AfGameChanceType(String code, String name) {
       this.code = code;
       this.name = name;
   }

   public static CouponStatus findRoleTypeByCode(String code) {
       for (CouponStatus roleType : CouponStatus.values()) {
           if (roleType.getCode().equals(code)) {
               return roleType;
           }
       }
       return null;
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
