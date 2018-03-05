package com.ald.fanbei.api.web.third.controller;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.RecycleUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.web.util.AppRecycleControllerUtil;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;

/**
 * @author weiqingeng
 * @Description: 有得卖三方 回收业务(与三方对接)
 * @date 2018年2月26日 下午4:14:31
 */
@Controller
@RequestMapping(value = "/fanbei/ydm")
public class RecycleController {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private AfRecycleService afRecycleService;
    @Autowired
    private BizCacheUtil bizCacheUtil;

    /**
     * 创建订单 有得卖 三方 推送过来的订单数据(发券)
     * '订单状态 1：待确认 2：待上门 3：待检测 6：待发货 7：待收货 8：待支付 20:确认发券 66：已完成 98：终止退回 99：已终止'
     * @param request
     * @return
     */
    @RequestMapping(value = "/addOrder", method = RequestMethod.POST)
    @ResponseBody
    public JSONObject addOrder(HttpServletRequest request) {
        JSONObject returnjson = new JSONObject();
        String result = "";
        String key = "";
        try {
            AfRecycleQuery afRecycleQuery = AppRecycleControllerUtil.buildParam(request);
            logger.info("addOrder请求参数=" + afRecycleQuery.toString());
            if(null == afRecycleQuery.getUid()){
                returnjson.put("success", false);
                returnjson.put("msg", "userId不能为空");
            }
            if(null == afRecycleQuery.getSettlePrice()){
                returnjson.put("success", false);
                returnjson.put("msg", "金额不能为空");
            }
            if(null == afRecycleQuery.getStatus()){
                returnjson.put("success", false);
                returnjson.put("msg", "状态不能为空");
            }
            if(StringUtils.isBlank(afRecycleQuery.getPartnerId())){
                returnjson.put("success", false);
                returnjson.put("msg", "合作商不能为空");
            }
            if (RecycleUtil.PARTNER_ID.equals(afRecycleQuery.getPartnerId())) {
                logger.info("/fanbei/ydm/addOrder,params ={}", afRecycleQuery.toString());
                String refOrderId = afRecycleQuery.getRefOrderId();
                Long uid = afRecycleQuery.getUid();
                key = Constants.CACHKEY_GET_COUPON_LOCK + ":" + refOrderId + ":" + uid;
                boolean isNotLock = bizCacheUtil.getLockTryTimes(key, "1", 1000);
                if (isNotLock) {
                    AfRecycleDo afRecycleDo = afRecycleService.getRecycleOrder(afRecycleQuery);
                    if (null == afRecycleDo) {//订单不存在，新增一条订单
                        afRecycleService.addRecycleOrder(afRecycleQuery);//新增一条订单
                    } else {
                        returnjson.put("success", false);
                        returnjson.put("msg", "订单已存在");
                    }
                }
            } else {
                logger.error("/fanbei/ydm/addOrder, partnerId error,partnerId={}", afRecycleQuery.getPartnerId());
                returnjson.put("success", false);
                returnjson.put("msg", "合作商id错误");
            }
        } catch (Exception e) {
            logger.error("/fanbei/ydm/addOrder, error = {}", e.getStackTrace());
            returnjson.put("success", false);
            returnjson.put("msg", "订单生成失败");
        } finally {
            bizCacheUtil.delCache(key);
        }
        returnjson.put("success", false);
        returnjson.put("msg", "操作成功");
        return returnjson;
    }

}
