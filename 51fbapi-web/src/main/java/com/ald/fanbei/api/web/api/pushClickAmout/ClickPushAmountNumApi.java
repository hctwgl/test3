/**
 * 
 */
package com.ald.fanbei.api.web.api.pushClickAmout;

import com.ald.fanbei.api.biz.service.AfPushManageService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfPushManageDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @类描述：统计点击量
 * @author chefeipeng 2017年10月28日下午3:40:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("clickPushAmountNumApi")
public class ClickPushAmountNumApi implements ApiHandle{

	@Resource
	AfPushManageService afPushManageService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("pushId"), null);
		Integer clickType = NumberUtil.objToInteger(requestDataVo.getParams().get("clickType"));
		if( null != id){
			AfPushManageDo afPushManageDo = afPushManageService.getPushById(id);
			if(null != afPushManageDo){
				if (null != clickType && 1 == clickType){
					afPushManageDo.setType("1");
					afPushManageService.updatePushManage(afPushManageDo);
				}else if (null != clickType && 2 == clickType){
					afPushManageDo.setType("2");
					afPushManageService.updatePushManage(afPushManageDo);
				}
			}else{
				logger.error("首页极光推送跳转失败，pushId："+id);
			}
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
					return "www"; //早期不是www后缀，兼容旧版本
				}
				else{
					return lasterStr;
				}
			}
		}
		return "";
	}
	


}
