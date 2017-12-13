package com.ald.fanbei.api.biz.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.BoluomeOrderInfoService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.common.enums.OrderSecType;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.dao.AfBoluomeDianyingDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJiayoukaDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJiudianDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeShoujiDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeWaimaiDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeDianyingDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJiayoukaDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJiudianDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeShoujiDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeWaimaiDo;
import com.ald.fanbei.api.dal.domain.dto.BoluomeOrderResponseDto;
import com.alibaba.fastjson.JSON;

@Service
public class BoluomeOrderInfoServiceImpl extends BaseService implements BoluomeOrderInfoService {

    @Autowired
    AfBoluomeDianyingDao afBoluomeDianyingDao;
    @Autowired
    AfBoluomeJiayoukaDao afBoluomeJiayoukaDao;
    @Autowired
    AfBoluomeJiudianDao afBoluomeJiudianDao;
    @Autowired
    AfBoluomeWaimaiDao afBoluomeWaimaiDao;
    @Autowired
    AfBoluomeShoujiDao afBoluomeShoujiDao;

    @Override
    public void addBoluomeOrderInfo(Long orderId, String thirdOrderNo, String secOrderType) {
	try {
	    if (OrderSecType.DIAN_YING.getCode().equals(secOrderType) || OrderSecType.JIU_DIAN.getCode().equals(secOrderType) || OrderSecType.WAI_MAI.getCode().equals(secOrderType) || OrderSecType.HUA_FEI.getCode().equals(secOrderType) || OrderSecType.LIU_LIANG.getCode().equals(secOrderType) || OrderSecType.JIA_YOU_KA.getCode().equals(secOrderType)) {
		// 构造查询参数
		Map<String, String> params = new HashMap<String, String>();
		params.put(BoluomeCore.ORDER_ID, thirdOrderNo);
		params.put(BoluomeCore.TIME_STAMP, String.valueOf(System.currentTimeMillis() / 1000));
		String detailsUrl = BoluomeCore.buildOrderDetailsQueryUrl(params);
		// 查询订单详情
		String response = HttpUtil.doGet(detailsUrl, 100);
		// 解析数据
		BoluomeOrderResponseDto orderResponse = JSON.parseObject(response, BoluomeOrderResponseDto.class);
		// 记录数据
		if (orderResponse.getCode() == 1000 && "true".equals(orderResponse.getSuccess())) {
		    if (OrderSecType.DIAN_YING.getCode().equals(secOrderType)) {
			AfBoluomeDianyingDo afBoluomeDianyingDo = JSON.parseObject(orderResponse.getData(), AfBoluomeDianyingDo.class);
			afBoluomeDianyingDao.saveRecord(afBoluomeDianyingDo);
		    } else if (OrderSecType.JIU_DIAN.getCode().equals(secOrderType)) {
			AfBoluomeJiudianDo afBoluomeJiudianDo = JSON.parseObject(orderResponse.getData(), AfBoluomeJiudianDo.class);
			afBoluomeJiudianDao.saveRecord(afBoluomeJiudianDo);
		    } else if (OrderSecType.WAI_MAI.getCode().equals(secOrderType)) {
			AfBoluomeWaimaiDo afBoluomeWaimaiDo = JSON.parseObject(orderResponse.getData(), AfBoluomeWaimaiDo.class);
			afBoluomeWaimaiDao.saveRecord(afBoluomeWaimaiDo);
		    } else if (OrderSecType.HUA_FEI.getCode().equals(secOrderType) || OrderSecType.LIU_LIANG.getCode().equals(secOrderType)) {
			AfBoluomeShoujiDo afBoluomeShoujiDo = JSON.parseObject(orderResponse.getData(), AfBoluomeShoujiDo.class);
			afBoluomeShoujiDao.saveRecord(afBoluomeShoujiDo);
		    } else if (OrderSecType.JIA_YOU_KA.getCode().equals(secOrderType)) {
			AfBoluomeJiayoukaDo afBoluomeJiayoukaDo = JSON.parseObject(orderResponse.getData(), AfBoluomeJiayoukaDo.class);
			afBoluomeJiayoukaDao.saveRecord(afBoluomeJiayoukaDo);
		    }
		} else {
		    logger.error("addBoluomeOrderInfo response error:" + orderResponse);
		}
	    }
	} catch (Exception e) {
	    logger.error("addBoluomeOrderInfo error:", e);
	}
    }
}
