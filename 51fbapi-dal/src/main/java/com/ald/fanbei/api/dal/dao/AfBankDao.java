package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfBankDo;

/**
 * @类现描述 
 * @author hexin 2017年3月1日 上午10:10:23
 * @version
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfBankDao {

	/**
    * 增加记录
    * @param afBankDo
    * @return
    */
    int addBank(AfBankDo afBankDo);
   /**
    * 更新记录
    * @param afBankDo
    * @return
    */
    int updateBank(AfBankDo afBankDo);
    
    /**
     * 银行列表
     * @return
     */
    List<AfBankDo> getBankList();
}
