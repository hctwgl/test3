package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.AfRedPacketHelpOpenService;
import com.ald.fanbei.api.biz.service.AfRedPacketTotalService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.dal.dao.AfRedPacketHelpOpenDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfRedPacketHelpOpenDo;
import com.ald.fanbei.api.dal.domain.AfRedPacketTotalDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
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
	public AfRedPacketHelpOpenDo getByOpenIdAndUserId(String openId, Long userId) {
		AfRedPacketHelpOpenDo query = new AfRedPacketHelpOpenDo();
		query.setOpenId(openId);
		query.setUserId(userId);
		return getByCommonCondition(query);
	}

	@Override
	@Transactional
	public AfRedPacketHelpOpenDo open(AfRedPacketHelpOpenDo helpOpenDo) {
		AfResourceDo config = afResourceService.getSingleResourceBytype(ResourceType.OPEN_REDPACKET.getCode());

		AfRedPacketTotalDo redPacketTotalDo = afRedPacketTotalService.getById(helpOpenDo.getRedPacketTotalId());
		helpOpenDo.setUserId(redPacketTotalDo.getUserId());
		fillAmountAndDiscountInfo(config, helpOpenDo);
		saveRecord(helpOpenDo);

		redPacketTotalDo.setAmount(redPacketTotalDo.getAmount().add(helpOpenDo.getAmount()));
		afRedPacketTotalService.updateById(redPacketTotalDo);

		return helpOpenDo;
	}

	@Override
	public BaseDao<AfRedPacketHelpOpenDo, Long> getDao() {
		return afRedPacketHelpOpenDao;
	}

	// 填充拆得的金额和比率
	private void fillAmountAndDiscountInfo(AfResourceDo config, AfRedPacketHelpOpenDo helpOpenDo) {
		JSONArray helpOpenRateConfig = JSONObject.parseArray(config.getValue3());
		JSONObject redPacketConfig = JSONObject.parseObject(config.getValue1());
		int num = afRedPacketHelpOpenDao.getOpenedNum(helpOpenDo.getRedPacketTotalId());

		String configRate = null;
		Iterator it = helpOpenRateConfig.iterator();
		while (it.hasNext()) {
			JSONObject e = (JSONObject) it.next();
			Integer minPepole = e.getInteger("minPeople");
			Integer maxPepole = e.getInteger("maxPepole");
			if (maxPepole != null) {
				if (num >= minPepole && num <= maxPepole) {
					configRate = e.getString("rate");
					break;
				}
			} else {
				if (num >= minPepole) {
					helpOpenDo.setAmount(e.getBigDecimal("amount"));
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