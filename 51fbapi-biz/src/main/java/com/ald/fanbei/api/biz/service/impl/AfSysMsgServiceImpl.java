package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfSysMsgService;
import com.ald.fanbei.api.dal.dao.AfSysMsgDao;
import com.ald.fanbei.api.dal.domain.AfSysMsgDo;
import com.ald.fanbei.api.dal.domain.query.AfSysMsgQuery;
/**
 * 
 *@类描述：AfSysMsgServiceImpl
 *@author 何鑫 2017年1月19日  20:19:57
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afSysMsgService")
public class AfSysMsgServiceImpl implements AfSysMsgService{

	@Resource
	private AfSysMsgDao afSysMsgDao;
	
	@Override
	public int addSysMsg(Long userId, String title, String content) {
		AfSysMsgDo afSysMsg	= new AfSysMsgDo();
		afSysMsg.setUserId(userId);
		afSysMsg.setTitle(title);
		afSysMsg.setContent(content);
		return afSysMsgDao.addSysMsg(afSysMsg);
	}

	@Override
	public List<AfSysMsgDo> getSysMsgList(AfSysMsgQuery query) {
		return afSysMsgDao.getSysMsgList(query);
	}

}
