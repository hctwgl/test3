package com.ald.fanbei.api.web.apph5.controller;

import com.ald.fanbei.api.biz.bo.AfOrderLogisticsBo;
import com.ald.fanbei.api.biz.bo.BorrowRateBo;
import com.ald.fanbei.api.biz.kafka.KafkaConstants;
import com.ald.fanbei.api.biz.kafka.KafkaSync;
import com.ald.fanbei.api.biz.rebate.RebateContext;
import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.ContractPdfThreadPool;
import com.ald.fanbei.api.biz.third.util.RiskUtil;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.BorrowRateBoUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfBorrowExtendDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.LeaseGoods;
import com.ald.fanbei.api.dal.domain.dto.LeaseOrderDto;
import com.ald.fanbei.api.dal.domain.dto.LeaseOrderListDto;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.vo.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author zhourui on 2018年03月03日 13:20
 * @类描述：
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/fanbei-web/lease/")
public class AppH5LeaseController extends BaseController {
    @Resource
    AfResourceService afResourceService;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfGoodsPriceService afGoodsPriceService;

    @Resource
    AfGoodsPropertyService afGoodsPropertyService;

    @Resource
    AfPropertyValueService afPropertyValueService;

    @Resource
    AfOrderService afOrderService;

    @Resource
    AfUserService afUserService;

    @Resource
    AfUserAddressService afUserAddressService;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    AfUserAuthStatusService afUserAuthStatusService;

    @Resource
    AfUserAuthService afUserAuthService;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    RebateContext rebateContext;

    @Autowired
    KafkaSync kafkaSync;

    @Resource
    AfBorrowService afBorrowService;

    @Resource
    AfBorrowExtendDao afBorrowExtendDao;

    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    AfESdkService afESdkService;

    @Resource
    ContractPdfThreadPool contractPdfThreadPool;

    @Resource
    BizCacheUtil bizCacheUtil;

    @Resource
    RiskUtil riskUtil;

    @Resource
    AfOrderLogisticsService afOrderLogisticsService;

    @Resource
    AfGoodsPriceService afgoodsPriceService;

