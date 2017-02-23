package com.ald.fanbei.api.dal.dao;

import java.util.Date;

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
    
    public String getCurrentLastRepayNo(Date current);
}
