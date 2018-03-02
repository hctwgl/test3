package com.ald.fanbei.api.dal.domain;

import java.math.BigDecimal;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @类描述：用户账号相关信息
 * @author Xiaotianjian 2017年1月19日下午4:04:09
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Getter
@Setter
public class AfUserAccountDo extends AbstractSerial {
	
	private static final long serialVersionUID = 570354935460086063L;
	
	private Long rid;
	private Long userId; //用户id
	private String userName;//用户名
	private String realName;
	private BigDecimal auAmount;//授信额度,总金额
	private BigDecimal usedAmount;//已使用金额
	private BigDecimal freezeAmount;//冻结金额
	private BigDecimal score;//积分	
	private String alipayAccount;//支付宝账号	
	private BigDecimal rebateAmount;//返利（淘宝返利）
	private String idNumber;//身份证号
	private BigDecimal jfbAmount;//返利（淘宝返利）
	private String password;//支付密码
	private String salt;//盐值
	private BigDecimal ucAmount;//已取现额度
	private Integer failCount; //密码连续错误次数
	private String bindCard;//是否绑卡：Y：绑卡：N：未绑卡
	private Integer creditScore;//信用分
	private String openId;//芝麻信用openId
	private BigDecimal borrowCashAmount;//借款最高金额


	public AfUserAccountDo(){

	}


	public AfUserAccountDo(Long userId, BigDecimal rebateAmount){
		this.userId = userId;
		this.rebateAmount = rebateAmount;
	}

}
