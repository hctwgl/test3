package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserSearchDo;

/**
 * 
 * @类描述：AfUserSearchService
 * @author hexin 2017年2月21日下午15:18:28
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserSearchService {

	/**
    * 增加记录
    * @param afUserSearchDo
    * @return
    */
    int addUserSearch(AfUserSearchDo afUserSearchDo);
}
