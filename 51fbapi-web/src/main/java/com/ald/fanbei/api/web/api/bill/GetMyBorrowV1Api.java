package com.ald.fanbei.api.web.api.bill;

import java.math.BigDecimal;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.UserAuthSceneStatus;
import com.ald.fanbei.api.dal.dao.AfBorrowCashDao;
import com.ald.fanbei.api.dal.dao.AfLoanDao;
import com.ald.fanbei.api.dal.domain.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import jodd.util.ObjectUtil;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.BorrowBillStatus;
import com.ald.fanbei.api.common.enums.RiskStatus;
import com.ald.fanbei.api.common.enums.SceneType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQuery;
import com.ald.fanbei.api.dal.domain.query.AfBorrowBillQueryNoPage;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.timevale.tgtext.text.af;

/**
 * @author yuyue
 * @ClassName: GetMyBorrowV1Api
 * @Description: 用户获取账单主页面的api——账单二期
 * @date 2017年11月13日 上午10:51:12
 */
@Component("getMyBorrowV1Api")
public class GetMyBorrowV1Api implements ApiHandle {

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
    
    @Resource
    AfBorrowCashDao afBorrowCashDao;
    
    @Resource
    AfLoanDao afLoanDao;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
        ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
        try {
            Long userId = context.getUserId();
            if (userId == null) {
                logger.info("getMyBorrowV1Api userId is null ,RequestDataVo id =" + requestDataVo.getId());
                resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
                return resp;
            }
            AfUserDo afUserDo = afUserService.getUserById(userId);
            if (afUserDo == null || afUserDo.getRid() == null) {
                logger.info("getMyBorrowV1Api user is null ,RequestDataVo id =" + requestDataVo.getId() + " ,userId=" + userId);
                resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
                return resp;
            }
            Map<String, Object> map = new HashMap<String, Object>();

            AfUserAuthDo userAuth = afUserAuthService.getUserAuthInfoByUserId(userId);
            //加入临时额度
            AfInterimAuDo afInterimAuDo = afBorrowBillService.selectInterimAmountByUserId(userId);
            BigDecimal interimAmount = new BigDecimal(0);
            BigDecimal usableAmount = new BigDecimal(0);
            Boolean interimExist =false;
            if (afInterimAuDo != null) {
                interimAmount = afInterimAuDo.getInterimAmount();
                usableAmount = interimAmount.subtract(afInterimAuDo.getInterimUsed());
                map.put("interimType", 1);//已获取临时额度
                map.put("interimAmount", afInterimAuDo.getInterimAmount());//临时额度
                map.put("interimUsed", afInterimAuDo.getInterimUsed());//已使用的额度
                int failureStatus = 0;//0未失效,1失效
                if (afInterimAuDo.getGmtFailuretime().getTime() < new Date().getTime()) {
                    failureStatus = 1;
                    interimAmount = new BigDecimal(0);
                    usableAmount = new BigDecimal(0);
                }else{
                    interimExist=true;
                }
                map.put("failureStatus", failureStatus);
            } else {
                map.put("interimType", 0);//未获取临时额度
            }

            //加入漂浮窗信息
            AfResourceDo afResourceDo = afResourceService.getConfigByTypesAndValue("SUSPENSION_FRAME_SETTING", "0");
            if (afResourceDo != null) {
                map.put("floatType", 1);//开启悬浮窗
                map.put("name", afResourceDo.getName());
                map.put("pic1", afResourceDo.getPic1());
                map.put("pic2", afResourceDo.getPic2());
            } else {
                map.put("floatType", 0);//未开启悬浮窗
            }
            //加入线上额度(即购物额度) 线下 add by caowu 2018/1/10 15:25
            AfUserAccountSenceDo afUserAccountSenceOnline = afUserAccountSenceService.getByUserIdAndScene("ONLINE",userId);
            AfUserAccountSenceDo afUserAccountSenceTrain = afUserAccountSenceService.getByUserIdAndScene("TRAIN",userId);
            // 线上,线下信用额度
            BigDecimal onlineAuAmount = BigDecimal.ZERO;
            BigDecimal trainAuAmount = BigDecimal.ZERO;
            // 线上,线下可用额度
            BigDecimal onlineAmount = BigDecimal.ZERO;
            BigDecimal trainAmount = BigDecimal.ZERO;
            if(afUserAccountSenceOnline!=null){
                onlineAuAmount=afUserAccountSenceOnline.getAuAmount();
                onlineAmount=BigDecimalUtil.subtract(onlineAuAmount, afUserAccountSenceOnline.getUsedAmount()).subtract(afUserAccountSenceOnline.getFreezeAmount());
            }
            if(afUserAccountSenceTrain!=null){
                trainAuAmount=afUserAccountSenceTrain.getAuAmount();
                trainAmount=BigDecimalUtil.subtract(trainAuAmount, afUserAccountSenceTrain.getUsedAmount()).subtract(afUserAccountSenceTrain.getFreezeAmount());
            }

            //信用描述
            AfResourceDo afResourceDoAuth = afResourceService.getSingleResourceBytype("CREDIT_AUTH_STATUS");
            String value3=afResourceDoAuth.getValue3();
            String value4=afResourceDoAuth.getValue4();
            List<String> listDesc1=getAuthDesc(value3,"two");
            List<String> listDesc2=getAuthDesc(value4,"two");
            map.put("showAmount", listDesc1.get(0));
            map.put("desc", listDesc1.get(1));
            map.put("borrowStatus","2");
            map.put("onlineShowAmount", listDesc2.get(0));
            map.put("onlineDesc", listDesc2.get(1));
            map.put("onlineStatus","2");
            //线下
            map.put("trainAuAmount", trainAuAmount);//线下授予额度
            map.put("trainAmount", trainAmount);//线下可用额度

            //现金贷 未通过强风控 状态
            if (StringUtil.equals(userAuth.getRiskStatus(), RiskStatus.NO.getCode())){
                List<String> listDesc=getAuthDesc(value3,"three");
                map.put("showAmount", listDesc.get(0));
                map.put("desc", listDesc.get(1));
                map.put("borrowStatus","3");
            }
            //购物额度 未通过强风控
            AfUserAuthStatusDo afUserAuthStatusDo=afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId,"ONLINE");
            List<String> listDesc=getAuthDesc(value4,"three");
            if(afUserAuthStatusDo!=null){
                if(afUserAuthStatusDo.getStatus().equals(UserAuthSceneStatus.FAILED.getCode())) {
                    map.put("onlineShowAmount", listDesc.get(0));
                    map.put("onlineDesc", listDesc.get(1));
                    map.put("onlineStatus", "3");
                }else if(afUserAuthStatusDo.getStatus().equals(UserAuthSceneStatus.YES.getCode()))
                {
                    map.put("onlineAuAmount", onlineAuAmount.add(interimAmount));//线上授予额度
                    map.put("onlineAmount", onlineAmount.add(usableAmount));//线上可用额度
                    String onlineDesc="总额度"+onlineAuAmount+"元";
                    if(interimExist){//有临时额度下的描述
                        onlineDesc="总额度"+onlineAuAmount.add(interimAmount)+"元";
                    }
                    if(afUserAccountSenceOnline.getFreezeAmount().compareTo(BigDecimal.ZERO) == 1){
                        onlineDesc=onlineDesc + "（含冻结" + afUserAccountSenceOnline.getFreezeAmount() + "）";
                    }
                    map.put("onlineDesc",onlineDesc);//线上描述
                    map.put("onlineStatus","4");
                }
            }

