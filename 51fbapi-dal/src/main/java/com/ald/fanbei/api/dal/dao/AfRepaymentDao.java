package com.ald.fanbei.api.dal.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.ald.fanbei.api.dal.domain.AfRepaymentDo;

/**
 * @类描述：
 * @author hexin 2017年2月22日下午14:14:26
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfRepaymentDao {

	/**
    * 增加记录
    * @param afRepaymentDo
    * @return
    */
    int addRepayment(AfRepaymentDo afRepaymentDo);
    
    int updateRepayment(@Param("status")String status,@Param("tradeNo")String tradeNo,@Param("rid")Long rid);
    
    /**
     * 获取最近还款编号
     * @param current
     * @return
     */
    public String getCurrentLastRepayNo(String orderNoPre);
    
    /**
     * 通过payTradeNo获取详情
     * @param rid
     * @return
     */
    AfRepaymentDo getRepaymentById(@Param("rid")Long rid);
    
    AfRepaymentDo getRepaymentByPayTradeNo(@Param("payTradeNo")String payTradeNo);
    
    /**
     * 通过payTradeNo获取详情
     * @param rid
     * @return
     */
    List<AfRepaymentDo> getRepaymentListByIds(@Param("items")List<Long> ids);
    
    
   int  updateRepaymentByAfRepaymentDo(AfRepaymentDo repaymentDo);
}
