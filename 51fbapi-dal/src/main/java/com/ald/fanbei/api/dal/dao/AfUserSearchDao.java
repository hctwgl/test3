package com.ald.fanbei.api.dal.dao;

import com.ald.fanbei.api.dal.domain.AfUserSearchDo;

/**
 * 
 * @类描述：AfUserSearchDao
 * @author hexin 2017年2月21日下午15:15:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserSearchDao {

	/**
    * 增加记录
    * @param afUserSearchDo
    * @return
    */
    int addUserSearch(AfUserSearchDo afUserSearchDo);
}
