package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.OrderStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.dao.AfOrderDao;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfOrderDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.dto.OrderInfoDto;
import com.ald.fanbei.api.dal.domain.dto.PayResultInfoDto;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;

@RestController
public class AppH5PayResultController extends BaseController {

    @Autowired
    AfUserDao afUserDao;
    @Autowired
    AfOrderDao afOrderDao;
    @Autowired
    AfResourceService afResourceService;
    @Autowired
    AfUserCouponService afUserCouponService;

    @RequestMapping(value = "/good/getPayResultPage", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String newUserGift(HttpServletRequest request, ModelMap model) throws IOException {
        try{
            FanbeiWebContext context = doWebCheck(request, true);

            String orderId = request.getParameter("orderId");
            if(StringUtils.isBlank(orderId)){
                throw  new FanbeiException(FanbeiExceptionCode.PARAM_ERROR);
            }

            String userName = context.getUserName();
            AfUserDo userDo = afUserDao.getUserByUserName(userName);
            if(userDo!=null) {
                AfOrderDo orderDo = afOrderDao.getOrderById(Long.parseLong(orderId));
                if(orderDo != null) {
                    PayResultInfoDto payResultInfoDto = new PayResultInfoDto();

                    OrderInfoDto orderInfoDto = new OrderInfoDto();
                    //实付金额需要加上信用卡手续费
                    orderInfoDto.setActualAmount(orderDo.getActualAmount().add(orderDo.getFeeAmount()));
                    orderInfoDto.setAddress(orderDo.getAddress());
                    orderInfoDto.setConsignee(orderDo.getConsignee());
                    orderInfoDto.setConsigneeMobile(orderDo.getConsigneeMobile());
                    orderInfoDto.setGoodsName(orderDo.getGoodsName());
                    orderInfoDto.setOrderStatus(orderDo.getStatus());
                    orderInfoDto.setRebateAmount(orderDo.getRebateAmount());

                    payResultInfoDto.setOrderInfoDto(orderInfoDto);

                    List<Object> bannerList = getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy("AGENCY_PURCHASE_PAGE_TOP"));
                    payResultInfoDto.setBannerList(bannerList);

                    // add by luoxiao 活动支付成功后赠送优惠券
                    if(StringUtils.equals(OrderStatus.PAID.getCode(),orderDo.getStatus())){
                        try{
                            AfResourceDo activityInfo = afResourceService.getSingleResourceBytype(Constants.TAC_ACTIVITY);
                            if(null != activityInfo){
                                String activityStartTime = activityInfo.getValue().split(",")[0];
                                String activityEndTime = activityInfo.getValue().split(",")[1];

                                if(StringUtils.isNotEmpty(activityStartTime) && StringUtils.isNotEmpty(activityEndTime)){
                                    Date startDate = DateUtil.parseDate(activityStartTime,DateUtil.DATE_TIME_SHORT);
                                    Date endDate = DateUtil.parseDate(activityEndTime, DateUtil.DATE_TIME_SHORT);
                                    Date now = new Date();
                                    // 活动期间
                                    if(DateUtil.compareDate(now, startDate) && (DateUtil.compareDate(endDate, now))){
                                        // 查询优惠券信息
                                        AfUserCouponDto userCouponDto = afUserCouponService.getUserCouponAfterPaidSuccess(orderDo.getUserId());
                                        if(null != userCouponDto){
                                            payResultInfoDto.setCouponCondition(userCouponDto.getLimitAmount());
                                            payResultInfoDto.setCouponAmount(userCouponDto.getAmount());
                                        }
                                    }
                                }
                            }
                        }catch(Exception e){
                            logger.error("unknown exception:", e);
                        }
                    }
                    // end by luoxiao

                    return H5CommonResponse.getNewInstance(true, FanbeiExceptionCode.SUCCESS.getDesc(), "", JSON.toJSON(payResultInfoDto)).toString();
                }
                else {
                    return H5CommonResponse.getNewInstance(false, "订单信息不存在").toString();
                }
            }
            else
            {
                return H5CommonResponse.getNewInstance(false, "用户信息不存在").toString();
            }
        } catch (Exception e){
            return H5CommonResponse.getNewInstance(false, "请求失败，错误信息" + e.toString()).toString();
        }

    }

    public List<Object> getBannerObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
        List<Object> bannerList = new ArrayList<Object>();
        for (AfResourceDo afResourceDo : bannerResclist) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("imageUrl", afResourceDo.getValue());
            data.put("titleName", afResourceDo.getName());
            data.put("type", afResourceDo.getValue1());
            data.put("content", afResourceDo.getValue2());
            data.put("sort", afResourceDo.getSort());

            bannerList.add(data);
        }
        return bannerList;
    }

    @Override
    public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
        return null;
    }

    @Override
    public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
        try {
            RequestDataVo reqVo = new RequestDataVo();

            JSONObject jsonObj = JSON.parseObject(requestData);
            reqVo.setId(jsonObj.getString("id"));
            reqVo.setMethod(request.getRequestURI());
            reqVo.setSystem(jsonObj);

            return reqVo;
        } catch (Exception e) {
            throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
}