            if(StringUtil.equals(userAuth.getBankcardStatus(),"N")&&StringUtil.equals(userAuth.getZmStatus(),"N")
                    &&StringUtil.equals(userAuth.getMobileStatus(),"N")&&StringUtil.equals(userAuth.getTeldirStatus(),"N")
                    &&StringUtil.equals(userAuth.getFacesStatus(),"N")){
                //尚未认证状态
                listDesc1=getAuthDesc(value3,"one");
                listDesc2=getAuthDesc(value4,"one");
                map.put("showAmount", listDesc1.get(0));
                map.put("desc", listDesc1.get(1));
                map.put("borrowStatus","1");
                map.put("onlineShowAmount", listDesc2.get(0));
                map.put("onlineDesc", listDesc2.get(1));
                map.put("onlineStatus","1");
            } else if(StringUtil.equals(userAuth.getBankcardStatus(),"N")||StringUtil.equals(userAuth.getZmStatus(),"N")
                ||StringUtil.equals(userAuth.getMobileStatus(),"N")||StringUtil.equals(userAuth.getTeldirStatus(),"N")
                    ||StringUtil.equals(userAuth.getFacesStatus(),"N")){
                //认证一般中途退出了
                String status="2";
                //认证人脸没有认证银行卡 状态为5
                if(StringUtil.equals(userAuth.getFacesStatus(),"Y")&&StringUtil.equals(userAuth.getBankcardStatus(),"N")){
                    status="5";
                }
                
                if(StringUtil.equals(userAuth.getFacesStatus(),"N")){
                    status="1";
                }
                listDesc1=getAuthDesc(value3,"two");
                listDesc2=getAuthDesc(value4,"two");
                map.put("showAmount", listDesc1.get(0));
                map.put("desc", listDesc1.get(1));
                map.put("borrowStatus",status);
                map.put("onlineShowAmount", listDesc2.get(0));
                map.put("onlineDesc", listDesc2.get(1));
                map.put("onlineStatus",status);
            }
            //真实姓名
            map.put("realName", afUserDo.getRealName()==null ? "":afUserDo.getRealName());

