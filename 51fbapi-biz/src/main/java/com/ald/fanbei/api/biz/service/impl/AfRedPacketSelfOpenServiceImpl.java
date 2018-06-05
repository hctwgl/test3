package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketSelfOpenService;
import com.ald.fanbei.api.biz.service.AfRedPacketTotalService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserThirdInfoService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.SelfOpenRedPacketSourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfRedPacketSelfOpenDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRedPacketSelfOpenDo;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.AfRedPacketSelfOpenDto;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
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
 
@Service("afRedPacketSelfOpenService")
public class AfRedPacketSelfOpenServiceImpl extends ParentServiceImpl<AfRedPacketSelfOpenDo, Long>
		implements AfRedPacketSelfOpenService {

	@Autowired
	private AfRedPacketSelfOpenDao afRedPacketSelfOpenDao;

	@Autowired
	private AfUserThirdInfoService afUserThirdInfoService;

	@Autowired
	private AfResourceService afResourceService;

	@Autowired
	private AfRedPacketTotalService afRedPacketTotalService;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private BizCacheUtil bizCacheUtil;

	@Override
	public List<AfRedPacketSelfOpenDto> findOpenRecordList(Long redPacketTotalId) {
		List<AfRedPacketSelfOpenDto> result = afRedPacketSelfOpenDao.findOpenRecordList(redPacketTotalId);
		if (CollectionUtil.isNotEmpty(result)) {
			UserWxInfoDto userWxInfo = afUserThirdInfoService.getWxOrLocalUserInfo(result.get(0).getUserId());
			for (AfRedPacketSelfOpenDto e : result) {
				e.setUserAvatar(userWxInfo.getAvatar());
				e.setUserNick(userWxInfo.getNick());
			}
		}
		return result;
	}

	@Override
	public int getOpenedNum(Long redPacketTotalId) {
		return afRedPacketSelfOpenDao.getOpenedNum(redPacketTotalId);
	}

	@Override
	public AfRedPacketSelfOpenDo open(final Long userId, final String modifier, final String sourceType) {
		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
		final JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		final JSONObject selfOpenRateConfig = JSONObject.parseObject(config.getValue2());

		String lock = "AfRedPacketSelfOpenServiceImpl_open_lock_" + userId;
		boolean isLock = bizCacheUtil.getLockTryTimesSpecExpire(lock, lock,500, Constants.SECOND_OF_TEN_MINITS);
		if (isLock) {
			try {
				return transactionTemplate.execute(new TransactionCallback<AfRedPacketSelfOpenDo>() {
					@Override
					public AfRedPacketSelfOpenDo doInTransaction(TransactionStatus transactionStatus) {
						AfRedPacketTotalDo theOpening = afRedPacketTotalService
								.getTheOpeningMust(userId, modifier, redPacketConfig.getInteger("overdueIntervalHour"));

						checkIsCanOpen(sourceType, theOpening, redPacketConfig);

						AfRedPacketSelfOpenDo selfOpenDo = new AfRedPacketSelfOpenDo();
						selfOpenDo.setRedPacketTotalId(theOpening.getRid());
						selfOpenDo.setSourceType(sourceType);
						selfOpenDo.setCreator(modifier);
						selfOpenDo.setModifier(modifier);
						selfOpenDo.setDiscountRate(calcDiscountRate(selfOpenRateConfig, sourceType));

						BigDecimal thresholdAmount = redPacketConfig.getBigDecimal("thresholdAmount");
						BigDecimal withdrawRestAmount = afRedPacketTotalService.calcWithdrawRestAmount(theOpening, thresholdAmount);
						selfOpenDo.setAmount(withdrawRestAmount.multiply(selfOpenDo.getDiscountRate()));

						saveRecord(selfOpenDo);

						afRedPacketTotalService.updateAmount(theOpening.getRid(), selfOpenDo.getAmount(), modifier);

						return selfOpenDo;
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
	public AfRedPacketSelfOpenDo bindPhoneAndOpen(Long userId, String modifier, String wxCode, String sourceType) {
		AfResourceDo afResourceDo = afResourceService.getWechatConfig();
		String appid = afResourceDo.getValue();
		String secret = afResourceDo.getValue1();
		JSONObject userWxInfo = WxUtil.getUserInfoWithCache(appid, secret, wxCode);
		afUserThirdInfoService.bindUserWxInfo(userWxInfo, userId, modifier);

		return open(userId, modifier, sourceType);
	}

	@Override
	public BaseDao<AfRedPacketSelfOpenDo, Long> getDao() {
		return afRedPacketSelfOpenDao;
	}

	// 判断是否已拆过某个来源的红包
	private boolean isHaveOpened(Long redPacketTotalId, String sourceType) {
		AfRedPacketSelfOpenDo query = new AfRedPacketSelfOpenDo();
		query.setRedPacketTotalId(redPacketTotalId);
		query.setSourceType(sourceType);
		AfRedPacketSelfOpenDo e = getByCommonCondition(query);
		return e != null;
	}

	// 检查是否能拆红包
	private void checkIsCanOpen(String sourceType, AfRedPacketTotalDo theOpening, JSONObject redPacketConfig) {
		if (theOpening == null) {
			return;
		}

		Integer shareTime = redPacketConfig.getInteger("shareTime");
		if (shareTime != null) {
			int openedNum = getOpenedNum(theOpening.getRid());
			if (openedNum >= (shareTime + 1)) {
				throw new FanbeiException("您已没有红包可拆，继续分享可以让好友帮您拆");
			}
		}

		if (StringUtil.isNotBlank(sourceType)) {
			boolean isOpened= isHaveOpened(theOpening.getRid(), sourceType);
			if (isOpened) {
				if (sourceType.equals(SelfOpenRedPacketSourceType.OPEN_SHARE_MOMENTS.getCode())) {
					throw new FanbeiException("分享微信好友可再获得一个红包哦");
				} else if (sourceType.equals(SelfOpenRedPacketSourceType.OPEN_SHARE_FRIEND.getCode())) {
					throw new FanbeiException("分享朋友圈可再获得一个红包哦");
				} else {
					throw new FanbeiException("您已拆过红包了，请刷新页面查看");
				}
			}
		}

		Integer everydayWithdrawNum = redPacketConfig.getInteger("everydayWithdrawNum");
		if (everydayWithdrawNum != null) {
			int todayWithdrawedNum = afRedPacketTotalService.getTodayWithdrawedNum(theOpening.getUserId());
			if (todayWithdrawedNum >= everydayWithdrawNum) {
				throw new FanbeiException("今日已不能再拆红包，请明天再来哦");

			}
		}

		BigDecimal everydayWithdrawAmount = redPacketConfig.getBigDecimal("withdrawAmount");
		if (afRedPacketTotalService.isReachWithdrawAmountThreshold(everydayWithdrawAmount)) {
			throw new FanbeiException("今日红包已瓜分完喽");
		}
	}

	// 计算拆红包比率
	private BigDecimal calcDiscountRate(JSONObject selfOpenRateConfig, String sourceType) {
		String configRate;
		if (sourceType.equals(SelfOpenRedPacketSourceType.OPEN_SELF.getCode())) {
			configRate = selfOpenRateConfig.getString("firstOpen");
		} else if(sourceType.equals(SelfOpenRedPacketSourceType.OPEN_SHARE_MOMENTS.getCode())) {
			configRate = selfOpenRateConfig.getString("shareMoments");
		} else {
			configRate = selfOpenRateConfig.getString("shareFriend");
		}

		String[] numArr = configRate.split("-");
		Integer min = Integer.valueOf(numArr[0]);
		Integer max = Integer.valueOf(numArr[1]);
		double rate = Math.random() * (max - min);
		return new BigDecimal(min).add(new BigDecimal(rate))
				.divide(new BigDecimal(100), 3, RoundingMode.HALF_UP);
	}
}