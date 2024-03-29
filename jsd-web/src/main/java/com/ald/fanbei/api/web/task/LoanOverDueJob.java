package com.ald.fanbei.api.web.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.third.util.CollectionNoticeUtil;
import com.ald.fanbei.api.biz.third.util.JobThreadPoolUtils;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.dingding.DingdingUtil;
import com.ald.fanbei.api.dal.dao.JsdContractPdfDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.JsdBorrowLegalOrderDao;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.bo.xgxy.XgxyBorrowNoticeBo;
import com.ald.fanbei.api.biz.service.JsdBorrowCashOverdueLogService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashRepaymentService;
import com.ald.fanbei.api.biz.service.JsdBorrowCashService;
import com.ald.fanbei.api.biz.service.JsdBorrowLegalOrderCashService;
import com.ald.fanbei.api.biz.service.JsdNoticeRecordService;
import com.ald.fanbei.api.biz.third.enums.XgxyBorrowNotifyStatus;
import com.ald.fanbei.api.biz.third.util.XgxyUtil;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashOverdueLogDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowCashRepaymentDo;
import com.ald.fanbei.api.dal.domain.JsdBorrowLegalOrderCashDo;


/**
 *@类描述：借钱逾期利息，逾期手续费计算 每日零点执行一次
 *@author jilong
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class LoanOverDueJob {
    Logger logger = LoggerFactory.getLogger(LoanOverDueJob.class);



    @Resource
    private XgxyUtil xgxyUtil;

    @Resource
    private CollectionNoticeUtil collectionNoticeUtil;
    @Resource
    JsdUserContactsService jsdUserContactsService;
    @Resource
    private JsdBorrowCashService borrowCashService;
    @Resource
    JsdContractPdfDao jsdContractPdfDao;
    @Resource
    private JsdBorrowCashOverdueLogService jsdBorrowCashOverdueLogService;
    @Resource
    private JsdBorrowCashRepaymentService jsdBorrowCashRepaymentService;
    @Resource
    private JsdNoticeRecordService jsdNoticeRecordService;
    @Resource
    JsdUserService jsdUserService;
    @Resource
    JsdBorrowLegalOrderDao jsdBorrowLegalOrderDao;
    @Resource
    JsdBorrowLegalOrderCashService jsdBorrowLegalOrderCashService;
    @Resource
    JsdBorrowCashRenewalService jsdBorrowCashRenewalService;
    @Resource
    JsdCollectionBorrowService jsdCollectionBorrowService;
    @Resource
    JobThreadPoolUtils jobThreadPoolUtils;
    @Resource
    JsdResourceService jsdResourceService;


    private static String HOST = "0.0.0.0";
    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_TASK_ACTIVE_HOST);

    @Scheduled(cron = "0 10 0 * * ?")
    public void laonDueJob(){
        try{
            String curHostIp = GetHostIpUtil.getIpAddress();
            logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
            if(StringUtils.equals(GetHostIpUtil.getIpAddress(), NOTICE_HOST) || StringUtils.equals(GetHostIpUtil.getIpAddress(), HOST)){
                int pageSize = 200;
                int totalRecord = borrowCashService.getBorrowCashOverdueCount();
                int totalPageNum = (totalRecord + pageSize - 1) / pageSize;
                if (totalRecord == 0) {
                    logger.info("borrowCashDueJob run finished,Loan Due size is 0.time=" + new Date());
                }else {
                    logger.info("borrowCashDueJob run start,time=" + new Date());
                    for(int i = 0; i < totalPageNum; i++){
                        List<JsdBorrowCashDo> borrowCashDos=borrowCashService.getBorrowCashOverdue(pageSize*i,pageSize);
                        //计算逾期
                        this.dealOverdueRecords(borrowCashDos);
                        //通知催收逾期人员通讯录
                        jobThreadPoolUtils.getThreadPool().execute(new Runnable() {
                            @Override
                            public void run() {
                                collectionPush(borrowCashDos);
                            }
                        });
                        //增加已入催数据
                        this.addCollectionBorrow(borrowCashDos);
                    }

                }
                int count = jsdBorrowCashOverdueLogService.getBorrowCashOverDueLogToDay();
                DingdingUtil.sendMessageByJob(NOTICE_HOST+"：计划执行"+totalRecord+"条，实际执行"+ count + "条！",true);
                logger.info("borrowCashDueJob run end,time=" + new Date());
            }
        } catch (Exception e){
            DingdingUtil.sendMessageByJob(NOTICE_HOST +"，逾期定时器执行失败！",true);
            logger.error("borrowCashDueJob  error, case=",e);
        }
    }
    /**
     * 解析当前借款需付逾期费
     *
     * @param
     */
    void dealOverdueRecords(List<JsdBorrowCashDo> jsdBorrowCashDos){
        Iterator<JsdBorrowCashDo> iterator = jsdBorrowCashDos.iterator();
        while (iterator.hasNext()){
            JsdBorrowCashDo jsdBorrowCashDo = iterator.next();
            try {
                addUserContancts(jsdBorrowCashDo.getUserId());
                logger.info("calcuOverdueRecords do borrowCashDueJob, borrowCashId="+jsdBorrowCashDo.getRid());
                if(jsdBorrowCashOverdueLogService.getBorrowCashOverDueLogByNow(String.valueOf(jsdBorrowCashDo.getRid()))>0){
                    logger.warn("calcuOverdueRecords, ignore have dealed borrowCashId "+jsdBorrowCashDo.getRid());
                    iterator.remove();
                    continue;
                }
                BigDecimal currentAmount = BigDecimalUtil.add(jsdBorrowCashDo.getAmount(), jsdBorrowCashDo.getSumRepaidOverdue(),jsdBorrowCashDo.getSumRepaidInterest(), jsdBorrowCashDo.getSumRepaidPoundage()).subtract(jsdBorrowCashDo.getRepayAmount());// 当前本金
                JsdBorrowCashRepaymentDo borrowCashRepaymentDo=jsdBorrowCashRepaymentService.getLastRepaymentBorrowCashByBorrowId(jsdBorrowCashDo.getRid());
                if(borrowCashRepaymentDo != null && JsdBorrowCashRepaymentStatus.PROCESS.getCode().equals(borrowCashRepaymentDo.getStatus())) {
                    currentAmount = BigDecimalUtil.add(jsdBorrowCashDo.getAmount(), jsdBorrowCashDo.getOverdueAmount(), jsdBorrowCashDo.getInterestAmount(), jsdBorrowCashDo.getPoundageAmount(), jsdBorrowCashDo.getSumRepaidInterest(), jsdBorrowCashDo.getSumRepaidOverdue(), jsdBorrowCashDo.getSumRepaidPoundage())
                            .subtract(jsdBorrowCashDo.getRepayAmount()).subtract(borrowCashRepaymentDo.getRepaymentAmount());// 当前本金
                }
                if (currentAmount.compareTo(BigDecimal.ZERO) == 0) {
                    logger.warn("calcuOverdueRecords, overdue amount final is 0, ignore borrowCashId "+jsdBorrowCashDo.getRid());
                    iterator.remove();
                    continue;
                }
                //获取逾期本金百分比
                JsdResourceDo overdueConfig = jsdResourceService.getByTypeAngSecType(ResourceType.OVERDUE.getCode(), ResourceSecType.OVERDUE_CONFIG.getCode());
                BigDecimal percentOverdue = new BigDecimal(50);
                if(overdueConfig!=null&&!StringUtil.isEmpty(overdueConfig.getValue())){
                    percentOverdue = new BigDecimal(overdueConfig.getValue());
                }
                //总逾期费（不能超过）
                BigDecimal totalOverdueAmount = jsdBorrowCashDo.getAmount().multiply(percentOverdue.divide(new BigDecimal(100),2, RoundingMode.HALF_UP)).setScale(2,BigDecimal.ROUND_HALF_UP);
                //商品逾期费
                JsdBorrowLegalOrderCashDo borrowLegalOrderCashDo=jsdBorrowLegalOrderCashService.getOverdueBorrowLegalOrderCashByBorrowId(jsdBorrowCashDo.getRid());
                BigDecimal orderOverdueAmount = borrowLegalOrderCashDo == null ? BigDecimal.ZERO : borrowLegalOrderCashDo.getOverdueAmount().add(borrowLegalOrderCashDo.getSumRepaidOverdue());

                BigDecimal oldOverdueAmount = jsdBorrowCashDo.getOverdueAmount();//当前逾期
                BigDecimal newOverdueAmount = currentAmount.multiply(jsdBorrowCashDo.getOverdueRate().divide(new BigDecimal(360),6,BigDecimal.ROUND_HALF_UP)).setScale(2,BigDecimal.ROUND_HALF_UP);
                JsdBorrowCashDo borrowCashDo = new JsdBorrowCashDo();
                jsdBorrowCashDo.setOverdueDay(jsdBorrowCashDo.getOverdueDay()+1);
                jsdBorrowCashDo.setOverdueStatus(YesNoStatus.YES.getCode());
                logger.info("totalOverdueAmount:"+totalOverdueAmount);
                logger.info("overdueAmount:"+totalOverdueAmount);
                if((oldOverdueAmount.add(newOverdueAmount).add(orderOverdueAmount).add(jsdBorrowCashDo.getSumRepaidOverdue())).compareTo(totalOverdueAmount) < 1) {
                    borrowCashDo.setOverdueAmount(oldOverdueAmount.add(newOverdueAmount));
                    jsdBorrowCashDo.setOverdueAmount(oldOverdueAmount.add(newOverdueAmount));
                }
                else {
                    borrowCashDo.setOverdueAmount(oldOverdueAmount);
                    newOverdueAmount = BigDecimal.ZERO;
                }
                borrowCashDo.setOverdueDay(jsdBorrowCashDo.getOverdueDay());
                borrowCashDo.setOverdueStatus(YesNoStatus.YES.getCode());
                borrowCashDo.setRid(jsdBorrowCashDo.getRid());
                borrowCashService.updateById(borrowCashDo);

                if(borrowLegalOrderCashDo!=null){
                    BigDecimal orderAmount = BigDecimalUtil.add(borrowLegalOrderCashDo.getAmount(), borrowLegalOrderCashDo.getSumRepaidInterest(), borrowLegalOrderCashDo.getSumRepaidPoundage()).subtract(borrowLegalOrderCashDo.getRepaidAmount());// 当前本金
                    BigDecimal oldOverdueorderAmount = borrowLegalOrderCashDo.getOverdueAmount();//当前逾期
                    BigDecimal newOverdueorderAmount = orderAmount.multiply(borrowLegalOrderCashDo.getOverdueRate().divide(new BigDecimal(360),6,BigDecimal.ROUND_HALF_UP)).setScale(2,BigDecimal.ROUND_HALF_UP);
                    borrowLegalOrderCashDo.setOverdueStatus(YesNoStatus.YES.getCode());
                    borrowLegalOrderCashDo.setOverdueDay((short) (borrowLegalOrderCashDo.getOverdueDay()+1));
                    borrowLegalOrderCashDo.setOverdueAmount(oldOverdueorderAmount.add(newOverdueorderAmount));
                    JsdBorrowLegalOrderCashDo jsdBorrowLegalOrderCashDo = new JsdBorrowLegalOrderCashDo();
                    jsdBorrowLegalOrderCashDo.setRid(borrowLegalOrderCashDo.getRid());
                    jsdBorrowLegalOrderCashDo.setOverdueStatus(YesNoStatus.YES.getCode());
                    jsdBorrowLegalOrderCashDo.setOverdueDay(borrowLegalOrderCashDo.getOverdueDay());
                    if((borrowCashDo.getOverdueAmount().add(jsdBorrowCashDo.getSumRepaidOverdue()).add(orderOverdueAmount).add(newOverdueorderAmount)).compareTo(totalOverdueAmount) < 1) {
                        jsdBorrowLegalOrderCashDo.setOverdueAmount(oldOverdueorderAmount.add(newOverdueorderAmount));
                    }
                    else {
                        newOverdueorderAmount = BigDecimal.ZERO;
                    }
                    jsdBorrowLegalOrderCashService.updateById(jsdBorrowLegalOrderCashDo);
                    jsdBorrowCashOverdueLogService.saveRecord(buildLoanOverdueLog(borrowLegalOrderCashDo.getRid(),orderAmount,newOverdueorderAmount,borrowLegalOrderCashDo.getUserId(), OverdueLogType.ORDER_CASH.name()) );
                }
                //新增逾期日志
                jsdBorrowCashOverdueLogService.saveRecord(buildLoanOverdueLog(jsdBorrowCashDo.getRid(), currentAmount, newOverdueAmount, jsdBorrowCashDo.getUserId(), OverdueLogType.CASH.name()));
                jsdNoticeRecordService.dealBorrowNoticed(jsdBorrowCashDo,XgxyBorrowNoticeBo.gen(jsdBorrowCashDo.getTradeNoXgxy(), XgxyBorrowNotifyStatus.OVERDUE.name(), "逾期"));
            } catch (Exception e) {
                logger.error("LoanOverDueTask calcuOverdueRecords error, legal loanId="+jsdBorrowCashDo.getRid(),e);
            }
        }
    }


    void addUserContancts(Long userId){
        try {
            JsdUserDo userDo = jsdUserService.getById(userId);
            HashMap<String,Object> contacts=xgxyUtil.getUserContactsInfo(userDo.getOpenId());
            if(StringUtils.isNotBlank(String.valueOf(contacts))){
                List<JsdUserContactsDo> userContactsDo= jsdUserContactsService.getUserContactsByUserId(userId);
                JsdUserContactsDo contactsDo=new JsdUserContactsDo();
                contactsDo.setUserId(userId);
                contactsDo.setContactsMobile(contacts.get("contacts").toString());
                contactsDo.setMxreportUrl(contacts.get("mxreportUrl").toString());
                if(userContactsDo.size()==0){
                    jsdUserContactsService.saveRecord(contactsDo);
                }else {
                    jsdUserContactsService.updateByUserId(contactsDo);
                }
            }
        }catch (Exception e) {
            DingdingUtil.sendMessageByJob(NOTICE_HOST +"，通讯录入库失败！",true);
            logger.error("calcuOverdueRecords.addUserContancts error, userId = "+ userId, e);
        }
    }


    void  collectionPush(List<JsdBorrowCashDo> list){
        if(list.size() == 0){
            logger.error("collectionPush needn't, list size is 0");
            return;
        }

        List<Map<String,String>>  data = new ArrayList<>();
        Map<String,String>  param = new HashMap<>();
        JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.COLLECT.name(),ResourceSecType.COLLECT_PRODUCT.name());
        for(JsdBorrowCashDo borrowCashDo : list){
            try {
                //--------------------start  催收上报接口需要参数---------------------------
                Long borrowId = borrowCashDo.getRid();
                logger.info("collectionPush is borrowId = " + borrowId);
                //搭售商品信息
                JsdBorrowLegalOrderDo jsdBorrowLegalOrder = jsdBorrowLegalOrderDao.getLastValidOrderByBorrowId(borrowId);
                JsdUserDo userDo= jsdUserService.getById(jsdBorrowLegalOrder.getUserId());
                Map<String, String> buildData = new HashMap<String, String>();
                param.put("borrowNo",borrowCashDo.getTradeNoXgxy());
                HashMap<String,String> map = xgxyUtil.borrowNoticeRequest(param);
                if(jsdBorrowLegalOrder != null){
                    buildData.put("goodsName",jsdBorrowLegalOrder.getGoodsName());//商品名称
                    buildData.put("goodsPrice",String.valueOf(jsdBorrowLegalOrder.getPriceAmount()));//商品价格
                    buildData.put("orderStatus",map.get("orderStatus"));//订单状态
                    buildData.put("expressNo",map.get("shipperNumber"));//快递单号
                    buildData.put("expressCompany",map.get("shipperName"));//物流公司
                    buildData.put("consigneeName",map.get("consignee"));//收货人姓名
                    buildData.put("consigneePhone",map.get("mobile"));//收货人手机号码
                    buildData.put("consigneeAddress",map.get("fullAddress"));//收货地址
                    buildData.put("deliveryTime",map.get("gmtSended"));//发货时间
                    buildData.put("gmtConfirmReceived",map.get("gmtReceived"));//确定收货时间
                    buildData.put("logisticsInfo",map.get("traces"));//物流信息
                    buildData.put("idNumberAddress",map.get("idNumberAddress")==null?userDo.getAddress():map.get("idNumberAddress").toString());//户籍地址
                    buildData.put("company",map.get("company")==null?"":map.get("company").toString());//公司单位
                    buildData.put("job",map.get("job")==null?"":map.get("job").toString());//job
                    buildData.put("marriageState",map.get("marriageState")==null?"":map.get("marriageState").toString());//N未婚Y已婚P订婚
                    buildData.put("companyTelephone",map.get("companyTelephone")==null?"":map.get("companyTelephone").toString());//单位电话
                    buildData.put("salary",map.get("salary")==null?"":map.get("salary").toString());//税前收入
                    buildData.put("channelName",map.get("channelName")==null?"":map.get("channelName").toString());//渠道名称
                    buildData.put("faceUrl",map.get("faceUrl")==null?"":map.get("faceUrl").toString());//人脸识别图片
                    buildData.put("idBehindUrl",map.get("idBehindUrl")==null?"":map.get("idBehindUrl").toString());//身份证反面照片
                    buildData.put("idFrontUrl",map.get("idFrontUrl")==null?"":map.get("idFrontUrl").toString());//身份证正面照片
                    buildData.put("borrowAddress",map.get("borrowAddress")==null?"":String.valueOf(map.get("borrowAddress")));//借款详细地址
                    buildData.put("latitude",map.get("latitude")==null?"":String.valueOf(map.get("latitude")));//借款纬度
                    buildData.put("longitude",map.get("longitude")==null?"":String.valueOf(map.get("longitude")));//借款经度
                }
                //用户信息
                if(userDo != null){
                    buildData.put("userId",String.valueOf(userDo.getRid()));//userId
                    buildData.put("realName",userDo.getRealName());//姓名
                    buildData.put("userName",userDo.getMobile());//账号
                    buildData.put("idNumber",userDo.getIdNumber());//身份证号码
                    buildData.put("phoneNumber",userDo.getMobile());//电话号码
                    buildData.put("address",userDo.getAddress());//户籍地址
                    String gender = userDo.getGender();
                    if(StringUtil.equals(gender, GenderType.M.getCode())){
                        gender = GenderType.M.getName();
                    }else if(StringUtil.equals(gender,GenderType.F.getCode())){
                        gender = GenderType.F.getName();
                    }else {
                        gender = GenderType.U.getName();
                    }
                    buildData.put("gender",gender);//性别(非必填)
                    buildData.put("birthday",userDo.getBirthday());//生日(非必填)
                }
                //案件信息
                JsdBorrowLegalOrderCashDo orderCashDo = jsdBorrowLegalOrderCashService.getBorrowLegalOrderCashByOrderId(jsdBorrowLegalOrder.getRid());

                BigDecimal repayAmount = BigDecimal.ZERO;
                BigDecimal residueAmount = BigDecimal.ZERO;//应还金额
                BigDecimal currentAmount = BigDecimal.ZERO;//应还本金
                BigDecimal overdueAmount = BigDecimal.ZERO;//逾期金额
                BigDecimal noReducteAmount = BigDecimal.ZERO;//不可减免金额
                //应还本金
                currentAmount = BigDecimalUtil.add(borrowCashDo.getAmount(), borrowCashDo.getSumRepaidInterest(), borrowCashDo.getSumRepaidPoundage(), borrowCashDo.getSumRepaidOverdue()).subtract(borrowCashDo.getRepayAmount());
                //催收金额
                BigDecimal collectAmount = BigDecimalUtil.add(borrowCashDo.getAmount(),borrowCashDo.getOverdueAmount(),borrowCashDo.getInterestAmount(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidInterest(),borrowCashDo.getSumRepaidOverdue(),borrowCashDo.getSumRepaidPoundage());
                //应还金额
                residueAmount = collectAmount.subtract(borrowCashDo.getRepayAmount());
                //借款费用
                BigDecimal borrowCash = BigDecimalUtil.add(borrowCashDo.getInterestAmount(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidPoundage(),borrowCashDo.getSumRepaidInterest());
                //逾期金额
                overdueAmount = borrowCashDo.getOverdueAmount();
                //还款金额
                repayAmount = borrowCashDo.getRepayAmount();
                //借款金额
                BigDecimal borrowAmount = borrowCashDo.getAmount();
                //滞纳金
                BigDecimal lateFee = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(),borrowCashDo.getSumRepaidOverdue());
                //不可减免金额
                noReducteAmount = BigDecimalUtil.add(borrowCashDo.getInterestAmount(),borrowCashDo.getSumRepaidInterest(),borrowCashDo.getPoundageAmount(),borrowCashDo.getSumRepaidPoundage(),borrowCashDo.getAmount());
                if(orderCashDo != null){
                    //应还本金
                    currentAmount = BigDecimalUtil.add(currentAmount, orderCashDo.getAmount(), orderCashDo.getSumRepaidInterest(), orderCashDo.getSumRepaidPoundage(), orderCashDo.getSumRepaidOverdue()).subtract(orderCashDo.getRepaidAmount());
                    //催收金额
                    collectAmount = BigDecimalUtil.add(collectAmount,orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getInterestAmount(),orderCashDo.getPoundageAmount(),orderCashDo.getOverdueAmount());
                    //应还金额
                    residueAmount = BigDecimalUtil.add(residueAmount,orderCashDo.getAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidOverdue(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getInterestAmount(),orderCashDo.getPoundageAmount(),orderCashDo.getOverdueAmount()).subtract(orderCashDo.getRepaidAmount());
                    //借款费用
                    borrowCash = BigDecimalUtil.add(borrowCash,orderCashDo.getPoundageAmount(),orderCashDo.getInterestAmount(),orderCashDo.getSumRepaidPoundage(),orderCashDo.getSumRepaidInterest());
                    //逾期金额
                    overdueAmount = BigDecimalUtil.add(borrowCashDo.getOverdueAmount(), orderCashDo.getOverdueAmount());
                    //还款金额
                    repayAmount = borrowCashDo.getRepayAmount().add(orderCashDo.getRepaidAmount());
                    //借款金额
                    borrowAmount = borrowAmount.add(orderCashDo.getAmount());
                    //滞纳金
                    lateFee = BigDecimalUtil.add(lateFee,orderCashDo.getOverdueAmount(),orderCashDo.getSumRepaidOverdue());
                    //不可减免金额
                    noReducteAmount = BigDecimalUtil.add(noReducteAmount,orderCashDo.getAmount(),orderCashDo.getInterestAmount(),orderCashDo.getPoundageAmount(),orderCashDo.getSumRepaidInterest(),orderCashDo.getSumRepaidPoundage());
                }
                buildData.put("productId",resourceDo.getValue2());//产品id
                buildData.put("caseName",resourceDo.getValue()+"_"+borrowCashDo.getType());//案件名称
                buildData.put("caseType",resourceDo.getValue1());//案件类型
                buildData.put("collectAmount",String.valueOf(collectAmount));//催收金额
                buildData.put("repaymentAmount",String.valueOf(repayAmount));//累计还款金额
                buildData.put("residueAmount",String.valueOf(residueAmount));//剩余应还
                buildData.put("currentAmount",String.valueOf(currentAmount));//委案未还金额
                buildData.put("dataId",String.valueOf(jsdBorrowLegalOrder.getRid()));//源数据id
                buildData.put("planRepaymenTime",DateUtil.formatDateTime(borrowCashDo.getGmtPlanRepayment()));//计划还款时间
                buildData.put("overdueAmount",String.valueOf(overdueAmount));//逾期金额
                buildData.put("noReducteAmount",String.valueOf(noReducteAmount));//不可减免金额
                //借款详情
                buildData.put("borrowNo",borrowCashDo.getBorrowNo());//借款编号
                buildData.put("borrowStatus",borrowCashDo.getStatus());//借款状态
                buildData.put("borrowCycle",borrowCashDo.getType());//借款周期
                buildData.put("cardNumber",borrowCashDo.getCardNumber());//收款账号
                buildData.put("borrowAddress",map.get("borrowAddress"));//借款地址
                buildData.put("longitude",map.get("longitude"));//借款经度
                buildData.put("latitude",map.get("latitude"));//借款纬度
                buildData.put("borrowTime",DateUtil.formatDateTime(borrowCashDo.getGmtCreate()));//借款时间
                buildData.put("overdueDay",String.valueOf(DateUtil.getNumberOfDatesBetween(DateUtil.formatDateToYYYYMMdd(borrowCashDo.getGmtPlanRepayment()),DateUtil.formatDateToYYYYMMdd(new Date()))));//逾期天数
                buildData.put("borrowAmount",String.valueOf(borrowAmount));//借款金额(委案金额)
                buildData.put("accountAmount",String.valueOf(borrowCashDo.getArrivalAmount()));//到账金额
                buildData.put("borrowCash",String.valueOf(borrowCash));//借款费用(手续费加利息)
                buildData.put("appName","jsd");//借款app
                JsdContractPdfDo jsdContractPdfDo = new JsdContractPdfDo();
                jsdContractPdfDo.setType((byte) 4);
                jsdContractPdfDo.setTypeId(borrowCashDo.getRid());
                JsdContractPdfDo contractPdfDo = jsdContractPdfDao.selectByTypeId(jsdContractPdfDo);
                if(null != contractPdfDo){
                    buildData.put("contractPdfUrl",contractPdfDo.getContractPdfUrl());
                }else {
                    buildData.put("contractPdfUrl","");
                }
                buildData.put("payTime",DateUtil.formatDateTime(borrowCashDo.getGmtArrival()));//打款时间
                buildData.put("type","");
                List<Map<String, String>> arrayList = new ArrayList<>();
                List<JsdBorrowCashRenewalDo> renewalList = jsdBorrowCashRenewalService.getJsdRenewalByBorrowIdAndStatus(borrowCashDo.getRid());
                for (JsdBorrowCashRenewalDo renewalDo : renewalList){
                    Map<String, String> renewalData = new HashMap<String, String>();
                    renewalData.put("tradeNo",renewalDo.getTradeNo());//续期编号
                    renewalData.put("renewalAmount",String.valueOf(renewalDo.getRenewalAmount()));//续期本金
                    renewalData.put("renewalRepayAmount",String.valueOf(renewalDo.getCapital()));//本期已还本金（
                    renewalData.put("priorFee",String.valueOf(BigDecimalUtil.add(renewalDo.getPriorOverdue(),renewalDo.getPriorInterest(),renewalDo.getPriorPoundage())));//上期费用
                    renewalData.put("renewalPoundage",String.valueOf(renewalDo.getNextPoundage()));//续期手续费
                    renewalData.put("renewalStatus",renewalDo.getStatus());//状态
                    renewalData.put("renewalTime",DateUtil.formatDateTime(renewalDo.getGmtCreate()));//续期时间
                    arrayList.add(renewalData);
                }
                buildData.put("renewalData",JSON.toJSONString(arrayList));
                buildData.put("lateFee",String.valueOf(lateFee));
                //--------------------end  催收上报接口需要参数---------------------------
                data.add(buildData);
            }catch (Exception e){
                DingdingUtil.sendMessageByJob(NOTICE_HOST +"，逾期通知催收系统失败！",true);
                logger.info("collectionPush is error " + borrowCashDo.getRid(),e);
                e.printStackTrace();
            }

        }
        collectionNoticeUtil.noticeCollectOverdue(data);
    }


    public void addCollectionBorrow(List<JsdBorrowCashDo> list){
        for(JsdBorrowCashDo borrowCashDo : list){
            try {

                Long borrowId = borrowCashDo.getRid();
                JsdCollectionBorrowDo jsdCollectionBorrowDo = jsdCollectionBorrowService.selectByBorrowId(borrowId);
                JsdCollectionBorrowDo borrowDo = new JsdCollectionBorrowDo();
                borrowDo.setBorrowId(borrowCashDo.getRid());
                borrowDo.setReviewStatus(CommonReviewStatus.WAIT.name());
                borrowDo.setStatus(CollectionBorrowStatus.NOTICED.name());
                if(jsdCollectionBorrowDo == null){
                    jsdCollectionBorrowService.saveRecord(borrowDo);
                }
            }catch (Exception e) {
                logger.error("calcuOverdueRecords.addCollectionBorrow error, borrowCashId = " + borrowCashDo.getRid(), e);
            }
        }
    }


    private JsdBorrowCashOverdueLogDo buildLoanOverdueLog(Long borrowId, BigDecimal currentAmount, BigDecimal interest, Long userId, String type){
        JsdBorrowCashOverdueLogDo overdueLog = new JsdBorrowCashOverdueLogDo();
        overdueLog.setBorrowId(borrowId);
        overdueLog.setCurrentAmount(currentAmount);
        overdueLog.setInterest(interest);
        overdueLog.setUserId(userId);
        overdueLog.setType(type);
        return overdueLog;
    }

}
