package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.OpenRedPacketHomeBo;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.WxUtil;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AccountLogType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CollectionConverterUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.Converter;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfRedPacketTotalDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfRedPacketSelfOpenDto;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.ald.fanbei.api.dal.domain.query.AfRedPacketTotalQueryNoPage;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


/**
 * 拆红包活动，用户总红包ServiceImpl
 * 
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-03 14:57:39
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afRedPacketTotalService")
public class AfRedPacketTotalServiceImpl extends ParentServiceImpl<AfRedPacketTotalDo, Long>
		implements AfRedPacketTotalService {
	
    @Autowired
    private AfRedPacketTotalDao afRedPacketTotalDao;

    @Autowired
    private AfRedPacketSelfOpenService afRedPacketSelfOpenService;

    @Autowired
    private AfRedPacketHelpOpenService afRedPacketHelpOpenService;

    @Autowired
    private AfResourceService afResourceService;

    @Autowired
    private AfUserService afUserService;

    @Autowired
    private AfUserAccountService afUserAccountService;

    @Autowired
    private AfUserThirdInfoService afUserThirdInfoService;

	@Override
	public OpenRedPacketHomeBo getHomeInfoInSite(FanbeiWebContext context) {
		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());

		checkActivityIsStop(config);

		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		OpenRedPacketHomeBo result = new OpenRedPacketHomeBo();

		result.setWithdrawTotalNum(getWithdrawTotalNum(redPacketConfig));
		if (context.isLogin()) {
			Long userId = afUserService.getUserByUserName(context.getUserName()).getRid();
			result.setRedPacket(getRedPacketInfoOfHome(userId, redPacketConfig));
			result.setWithdrawList(findWithdrawListOfHome(userId, 2));
			if (result.getRedPacket() != null) {
				Long id = Long.valueOf(result.getRedPacket().get("id"));
				// TODO:queryNum传null，测试使用，记得改回2
				result.setOpenList(findOpenListOfHome(id, null));
			}
		}

		return result;
	}

	@Override
	public OpenRedPacketHomeBo getHomeInfoOutSite(String wxCode, Long shareId) {
		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
		checkActivityIsStop(config);

		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		OpenRedPacketHomeBo result = new OpenRedPacketHomeBo();
		result.setWithdrawTotalNum(getWithdrawTotalNum(redPacketConfig));

		JSONObject userWxInfo = WxUtil.getUserInfoWithCache(wxCode);
		Long userId = afUserThirdInfoService.getUserIdByWxOpenId(userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID));
		if (userId != null) {
			result.setIsBindMobile(YesNoStatus.YES.getCode());
			result.setRedPacket(getRedPacketInfoOfHome(userId, redPacketConfig));
			result.setWithdrawList(findWithdrawListOfHome(userId, 2));
			if (result.getRedPacket() != null) {
				Long id = Long.valueOf(result.getRedPacket().get("id"));
				// TODO:queryNum传null，测试使用，记得改回2
				result.setOpenList(findOpenListOfHome(id, null));
			}
		} else {
			result.setIsBindMobile(YesNoStatus.NO.getCode());
		}

		AfRedPacketTotalDo shareRedPacket = getById(shareId);
		String openId = userWxInfo.getString(UserWxInfoDto.KEY_OPEN_ID);
		if (afRedPacketHelpOpenService.isCanOpen(shareRedPacket, openId)) {
			result.setIsCanHelpOpen(YesNoStatus.YES.getCode());
		} else {
			result.setIsCanHelpOpen(YesNoStatus.NO.getCode());
		}
		fillHelpOpenInfo(shareRedPacket, openId, redPacketConfig, result);

		return result;
	}

	@Override
	public AfRedPacketTotalDo getTheOpening(Long userId, Integer overdueIntervalHour) {
		return afRedPacketTotalDao.getTheOpening(userId, overdueIntervalHour);
	}

	@Override
	@Transactional
	public AfRedPacketTotalDo getTheOpeningMust(Long userId, String modifier, Integer overdueIntervalHour) {
		AfRedPacketTotalDo theOpening = getTheOpening(userId, overdueIntervalHour);
		if (theOpening == null) {
			theOpening = new AfRedPacketTotalDo();
			theOpening.setUserId(userId);
			theOpening.setCreator(modifier);
			theOpening.setModifier(modifier);
			theOpening.setAmount(BigDecimal.ZERO);
			saveRecord(theOpening);
		}
		return theOpening;
	}

	@Override
	public List<AfRedPacketTotalDo> findWithdrawList(Long userId, Integer queryNum) {
		return afRedPacketTotalDao.findWithdrawList(userId, queryNum);
	}

	@Override
	public List<Map<String, String>> findWithdrawListOfHome(Long userId, Integer queryNum) {
		List<Map<String, String>> result = new ArrayList<>();
		List<AfRedPacketTotalDo> withdrawList = findWithdrawList(userId, queryNum);
		if (CollectionUtil.isNotEmpty(withdrawList)) {
			result = CollectionConverterUtil.convertToListFromList(withdrawList,
					new Converter<AfRedPacketTotalDo, Map<String, String>>() {
						@Override
						public Map<String, String> convert(AfRedPacketTotalDo source) {
							Map<String, String> e = new HashMap<>();
							e.put("gmtWithdraw", DateUtil.formatDateTime(source.getGmtWithdraw()));
							e.put("desc", "成功提现" + source.getAmount().setScale(2, RoundingMode.HALF_UP).toString() + "元");
							return e;
						}
					});
			return result;
		}
		return result;
	}

	@Override
	public List<Map<String, String>> findOpenListOfHome(Long id, Integer queryNum) {
		List<Map<String, String>> result = new ArrayList<>();

		List<AfRedPacketHelpOpenDo> helpOpenList = afRedPacketHelpOpenService
				.findOpenRecordList(id, queryNum);
		List<AfRedPacketSelfOpenDto> selfOpenList = null;
		if (queryNum == null || queryNum == 0) {
			selfOpenList = afRedPacketSelfOpenService.findOpenRecordList(id);
		}

		if (CollectionUtil.isNotEmpty(helpOpenList)) {
			result.addAll(CollectionConverterUtil.convertToListFromList(helpOpenList,
					new Converter<AfRedPacketHelpOpenDo, Map<String, String>>() {
						@Override
						public Map<String, String> convert(AfRedPacketHelpOpenDo source) {
							Map<String, String> e = new HashMap<>();
							e.put("avatar", source.getFriendAvatar());
							e.put("nick", source.getFriendNick());
							e.put("desc", "帮你拆了" +
									source.getAmount().setScale(2, RoundingMode.HALF_UP).toString() + "元");
							return e;
						}
					}));
		}
		if (CollectionUtil.isNotEmpty(selfOpenList)) {
			Map<String, String> e = new HashMap<>();
			e.put("avatar", selfOpenList.get(0).getUserAvatar());
			e.put("nick", selfOpenList.get(0).getUserNick());
			StringBuilder sb = new StringBuilder("你拆了");
			for (int i = 0; i < selfOpenList.size(); i++) {
				if (i < selfOpenList.size() - 1) {
					sb.append(selfOpenList.get(i).getAmount().setScale(2, RoundingMode.HALF_UP).toString())
					  .append("+");
				} else {
					sb.append(selfOpenList.get(i).getAmount().setScale(2, RoundingMode.HALF_UP).toString())
					  .append("元");
				}
			}
			e.put("desc", sb.toString());
			result.add(e);
		}

		return result;
	}

	@Override
	public int getTodayWithdrawedNum(Long userId) {
		AfRedPacketTotalQueryNoPage query = new AfRedPacketTotalQueryNoPage();
		query.setUserId(userId);
		Date now = new Date();
		query.setGmtWithdrawStart(DateUtil.getStartOfDate(now));
		query.setGmtWithdrawEnd(DateUtil.getEndOfDate(now));
		return afRedPacketTotalDao.countByQuery(query);
	}

	@Override
	public boolean isCanGainOne(Long id, Integer shareTime) {
		if (shareTime != null) {
			int openedNum = afRedPacketSelfOpenService.getOpenedNum(id);
			return openedNum < (shareTime + 1);
		}

		return true;
	}

	@Override
	public boolean isInvalid(AfRedPacketTotalDo redPacketTotalDo) {
		if (redPacketTotalDo.getIsWithdraw() == 1) return true;

		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		return isOverdue(redPacketTotalDo, redPacketConfig);
	}

	@Override
	@Transactional
	public void withdraw(Long id, String modifier) {
		AfRedPacketTotalDo redPacketTotalDo = getById(id);

		checkIsCanWithdraw(redPacketTotalDo);

		if (redPacketTotalDo.getIsWithdraw() == 1) return;

		redPacketTotalDo.setIsWithdraw(1);
		redPacketTotalDo.setModifier(modifier);
		redPacketTotalDo.setGmtWithdraw(new Date());
		updateById(redPacketTotalDo);

		withdrawToUserAccount(redPacketTotalDo);
	}

	@Override
	@Transactional
	public void updateAmount(AfRedPacketTotalDo theOpening, BigDecimal openAmount, String modifier) {
		theOpening.setAmount(theOpening.getAmount().add(openAmount));
		theOpening.setModifier(modifier);
		updateById(theOpening);
	}

	@Override
	public BigDecimal calcWithdrawRestAmount(Long id, BigDecimal thresholdAmount) {
		AfRedPacketTotalDo redPacketTotalDo = getById(id);
		return calcWithdrawRestAmount(redPacketTotalDo, thresholdAmount);
	}

	@Override
	public BigDecimal calcWithdrawRestAmount(AfRedPacketTotalDo redPacketTotalDo, BigDecimal thresholdAmount) {
		BigDecimal restAmount = thresholdAmount.subtract(redPacketTotalDo.getAmount());
		return restAmount.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : restAmount;
	}

	@Override
	public BaseDao<AfRedPacketTotalDo, Long> getDao() {
		return afRedPacketTotalDao;
	}

	// 检查活动是否停止
	private void checkActivityIsStop(AfResourceDo config) {
		if (config.getValue().trim().equals(YesNoStatus.NO.getCode())) {
			throw new FanbeiException("活动已结束");
		}
	}

	// 获取提现总人数
	private int getWithdrawTotalNum(JSONObject redPacketConfig) {
		int realTimeNum = afRedPacketTotalDao.getWithdrawTotalNum();
		Integer configNum = redPacketConfig.getInteger("withdrawTotalNum");
		configNum = configNum == null ? 0 : configNum;
		return realTimeNum + configNum;
	}

	// 获取拆红包主页的红包信息
	private Map<String, String> getRedPacketInfoOfHome(Long userId, JSONObject redPacketConfig) {
		Integer overdueIntervalHour = redPacketConfig.getInteger("overdueIntervalHour");
		AfRedPacketTotalDo theOpening = getTheOpening(userId, overdueIntervalHour);
		if (theOpening == null) return null;

		Map<String, String> result = new HashMap<>();
		result.put("id", theOpening.getRid().toString());
		result.put("amount", theOpening.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
		result.put("withdrawLimitAmount", redPacketConfig.getString("thresholdAmount"));
		result.put("gmtOverdue",
				DateUtil.formatDateTime(DateUtil.addHoures(theOpening.getGmtCreate(), overdueIntervalHour)));
		result.put("isCanGainOne", isCanGainOne(theOpening.getRid(), redPacketConfig.getInteger("shareTime"))
				? YesNoStatus.YES.getCode()
				: YesNoStatus.NO.getCode());
		return result;
	}

	// 填充帮拆红包信息
	private void fillHelpOpenInfo(AfRedPacketTotalDo shareRedPacket, String openId,
								  JSONObject redPacketConfig, OpenRedPacketHomeBo result) {
		Map<String, String> helpOpenInfo = new HashMap<>();
		helpOpenInfo.put("isOverdue", YesNoStatus.NO.getCode());
		if (isOverdue(shareRedPacket, redPacketConfig)) {
			helpOpenInfo.put("isOverdue", YesNoStatus.YES.getCode());
			result.setIsCanHelpOpen(YesNoStatus.NO.getCode());
		}

		UserWxInfoDto shareUserWxInfo = afUserThirdInfoService.getWxOrLocalUserInfo(shareRedPacket.getUserId());
		helpOpenInfo.put("avatar", shareUserWxInfo.getAvatar());
		helpOpenInfo.put("nick", shareUserWxInfo.getNick());

		if (result.getIsCanHelpOpen().equals(YesNoStatus.NO.getCode())) {
			AfRedPacketHelpOpenDo helpOpenDo = afRedPacketHelpOpenService.getHelpOpenRecord(openId, shareRedPacket.getUserId());
			if (helpOpenDo != null && helpOpenDo.getRedPacketTotalId().equals(shareRedPacket.getRid())) {
				helpOpenInfo.put("amount", helpOpenDo.getAmount().setScale(2, RoundingMode.HALF_UP).toString());
			}
		}
		result.setHelpOpenInfo(helpOpenInfo);
	}

	// 判断红包是否过期
	private boolean isOverdue(AfRedPacketTotalDo redPacketTotalDo, JSONObject redPacketConfig) {
		Integer overdueIntervalHour = redPacketConfig.getInteger("overdueIntervalHour");
		Date gmtOverdue = DateUtil.addHoures(redPacketTotalDo.getGmtCreate(), overdueIntervalHour);
		return gmtOverdue.before(new Date());
	}

	// 检查是否能提现
	private void checkIsCanWithdraw(AfRedPacketTotalDo redPacketTotalDo) {
		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());
		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());

		if (isOverdue(redPacketTotalDo, redPacketConfig)) {
			throw new FanbeiException("红包已过期");
		}

		BigDecimal thresholdAmount =  redPacketConfig.getBigDecimal("thresholdAmount");
		if (redPacketTotalDo.getAmount().compareTo(thresholdAmount) < 0) {
			BigDecimal restAmount = calcWithdrawRestAmount(redPacketTotalDo, thresholdAmount);
			throw new FanbeiException("您还剩" + restAmount.setScale(2, RoundingMode.HALF_UP) + "元才能提现");
		}
	}

	// 提现到用户账户
	private void withdrawToUserAccount(AfRedPacketTotalDo redPacketTotalDo) {
		// 红包金额提现到返现金额
		AfUserAccountDo userAccountDo = afUserAccountService.getUserAccountByUserId(redPacketTotalDo.getUserId());
		if (userAccountDo == null) {
			throw new FanbeiException("account is invalid", FanbeiExceptionCode.USER_ACCOUNT_NOT_EXIST_ERROR);
		}
		// 2.1更新用户账户额度
		AfUserAccountDo newUserAccountDo = new AfUserAccountDo();
		newUserAccountDo.setRebateAmount(redPacketTotalDo.getAmount());
		newUserAccountDo.setUserId(userAccountDo.getUserId());
		afUserAccountService.updateUserAccount(newUserAccountDo);
		// 记录数据到账户记录表
		AfUserAccountLogDo afUserAccountLogDo = new AfUserAccountLogDo();
		afUserAccountLogDo.setGmtCreate(new Date());
		afUserAccountLogDo.setUserId(redPacketTotalDo.getUserId());
		afUserAccountLogDo.setAmount(redPacketTotalDo.getAmount());
		afUserAccountLogDo.setType(AccountLogType.OPEN_REDPACKET.getCode());
		afUserAccountLogDo.setRefId("");
		afUserAccountService.addUserAccountLog(afUserAccountLogDo);
	}
}