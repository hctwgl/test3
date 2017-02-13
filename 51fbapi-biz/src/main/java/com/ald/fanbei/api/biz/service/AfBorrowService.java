package com.ald.fanbei.api.biz.service;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月09日下午4:41:25
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBorrowService {

	/**
	 * 转账成功后处理
	 * @param borrowId
	 * @return
	 */
	int dealWithTransferSuccess(Long borrowId);
}
