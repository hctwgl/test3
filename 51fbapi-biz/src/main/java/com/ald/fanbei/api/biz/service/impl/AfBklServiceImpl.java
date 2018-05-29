package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.SmsUtil;
import com.ald.fanbei.api.biz.third.util.YFSmsUtil;
import com.ald.fanbei.api.biz.third.util.bkl.BklUtils;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfGoodsSpecType;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsCategoryDao;
import com.ald.fanbei.api.dal.dao.AfIagentResultDao;
import com.ald.fanbei.api.dal.dao.AfIdNumberDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.dao.AfUserAuthDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.dto.AfIagentResultDto;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.beanutils.ConvertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("afBklService")
public class AfBklServiceImpl implements AfBklService {

    private static final Logger logger = LoggerFactory.getLogger(AfBklServiceImpl.class);

    @Resource
    AfResourceService afResourceService;

    @Resource
    AfIagentResultDao iagentResultDao;

    @Resource
    AfOrderService afOrderService;

    @Resource
    BklUtils bklUtils;

    @Resource
    AfUserService afUserService;

    @Resource
    AfUserAccountDao afUserAccountDao;
    
    @Resource
    AfUserAuthDao afUserAuthDao;

    @Resource
    AfGoodsService afGoodsService;

    @Resource
    AfIdNumberDao idNumberDao;

    @Resource
    AfGoodsCategoryDao afGoodsCategoryDao;

    @Resource
    AfUserSeedService afUserSeedService;

    @Resource
    SmsUtil smsUtil;

    @Override
    public String  isBklResult(AfOrderDo orderInfo) {

        final AfUserDo userDo = afUserService.getUserById(orderInfo.getUserId());
        AfUserAuthDo afUserAuthDo = afUserAuthDao.getUserAuthInfoByUserId(orderInfo.getUserId());
        
        String result = "v2";//需电核
        //种子名单
		AfUserSeedDo userSeedDo = afUserSeedService.getAfUserSeedDoByUserId(orderInfo.getUserId());
		if (userSeedDo != null){
			result = "v1";
			orderInfo.setIagentStatus("I");
			return result;
		}
		
		//购买过权限包的用户，且在有效期限内，则电核直接通过
		//权限包商品配置信息
		AfResourceDo vipGoodsResourceDo = afResourceService.getConfigByTypesAndSecType(AfResourceType.WEAK_VERIFY_VIP_CONFIG.getCode(), AfResourceSecType.ORDER_WEAK_VERIFY_VIP_CONFIG.getCode());
		Integer vipGoodsValidDay = 0;
		if (vipGoodsResourceDo != null){
	    	vipGoodsValidDay = NumberUtil.objToIntDefault(vipGoodsResourceDo.getValue4(), 0);
	    }
		boolean vipGoodsIsValidForDate = true;
		if(vipGoodsValidDay>0 && afUserAuthDo.getGmtOrderWeakRisk()!=null
				&& DateUtil.compareDate(new Date(),DateUtil.addDays(afUserAuthDo.getGmtOrderWeakRisk(), vipGoodsValidDay))){
		    //代表需要对权限包进行有效日期的校验
			vipGoodsIsValidForDate = false;
		}
		if(afUserAuthDo!=null && YesNoStatus.YES.getCode().equals(afUserAuthDo.getOrderWeakRiskStatus()) && vipGoodsIsValidForDate){
			result = "v1";
			return result;
		}
		
        AfResourceDo bklWhiteResource = afResourceService.getConfigByTypesAndSecType(ResourceType.BKL_WHITE_LIST_CONF.getCode(), AfResourceSecType.BKL_WHITE_LIST_CONF.getCode());
        if (bklWhiteResource != null) {
            //白名单开启
            String[] whiteUserIdStrs = bklWhiteResource.getValue3().split(",");
            Long[]  whiteUserIds = (Long[]) ConvertUtils.convert(whiteUserIdStrs, Long.class);
            if (bklWhiteResource.getValue3() != null && !bklWhiteResource.getValue3().equals("")){
                if(!Arrays.asList(whiteUserIds).contains(orderInfo.getUserId())){//不在白名单不走电核
                    result = "v0";//不在白名单用户不走电核，并且没有电核状态
                    return result;
                }
            }
        }
        AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndSecType(ResourceType.ORDER_MOBILE_VERIFY_SET.getCode(), AfResourceSecType.ORDER_MOBILE_VERIFY_SET.getCode());
        if (afResourceDo != null){
            logger.info("afBklService bklUtils submitBklInfo actualAmount ="+orderInfo.getActualAmount()+",afResourceDo value="+afResourceDo.getValue());
            if (orderInfo.getActualAmount().compareTo(BigDecimal.valueOf(Long.parseLong(afResourceDo.getValue()))) <= 0){//借款金额<=订单直接通过
                result = "v1";//电核通过
                return result;
            }
            AfIagentResultDto iagentResultDo = new AfIagentResultDto();
            iagentResultDo.setUserId(orderInfo.getUserId());
            iagentResultDo.setCheckResult("0");//通过审核
            iagentResultDo.setDayNum(Integer.parseInt(afResourceDo.getValue1()));
            List<AfIagentResultDo> iagentResultDoList = iagentResultDao.getIagentByUserIdAndStatusTime(iagentResultDo);
            logger.info("afBklService bklUtils submitBklInfo iagentResultDoList  ="+JSON.toJSONString(iagentResultDoList));
            if (iagentResultDoList != null && iagentResultDoList.size() > 0){//x天内已电核过且存在通过订单用户不需电核直接通过
                logger.info("afBklService bklUtils submitBklInfo iagentResultDoList size ="+iagentResultDoList.size());
                result = "v1";//电核通过
                return result;
            }
            AfIagentResultDto resultDto = new AfIagentResultDto();
            resultDto.setUserId(orderInfo.getUserId());
            resultDto.setCheckResult("1");
            resultDto.setDayNum(Integer.parseInt(afResourceDo.getValue2()));
            List<AfIagentResultDo> resultDoList = iagentResultDao.getIagentByUserIdAndStatusTime(resultDto);
            logger.info("afBklService bklUtils submitBklInfo resultDoList ="+JSON.toJSONString(resultDoList)+",iagentResultDo="+JSON.toJSONString(resultDto));
            if (resultDoList != null && resultDoList.size() > 0){//天已电核过且拒绝订单>=2直接拒绝
                logger.info("afBklService bklUtils submitBklInfo resultDoList size ="+resultDoList.size()+",afResourceDo value3 ="+afResourceDo.getValue3());
                if (resultDoList.size() >= Integer.parseInt(afResourceDo.getValue3().trim())){
                    afOrderService.updateIagentStatusByOrderId(orderInfo.getRid(),"B");
                    Map<String,String> qmap = new HashMap<>();
                    qmap.put("orderNo",orderInfo.getOrderNo());
                    result = "v3";//直接拒绝
                    final String  orderNo = orderInfo.getOrderNo();
                    final String json = JSONObject.toJSONString(qmap);
                    YFSmsUtil.pool.execute(new Runnable() {
                        @Override
                        public void run() {
                            logger.info("bklUtils submitBklInfo closeOrderAndBorrow isBklResult info ="+orderNo);
                            HttpUtil.doHttpPost(ConfigProperties.get(Constants.CONFKEY_ADMIN_URL)+"/orderClose/closeOrderAndBorrow?orderNo="+orderNo,json);
                        }
                    });
                }else {
                    result = "v2";//需电核
                }
            }
        }
        return result;
    }



