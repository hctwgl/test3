package com.ald.fanbei.api.web.third.controller;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceSecType;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.enums.afu.ApprovalStatusCode;
import com.ald.fanbei.api.common.enums.afu.LoanStatusCode;
import com.ald.fanbei.api.common.enums.afu.LoanTypeCode;
import com.ald.fanbei.api.common.enums.afu.OverdueStatus;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.RC4_128_V2;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.web.vo.afu.Data;
import com.ald.fanbei.api.web.vo.afu.LoanRecord;
import com.ald.fanbei.api.web.vo.afu.Params;
import com.ald.fanbei.api.web.vo.afu.ParamsFather;
import com.ald.fanbei.api.web.vo.afu.ParamsSon;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：致诚阿福通过调用此接口获取用户信息
 * @author chenxuankai 2017年11月22日17:52:47
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/third")
public class GetPersonInfoController {
	
	@Resource
	private AfUserAccountService afUserAccountService;
	@Resource
	private AfBorrowCashService afBorrowCashService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	@Resource
	private AfResourceService afResourceService;
	protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");

	@RequestMapping(value="/getPersonInfo", method = RequestMethod.POST,produces="text/html;charset=UTF-8")
	@ResponseBody
	public String findPersonInfoByCardNo(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
	    String jsonString = null;
	    String idNo = null;
	    String name = null;
	    
		try {
			//解析第一层params数据
			JSONObject obj = JSON.parseObject(request.getParameter("params"));
			ParamsFather paramsFather = JSONObject.toJavaObject(obj, ParamsFather.class);
			//解析第二层params数据
			String params = paramsFather.getParams();
			//对加密过的params数据进行解密
			String urlDecoder = StringUtil.UrlDecoder(params);//1：urlDecode解码
			//获取rc4秘钥
			String rc4Key = ConfigProperties.get(Constants.YIXIN_AFU_PASSWORD);
		    //3:rc4解密
		    String decode = RC4_128_V2.decode(urlDecoder, rc4Key);
		    JSONObject jsonObj = JSONObject.parseObject(decode);
		    ParamsSon paramsSon = JSONObject.toJavaObject(jsonObj, ParamsSon.class);
		    
			//判断请求的业务类型编号是否为201
			if (StringUtil.equals(paramsSon.getTx(), "201")) {
				//借款人身份证号码
				idNo = paramsSon.getData().getIdNo();
				//借款人姓名
				name = paramsSon.getData().getName();
				//查询缓存
				jsonString = StringUtil.null2Str(bizCacheUtil.getObject(Constants.YIXIN_AFU_SEARCH_KEY+rc4Key+idNo));
				if (StringUtil.isNotBlank(jsonString)) {
					//有缓存，直接返回
					thirdLog.info("yiXin zhiChengAfu search personInfo from redis,idNo = "+idNo+", name="+name+" time = " + new Date());
					return jsonString;
				}else{
					//限定每天查询次数,判断当前查询次数是否超限
					String currDayReqNumsKey = Constants.YIXIN_AFU_SEARCH_KEY+rc4Key+DateUtil.formatDate(new Date());
					Long currDayReqNums = NumberUtil.objToLongDefault(bizCacheUtil.getObject(currDayReqNumsKey), 0L);
					currDayReqNums = currDayReqNums+1;
					
					//查询相关限制配置项
					//单日请求上限，配置开户且大于0时进行有效校验
					Long limitDayTimes = 0L;
					Long maxReturnRows = 0L;
					AfResourceDo afResourceConfigInfo = afResourceService.getConfigByTypesAndSecType(AfResourceType.YIXIN_AFU_SEARCH.getCode(), AfResourceSecType.YIXIN_AFU_SEARCH.getCode());
					if(afResourceConfigInfo!=null && "O".equals(afResourceConfigInfo.getValue4())){
						limitDayTimes = NumberUtil.objToLongDefault(afResourceConfigInfo.getValue2(),0L);
						maxReturnRows = NumberUtil.objToLongDefault(afResourceConfigInfo.getValue3(),0L);
					}
					
					if (limitDayTimes>0 && currDayReqNums > limitDayTimes) {
						map.put("errorCode", "0001");
						map.put("message", "当日查询次数超限!");
						map.put("params", null);
						jsonString = JsonUtil.toJSONString(map);
						return jsonString;
					}
					//没有缓存，走查询
					//根据姓名和身份证号查询借款人id
					AfUserAccountDo userAccount = afUserAccountService.findByIdNo(idNo);
					
					if (userAccount == null || !StringUtil.equals(userAccount.getRealName(), name)) {
						map.put("errorCode", "4101");
						map.put("message", "用户不存在!");
						map.put("params", null);
						jsonString = JsonUtil.toJSONString(map);
						return jsonString;
					}
					long userId = userAccount.getUserId();
					//根据用户ID查询借款表,最多条数控制
					List<AfBorrowCashDo> borrowCashDoList = new ArrayList<AfBorrowCashDo>();
					if(maxReturnRows>0){
						borrowCashDoList = afBorrowCashService.getListByUserId(userId,maxReturnRows);
					}else{
						borrowCashDoList = afBorrowCashService.getListByUserId(userId,null);
					}
					
					//判断用户是否存在借款
					if (borrowCashDoList == null || borrowCashDoList.size() == 0) {
						map.put("errorCode", "0001");
						map.put("message", "用户没有借款信息!");
						map.put("params", null);
						jsonString = JsonUtil.toJSONString(map);
						return jsonString;
					}
					//loanRecord数据的封装
					List<LoanRecord> loanRecordList = new ArrayList<LoanRecord>();
					for (AfBorrowCashDo borrowCashDo : borrowCashDoList) {
						
						//封装第一层数据
						LoanRecord loanRecord = new LoanRecord();
						loanRecord.setName(name);
						loanRecord.setCertNo(idNo);
						
						if (StringUtil.equals(borrowCashDo.getStatus(), "TRANSED") || StringUtil.equals(borrowCashDo.getStatus(), "FINSH")) {
							//取打款时间，并格式化日期类型				
							Date gmtArrival = borrowCashDo.getGmtArrival();				
							loanRecord.setLoanDate(DateUtil.formatDate(gmtArrival));	
							//通过审核的结果码
							loanRecord.setApprovalStatusCode(ApprovalStatusCode.Approved.getCode());
							if (StringUtil.equals(borrowCashDo.getStatus(), "FINSH")) {
								//账单已结清
								loanRecord.setLoanStatusCode(LoanStatusCode.Finish.getCode());
							}else if (borrowCashDo.getOverdueDay() > 0 && StringUtil.equals(borrowCashDo.getStatus(), "TRANSED")) {
								//说明借款人逾期并且未还款
								loanRecord.setLoanStatusCode(LoanStatusCode.Overdue.getCode());
								//借款人逾期总金额
								BigDecimal shouldRepay = borrowCashDo.getAmount().add(borrowCashDo.getRateAmount()).add(borrowCashDo.getOverdueAmount()).add(borrowCashDo.getSumRate()).add(borrowCashDo.getSumOverdue()).subtract(borrowCashDo.getRepayAmount());
								loanRecord.setOverdueAmount(shouldRepay);
							}else {
								//账单正常
								loanRecord.setLoanStatusCode(LoanStatusCode.Normal.getCode());
							}
						}else{
							//取申请时间，并格式化日期类型
							Date gmtCreate = borrowCashDo.getGmtCreate();
							loanRecord.setLoanDate(DateUtil.formatDate(gmtCreate));				
							if (StringUtil.equals(borrowCashDo.getReviewStatus(), "REFUSE") || StringUtil.equals(borrowCashDo.getReviewStatus(), "FBREFUSE")) {
								//审核未通过的结果码,拒贷
								loanRecord.setApprovalStatusCode(ApprovalStatusCode.UnApprove.getCode());
								loanRecord.setLoanStatusCode("");
							}else {
								//审核中结果码
								loanRecord.setApprovalStatusCode(ApprovalStatusCode.Approving.getCode());
								loanRecord.setLoanStatusCode("");
							}
						}
						//期数，默认写死为1
						loanRecord.setPeriods(1);
						//借款金额,
						loanRecord.setLoanAmount(borrowCashDo.getAmount());
						
						
						//借款类型码，默认是信用借款
						loanRecord.setLoanTypeCode(LoanTypeCode.Credit.getCode());
						if (borrowCashDo.getOverdueDay() > 0 && StringUtil.equals(borrowCashDo.getStatus(), "TRANSED")){
							int overdueTotal = borrowCashDo.getOverdueDay().intValue()/30;
							if (borrowCashDo.getOverdueDay() % 30 != 0) {
								overdueTotal += 1;
							}
							if (overdueTotal == 1) {
								loanRecord.setOverdueStatus(OverdueStatus.One.getCode());
							}else if (overdueTotal == 2) {
								loanRecord.setOverdueStatus(OverdueStatus.Two.getCode());
							}else if (overdueTotal == 3) {											
								loanRecord.setOverdueStatus(OverdueStatus.Three.getCode());
							}else if (overdueTotal == 4) {
								if (borrowCashDo.getOverdueDay() % 30 == 0) {
									loanRecord.setOverdueStatus(OverdueStatus.Four.getCode());
								}else {
									loanRecord.setOverdueStatus(OverdueStatus.ThreePlus.getCode());
								}
							}else if (overdueTotal == 5) {
								loanRecord.setOverdueStatus(OverdueStatus.Five.getCode());
							}else if (overdueTotal == 6) {
								loanRecord.setOverdueStatus(OverdueStatus.Six.getCode());
							}else {
								loanRecord.setOverdueStatus(OverdueStatus.SixPlus.getCode());
							}
							loanRecord.setOverdueTotal(overdueTotal);
							if (StringUtil.equals(loanRecord.getOverdueStatus(), "M3+")) {
								loanRecord.setOverdueM3(1);
							}
							if (StringUtil.equals(loanRecord.getOverdueStatus(), "M6+")) {
								loanRecord.setOverdueM6(1);
							}				
						}else {
							//账单结清或者没有逾期，默认返回空串
							loanRecord.setOverdueStatus("");
						}
						
						loanRecordList.add(loanRecord);
					}					
					
					Data data = new Data();
					data.setLoanRecords(loanRecordList);
					
					Params paramsResp = new Params();
					paramsResp.setTx("202");
					paramsResp.setData(data);
					paramsResp.setVersion("V3");
					//将paramsResp转为json
					String paramsRespJson = JsonUtil.toJSONString(paramsResp);
					//对响应的数据加密
					String Rc4Resp = RC4_128_V2.encode(paramsRespJson, rc4Key);
					String urlResp = StringUtil.UrlEncoder(Rc4Resp);
					
					
					map.put("errorCode", "0000");
					map.put("message", "查询成功!");
					map.put("params", urlResp);
					jsonString = JsonUtil.toJSONString(map);
					//将请求次数加入缓存
					bizCacheUtil.saveObject(currDayReqNumsKey, currDayReqNums, Constants.SECOND_OF_ONE_DAY);
					//将数据存入缓存
					bizCacheUtil.saveObject(Constants.YIXIN_AFU_SEARCH_KEY+rc4Key+idNo, jsonString, Constants.SECOND_OF_AN_HOUR_INT);
					thirdLog.info("yiXin zhiChengAfu search personInfo from dataBase success,idNo = "+idNo+", name="+name+" time = " + new Date());
					return jsonString;			
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
			thirdLog.info("yiXin zhiChengAfu search personInfo error, idNo ="+ idNo + ",name="+name+"exception : "+ e + ", time = " + new Date());
		}		
		
		map.put("errorCode", "4005");
		map.put("message", "tx业务码不正确!");
		map.put("params", null);
		jsonString = JsonUtil.toJSONString(map);
		return jsonString;
	}
	
}
