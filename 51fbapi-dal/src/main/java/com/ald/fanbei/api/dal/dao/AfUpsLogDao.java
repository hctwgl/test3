package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUpsLogDo;

/**
 * 
 * @类描述：
 * @author hexin 2017年3月24日下午13:21:33
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUpsLogDao {

	/**
    * 增加记录
    * @param afUpsLogDo
    * @return
    */
    int addUpsLog(AfUpsLogDo afUpsLogDo);
}