    @Override
    public void submitBklInfo(AfOrderDo orderInfo,String type,BigDecimal amount) {
        try {
            AfUserDo userDo = afUserService.getUserById(orderInfo.getUserId());
            AfUserAccountDo accountDo = afUserAccountDao.getUserAccountInfoByUserId(orderInfo.getUserId());
            AfGoodsDo goods = afGoodsService.getGoodsById(orderInfo.getGoodsId());
            AfIdNumberDo idNumberDo = idNumberDao.getIdNumberInfoByUserId(userDo.getRid());
            AfGoodsCategoryDo afGoodsCategoryDo = afGoodsCategoryDao.getGoodsCategoryById(goods.getPrimaryCategoryId());
            String csvDigit4 = accountDo.getIdNumber().substring(accountDo.getIdNumber().length()-4,accountDo.getIdNumber().length());
            String csvBirthDate = accountDo.getIdNumber().substring(accountDo.getIdNumber().length()-12,accountDo.getIdNumber().length()-4);
            String sex ;
            if (idNumberDo != null){
                sex = idNumberDo.getGender();
            }else {
                sex = "";
            }
            AfBklDo bklDo = new AfBklDo();
            bklDo.setCsvArn(orderInfo.getOrderNo());
            bklDo.setCsvPhoneNum(userDo.getMobile());
            bklDo.setCsvAmt(String.valueOf(amount));
            bklDo.setCsvDigit4(csvDigit4);
            bklDo.setCsvBirthDate(csvBirthDate);
            bklDo.setCsvName(userDo.getRealName());
            bklDo.setCsvPayWay(type);
            bklDo.setCsvProductCategory(afGoodsCategoryDo.getName());
            bklDo.setCsvSex(sex);
            bklDo.setCsvStaging(String.valueOf(orderInfo.getNper()));
            bklDo.setOrderId(orderInfo.getRid());
            bklDo.setUserId(orderInfo.getUserId());
            bklUtils.submitJob(bklDo);
            orderInfo.setIagentStatus(bklDo.getIagentState());
        }catch (Exception e){
            logger.error("submitBklInfo error = >{}",e);
        }
    }
}
