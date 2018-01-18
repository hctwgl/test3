package com.ald.fanbei.api.web.api.auth;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author caowu
 * @ClassName: lookAllQuotaApi
 * @Description: 查看用户信用中心的全部额度
 * @date 2017年11月13日 上午10:51:12
 */
@Component("lookAllQuotaApi")
public class lookAllQuotaApi implements ApiHandle {

    @Resource
    AfUserService afUserService;

    @Resource
    AfUserAuthService afUserAuthService;

    @Resource
    AfBorrowBillService afBorrowBillService;

    @Resource
    AfUserAccountService afUserAccountService;

    @Resource
    AfResourceService afResourceService;

    @Resource
    AfUserAccountSenceService afUserAccountSenceService;

    @Resource
    AfUserAuthStatusService afUserAuthStatusService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        try {
            Long userId = context.getUserId();
            if (userId == null) {
                logger.info("lookAllQuotaApi userId is null ,RequestDataVo id =" + requestDataVo.getId());
                resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
                return resp;
            }
            AfUserDo afUserDo = afUserService.getUserById(userId);
            if (afUserDo == null || afUserDo.getRid() == null) {
                logger.info("lookAllQuotaApi user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
                resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
                return resp;
            }

            List<Map<String, Object>> mapList =new ArrayList<>();
            Map<String, Object> cashMap = new HashMap<String, Object>();
            Map<String, Object> onlineMap = new HashMap<String, Object>();
            Map<String, Object> trainMap = new HashMap<String, Object>();

            AfUserAuthDo userAuth = afUserAuthService.getUserAuthInfoByUserId(userId);
            //加入临时额度
            AfInterimAuDo afInterimAuDo = afBorrowBillService.selectInterimAmountByUserId(userId);
            BigDecimal interimAmount = new BigDecimal(0);
            BigDecimal usableAmount = new BigDecimal(0);
            Boolean interimExist =false;
            if (afInterimAuDo != null) {
                interimAmount = afInterimAuDo.getInterimAmount();
                usableAmount = interimAmount.subtract(afInterimAuDo.getInterimUsed());
                if (afInterimAuDo.getGmtFailuretime().getTime() < new Date().getTime()) {
                    interimAmount = new BigDecimal(0);
                    usableAmount = new BigDecimal(0);
                }else{
                    interimExist=true;
                }
            }

            //加入线上额度(即购物额度) 线下 add by caowu 2018/1/10 15:25
            AfUserAccountSenceDo afUserAccountSenceDo = afUserAccountSenceService.getByUserIdAndScene("ONLINE",userId);
            AfUserAccountSenceDo afUserAccountSenceDo1 = afUserAccountSenceService.getByUserIdAndScene("TRAIN",userId);
            // 线上,线下信用额度
            BigDecimal onlineAuAmount = BigDecimal.ZERO;
            BigDecimal trainAuAmount = BigDecimal.ZERO;
            // 线上,线下可用额度
            BigDecimal onlineAmount = BigDecimal.ZERO;
            BigDecimal trainAmount = BigDecimal.ZERO;
            if(afUserAccountSenceDo!=null){
                onlineAuAmount=afUserAccountSenceDo.getAuAmount();
                onlineAmount=BigDecimalUtil.subtract(onlineAuAmount, afUserAccountSenceDo.getUsedAmount());
            }
            if(afUserAccountSenceDo!=null){
                trainAuAmount=afUserAccountSenceDo1.getAuAmount();
                trainAmount=BigDecimalUtil.subtract(onlineAuAmount, afUserAccountSenceDo1.getUsedAmount());
            }
            onlineMap.put("auAmount", onlineAuAmount.add(interimAmount));//线上授予额度
            onlineMap.put("amount", onlineAmount.add(usableAmount));//线上可用额度
            String onlineDesc="总额度"+onlineAuAmount+"元";
            if(interimExist){//有临时额度下的描述
                onlineDesc="总额度"+onlineAuAmount.add(interimAmount)+"元（含"+interimAmount+"临时额度）";
            }
            onlineMap.put("desc",onlineDesc);//线上描述
            onlineMap.put("status","4");

            //线下
            trainMap.put("auAmount", trainAuAmount);//线下授予额度
            trainMap.put("amount", trainAmount);//线下可用额度
            trainMap.put("status","4");

            //信用描述
            AfResourceDo afResourceDoAuth = afResourceService.getSingleResourceBytype("CREDIT_AUTH_STATUS");
            String value2=afResourceDoAuth.getValue2();//线下描述
            JSONObject jsonObject =JSON.parseObject(value2);
            String trainTitle = jsonObject.getString("title");
            String picUrl = jsonObject.getString("picUrl");
            String jumpUrl = jsonObject.getString("jumpUrl");
            String trainDesc = jsonObject.getString("desc");
            trainMap.put("desc",trainDesc);
            trainMap.put("title",trainTitle);
            trainMap.put("picUrl",picUrl);
            trainMap.put("jumpUrl",jumpUrl);

            String value3=afResourceDoAuth.getValue3();
            String value4=afResourceDoAuth.getValue4();
            //现金贷 未通过强风控 状态
            if (StringUtil.equals(userAuth.getRiskStatus(), RiskStatus.NO.getCode())){
                List<String> listDesc=getAuthDesc(value3,"three");
                cashMap.put("showAmount", listDesc.get(0));
                cashMap.put("desc", listDesc.get(1));
                cashMap.put("status","3");

            }else if(StringUtil.equals(userAuth.getBankcardStatus(),"N")&&StringUtil.equals(userAuth.getZmStatus(),"N")
                    &&StringUtil.equals(userAuth.getMobileStatus(),"N")&&StringUtil.equals(userAuth.getTeldirStatus(),"N")
                    &&StringUtil.equals(userAuth.getRealnameStatus(),"N")){
                //尚未认证状态
                List<String> listDesc1=getAuthDesc(value3,"one");
                List<String> listDesc2=getAuthDesc(value4,"one");
                cashMap.put("showAmount", listDesc1.get(0));
                cashMap.put("desc", listDesc1.get(1));
                cashMap.put("status","1");
                onlineMap.put("showAmount", listDesc2.get(0));
                onlineMap.put("desc", listDesc2.get(1));
                onlineMap.put("status","1");

                trainMap.put("desc",trainDesc);
                trainMap.put("status","1");

            } else{
                //认证一般中途退出了
                String status="2";
                //认证人脸没有认证银行卡 状态为5
                if(StringUtil.equals(userAuth.getFacesStatus(),"Y")&&StringUtil.equals(userAuth.getBankcardStatus(),"N")){
                    status="5";
                }
                List<String> listDesc1=getAuthDesc(value3,"two");
                List<String> listDesc2=getAuthDesc(value4,"two");
                cashMap.put("amount", listDesc1.get(0));
                cashMap.put("desc", listDesc1.get(1));
                cashMap.put("status",status);
                onlineMap.put("showAmount", listDesc2.get(0));
                onlineMap.put("desc", listDesc2.get(1));
                onlineMap.put("status",status);


                trainMap.put("desc", trainDesc);
                trainMap.put("status",status);
            }

            //购物额度 未通过强风控
            AfUserAuthStatusDo afUserAuthStatusDo=afUserAuthStatusService.selectAfUserAuthStatusByCondition(userId,"ONLINE","C");
            if(afUserAuthStatusDo!=null){
                List<String> listDesc=getAuthDesc(value4,"three");
                onlineMap.put("showAmount", listDesc.get(0));
                onlineMap.put("desc", listDesc.get(1));
                onlineMap.put("status","3");
            }
            //线下培训 未通过强风控
            AfUserAuthStatusDo afUserAuthStatusDo1=afUserAuthStatusService.selectAfUserAuthStatusByCondition(userId,"TRAIN","C");
            if(afUserAuthStatusDo1!=null){
                trainMap.put("desc", trainDesc);
                trainMap.put("status","3");
            }

            if (StringUtil.equals(userAuth.getRiskStatus(), RiskStatus.YES.getCode())) {
                // 获取用户额度
                AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
                if (userAccount == null || userAccount.getRid() == null) {
                    logger.error("lookAllQuotaApi error ; userAccount is null and userId = " + userId);
                    resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
                    return resp;
                }
                // 信用额度
                BigDecimal auAmount = userAccount.getAuAmount();
                // 可用额度
                BigDecimal amount = BigDecimalUtil.subtract(auAmount, userAccount.getUsedAmount());
                cashMap.put("auAmount", auAmount);
                cashMap.put("amount", amount);
                cashMap.put("status","4");
                cashMap.put("desc", "总额度"+auAmount+"元");
            }
            cashMap.put("scene","CASH");
            cashMap.put("title","借钱额度");
            cashMap.put("picUrl","");
            cashMap.put("jumpUrl","");
            onlineMap.put("scene","ONLINE");
            onlineMap.put("title","购物额度");
            onlineMap.put("picUrl","");
            onlineMap.put("jumpUrl","");
            trainMap.put("scene","TRAIN");
            trainMap.put("showAmount","");
            mapList.add(cashMap);
            mapList.add(onlineMap);
            mapList.add(trainMap);
            Map<String,Object> data = new HashMap<>();
            data.put("data",mapList);
           // resp.setResponseData(mapList);
            resp.setResponseData(data);
        } catch (Exception e) {
            logger.error("lookAllQuotaApi error :", e);
            resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.CALCULATE_SHA_256_ERROR);
            return resp;
        }
        return resp;
    }

    public List<String> getAuthDesc(String value,String status){
        List<String> listString = new ArrayList();
        JSONArray jsonArray = JSON.parseArray(value);
        boolean judge=true;
        for(int i =0;i<jsonArray.size();i++){
            if(judge){
                JSONObject jsonObject =jsonArray.getJSONObject(i);
                String jsonStatus=jsonObject.getString("status");
                if(status.equals(jsonStatus)){
                    listString.add(jsonObject.getString("quotaSection"));
                    listString.add(jsonObject.getString("desc"));
                    judge=false;
                }
            }
        }
        return listString;
    }

}
