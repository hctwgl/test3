package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;

import java.util.List;

/**
 * @类描述：
 * 贷款超市Service
 * @author chengkang 2017年6月3日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanSupermarketService {
	/**
    * 增加记录
    * @param afLoanSupermarketDo
    * @return
    */
    int addLoanSupermarket(AfLoanSupermarketDo afLoanSupermarketDo);
   /**
    * 更新记录
    * @param afLoanSupermarketDo
    * @return
    */
    int updateLoanSupermarket(AfLoanSupermarketDo afLoanSupermarketDo);
	/**
	 * 贷款超市信息
	 * @param 开始条数
	 * @return
	 */
	List<AfLoanSupermarketDo> getLoanSupermarketListOrderNo(Integer start);
	/**
	 * 根据贷款超市编号获取贷款超市信息
	 * @param lsmNo
	 * @return
	 */
	AfLoanSupermarketDo getLoanSupermarketByLsmNo(String lsmNo);

	/**
	 * 根据标签查询贷款超市
	 * @param label
	 * @return
	 */
	List<AfLoanSupermarketDo> getLoanSupermarketByLabel(String label,String systemType);

	List<Object> getLoanHomeListByLable(String label,String systemType);

}
