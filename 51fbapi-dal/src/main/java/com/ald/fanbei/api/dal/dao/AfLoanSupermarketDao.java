/**
 * 
 */
package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;

/**
 * @类描述：
 * 贷款超市Dao
 * @author chengkang 2017年6月3日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfLoanSupermarketDao {

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
	List<AfLoanSupermarketDo> getLoanSupermarketListOrderNo(@Param("start") Integer start);

	/**
	 * 根据贷款超市编号获取贷款超市信息
	 * @param lsmNo
	 * @return
	 */
	AfLoanSupermarketDo getLoanSupermarketByLsmNo(@Param("lsmNo") String lsmNo);

	/**
	 * 根据标签查询贷款超市
	 * @param label
	 * @return
	 */
	List<AfLoanSupermarketDo> getLoanSupermarketByLabel(@Param("label")String label);
}
