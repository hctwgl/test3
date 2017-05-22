package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;

/**
 * @类描述：
 * 
 * @author fumeiai 2017年5月18日 20:15:10
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRenewalDetailDao {
	/**
	 * 增加记录
	 * 
	 * @param afRenewalDetailDo
	 * @return
	 */
	int addRenewalDetail(AfRenewalDetailDo afRenewalDetailDo);

	/**
	 * 更新记录
	 * 
	 * @param afRenewalDetailDo
	 * @return
	 */
	int updateRenewalDetail(AfRenewalDetailDo afRenewalDetailDo);

	AfRenewalDetailDo getRenewalDetailByPayTradeNo(@Param("payTradeNo") String payTradeNo);

	/**
	 * 续借信息
	 * 
	 * @param borrowId
	 * @param 开始条数
	 * @return
	 */
	List<AfRenewalDetailDo> getRenewalListByBorrowId(Long borrowId, Integer start);

	/**
	 * 续借详情
	 * 
	 * @param rId
	 * @return
	 */
	public AfRenewalDetailDo getRenewalDetailByRenewalId(Long rId);

	/**
	 * 获取最新的续借信息
	 * 
	 * @param borrowId
	 * @return
	 */
	public AfRenewalDetailDo getRenewalDetailByBorrowId(Long borrowId);

}
