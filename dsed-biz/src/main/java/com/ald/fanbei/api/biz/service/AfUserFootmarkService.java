package com.ald.fanbei.api.biz.service;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfUserFootmarkDo;
import com.ald.fanbei.api.dal.domain.query.AfUserFootmarkQuery;

/**
 * 
 *@类描述：AfUserFootmarkService
 *@author 何鑫 2017年1月19日  15:09:42
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserFootmarkService {

	/**
	 * 处理用户足迹
	 * @param userFootmark
	 * @return
	 */
	int dealUserFootmark(Long userId,Long goodsId);
	
	
	/**
	 * 获取用户足迹数据
	 * @param userId
	 * @return
	 */
	List<AfUserFootmarkDo> getUserFootmarkList(AfUserFootmarkQuery query);
}
