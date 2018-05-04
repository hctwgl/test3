package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketSelfOpenService;
import com.ald.fanbei.api.biz.service.AfUserThirdInfoService;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.dal.dao.AfRedPacketSelfOpenDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRedPacketSelfOpenDo;
import com.ald.fanbei.api.dal.domain.dto.AfRedPacketSelfOpenDto;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
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
 
@Service("afRedPacketSelfOpenService")
public class AfRedPacketSelfOpenServiceImpl extends ParentServiceImpl<AfRedPacketSelfOpenDo, Long>
		implements AfRedPacketSelfOpenService {

	@Autowired
	private AfRedPacketSelfOpenDao afRedPacketSelfOpenDao;

	@Autowired
	private AfUserThirdInfoService afUserThirdInfoService;

	@Override
	public List<AfRedPacketSelfOpenDto> findOpenRecordList(Long redPacketTotalId) {
		List<AfRedPacketSelfOpenDto> result = afRedPacketSelfOpenDao.findOpenRecordList(redPacketTotalId);
		if (CollectionUtil.isNotEmpty(result)) {
			// 用户有微信头像和昵称，优先使用微信的
			UserWxInfoDto userWxInfo = afUserThirdInfoService.getUserWxInfo(result.get(0).getUserId());
			if (userWxInfo != null) {
				for (AfRedPacketSelfOpenDto e : result) {
					e.setUserAvatar(userWxInfo.getAvatar());
					e.setUserNick(userWxInfo.getNick());
				}
			}
		}
		return result;
	}

		@Override
	public BaseDao<AfRedPacketSelfOpenDo, Long> getDao() {
		return afRedPacketSelfOpenDao;
	}

}