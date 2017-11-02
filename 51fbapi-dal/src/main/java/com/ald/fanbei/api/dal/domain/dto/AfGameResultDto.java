/**
 * 
 */
package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;

/**
 *@类现描述：
 *@author chenjinhu 2017年6月4日 下午7:51:25
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AfGameResultDto extends AbstractSerial {
	private static final long serialVersionUID = 4680653594542699709L;
	private String result;
	private String item;
	private Long lotteryResult;
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public Long getLotteryResult() {
		return lotteryResult;
	}
	public void setLotteryResult(Long lotteryResult) {
		this.lotteryResult = lotteryResult;
	}
	
	
}
