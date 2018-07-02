package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.kdniao.KdniaoReqDataData;
import com.ald.fanbei.api.common.kdniao.KdniaoTrackQueryAPI;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderLogisticsDo;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.biz.service.AfBorrowLegalOrderLogisticsService;
import com.ald.fanbei.api.common.kdniao.KdniaoReqDataDataTraces;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderLogisticsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderLogisticsDo;
import com.alibaba.fastjson.JSONObject;

/**
 * 借钱合规改造ServiceImpl
 * 
 * @author Jiang Rongbo
 * @version 1.0.0 初始化
 * @date 2017-12-20 18:49:14 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afBorrowLegalOrderLogisticsService")
public class AfBorrowLegalOrderLogisticsServiceImpl extends ParentServiceImpl<AfBorrowLegalOrderLogisticsDo, Long>
		implements AfBorrowLegalOrderLogisticsService {

	private static final Logger logger = LoggerFactory.getLogger(AfBorrowLegalOrderLogisticsServiceImpl.class);

	@Resource
	private AfBorrowLegalOrderLogisticsDao afBorrowLegalOrderLogisticsDao;
	
	@Resource
	private AfBorrowLegalOrderDao afBorrowLegalOrderDao;

	@Override
	public BaseDao<AfBorrowLegalOrderLogisticsDo, Long> getDao() {
		return afBorrowLegalOrderLogisticsDao;
	}

	@Override
	public AfOrderLogisticsBo getLegalOrderLogisticsBo(long orderId, long isOutTraces) {
		AfBorrowLegalOrderLogisticsDo afOrderLogisticsDo = getByOrderId(orderId);
		AfOrderLogisticsBo afOrderLogisticsBo = new AfOrderLogisticsBo();
		if (afOrderLogisticsDo != null) {
			afOrderLogisticsBo.setStateDesc(convertState(afOrderLogisticsDo.getState()));
			afOrderLogisticsBo.setShipperName(afOrderLogisticsDo.getShipperName());
			afOrderLogisticsBo.setShipperCode(afOrderLogisticsDo.getLogisticCode());
			List<KdniaoReqDataDataTraces> traces = JSONObject.parseArray(afOrderLogisticsDo.getTraces(),
					KdniaoReqDataDataTraces.class);
			if (traces.size() > 0) {
				KdniaoReqDataDataTraces last = new KdniaoReqDataDataTraces();
				KdniaoReqDataDataTraces listLast = traces.get(traces.size() - 1);
				last.setAcceptStation(listLast.getAcceptStation());
				last.setAcceptTime(listLast.getAcceptTime());
				afOrderLogisticsBo.setNewestInfo(last);
			} else {
				// 如果没有就虚拟一条空的轨迹
				KdniaoReqDataDataTraces empty = new KdniaoReqDataDataTraces();
				empty.setAcceptTime(new Date());
				empty.setAcceptStation("暂无物流轨迹");
				afOrderLogisticsBo.setNewestInfo(empty);
			}
			if (isOutTraces > 0) {

				List<KdniaoReqDataDataTraces> sortTraces = new ArrayList<KdniaoReqDataDataTraces>();
				for (int i = traces.size() - 1; i >= 0; i--) {
					sortTraces.add(traces.get(i));
				}
				afOrderLogisticsBo.setTracesInfo(sortTraces);
			}
			return afOrderLogisticsBo;
		} else {
			List<KdniaoReqDataDataTraces> emptyTraces = new ArrayList<KdniaoReqDataDataTraces>();
			KdniaoReqDataDataTraces empty = new KdniaoReqDataDataTraces();
			empty.setAcceptTime(new Date());
			empty.setAcceptStation("暂无物流轨迹");
			AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderDao.getById(orderId);
			String logisticsNo = "";
			if (afBorrowLegalOrderDo != null) {
				logisticsNo = afBorrowLegalOrderDo.getLogisticsNo();
			}
			afOrderLogisticsBo.setShipperCode(logisticsNo);
			afOrderLogisticsBo.setNewestInfo(empty);
			afOrderLogisticsBo.setTracesInfo(emptyTraces);
			return afOrderLogisticsBo;
		}
	}
	
	/**
     * 枚举转换
     *
     * @param state 状态值
     * @return 描述
     */
    String convertState(int state) {
        String stateDesc = "";
        switch (state) {
            case 0:
                stateDesc = "无轨迹";
                break;
            case 1:
                stateDesc = "已揽收";
                break;
            case 2:
                stateDesc = "在途中";
                break;
            case 201:
                stateDesc = "到达派件城市";
                break;
            case 3:
                stateDesc = "签收";
                break;
            case 4:
                stateDesc = "问题件";
                break;
            default:
                stateDesc = "未识别的物流状态";
                break;
        }
        return stateDesc;
    }

	private AfBorrowLegalOrderLogisticsDo getByOrderId(long orderId) {
		AfBorrowLegalOrderLogisticsDo afBorrowLegalOrderLogisticsDo = afBorrowLegalOrderLogisticsDao.getByOrderId(orderId);
		if (afBorrowLegalOrderLogisticsDo == null) {
			return afBorrowLegalOrderLogisticsDo;
		}
		if (afBorrowLegalOrderLogisticsDo.getState() != 3) {
			KdniaoTrackQueryAPI kdniaoTrackQueryAPI = new KdniaoTrackQueryAPI();
			KdniaoReqDataData kdniaoReqDataData = kdniaoTrackQueryAPI.getOrderTraces(afBorrowLegalOrderLogisticsDo.getShipperCode(), afBorrowLegalOrderLogisticsDo.getLogisticCode());
			try {
				if (!kdniaoReqDataData.isSuccess()) {
					return afBorrowLegalOrderLogisticsDo;
				}
				int state = kdniaoReqDataData.getState();
				AfBorrowLegalOrderDo afBorrowLegalOrderDo = afBorrowLegalOrderDao.getById(afBorrowLegalOrderLogisticsDo.getOrderId());
				afBorrowLegalOrderLogisticsDo.setState(state);
				afBorrowLegalOrderLogisticsDo.setTraces(JSON.toJSONString(kdniaoReqDataData.getTraces()));
				afBorrowLegalOrderLogisticsDo.setGmtModified(new Date());
				afBorrowLegalOrderLogisticsDao.updateById(afBorrowLegalOrderLogisticsDo);
				if (state == 3) {//已签收
					//更新订单状态
					afBorrowLegalOrderDo.setLogisticsInfo("已签收");
					afBorrowLegalOrderDao.updateById(afBorrowLegalOrderDo);
				}
			} catch (Exception e) {
				logger.error("kddata process exception：" + e);
			}
		}
		return afBorrowLegalOrderLogisticsDo;

	}
}