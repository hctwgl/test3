package com.ald.fanbei.api.web.third.controller;



import java.math.BigDecimal;
import java.text.SimpleDateFormat;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfBorrowCashService;
import com.ald.fanbei.api.biz.service.AfIdNumberService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.afu.ApprovalStatusCode;
import com.ald.fanbei.api.common.enums.afu.LoanStatusCode;
import com.ald.fanbei.api.common.enums.afu.LoanTypeCode;
import com.ald.fanbei.api.common.enums.afu.OverdueStatus;
import com.ald.fanbei.api.common.enums.afu.RiskItemTypeCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.RC4_128_V2;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.web.vo.afu.Data;
import com.ald.fanbei.api.web.vo.afu.LoanRecord;
import com.ald.fanbei.api.web.vo.afu.Params;
import com.ald.fanbei.api.web.vo.afu.ParamsFather;
import com.ald.fanbei.api.web.vo.afu.ParamsSon;
import com.ald.fanbei.api.web.vo.afu.RiskResult;
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
	private AfIdNumberService afIdNumberService;
	@Resource
	private AfBorrowCashService afBorrowCashService;
	@Resource
	private BizCacheUtil bizCacheUtil;
	protected static final Logger thirdLog = LoggerFactory.getLogger("FANBEI_THIRD");

	@RequestMapping(value="/getPersonInfo",produces="text/html;charset=UTF-8")
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
			//读取配置文件，获取rc4秘钥
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
				jsonString = (String) bizCacheUtil.getObject(Constants.YIXIN_AFU_SEARCH_KEY+idNo);
				if (StringUtil.isNotBlank(jsonString)) {
					//有缓存，直接返回
					thirdLog.info("yiXin zhiChengAfu search personInfo from redis,idNo = "+idNo+", name="+name+" time = " + new Date());
					return jsonString;
				}else{
					//没有缓存，走查询
					//根据姓名和身份证号查询借款人id
					long userId = afIdNumberService.findByIdNoAndName(name,idNo);
					if (userId == 0) {
						map.put("errorCode", "4101");
						map.put("message", "用户不存在!");
						map.put("params", null);
						jsonString = JsonUtil.toJSONString(map);
						return jsonString;
					}
					//根据用户ID查询借款表
					AfBorrowCashDo borrowCashDo = afBorrowCashService.getBorrowCashByUserId(userId);
					//判断用户是否存在借款
					if (borrowCashDo == null) {
						map.put("errorCode", "0001");
						map.put("message", "用户没有借款信息!");
						map.put("params", null);
						jsonString = JsonUtil.toJSONString(map);
						return jsonString;
					}
					//封装第一层数据
					LoanRecord loanRecord = new LoanRecord();
					loanRecord.setName(name);
					loanRecord.setCertNo(idNo);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					if (StringUtil.equals(borrowCashDo.getStatus(), "TRANSED") || StringUtil.equals(borrowCashDo.getStatus(), "FINSH")) {
						//取打款时间，并格式化日期类型				
						Date gmtArrival = borrowCashDo.getGmtArrival();				
						loanRecord.setLoanDate(sdf.format(gmtArrival));	
						//通过审核的结果码
						loanRecord.setApprovalStatusCode(ApprovalStatusCode.Approved.getCode());
					}else{
						//取申请时间，并格式化日期类型
						Date gmtCreate = borrowCashDo.getGmtCreate();
						loanRecord.setLoanDate(sdf.format(gmtCreate));				
						if (StringUtil.equals(borrowCashDo.getReviewStatus(), "REFUSE") || StringUtil.equals(borrowCashDo.getReviewStatus(), "FBREFUSE")) {
							//审核未通过的结果码,拒贷
							loanRecord.setApprovalStatusCode(ApprovalStatusCode.UnApprove.getCode());
						}else {
							//审核中结果码
							loanRecord.setApprovalStatusCode(ApprovalStatusCode.Approving.getCode());
						}
					}
					//期数，默认写死为1
					loanRecord.setPeriods(1);
					//借款金额,
					loanRecord.setLoanAmount(borrowCashDo.getAmount());
					
					if (borrowCashDo.getOverdueDay() > 0 && !StringUtil.equals(borrowCashDo.getStatus(), "FINSH")) {
						//说明借款人逾期并且未还款
						loanRecord.setLoanStatusCode(LoanStatusCode.Overdue.getCode());
						//借款人逾期总金额
						BigDecimal shouldRepay = borrowCashDo.getAmount().add(borrowCashDo.getRateAmount()).add(borrowCashDo.getOverdueAmount()).add(borrowCashDo.getSumRate()).add(borrowCashDo.getSumOverdue()).subtract(borrowCashDo.getRepayAmount());
						loanRecord.setOverdueAmount(shouldRepay);
					}else if (StringUtil.equals(borrowCashDo.getStatus(), "FINSH")) {
						//账单已结清
						loanRecord.setLoanStatusCode(LoanStatusCode.Finish.getCode());
					}else {
						//账单正常
						loanRecord.setLoanStatusCode(LoanStatusCode.Normal.getCode());
					}
					//借款类型码，默认是信用借款
					loanRecord.setLoanTypeCode(LoanTypeCode.Credit.getCode());
					if (borrowCashDo.getOverdueDay() > 0){
						int overdueTotal = borrowCashDo.getOverdueDay().intValue()/7;
						if (borrowCashDo.getOverdueDay() % 7 != 0) {
							overdueTotal += 1;
						}
						if (overdueTotal == 1) {
							loanRecord.setOverdueStatus(OverdueStatus.One.getCode());
						}else if (overdueTotal == 2) {
							loanRecord.setOverdueStatus(OverdueStatus.Two.getCode());
						}else if (overdueTotal == 3) {											
							loanRecord.setOverdueStatus(OverdueStatus.Three.getCode());
						}else if (overdueTotal == 4) {
							if (borrowCashDo.getOverdueDay() % 7 == 0) {
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
					}
					//loanRecord数据的封装
					List<LoanRecord> loanRecordList = new ArrayList<LoanRecord>();
					loanRecordList.add(loanRecord);
					
					/** riskResult */
					RiskResult riskResult = new RiskResult();
					riskResult.setRiskItemTypeCode(RiskItemTypeCode.CardNum.getCode());
					riskResult.setRiskItemValue(idNo);
					//根据借款人身份证号查询用户名
					String userName = afIdNumberService.findUserNameByIdNo(idNo);
					if (!StringUtil.equals(userName, name)) {
						riskResult.setRiskDetail("身份证虚假");
						riskResult.setRiskTime(sdf.format(new Date()));
					}
					List<RiskResult> riskResultList = new ArrayList<RiskResult>();
					riskResultList.add(riskResult);
					
					//将List转成json对象
					String loanRecords = JsonUtil.toJSONString(loanRecordList);
					String riskResults = JsonUtil.toJSONString(riskResultList);
					Data data = new Data();
					data.setLoanRecords(loanRecords);
					data.setRiskResults(riskResults);
					//data转为json
					String dataJson = JsonUtil.toJSONString(data);
					
					Params paramsResp = new Params();
					paramsResp.setTx("202");
					paramsResp.setData(dataJson);
					paramsResp.setVersion("V3");
					//将paramsResp转为json
					String paramsRespJson = JsonUtil.toJSONString(paramsResp);
					//对响应的数据加密
					//String Rc4Resp = RC4_128_V2.encode(paramsRespJson, rc4Key);
					String Rc4Resp = RC4_128_V2.encode(paramsRespJson, rc4Key);
					String urlResp = StringUtil.UrlEncoder(Rc4Resp);
					
					
					map.put("errorCode", "0000");
					map.put("message", "查询成功!");
					map.put("params", urlResp);
					jsonString = JsonUtil.toJSONString(map);
					//将数据存入缓存
					bizCacheUtil.saveObject(Constants.YIXIN_AFU_SEARCH_KEY+idNo, jsonString, Constants.SECOND_OF_ONE_DAY);
					thirdLog.info("yiXin zhiChengAfu search personInfo from dataBase success,idNo = "+idNo+", name="+name+" time = " + new Date());
					return jsonString;			
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
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
