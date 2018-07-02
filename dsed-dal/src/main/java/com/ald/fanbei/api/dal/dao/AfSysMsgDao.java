package com.ald.fanbei.api.dal.dao;

import java.util.List;

import com.ald.fanbei.api.dal.domain.AfSysMsgDo;
import com.ald.fanbei.api.dal.domain.query.AfSysMsgQuery;



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
