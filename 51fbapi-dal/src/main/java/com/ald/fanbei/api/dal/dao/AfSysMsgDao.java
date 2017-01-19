package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfSysMsgDo;
import com.ald.fanbei.api.dal.domain.query.AfSysMsgQuery;

/**
 * 
 *@类描述：AfSysMsgDao
 *@author 何鑫 2017年1月19日  20:08:35
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfSysMsgDao {

	/**
	 * 新增消息日志
	 * @param userFootmark
	 * @return
	 */
	int addSysMsg(AfSysMsgDo afSysMsgDo);
	
	/**
	 * 获取消息日志数据
	 * @param userId
	 * @param pageNo
	 * @return
	 */
	List<AfSysMsgDo> getSysMsgList(AfSysMsgQuery query);
}
