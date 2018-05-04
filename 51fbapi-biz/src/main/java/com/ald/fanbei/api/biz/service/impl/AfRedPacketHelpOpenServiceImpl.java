package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketHelpOpenService;
import com.ald.fanbei.api.dal.dao.AfRedPacketHelpOpenDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRedPacketHelpOpenDo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * ServiceImpl
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRedPacketHelpOpenService")
public class AfRedPacketHelpOpenServiceImpl extends ParentServiceImpl<AfRedPacketHelpOpenDo, Long>
		implements AfRedPacketHelpOpenService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfRedPacketHelpOpenServiceImpl.class);
   
    @Autowired
    private AfRedPacketHelpOpenDao afRedPacketHelpOpenDao;

	@Override
	public List<AfRedPacketHelpOpenDo> findOpenRecordList(Long redPacketTotalId, Integer queryNum) {
		return afRedPacketHelpOpenDao.findOpenRecordList(redPacketTotalId, queryNum);
	}

	@Override
	public AfRedPacketHelpOpenDo getByOpenIdAndUserId(String openId, Long userId) {
		AfRedPacketHelpOpenDo query = new AfRedPacketHelpOpenDo();
		query.setOpenId(openId);
		query.setUserId(userId);
		return getByCommonCondition(query);
	}

	@Override
	public BaseDao<AfRedPacketHelpOpenDo, Long> getDao() {
		return afRedPacketHelpOpenDao;
	}

}