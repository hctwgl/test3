package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketHelpOpenService;
import com.ald.fanbei.api.biz.service.AfRedPacketTotalService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
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
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private BizCacheUtil bizCacheUtil;

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
	public boolean isCanOpen(AfRedPacketTotalDo shareRedPacket, String openId) {
		try {
			checkIsCanOpen(shareRedPacket, openId);
		} catch (FanbeiException e) {
			return false;
		}
		return true;
	}

	@Override
	public AfRedPacketHelpOpenDo open(final String wxCode, final Long shareId) {
        AfResourceDo afResourceDo = afResourceService.getWechatConfig();
        String appid = afResourceDo.getValue();
        String secret = afResourceDo.getValue1();
        final JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, wxCode);

        String lock = "AfRedPacketHelpOpenServiceImpl_open_lock_" + wxCode;
		boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(lock, lock,500, Constants.SECOND_OF_TEN_MINITS);
		if (isLock) {
			try {
				return transactionTemplate.execute(new TransactionCallback<AfRedPacketHelpOpenDo>() {
					@Override
					public AfRedPacketHelpOpenDo doInTransaction(TransactionStatus transactionStatus) {
						AfRedPacketTotalDo shareRedPacket = afRedPacketTotalService.getById(shareId);

						checkIsCanOpen(shareRedPacket, userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));

						AfRedPacketHelpOpenDo helpOpenDo = new AfRedPacketHelpOpenDo();
						helpOpenDo.setOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
						helpOpenDo.setFriendNick(userWxInfo.getString(UserWxInfoDto.KEY_NICK));
						helpOpenDo.setFriendAvatar(userWxInfo.getString(UserWxInfoDto.KEY_AVATAR));
						helpOpenDo.setRedPacketTotalId(shareId);
						helpOpenDo.setUserId(shareRedPacket.getUserId());

						AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
						fillAmountAndDiscountInfo(config, helpOpenDo, shareRedPacket);
						saveRecord(helpOpenDo);

						afRedPacketTotalService.updateAmount(shareRedPacket.getRid(), helpOpenDo.getAmount(), "");

						return helpOpenDo;
					}
				});
			} finally {
				bizCacheUtil.delCache(lock);
			}
		} else {
			throw new RuntimeException(lock + "锁没有获取到");
		}
	}

	@Override
	public BaseDao<AfRedPacketHelpOpenDo, Long> getDao() {
		return afRedPacketHelpOpenDao;
	}

	// 检查是否可以拆红包
	private void checkIsCanOpen(AfRedPacketTotalDo shareRedPacket, String openId) {
		if (afRedPacketTotalService.isInvalid(shareRedPacket)) {
			throw new FanbeiException("分享的红包已失效");
		}

		AfRedPacketHelpOpenDo helpOpenDo = getHelpOpenRecord(openId, shareRedPacket.getUserId());
		if (helpOpenDo != null) {
			throw new FanbeiException("您已帮此用户拆过红包了");
		}
	}

	// 填充拆得的金额和比率
	private void fillAmountAndDiscountInfo(AfResourceDo config, AfRedPacketHelpOpenDo helpOpenDo,
										   AfRedPacketTotalDo shareRedPacket) {
		JSONArray helpOpenRateConfig = JSONArray.parseArray(config.getValue3());
		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		int openedNum = afRedPacketHelpOpenDao.getOpenedNum(shareRedPacket.getRid());
		int openingNum = openedNum + 1;

		String configRate = null;
		for (Object obj : helpOpenRateConfig) {
			JSONObject e = (JSONObject) obj;
			Integer minPepole = Integer.valueOf(e.get("minPepole").toString());
			Integer maxPepole = null;
			if (e.get("maxPepole") != null) {
				maxPepole = Integer.valueOf(e.get("maxPepole").toString());
			}

			if (maxPepole != null) {
				if (openingNum >= minPepole && openingNum <= maxPepole) {
					configRate = e.get("rate").toString();
					break;
				}
			} else {
				if (openingNum >= minPepole) {
					BigDecimal amount = new BigDecimal(e.get("amount").toString());
					helpOpenDo.setAmount(amount);
					return;
				}
			}
		}

		if (configRate == null) {
			throw new FanbeiException("帮差红包比率配置错误，第" + openingNum + "个人帮拆，却找不到对应的比率配置");
		}
		String[] numArr = configRate.split("-");
		Integer min = Integer.valueOf(numArr[0]);
		Integer max = Integer.valueOf(numArr[1]);
		double rate = Math.random() * (max - min);
		helpOpenDo.setDiscountRate(new BigDecimal(min).add(new BigDecimal(rate))
				.divide(new BigDecimal(100), 3, RoundingMode.HALF_UP));

		BigDecimal thresholdAmount = redPacketConfig.getBigDecimal("thresholdAmount");
		BigDecimal withdrawRestAmount = afRedPacketTotalService.calcWithdrawRestAmount(shareRedPacket, thresholdAmount);
		BigDecimal amount = withdrawRestAmount.multiply(helpOpenDo.getDiscountRate());
		// 如果算得的金额是0，则随机选择0或是0.01
		if (amount.compareTo(BigDecimal.ZERO) == 0) {
			long currTime = System.currentTimeMillis();
			if (currTime % 2 == 1) {
				amount = new BigDecimal(0.01);
			}
		}
		helpOpenDo.setAmount(amount);
	}
}