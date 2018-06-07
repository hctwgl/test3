/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.common.enums.AfTaskType;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.AfGameAwardDo;
import com.ald.fanbei.api.dal.domain.AfGameDo;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.dal.domain.query.AfBusinessAccessRecordQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 贷款超市访问
 * @author chengkang 2017年6月3日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("accessLoanSupermarketApi")
public class AccessLoanSupermarketApi implements ApiHandle  {
	@Resource
	AfLoanSupermarketService afLoanSupermarketService;
	@Resource
	AfBusinessAccessRecordsService afBusinessAccessRecordsService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfGameService afGameService;
	@Resource
	AfGameAwardService afGameAwardService;
	@Resource
	AfTaskUserService afTaskUserService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String lsmNo = ObjectUtils.toString(requestDataVo.getParams().get("lsmNo"));
		AfLoanSupermarketDo afLoanSupermarket  = afLoanSupermarketService.getLoanSupermarketByLsmNo(lsmNo);
		if(afLoanSupermarket!=null && StringUtil.isNotBlank(afLoanSupermarket.getLinkUrl())){
			String accessUrl = afLoanSupermarket.getLinkUrl();
			accessUrl = accessUrl.replaceAll("\\*", "\\&");
			//int isUnionLogin =  afLoanSupermarket.getIsUnionLogin();
			//resp.addResponseData("isUnionLogin", isUnionLogin);
			logger.info("贷款超市请求发起正常，地址："+accessUrl+"-id:"+afLoanSupermarket.getId()+"-名称:"+afLoanSupermarket.getLsmName()+"-userId:"+userId);
			try {
				//访问记入数据库处理
				String extraInfo = "sysModeId="+requestDataVo.getId()+",appVersion="+context.getAppVersion()+",lsmName="+afLoanSupermarket.getLsmName()+",accessUrl="+accessUrl;
				AfBusinessAccessRecordsDo afBusinessAccessRecordsDo = new AfBusinessAccessRecordsDo(userId, CommonUtil.getIpAddr(request), AfBusinessAccessRecordsRefType.LOANSUPERMARKET.getCode(), afLoanSupermarket.getId(), extraInfo);
				String channel = getChannel(requestDataVo.getId());
				afBusinessAccessRecordsDo.setChannel(channel);
				afBusinessAccessRecordsService.saveRecord(afBusinessAccessRecordsDo);
				
				this.sign(userId);

				// add by luoxiao 边逛边赚，点击借贷超市链接送奖励，此需求删除
				// afTaskUserService.taskHandler(context.getUserId(), AfTaskType.VERIFIED.getCode(), null);
				// end by luoxiao
			} catch (Exception e) {
				logger.error("贷款超市访问入库异常-id:"+afLoanSupermarket.getId()+"-名称:"+afLoanSupermarket.getLsmName()+"-userId:"+userId);
			}
		}else{
			logger.error("贷款超市请求发起异常-贷款超市不存在或跳转链接为空，lsmNo："+lsmNo+"-userId:"+userId);
			resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		}
		return resp;
	}
	
	private String getChannel(String sysModeId){
		if(sysModeId!=null) {
            int lastIndex = sysModeId.lastIndexOf("_");
            if (lastIndex!=-1){
                String lasterStr = sysModeId.substring(++lastIndex);
                if(NumberUtils.isNumber(lasterStr))
                {
                	return "www"; //早期不是www后缀
                }
                else{
                	return lasterStr;
                }
            }
        }
        return "";
    }
	
	/**
	 * 签到 
	 * 规则（用户每天逛4个借贷超市算签到，否则不算，已签到的不能重复签到）
	 */
	private void sign(Long userId){
		
		AfGameDo gameDo = afGameService.getByCode("loan_supermaket_sign");
		// 判断活动时间
		Date startDate = gameDo.getGmtStart();
		Date endDate = gameDo.getGmtEnd();
		Date nowDate = new Date();
		if(nowDate.before(startDate) || nowDate.after(endDate)) {
			return; //未开始或者已结束
		}
		
		//判断是否领取过,领取过就活动结束
		AfGameAwardDo gameAward = afGameAwardService.getLoanSignAward(userId, gameDo.getRid());
		if(gameAward!=null){
			return;
		}
		
		//判断今天是否签过到
		boolean signed = afBusinessAccessRecordsService.checkIsSignToday(userId);
		if(signed){
			return; //今天签到过
		}
		
		afBusinessAccessRecordsService.doSign(gameDo,userId);
	}
}
