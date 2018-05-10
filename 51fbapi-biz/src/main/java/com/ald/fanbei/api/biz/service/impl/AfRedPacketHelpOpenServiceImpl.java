package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketHelpOpenService;
import com.ald.fanbei.api.biz.service.AfRedPacketTotalService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.dal.dao.AfRedPacketHelpOpenDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRedPacketHelpOpenDo;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Iterator;
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
	
    @Autowired
    private AfRedPacketHelpOpenDao afRedPacketHelpOpenDao;

    @Autowired
    private AfRedPacketTotalService afRedPacketTotalService;

    @Autowired
    private AfResourceService afResourceService;

	@Override
	public List<AfRedPacketHelpOpenDo> findOpenRecordList(Long redPacketTotalId, Integer queryNum) {
		return afRedPacketHelpOpenDao.findOpenRecordList(redPacketTotalId, queryNum);
	}

	@Override
	public AfRedPacketHelpOpenDo getHelpOpenRecord(String openId, Long userId) {
		AfRedPacketHelpOpenDo query = new AfRedPacketHelpOpenDo();
		query.setOpenId(openId);
		query.setUserId(userId);
		return getByCommonCondition(query);
	}

	@Override
	@Transactional
	public AfRedPacketHelpOpenDo open(String wxCode, Long shareId) {
		JSONObject userWxInfo = WxUtil.getUserInfoWithCache(wxCode);
		AfRedPacketTotalDo shareRedPacket = afRedPacketTotalService.getById(shareId);

		checkIsCanOpen(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID), shareRedPacket.getUserId());

		AfRedPacketHelpOpenDo helpOpenDo = new AfRedPacketHelpOpenDo();
		helpOpenDo.setOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
		helpOpenDo.setFriendNick(userWxInfo.getString(UserWxInfoDto.KEY_NICK));
		helpOpenDo.setFriendAvatar(userWxInfo.getString(UserWxInfoDto.KEY_AVATAR));
		helpOpenDo.setRedPacketTotalId(shareId);
		helpOpenDo.setUserId(shareRedPacket.getUserId());

		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
		fillAmountAndDiscountInfo(config, helpOpenDo);
		saveRecord(helpOpenDo);

		afRedPacketTotalService.updateAmount(shareRedPacket, helpOpenDo.getAmount(), "");

		return helpOpenDo;
	}

	@Override
	public BaseDao<AfRedPacketHelpOpenDo, Long> getDao() {
		return afRedPacketHelpOpenDao;
	}

	// 检查是否可以帮拆红包
	private void checkIsCanOpen(String openId, Long userId) {
		AfRedPacketHelpOpenDo helpOpenDo = getHelpOpenRecord(openId, userId);
		if (helpOpenDo != null) {
			throw new FanbeiException("您已帮此用户拆过红包了");
		}
	}

	// 填充拆得的金额和比率
	private void fillAmountAndDiscountInfo(AfResourceDo config, AfRedPacketHelpOpenDo helpOpenDo) {
		JSONArray helpOpenRateConfig = JSONArray.parseArray(config.getValue3());
		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		int num = afRedPacketHelpOpenDao.getOpenedNum(helpOpenDo.getRedPacketTotalId());
		num = num == 0 ? 1 : num;

		String configRate = null;
		for (Iterator it = helpOpenRateConfig.iterator(); it.hasNext();) {
			JSONObject e = (JSONObject) it.next();
			Integer minPepole = Integer.valueOf(e.get("minPepole").toString());
			Integer maxPepole = Integer.valueOf(e.get("maxPepole").toString());
			if (maxPepole != null) {
				if (num >= minPepole && num <= maxPepole) {
					configRate = e.get("rate").toString();
					break;
				}
			} else {
				if (num >= minPepole) {
					BigDecimal amount = new BigDecimal(e.get("amount").toString());
					helpOpenDo.setAmount(amount);
					return;
				}
			}
			continue;
		}

		String[] numArr = configRate.split("-");
		Integer min = Integer.valueOf(numArr[0]);
		Integer max = Integer.valueOf(numArr[1]);
		double rate = Math.random() * (max - min);
		helpOpenDo.setDiscountRate(new BigDecimal(min).add(new BigDecimal(rate))
				.divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
		BigDecimal thresholdAmount = redPacketConfig.getBigDecimal("thresholdAmount");
		helpOpenDo.setAmount(thresholdAmount.multiply(helpOpenDo.getDiscountRate()));
	}

}