    /**
     *获取租赁首页banner
     */
    @ResponseBody
    @RequestMapping(value = "getHomeLeaseBanner", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getHomeLeaseBanner(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            List<Object> bannerList = new ArrayList<Object>();
            String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
            if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {
                bannerList = getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.LEASE_BANNER.getCode()));
            }else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type) ){
                bannerList = getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.LEASE_BANNER.getCode()));
            }

            resp = H5CommonResponse.getNewInstance(true,"请求成功", "", bannerList);
            return resp.toString();
        }catch  (Exception e) {
            logger.error("getHomeLeaseBanner", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取租赁首页商品
     */
    @ResponseBody
    @RequestMapping(value = "getHomeLeaseGoods", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getHomeLeaseGoods(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        List<Map<String, Object>> goodsInfoList = new ArrayList<Map<String, Object>>();
        try{
            context = doWebCheck(request, false);
            if(context.isLogin()){
                AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
                AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(afUser.getRid(), "ONLINE");
                if(afUserAuthStatusDo!=null&&afUserAuthStatusDo.getStatus().equals("Y")) {
                    if (StringUtil.isEmpty(bizCacheUtil.hget("Lease_Score", afUser.getRid().toString()))) {
                        riskUtil.updateRentScore(afUser.getRid().toString());
                        bizCacheUtil.hset("Lease_Score", afUser.getRid().toString(), DateUtil.getNow(), DateUtil.getTodayLast());
                    }
                }
            }
            Long pageIndex = NumberUtil.objToLongDefault(request.getParameter("pageIndex"), 1);
            Long pageSize = NumberUtil.objToLongDefault(request.getParameter("pageSize"), 50);
            List<LeaseGoods> list = afGoodsService.getHomeLeaseGoods(pageIndex,pageSize);
            for (LeaseGoods item:list) {
                Map<String, Object> goodsInfo = new HashMap<String, Object>();
                goodsInfo.put("id",item.getRid());
                goodsInfo.put("goodsIcon",item.getGoodsIcon());
                goodsInfo.put("name",item.getName());
                goodsInfo.put("priceAmount","0");
                goodsInfo.put("nperAmount","0");
                if(item.getGoodsPrice() != null && item.getGoodsPrice().size() > 0) {
                    BigDecimal priceAmount = new BigDecimal(0);
                    BigDecimal nperAmount = new BigDecimal(0);
                    for (AfGoodsPriceDo afGoodsPriceDo:item.getGoodsPrice()) {
                        if(afGoodsPriceDo.getLeaseParam() != null && afGoodsPriceDo.getLeaseParam() != ""){
                            if(priceAmount.compareTo(afGoodsPriceDo.getLeaseAmount())>0 || priceAmount.compareTo(new BigDecimal(0))==0){
                                priceAmount = afGoodsPriceDo.getLeaseAmount();
                                JSONArray leaseParam = JSON.parseArray(afGoodsPriceDo.getLeaseParam());
                                for (int i = 0;i < leaseParam.size(); i++){
                                    JSONObject obj = leaseParam.getJSONObject(i);
                                    if(nperAmount.compareTo(obj.getBigDecimal("monthlyRent"))>0 || nperAmount.compareTo(new BigDecimal(0))==0){
                                        nperAmount = obj.getBigDecimal("monthlyRent");
                                    }
                                }
                            }
                        }
                    }
                    goodsInfo.put("priceAmount",priceAmount);
                    goodsInfo.put("nperAmount",nperAmount);
                }
                goodsInfoList.add(goodsInfo);
            }
            resp = H5CommonResponse.getNewInstance(true,"请求成功", "", goodsInfoList);
            return resp.toString();
        }catch  (Exception e) {
            logger.error("getHomeLeaseGoods", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取租赁商品详情
     */
    @ResponseBody
    @RequestMapping(value = "getLeaseGoodsDetail", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getLeaseGoodsDetail(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        Map<String, Object> data = new HashMap<String, Object>();
        List<Map<String, Object>> goodsInfoList = new ArrayList<Map<String, Object>>();
        try{
            Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("goodsId")), 0l);
            AfGoodsDo goods = afGoodsService.getGoodsById(goodsId);
            if(null == goods){
                throw new FanbeiException("goods not exist error", FanbeiExceptionCode.GOODS_NOT_EXIST_ERROR);
            }
            data = getGoodsVo(goods);

            List<AfLeaseGoodsPriceVo> priceData = new ArrayList<>();
            List<AfPropertyVo> propertyData = new ArrayList<>();
            AfGoodsPropertyDo propertyDo = new AfGoodsPropertyDo();

            List<AfGoodsPriceDo> priceDos = afGoodsPriceService.getLeaseListByGoodsId(goodsId);
            priceData = convertListForPrice(priceDos);
            data.put("priceData",priceData);

            propertyDo.setGoodsId(goodsId);
            List<AfGoodsPropertyDo> propertyDos = afGoodsPropertyService.getListByCommonCondition(propertyDo);
            if (propertyDos != null && propertyDos.size() > 0) {
                for (AfGoodsPropertyDo pdo : propertyDos) {
                    AfPropertyVo propertyVo = new AfPropertyVo();

                    List<AfPropertyValueDo> valueDos = new ArrayList<>();
                    List<AfpropertyValueVo> propertyValues = new ArrayList<>();
                    AfPropertyValueDo valueDo = new AfPropertyValueDo();
                    valueDo.setPropertyId(pdo.getRid());
                    valueDos = afPropertyValueService.getListByCommonCondition(valueDo);

                    if (valueDos != null && valueDos.size() > 0) {
                        for (AfPropertyValueDo pvd : valueDos) {
                            AfpropertyValueVo pValueVo = new AfpropertyValueVo();
                            pValueVo.setPropertyValueId(pvd.getRid());
                            pValueVo.setPropertyValueName(pvd.getValue());
                            propertyValues.add(pValueVo);
                        }
                    }
                    propertyVo.setPropertyId(pdo.getRid());
                    propertyVo.setPropertyName(pdo.getName());
                    propertyVo.setPropertyValues(propertyValues);
                    propertyData.add(propertyVo);
                }
            }
            data.put("propertyData",propertyData);
            resp = H5CommonResponse.getNewInstance(true,"请求成功", "", data);
            return resp.toString();
        }catch  (Exception e) {
            logger.error("getLeaseGoodsDetail", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取商品是否存在有效订单
     */
    @ResponseBody
    @RequestMapping(value = "checkOrder", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String checkOrder(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        Map<String, Object> data = new HashMap<String, Object>();
        try{
            data.put("status",0);
            context = doWebCheck(request, true);
            if(context.getAppVersion() < 411) {
                resp = H5CommonResponse.getNewInstance(false, "请更新到最新版本", "", null);
                return resp.toString();
            }
            Long goodsId = NumberUtil.objToLongDefault(ObjectUtils.toString(request.getParameter("goodsId")), 0l);
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            HashMap result = afOrderService.checkLeaseOrder(afUser.getRid(),goodsId);
            if(result != null){
                if(result.get("status").equals("NEW")){
                    //待支付
                    data.put("status",1);
                }
                else {
                    //进行中
                    data.put("status",2);
                }
                data.put("orderId",result.get("id"));
            }
            resp = H5CommonResponse.getNewInstance(true,"请求成功", "", data);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("checkOrder", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("checkOrder", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取用户默认收货地址
     */
    @ResponseBody
    @RequestMapping(value = "getDefaultUserAddress", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getDefaultUserAddress(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            AfUserAddressDo defauleDo = afUserAddressService.selectUserAddressDefaultByUserId(afUser.getRid());
            if(defauleDo == null){
                defauleDo = new AfUserAddressDo();
            }
            resp = H5CommonResponse.getNewInstance(true,"请求成功", "", defauleDo);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("getDefaultUserAddress", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("getDefaultUserAddress", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取用户收货地址列表
     */
    @ResponseBody
    @RequestMapping(value = "getUserAddressList", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getUserAddressList(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            List<AfUserAddressDo> list = afUserAddressService.selectUserAddressByUserId(afUser.getRid());
            resp = H5CommonResponse.getNewInstance(true,"请求成功", "", list);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("getUserAddressList", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("getUserAddressList", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *添加用户收货地址
     */
    @ResponseBody
    @RequestMapping(value = "addUserAddress", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String addUserAddress(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            String province = ObjectUtils.toString(request.getParameter("province"));
            String city = ObjectUtils.toString(request.getParameter("city"));
            String county = ObjectUtils.toString(request.getParameter("county"));
            String address = ObjectUtils.toString(request.getParameter("address"));
            String isDefault = ObjectUtils.toString(request.getParameter("isDefault"));
            String consignee = ObjectUtils.toString(request.getParameter("consignee"));
            String mobile = ObjectUtils.toString(request.getParameter("mobile"));
            if (StringUtils.isBlank(province) || StringUtils.isBlank(city) || StringUtils.isBlank(address) || StringUtils.isBlank(isDefault) || StringUtils.isBlank(consignee)
                    || StringUtils.isBlank(mobile)) {
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
            }
            if (!CommonUtil.isMobile(mobile)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
            }

            if(!StringUtils.equals(isDefault, YesNoStatus.YES.getCode())&&!StringUtils.equals(isDefault, YesNoStatus.NO.getCode())){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null).toString();
            }

            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            // 如果是第一个的地址  ,就自动设置为默认地址
            List<AfUserAddressDo> userAddressDo = afUserAddressService.selectUserAddressByUserId(afUser.getRid());
            if(userAddressDo.size() == 0){
                isDefault = "Y";
            }



            if(StringUtils.equals(isDefault, YesNoStatus.YES.getCode())){
                AfUserAddressDo defauleDo = afUserAddressService.selectUserAddressDefaultByUserId(afUser.getRid());
                if(defauleDo!=null ){
                    defauleDo.setIsDefault(YesNoStatus.NO.getCode());
                    afUserAddressService.updateUserAddress(defauleDo);
                }
            }
            AfUserAddressDo addressDo = new AfUserAddressDo();
            addressDo.setAddress(address);
            addressDo.setUserId(afUser.getRid());
            addressDo.setCity(city);
            addressDo.setProvince(province);
            addressDo.setCounty(county);
            addressDo.setConsignee(consignee);
            addressDo.setIsDefault(isDefault);
            addressDo.setMobile(mobile);
            if (afUserAddressService.addUserAddress(addressDo) > 0) {
                resp = H5CommonResponse.getNewInstance(true, "添加成功", "", "");
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, "添加失败", "", "");
            }
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("addUserAddress", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("addUserAddress", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *修改用户收货地址
     */
    @ResponseBody
    @RequestMapping(value = "changeUserAddress", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String changeUserAddress(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            Long addressId = NumberUtil.objToLongDefault(request.getParameter("addressId"), 0);
            String province = ObjectUtils.toString(request.getParameter("province"));
            String city = ObjectUtils.toString(request.getParameter("city"));
            String county = ObjectUtils.toString(request.getParameter("county"));
            String address = ObjectUtils.toString(request.getParameter("address"));
            String isDefault = ObjectUtils.toString(request.getParameter("isDefault"));
            String consignee = ObjectUtils.toString(request.getParameter("consignee"));
            String mobile = ObjectUtils.toString(request.getParameter("mobile"));
            if (StringUtils.isBlank(province) || StringUtils.isBlank(city) || StringUtils.isBlank(address) || StringUtils.isBlank(isDefault) || StringUtils.isBlank(consignee)
                    || StringUtils.isBlank(mobile)) {
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
            }
            if (!CommonUtil.isMobile(mobile)){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.SMS_MOBILE_ERROR.getDesc(), "", null).toString();
            }

            if(!StringUtils.equals(isDefault, YesNoStatus.YES.getCode())&&!StringUtils.equals(isDefault, YesNoStatus.NO.getCode())){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null).toString();
            }

            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            // 取消上一个默认地址
            if(StringUtils.equals(isDefault, YesNoStatus.YES.getCode())){
                AfUserAddressDo defauleDo = afUserAddressService.selectUserAddressDefaultByUserId(afUser.getRid());
                if(defauleDo!=null && addressId!=defauleDo.getRid()){
                    defauleDo.setIsDefault(YesNoStatus.NO.getCode());
                    afUserAddressService.updateUserAddress(defauleDo);
                }
            }else{
                // 不能从一个默认到不默认地址改变

                AfUserAddressDo userAddressDo = afUserAddressService.selectUserAddressByrid(addressId);
                if(userAddressDo != null){
                    if(!StringUtils.equals(userAddressDo.getIsDefault(), isDefault)){
                        return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.CHANG_DEFAULT_ADDRESS_ERROR.getDesc(), "", null).toString();
                    }
                }

                if(afUserAddressService.getCountOfAddressByUserId(afUser.getRid()) <= 1){
                    isDefault = YesNoStatus.YES.getCode();
                }
            }


            AfUserAddressDo addressDo = new AfUserAddressDo();
            addressDo.setRid(addressId);
            addressDo.setUserId(afUser.getRid());

            if(StringUtils.isNotBlank(address)){
                addressDo.setAddress(address);

            }
            if(StringUtils.isNotBlank(city)){
                addressDo.setCity(city);

            }
            if(StringUtils.isNotBlank(province)){
                addressDo.setProvince(province);

            }
            //如果改为空字符串是可以的
            addressDo.setCounty(county);
            if(StringUtils.isNotBlank(consignee)){
                addressDo.setConsignee(consignee);

            }
            if(StringUtils.isNotBlank(isDefault)){
                addressDo.setIsDefault(isDefault);

            }
            if(StringUtils.isNotBlank(mobile)){
                addressDo.setMobile(mobile);

            }
            if (afUserAddressService.updateUserAddress(addressDo) > 0) {
                resp = H5CommonResponse.getNewInstance(true, "修改成功", "", "");
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, "添加失败", "", "");
            }
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("changeUserAddress", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("changeUserAddress", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *删除用户收货地址
     */
    @ResponseBody
    @RequestMapping(value = "deleteUserAddress", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String deleteUserAddress(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            Long addressId = NumberUtil.objToLongDefault(request.getParameter("addressId"), 0);
            if(addressId == 0 ){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_NOT_EXIST.getDesc(), "", null).toString();
            }
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            AfUserAddressDo defauleDo = afUserAddressService.selectUserAddressByrid(addressId);
            Integer count = afUserAddressService.getCountOfAddressByUserId(defauleDo.getUserId());
            if(defauleDo == null || !defauleDo.getUserId().equals(afUser.getRid())){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null).toString();
            }
            if(count == 1){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.CHANG_ADDRESS_ERROR.getDesc(), "", null).toString();
            }
            afUserAddressService.deleteUserAddress(addressId);
            if(defauleDo.getIsDefault().equals("Y")){
                afUserAddressService.reselectTheDefaultAddress(afUser.getRid());
            }
            resp = H5CommonResponse.getNewInstance(true,"删除成功", "", "");
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("deleteUserAddress", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("deleteUserAddress", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取用户冻结购物额度
     */
    @ResponseBody
    @RequestMapping(value = "getUserFreeze", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getUserFreeze(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        Map<String, Object> data = new HashMap<String, Object>();
        try{
            context = doWebCheck(request, true);
            Long goodsPriceId = NumberUtil.objToLongDefault(request.getParameter("goodsPriceId"), 0);
            String blackBox = ObjectUtils.toString(request.getParameter("blackBox"));
            AfGoodsPriceDo priceDo = afGoodsPriceService.getById(goodsPriceId);
            if(priceDo == null){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.PARAM_ERROR.getDesc(), "", null).toString();
            }
            AfUserDo afUserDo = afUserService.getUserByUserName(context.getUserName());
            AfUserAccountDo afUser = afUserAccountService.getUserAccountByUserId(afUserDo.getRid());
            AfUserAuthDo userAuth = afUserAuthService.getUserAuthInfoByUserId(afUser.getUserId());
            AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(afUser.getUserId(), "ONLINE");
            BigDecimal useableAmount = new BigDecimal(0);
            data.put("riskStatus","Y");
            if(afUserAuthStatusDo != null){
                if(afUserAuthStatusDo.getStatus().equals("Y")){
                    AfUserAccountSenceDo afUserAccountSenceOnline = afUserAccountSenceService.getByUserIdAndScene("ONLINE", afUser.getUserId());
                    useableAmount = afUserAccountSenceOnline.getAuAmount().subtract(afUserAccountSenceOnline.getUsedAmount()).subtract(afUserAccountSenceOnline.getFreezeAmount());
                    if(useableAmount.compareTo(BigDecimal.ZERO) == -1){
                        useableAmount = BigDecimal.ZERO;
                    }
                    Map<String, Object> dataLeaseFreeze = new HashMap<String, Object>();
                    dataLeaseFreeze.put("ipAddress", CommonUtil.getIpAddr(request));
                    String sysModeId = JSON.parseObject(context.getAppInfo()).getString("id");
                    String appName = sysModeId.startsWith("i") ? "alading_ios" : "alading_and";
                    dataLeaseFreeze.put("appName",appName);
                    dataLeaseFreeze.put("blackBox",blackBox == null ? "":blackBox);
                    JSONObject dataObj = afOrderService.getLeaseFreeze(dataLeaseFreeze,priceDo.getLeaseAmount(),afUser.getUserId());
                    BigDecimal freezeAmount = dataObj.getBigDecimal("freezeAmount");
                    // -1 只能现金支付（配置规则没匹配）
                    if(dataObj.getInteger("freeze") == -1){
                        data.put("freezeAmount",priceDo.getLeaseAmount());//总冻结额度
                        data.put("quotaDeposit",0);//冻结额度
                        data.put("cashDeposit", priceDo.getLeaseAmount());//支付金额
                    }
                    else {
                        data.put("freezeAmount",freezeAmount);//总冻结额度
                        if(useableAmount.compareTo(freezeAmount)>=0){
                            data.put("quotaDeposit",freezeAmount);//冻结额度
                            data.put("cashDeposit", 0);//支付金额
                        }
                        else {
                            data.put("quotaDeposit",useableAmount);//冻结额度
                            data.put("cashDeposit", freezeAmount.subtract(useableAmount));//支付金额
                        }
                    }

                }
                else {
                    data.put("riskStatus","N");
                    data.put("action", "DO_PROMOTE_BASIC");
                }
            }else {
                data.put("riskStatus","N");
                data.put("action", "DO_SCAN_ID");
            }
            if(data.get("riskStatus").toString().equals("N")){
                if(StringUtil.equals(userAuth.getBankcardStatus(),"N")&&StringUtil.equals(userAuth.getZmStatus(),"N")
                        &&StringUtil.equals(userAuth.getMobileStatus(),"N")&&StringUtil.equals(userAuth.getTeldirStatus(),"N")
                        &&StringUtil.equals(userAuth.getFacesStatus(),"N")) {
                    data.put("action", "DO_SCAN_ID");
                }
                else if(StringUtil.equals(userAuth.getBankcardStatus(),"N")||StringUtil.equals(userAuth.getZmStatus(),"N")
                        ||StringUtil.equals(userAuth.getMobileStatus(),"N")||StringUtil.equals(userAuth.getTeldirStatus(),"N")
                        ||StringUtil.equals(userAuth.getFacesStatus(),"N")){
                    data.put("action", "DO_PROMOTE_BASIC");
                    if (StringUtil.equals(userAuth.getFacesStatus(), "Y") && StringUtil.equals(userAuth.getBankcardStatus(), "N")) {
                        data.put("action", "DO_BIND_CARD");
                    }
                    if(StringUtil.equals(userAuth.getFacesStatus(),"N")&&StringUtil.equals(userAuth.getBankcardStatus(),"N")){
                        data.put("action", "DO_SCAN_ID");
                    }
                }
            }
            data.put("realName",afUser.getRealName());//真实姓名
            data.put("idNumber",afUser.getIdNumber());//身份证
            resp = H5CommonResponse.getNewInstance(true,"请求成功", "", data);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("getUserFreeze", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("getUserFreeze", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取租赁订单列表
     */
    @ResponseBody
    @RequestMapping(value = "getLeaseOrderList", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getLeaseOrderList(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        List<LeaseOrderListDto> list = new ArrayList<>();
        try{
            context = doWebCheck(request, true);
            Long pageIndex = NumberUtil.objToLongDefault(request.getParameter("pageIndex"), 1);
            Long pageSize = NumberUtil.objToLongDefault(request.getParameter("pageSize"), 50);
            Integer type = NumberUtil.objToIntDefault(request.getParameter("type"), 0);
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            list = afOrderService.getOrderLeaseList(pageIndex,pageSize,type,afUser.getRid());
            if(list!=null&&list.size()>0){
                for (LeaseOrderListDto item:list) {
//                    if(item.getGmtPayEnd().getTime() < new Date().getTime() && (item.getStatus().equals("NEW") || item.getStatus().equals("PAYFAIL"))){
//                        item.setStatus("CLOSED");
//                        item.setClosedReason("超时未支付");
//                        afOrderService.closeOrder("超时未支付","",item.getId());
//                    }
                    //待收货
                    if(item.getStatus().equals("AUDITSUCCESS")){
                        item.setStatus("PAID");
                    }
                    if(StringUtil.isNotEmpty(item.getLeaseStatus())){
                        item.setStatus(item.getLeaseStatus());
                    }
                }
            }
            resp = H5CommonResponse.getNewInstance(true, "请求成功", "", list);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("getLeaseOrderList", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("getLeaseOrderList", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取租赁订单
     */
    @ResponseBody
    @RequestMapping(value = "getLeaseOrder", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getLeaseOrder(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        LeaseOrderDto lease = new LeaseOrderDto();
        try{
            context = doWebCheck(request, true);
            Long orderId = NumberUtil.objToLongDefault(request.getParameter("orderId"), 0);
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            lease = afOrderService.getAllOrderLeaseByOrderId(orderId,afUser.getRid());
            if(lease == null){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.ORDER_NOT_EXIST.getDesc(), "", null).toString();
            }
//            if(lease.getGmtPayEnd().getTime() < new Date().getTime() && (lease.getStatus().equals("NEW") || lease.getStatus().equals("PAYFAIL"))){
//                lease.setStatus("CLOSED");
//                lease.setClosedReason("超时未支付");
//                afOrderService.closeOrder("超时未支付","",orderId);
//            }
            if(StringUtils.isNotBlank(lease.getLogisticsNo())){
                //有物流单号就显示物流信息
                lease.setShowLogistics("Y");
                AfOrderLogisticsBo afOrderLogisticsBo = afOrderLogisticsService.getOrderLogisticsBo(orderId,
                        0);
                lease.setLogisticsInfo(afOrderLogisticsBo.getNewestInfo().getAcceptStation());
                lease.setGmtDeliver(afOrderLogisticsBo.getNewestInfo().getAcceptTime());
            }
            else {
                lease.setShowLogistics("N");
            }
            //待收货
            if(lease.getStatus().equals("AUDITSUCCESS")){
                lease.setStatus("PAID");
            }
            if(StringUtil.isNotEmpty(lease.getLeaseStatus())){
                lease.setStatus(lease.getLeaseStatus());
            }
            //计算冻结额度
            if(lease.getStatus().equals(OrderStatus.NEW.getCode()) || lease.getStatus().equals(OrderStatus.PAYFAIL.getCode())){
                if(lease.getActualAmount().compareTo(BigDecimal.ZERO) == 0){
                    AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService.getByUserIdAndType(UserAccountSceneType.ONLINE.getCode(), afUser.getRid());
                    BigDecimal useableAmount = afUserAccountSenceDo.getAuAmount().subtract(afUserAccountSenceDo.getUsedAmount()).subtract(afUserAccountSenceDo.getFreezeAmount());
                    if(useableAmount.compareTo(BigDecimal.ZERO) == -1){
                        useableAmount = BigDecimal.ZERO;
                    }
                    if(useableAmount.compareTo(lease.getFreezeAmount()) >= 0){
                        lease.setQuotaDeposit(lease.getFreezeAmount());
                        lease.setCashDeposit(new BigDecimal(0));
                    }
                    else {
                        lease.setQuotaDeposit(useableAmount);
                        lease.setCashDeposit(lease.getFreezeAmount().subtract(useableAmount));
                    }
                }
            }
            resp = H5CommonResponse.getNewInstance(true, "请求成功", "", lease);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("getLeaseOrder", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("getLeaseOrder", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *取消租赁订单
     */
    @ResponseBody
    @RequestMapping(value = "cancelLeaseOrder", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String cancelLeaseOrder(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            Long orderId = NumberUtil.objToLongDefault(request.getParameter("orderId"), 0);
            String closedReason = ObjectUtils.toString(request.getParameter("closedReason"));
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            //用户订单检查
            AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,afUser.getRid());
            if(orderInfo == null){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.ORDER_NOT_EXIST.getDesc(), "", null).toString();
            }
            afgoodsPriceService.updateStockAndSaleByPriceId(orderInfo.getGoodsPriceId(), orderInfo, false);
            afOrderService.closeOrder(closedReason,"",orderId,afUser.getRid());
            resp = H5CommonResponse.getNewInstance(true, "取消成功", "", null);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("cancelLeaseOrder", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("cancelLeaseOrder", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *删除租赁订单
     */
    @ResponseBody
    @RequestMapping(value = "deleteLeaseOrder", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String deleteLeaseOrder(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            Long orderId = NumberUtil.objToLongDefault(request.getParameter("orderId"), 0);
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            //用户订单检查
            AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,afUser.getRid());
            if(orderInfo == null){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.ORDER_NOT_EXIST.getDesc(), "", null).toString();
            }
            afOrderService.UpdateOrderLeaseShow(orderId,afUser.getRid());
            resp = H5CommonResponse.getNewInstance(true, "删除成功", "", null);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("deleteLeaseOrder", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("deleteLeaseOrder", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *确认收货
     */
    @ResponseBody
    @RequestMapping(value = "completedLeaseOrder", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String completedLeaseOrder(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        LeaseOrderDto lease = new LeaseOrderDto();
        try{
            context = doWebCheck(request, true);
            Long orderId = NumberUtil.objToLongDefault(request.getParameter("orderId"), 0);
            AfUserDo afUser = afUserService.getUserByUserName(context.getUserName());
            //用户订单检查
            AfOrderDo orderInfo = afOrderService.getOrderInfoById(orderId,afUser.getRid());
            if(orderInfo == null){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.ORDER_NOT_EXIST.getDesc(), "", null).toString();
            }
            if(!orderInfo.getStatus().equals(OrderStatus.DELIVERED.getCode())){
                return H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.RESUBMIT_ERROR.getDesc(), "", null).toString();
            }
            orderInfo.setStatus(OrderStatus.FINISHED.getCode());
            afOrderService.updateOrder(orderInfo);
            rebateContext.rebate(orderInfo);

            addBorrowBill_1(orderInfo,afUser);
            Date today = new Date();
            Date gmtStart = DateUtil.addDays(today,1);
            Date gmtEnd = DateUtil.addMonths(gmtStart,orderInfo.getNper() + 1);
            afOrderService.UpdateOrderLeaseTime(gmtStart,gmtEnd,orderInfo.getRid());
            //生成pdf
            Map<String,Object> data = afOrderService.getLeaseProtocol(orderId);
            BorrowRateBo borrowRateBo =  BorrowRateBoUtil.parseToBoFromDataTableStr(data.get("borrowRate").toString());
            data.put("overdueRate",borrowRateBo.getOverdueRate());
            contractPdfThreadPool.LeaseProtocolPdf(data,afUser.getRid(),orderId);
            resp = H5CommonResponse.getNewInstance(true, "请求成功", "", lease);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("completedLeaseOrder", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("completedLeaseOrder", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *获取租赁协议
     */
    @ResponseBody
    @RequestMapping(value = "getLeaseProtocol", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String getLeaseProtocol(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            Long orderId = NumberUtil.objToLongDefault(request.getParameter("orderId"), 0);
            Integer nper = NumberUtil.objToIntDefault(request.getParameter("nper"), 0);
            Long goodsId = NumberUtil.objToLongDefault(request.getParameter("goodsId"), 0);
            if(orderId != 0){
                HashMap data = afOrderService.getLeaseProtocol(orderId);
                //拿到日利率快照Bo
                BorrowRateBo borrowRateBo =  BorrowRateBoUtil.parseToBoFromDataTableStr(data.get("borrowRate").toString());
                data.put("overdueRate",borrowRateBo.getOverdueRate());
                AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
                if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()){
                    data.put("companyUserSeal","data:image/png;base64," + companyUserSealDo.getUserSeal());
                }
                AfUserDo afUserDo = afUserService.getUserByUserName(data.get("userName").toString());
                AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(afUserDo.getRid());
                AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
                if (null != afUserSealDo && null != afUserSealDo.getUserSeal()){
                    data.put("personUserSeal","data:image/png;base64,"+afUserSealDo.getUserSeal());
                }
                resp = H5CommonResponse.getNewInstance(true, "请求成功", "", data);
            }
            else {
                context = doWebCheck(request, true);
                HashMap data =new HashMap();
                AfUserSealDo companyUserSealDo = afESdkService.selectUserSealByUserId(-1l);
                if (null != companyUserSealDo && null != companyUserSealDo.getUserSeal()){
                    data.put("companyUserSeal","data:image/png;base64," + companyUserSealDo.getUserSeal());
                }
                AfUserDo afUserDo = afUserService.getUserByUserName(context.getUserName());
                AfUserAccountDo accountDo = afUserAccountService.getUserAccountByUserId(afUserDo.getRid());
                AfUserSealDo afUserSealDo = afESdkService.getSealPersonal(afUserDo, accountDo);
                if (null != afUserSealDo && null != afUserSealDo.getUserSeal()){
                    data.put("personUserSeal","data:image/png;base64,"+afUserSealDo.getUserSeal());
                }
                // 保存手续费信息
                BorrowRateBo borrowRate = afResourceService.borrowRateWithResource(nper,context.getUserName(),goodsId);
                data.put("overdueRate",borrowRate.getOverdueRate());
                data.put("realName",accountDo.getRealName());
                data.put("userName",accountDo.getUserName());
                data.put("idNumber",accountDo.getIdNumber());
                resp = H5CommonResponse.getNewInstance(true, "请求成功", "", data);
            }
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("getLeaseProtocol", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("getLeaseProtocol", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    /**
     *判断登陆
     */
    @ResponseBody
    @RequestMapping(value = "checkLogin", produces = "text/html;charset=UTF-8",method = RequestMethod.POST)
    public String checkLogin(HttpServletRequest request){
        FanbeiWebContext context = new FanbeiWebContext();
        H5CommonResponse resp = H5CommonResponse.getNewInstance();
        try{
            context = doWebCheck(request, true);
            resp = H5CommonResponse.getNewInstance(true, "请求成功", "", null);
            return resp.toString();
        }
        catch (FanbeiException e){
            logger.error("checkLogin", e);
            if(e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.USER_BORROW_NOT_EXIST_ERROR)
                    || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_INVALID_SIGN_ERROR) || e.getErrorCode().equals(FanbeiExceptionCode.REQUEST_PARAM_TOKEN_TIMEOUT)){
                resp = H5CommonResponse.getNewInstance(false, FanbeiExceptionCode.REQUEST_PARAM_TOKEN_ERROR.getCode(), "", null);
            }
            else {
                resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            }
            return resp.toString();
        }
        catch  (Exception e) {
            logger.error("checkLogin", e);
            resp = H5CommonResponse.getNewInstance(false, e.getMessage(), "", null);
            return resp.toString();
        }
    }

    private void addBorrowBill_1(AfOrderDo afOrderDo,AfUserDo afUser){
        AfBorrowDo afBorrowDo = afBorrowService.getBorrowByOrderId(afOrderDo.getRid());
        if(afBorrowDo !=null && !(afBorrowDo.getStatus().equals(BorrowStatus.CLOSED) || afBorrowDo.getStatus().equals(BorrowStatus.FINISH))) {
            //查询是否己产生
            List<AfBorrowBillDo> borrowList = afBorrowBillService.getAllBorrowBillByBorrowId(afBorrowDo.getRid());
            if (borrowList == null || borrowList.size() == 0) {
                AfBorrowExtendDo _aa = afBorrowExtendDao.getAfBorrowExtendDoByBorrowId(afBorrowDo.getRid());
                if (_aa == null) {
                    AfBorrowExtendDo afBorrowExtendDo = new AfBorrowExtendDo();
                    afBorrowExtendDo.setId(afBorrowDo.getRid());
                    afBorrowExtendDo.setInBill(1);
                    afBorrowExtendDao.addBorrowExtend(afBorrowExtendDo);
                } else {
                    _aa.setInBill(1);
                    afBorrowExtendDao.updateBorrowExtend(_aa);
                }
                List<AfBorrowBillDo> billList = afBorrowService.buildBorrowBillForNewInterest(afBorrowDo, afOrderDo.getPayType());
                for(AfBorrowBillDo _afBorrowExtendDo:billList){
                    _afBorrowExtendDo.setStatus("N");
                }
                afBorrowService.addBorrowBill(billList);

                afBorrowService.updateBorrowStatus(afBorrowDo, afUser.getUserName(), afOrderDo.getUserId());
            }
        }
        kafkaSync.syncEvent(afOrderDo.getUserId(), KafkaConstants.SYNC_SCENE_ONE,true);
    }

    private List<Object> getBannerObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
        List<Object> bannerList = new ArrayList<Object>();
        for (AfResourceDo afResourceDo : bannerResclist) {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("imageUrl", afResourceDo.getValue());
            data.put("skipUrl", afResourceDo.getValue1());
            data.put("sort", afResourceDo.getSort());
            bannerList.add(data);
        }
        return bannerList;
    }

    private Map<String, Object> getGoodsVo(AfGoodsDo goods){
        Map<String, Object> data = new HashMap<String, Object>();
        List<String> goodsPics = new ArrayList<String>();
        List<GoodsDetailPicInfoVo> goodsDetail = new ArrayList<GoodsDetailPicInfoVo>();
        data.put("goodsId",goods.getRid());
        data.put("goodsIcon",goods.getGoodsIcon());
        data.put("goodsName",goods.getName());
        data.put("goodsUrl",goods.getGoodsUrl());
        data.put("rebateAmount",goods.getRebateAmount());
        /* 商品详情图：多图，用英文逗号隔开  by weiqingeng
         * 1: 图片没有尺寸 http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg,http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg
         * 2: 图片有尺寸 http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg;1024;760,http://f.51fanbei.com/preEnv/a9e18267dd92e6ea.jpg;1024;760
         */
        if(StringUtil.isNotBlank(goods.getGoodsDetail())){
            String[] details = goods.getGoodsDetail().split(",");
            if(null != details && details.length > 0){
                for(String detail : details){
                    String[] gdArray = detail.split(";");
                    if(gdArray != null && gdArray.length > 0){
                        if(gdArray.length == Constants.GOODSDETAIL_PIC_PARTS){
                            GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], gdArray[1], gdArray[2]);
                            goodsDetail.add(goodsDetailPicInfoVo);
                        }else{
                            GoodsDetailPicInfoVo goodsDetailPicInfoVo = new GoodsDetailPicInfoVo(gdArray[0], null, null);
                            goodsDetail.add(goodsDetailPicInfoVo);
                        }
                    }
                }
            }
        }
        data.put("goodsDetail",goodsDetail);
        //商品图片汇总处理
        if(StringUtil.isNotBlank(goods.getGoodsPic1())){
            goodsPics.add(goods.getGoodsPic1());
        }
        if(StringUtil.isNotBlank(goods.getGoodsPic2())){
            goodsPics.add(goods.getGoodsPic2());
        }
        if(StringUtil.isNotBlank(goods.getGoodsPic3())){
            goodsPics.add(goods.getGoodsPic3());
        }
        if(StringUtil.isNotBlank(goods.getGoodsPic4())){
            goodsPics.add(goods.getGoodsPic4());
        }
        data.put("goodsPics",goodsPics);
        return data;
    }

    private List<AfLeaseGoodsPriceVo> convertListForPrice(List<AfGoodsPriceDo> priceDos) {
        List<AfLeaseGoodsPriceVo> propertyData = new ArrayList<>();
        if (priceDos != null && priceDos.size() > 0) {
            for (AfGoodsPriceDo priceDo : priceDos) {
                AfLeaseGoodsPriceVo goodsPriceVo = new AfLeaseGoodsPriceVo();

                goodsPriceVo.setStock(priceDo.getStock());
                goodsPriceVo.setIsSale(priceDo.getLeaseSale());
                goodsPriceVo.setPriceId(priceDo.getRid());
                goodsPriceVo.setPropertyValueIds(priceDo.getPropertyValueIds());
                goodsPriceVo.setPropertyValueNames(priceDo.getPropertyValueNames());
                goodsPriceVo.setPriceAmount(priceDo.getLeaseAmount());
                goodsPriceVo.setLeaseParam(priceDo.getLeaseParam());
                propertyData.add(goodsPriceVo);
            }
        }
        return propertyData;
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
            throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
        }
    }

    @Override
    public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
        return null;
    }
}
