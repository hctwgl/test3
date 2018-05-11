package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketSelfOpenService;
import com.ald.fanbei.api.biz.service.AfRedPacketTotalService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserThirdInfoService;
import com.ald.fanbei.api.biz.util.WxUtil;
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
import org.springframework.transaction.annotation.Transactional;

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
	public int getOpenedNum(Long redPacketTotalId) {
		return afRedPacketSelfOpenDao.getOpenedNum(redPacketTotalId);
	}

	@Override
	@Transactional
	public AfRedPacketSelfOpenDo open(Long userId, String modifier, String sourceType) {
		// TODO:这里事务没有起作用，记得检查
		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		JSONObject selfOpenRateConfig = JSONObject.parseObject(config.getValue2());
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

		afRedPacketTotalService.updateAmount(theOpening, selfOpenDo.getAmount(), modifier);

		return selfOpenDo;
	}

	@Override
	public AfRedPacketSelfOpenDo bindPhoneAndOpen(Long userId, String modifier, String wxCode, String sourceType) {
		JSONObject userWxInfo = WxUtil.getUserInfoWithCache(wxCode);
		afUserThirdInfoService.bindUserWxInfo(userWxInfo, userId, modifier);

		return open(userId, modifier, sourceType);
	}

	@Override
	public BaseDao<AfRedPacketSelfOpenDo, Long> getDao() {
		return afRedPacketSelfOpenDao;
	}

	private boolean hadSelfOpenedRedPacket(Long redPacketTotalId) {
		AfRedPacketSelfOpenDo query = new AfRedPacketSelfOpenDo();
		query.setRedPacketTotalId(redPacketTotalId);
		query.setSourceType(SelfOpenRedPacketSourceType.OPEN_SELF.getCode());
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

		if (StringUtil.isNotBlank(sourceType) && sourceType.equals(SelfOpenRedPacketSourceType.OPEN_SELF.getCode())) {
			boolean isOpened= hadSelfOpenedRedPacket(theOpening.getRid());
			if (isOpened) {
				throw new FanbeiException("您已拆过自己的红包了，继续分享可以再拆红包");
			}
		}

		Integer everydayWithdrawNum = redPacketConfig.getInteger("everydayWithdrawNum");
		if (everydayWithdrawNum != null) {
			int todayWithdrawedNum = afRedPacketTotalService.getTodayWithdrawedNum(theOpening.getUserId());
			if (todayWithdrawedNum >= everydayWithdrawNum) {
				throw new FanbeiException("今日已不能再拆红包，请明天再来哦");
			}
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