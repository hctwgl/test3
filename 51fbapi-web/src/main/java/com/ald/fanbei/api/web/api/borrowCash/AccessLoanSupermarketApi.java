/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBusinessAccessRecordsService;
import com.ald.fanbei.api.biz.service.AfLoanSupermarketService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
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

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		String lsmNo = ObjectUtils.toString(requestDataVo.getParams().get("lsmNo"));
		AfLoanSupermarketDo afLoanSupermarket  = afLoanSupermarketService.getLoanSupermarketByLsmNo(lsmNo);
		if(afLoanSupermarket!=null && StringUtil.isNotBlank(afLoanSupermarket.getLinkUrl())){
			String accessUrl = afLoanSupermarket.getLinkUrl();
			accessUrl = accessUrl.replaceAll("\\*", "\\&");
			logger.info("贷款超市请求发起正常，地址："+accessUrl+"-id:"+afLoanSupermarket.getId()+"-名称:"+afLoanSupermarket.getLsmName()+"-userId:"+userId);
			try {
				//访问记入数据库处理
				String extraInfo = "sysModeId="+requestDataVo.getId()+",appVersion="+context.getAppVersion()+",lsmName="+afLoanSupermarket.getLsmName()+",accessUrl="+accessUrl;
				AfBusinessAccessRecordsDo afBusinessAccessRecordsDo = new AfBusinessAccessRecordsDo(userId, CommonUtil.getIpAddr(request), AfBusinessAccessRecordsRefType.LOANSUPERMARKET.getCode(), afLoanSupermarket.getId(), extraInfo);
				String channel = getChannel(requestDataVo.getId());
				afBusinessAccessRecordsDo.setChannel(channel);
				afBusinessAccessRecordsService.saveRecord(afBusinessAccessRecordsDo);
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
}
