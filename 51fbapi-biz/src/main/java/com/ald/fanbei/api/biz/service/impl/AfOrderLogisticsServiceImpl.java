package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.biz.service.AfOrderLogisticsService;
import com.ald.fanbei.api.common.kdniao.KdniaoReqDataDataTraces;
import com.ald.fanbei.api.dal.dao.AfBorrowLegalOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfOrderLogisticsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfBorrowLegalOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfOrderLogisticsDo;
import com.alibaba.fastjson.JSONObject;


/**
 * '第三方-上树请求记录ServiceImpl
 *
 * @author renchunlei
 * @version 1.0.0 初始化
 * @date 2017-08-21 09:28:02
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afOrderLogisticsService")
public class AfOrderLogisticsServiceImpl extends ParentServiceImpl<AfOrderLogisticsDo, Long> implements AfOrderLogisticsService {

    private static final Logger logger = LoggerFactory.getLogger(AfOrderLogisticsServiceImpl.class);

    @Resource
    private AfOrderLogisticsDao afOrderLogisticsDao;
    @Resource
    private AfBorrowLegalOrderDao afBorrowLegalOrderDao;
    
    @Resource
    private AfOrderDao afOrderDao;

    @Override
    public BaseDao<AfOrderLogisticsDo, Long> getDao() {
        return afOrderLogisticsDao;
    }

    @Override
    public AfOrderLogisticsDo getByOrderId(Long orderId) {
        return afOrderLogisticsDao.getByOrderId(orderId);
    }

    @Override
    public AfOrderLogisticsBo getOrderLogisticsBo(long orderId, long isOutTraces) {
        AfOrderLogisticsDo afOrderLogisticsDo = getByOrderId(orderId);
        AfOrderLogisticsBo afOrderLogisticsBo = new AfOrderLogisticsBo();
        if (afOrderLogisticsDo != null) {
            afOrderLogisticsBo.setStateDesc(convertState(afOrderLogisticsDo.getState()));
            afOrderLogisticsBo.setShipperName(afOrderLogisticsDo.getShipperName());
            afOrderLogisticsBo.setShipperCode(afOrderLogisticsDo.getLogisticCode());
            List<KdniaoReqDataDataTraces> traces = JSONObject.parseArray(afOrderLogisticsDo.getTraces(), KdniaoReqDataDataTraces.class);
            if (traces.size() > 0) {
                KdniaoReqDataDataTraces last = new KdniaoReqDataDataTraces();
                KdniaoReqDataDataTraces listLast = traces.get(traces.size() - 1);
                last.setAcceptStation(listLast.getAcceptStation());
                last.setAcceptTime(listLast.getAcceptTime());
                afOrderLogisticsBo.setNewestInfo(last);
            } else {
                //如果没有就虚拟一条空的轨迹
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
            AfOrderDo afOrder = afOrderDao.getOrderById(orderId);
            afOrderLogisticsBo.setShipperCode(afOrder.getLogisticsNo());
            afOrderLogisticsBo.setNewestInfo(empty);
            afOrderLogisticsBo.setTracesInfo(emptyTraces);
            return afOrderLogisticsBo;
        }
    }
    
    
    
    @Override
    public AfOrderLogisticsBo getLegalOrderLogisticsBo(long orderId, long isOutTraces) {
        AfOrderLogisticsDo afOrderLogisticsDo = getByOrderId(orderId);
        AfOrderLogisticsBo afOrderLogisticsBo = new AfOrderLogisticsBo();
        if (afOrderLogisticsDo != null) {
            afOrderLogisticsBo.setStateDesc(convertState(afOrderLogisticsDo.getState()));
            afOrderLogisticsBo.setShipperName(afOrderLogisticsDo.getShipperName());
            afOrderLogisticsBo.setShipperCode(afOrderLogisticsDo.getLogisticCode());
            List<KdniaoReqDataDataTraces> traces = JSONObject.parseArray(afOrderLogisticsDo.getTraces(), KdniaoReqDataDataTraces.class);
            if (traces.size() > 0) {
                KdniaoReqDataDataTraces last = new KdniaoReqDataDataTraces();
                KdniaoReqDataDataTraces listLast = traces.get(traces.size() - 1);
                last.setAcceptStation(listLast.getAcceptStation());
                last.setAcceptTime(listLast.getAcceptTime());
                afOrderLogisticsBo.setNewestInfo(last);
            } else {
                //如果没有就虚拟一条空的轨迹
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
            if(afBorrowLegalOrderDo != null) {
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
}