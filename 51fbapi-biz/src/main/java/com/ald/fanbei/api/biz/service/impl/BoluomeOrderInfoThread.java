package com.ald.fanbei.api.biz.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;

import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.biz.service.BoluomeOrderInfoService;
import com.ald.fanbei.api.biz.service.boluome.BoluomeCore;
import com.ald.fanbei.api.common.enums.OrderSecType;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.dal.dao.AfBoluomeDianyingDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeHuocheDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeHuochePassengerDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJiayoukaDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJipiaoDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJipiaoFlightDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJipiaoPassengerDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeJiudianDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeShoujiDao;
import com.ald.fanbei.api.dal.dao.AfBoluomeWaimaiDao;
import com.ald.fanbei.api.dal.domain.AfBoluomeDianyingDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeHuocheDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeHuochePassengerDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJiayoukaDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJipiaoDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJipiaoFlightDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJipiaoPassengerDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeJiudianDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeShoujiDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeWaimaiDo;
import com.ald.fanbei.api.dal.domain.dto.BoluomeOrderResponseDto;
import com.alibaba.fastjson.JSON;

public class BoluomeOrderInfoThread extends BaseService implements BoluomeOrderInfoService, Runnable {

    private AfBoluomeDianyingDao afBoluomeDianyingDao;
    private AfBoluomeJiayoukaDao afBoluomeJiayoukaDao;
    private AfBoluomeJiudianDao afBoluomeJiudianDao;
    private AfBoluomeWaimaiDao afBoluomeWaimaiDao;
    private AfBoluomeShoujiDao afBoluomeShoujiDao;
    private AfBoluomeHuocheDao afBoluomeHuocheDao;
    private AfBoluomeJipiaoDao afBoluomeJipiaoDao;
    private AfBoluomeHuochePassengerDao afBoluomeHuochePassengerDao;
    private AfBoluomeJipiaoFlightDao afBoluomeJipiaoFlightDao;
    private AfBoluomeJipiaoPassengerDao afBoluomeJipiaoPassengerDao;

    private String thirdOrderNo;
    private String secOrderType;

    public BoluomeOrderInfoThread(String thirdOrderNo, String secOrderType, AfBoluomeDianyingDao afBoluomeDianyingDao, AfBoluomeJiayoukaDao afBoluomeJiayoukaDao, AfBoluomeJiudianDao afBoluomeJiudianDao, AfBoluomeWaimaiDao afBoluomeWaimaiDao, AfBoluomeShoujiDao afBoluomeShoujiDao, AfBoluomeHuocheDao afBoluomeHuocheDao, AfBoluomeHuochePassengerDao afBoluomeHuochePassengerDao, AfBoluomeJipiaoDao afBoluomeJipiaoDao, AfBoluomeJipiaoFlightDao afBoluomeJipiaoFlightDao, AfBoluomeJipiaoPassengerDao afBoluomeJipiaoPassengerDao) {
	this.thirdOrderNo = thirdOrderNo;
	this.secOrderType = secOrderType;

	this.afBoluomeDianyingDao = afBoluomeDianyingDao;
	this.afBoluomeJiayoukaDao = afBoluomeJiayoukaDao;
	this.afBoluomeJiudianDao = afBoluomeJiudianDao;
	this.afBoluomeWaimaiDao = afBoluomeWaimaiDao;
	this.afBoluomeShoujiDao = afBoluomeShoujiDao;

	this.afBoluomeHuocheDao = afBoluomeHuocheDao;
	this.afBoluomeJipiaoDao = afBoluomeJipiaoDao;
	this.afBoluomeHuochePassengerDao = afBoluomeHuochePassengerDao;
	this.afBoluomeJipiaoFlightDao = afBoluomeJipiaoFlightDao;
	this.afBoluomeJipiaoPassengerDao = afBoluomeJipiaoPassengerDao;
    }

    @Override
    public void addBoluomeOrderInfo(String thirdOrderNo, String secOrderType) {
	try {
	    if (OrderSecType.HUO_CHE.getCode().equals(secOrderType) || OrderSecType.JI_PIAO.getCode().equals(secOrderType) || OrderSecType.DIAN_YING.getCode().equals(secOrderType) || OrderSecType.JIU_DIAN.getCode().equals(secOrderType) || OrderSecType.WAI_MAI.getCode().equals(secOrderType) || OrderSecType.HUA_FEI.getCode().equals(secOrderType) || OrderSecType.LIU_LIANG.getCode().equals(secOrderType) || OrderSecType.JIA_YOU_KA.getCode().equals(secOrderType)) {

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
		    } else if (OrderSecType.HUO_CHE.getCode().equals(secOrderType)) {
			AfBoluomeHuocheDo afBoluomeHuocheDo = JSON.parseObject(orderResponse.getData(), AfBoluomeHuocheDo.class);
			afBoluomeHuocheDao.saveRecord(afBoluomeHuocheDo);

			for (AfBoluomeHuochePassengerDo boluomeHuochePassengerDo : afBoluomeHuocheDo.getPassengers()) {
			    boluomeHuochePassengerDo.setThirdOrderNo(thirdOrderNo);
			    afBoluomeHuochePassengerDao.saveRecord(boluomeHuochePassengerDo);
			}

		    } else if (OrderSecType.JI_PIAO.getCode().equals(secOrderType)) {
			AfBoluomeJipiaoDo afBoluomeJipiaoDo = JSON.parseObject(orderResponse.getData(), AfBoluomeJipiaoDo.class);
			afBoluomeJipiaoDao.saveRecord(afBoluomeJipiaoDo);
			
			for(AfBoluomeJipiaoFlightDo afBoluomeJipiaoFlightDo : afBoluomeJipiaoDo.getFlights()){
			    afBoluomeJipiaoFlightDo.setThirdOrderNo(thirdOrderNo);			    
			    afBoluomeJipiaoFlightDao.saveRecord(afBoluomeJipiaoFlightDo);
			}
			for(AfBoluomeJipiaoPassengerDo afBoluomeJipiaoPassengerDo:afBoluomeJipiaoDo.getPassengers()){
			    afBoluomeJipiaoPassengerDo.setThirdOrderNo(thirdOrderNo);			 
			    afBoluomeJipiaoPassengerDao.saveRecord(afBoluomeJipiaoPassengerDo);
			}
		    }
		} else {
		    logger.error("addBoluomeOrderInfo response error:" + orderResponse);
		}
	    }
	} catch (Exception e) {
	    logger.error("addBoluomeOrderInfo error:", e);
	}
    }

    @Async
    @Override
    public void run() {
	addBoluomeOrderInfo(this.thirdOrderNo, this.secOrderType);
    }
}