            if (StringUtil.equals(userAuth.getRiskStatus(), RiskStatus.YES.getCode())) {
                // 获取用户额度
                AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
                if (userAccount == null || userAccount.getRid() == null) {
                    logger.error("getMyBorrowV1Api error ; userAccount is null and userId = " + userId);
                    resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.USER_NOT_EXIST_ERROR);
                    return resp;
                }
                // 信用额度
                BigDecimal auAmount = userAccount.getAuAmount();
                // 可用额度
                BigDecimal amount = BigDecimalUtil.subtract(auAmount, userAccount.getUsedAmount());

		map.put("borrowStatus", "4");
		if (context.getAppVersion() >= 406) {
		    BigDecimal cashUsedAmount=BigDecimal.ZERO;
		    AfBorrowCashDo afBorrowCashDo = afBorrowCashDao.getDealingCashByUserId(userId);
		    List<AfLoanDo> listLoan = afLoanDao.listDealingLoansByUserId(userId);
		    if(listLoan != null && listLoan.size()>0){
			for (AfLoanDo afLoanDo : listLoan) {
				cashUsedAmount = cashUsedAmount.add(afLoanDo.getAmount());
			}
		    }
		    if(afBorrowCashDo!=null)
		    {
			cashUsedAmount = cashUsedAmount.add(afBorrowCashDo.getAmount());
		    }		    
		    
		    AfUserAccountSenceDo loanTotalSenceDo = afUserAccountSenceService.getByUserIdAndScene(SceneType.LOAN_TOTAL.getName(), userId);
		    if (loanTotalSenceDo != null) {
			if (BigDecimal.ZERO.compareTo(auAmount) != 0) {
			    auAmount = loanTotalSenceDo.getAuAmount();
			}
		    }
		    map.put("auAmount", auAmount);
		    map.put("amount", auAmount.subtract(cashUsedAmount));
		    map.put("desc", "总额度" + auAmount + "元");
		} else {
		    map.put("auAmount", auAmount.add(interimAmount).add(onlineAuAmount));
		    map.put("amount", amount.add(usableAmount).add(onlineAmount));
		    map.put("desc", "总额度" + amount.add(usableAmount).add(onlineAmount) + "元");
		}
            }
            //if (afUserAuthStatusDo != null && StringUtil.equals(afUserAuthStatusDo.getStatus(), RiskStatus.YES.getCode())) {
                // 获取逾期账单月数量
                int overduedMonth = afBorrowBillService.getOverduedMonthByUserId(userId);
                AfBorrowBillQueryNoPage query = new AfBorrowBillQueryNoPage();
                query.setUserId(userId);
                int billCount = afBorrowBillService.countBillByQuery(query);
                if (billCount < 1) {
                    map.put("status", "noBill");
                } else {
                    map.put("status", "bill");
                    // 查询下月未出账单
                    AfBorrowBillQueryNoPage _query = new AfBorrowBillQueryNoPage();
                    Date strOutDay = DateUtil.getFirstOfMonth(new Date());
                    strOutDay = DateUtil.addHoures(strOutDay, -12);
                    Date endOutDay = DateUtil.addMonths(strOutDay, 1);
                    _query.setUserId(userId);
                    _query.setIsOut(1);
                    _query.setOutDayStr(strOutDay);
                    _query.setOutDayEnd(endOutDay);
                    int _billCount = afBorrowBillService.countBillByQuery(_query);
                    if (_billCount < 1) {
                        // 没有本月已出，查询是否有本月未出未还
                        _query.setIsOut(0);
                        _query.setStatus("N");
                        _billCount = afBorrowBillService.countBillByQuery(_query);
                        if (_billCount > 0) {
                            map.put("status", "nextBill");
                        }else if (_billCount < 1) {
                            // 没有本月未出，查询下月未出
                            strOutDay = DateUtil.addMonths(strOutDay, 1);
                            endOutDay = DateUtil.addMonths(strOutDay, 1);
                            _query.setOverdueStatus("N");
                            _billCount = afBorrowBillService.countBillByQuery(_query);
                            if (_billCount > 0) {
                                // 有下月未出未还
                                map.put("status", "nextBill");
                            }
                        }
                    }else if (_billCount > 0) {
                        // 有本月已出,查询是否有下月未出未还
                        strOutDay = DateUtil.addMonths(strOutDay, 1);
                        endOutDay = DateUtil.addMonths(strOutDay, 1);
                        _query.setIsOut(0);
                        _query.setStatus("N");
                        _billCount = afBorrowBillService.countBillByQuery(_query);
                        if (_billCount > 0) {
                            // 有下月未出未还
                            map.put("status", "nextBill");
                        }
                    }
                }
                // 已出账单
                query.setIsOut(1);
                query.setStatus(BorrowBillStatus.NO.getCode());
                BigDecimal outMoney = afBorrowBillService.getUserBillMoneyByQuery(query);
                // 未出账单
                query.setIsOut(0);
                BigDecimal notOutMoeny = afBorrowBillService.getUserBillMoneyByQuery(query);
                map.put("lastPayDay", null);
                if (outMoney.compareTo(new BigDecimal(0)) == 1) {
                    if (overduedMonth < 1) {
                        Date lastPayDay = afBorrowBillService.getLastPayDayByUserId(userId);
                        map.put("lastPayDay", DateUtil.formatMonthAndDay(lastPayDay));
                    }
                }
                int onRepaymentCount = afBorrowBillService.getOnRepaymentCountByUserId(userId);
                map.put("onRepaymentCount", onRepaymentCount);
                map.put("overduedMonth", overduedMonth);
                map.put("outMoney", outMoney);
                map.put("notOutMoeny", notOutMoeny);
            //}
            resp.setResponseData(map);
        } catch (Exception e) {
            logger.error("getMyBorrowV1Api error :", e);
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
