/**
 * 
 */
package com.ald.fanbei.api.web.api.clickAmount;

import com.ald.fanbei.api.biz.service.AfBusinessAccessRecordsService;
import com.ald.fanbei.api.biz.service.AfPopupsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.RedisLock;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
import com.ald.fanbei.api.common.enums.OrderType;
import com.ald.fanbei.api.common.enums.ThirdPartyLinkType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.*;
import com.ald.fanbei.api.dal.dao.AfUserDao;
import com.ald.fanbei.api.dal.domain.AfBusinessAccessRecordsDo;
import com.ald.fanbei.api.dal.domain.AfPopupsDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
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
@Component("clickAmountNumApi")
public class ClickAmountNumApi implements ApiHandle{


	@Resource
	AfBusinessAccessRecordsService afBusinessAccessRecordsService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfPopupsService afPopupsService;
	@Resource
	RedisLock redisLock;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long id = NumberUtil.objToLongDefault(requestDataVo.getParams().get("popupsId"), null);
		String userType = ObjectUtils.toString(requestDataVo.getParams().get("userType"), "").toString();
		String amountType = ObjectUtils.toString(requestDataVo.getParams().get("amountType"), "").toString();
		if(!("".equals(userType)) && !("4".equals(userType))){
				AfPopupsDo afPopupsDo = afPopupsService.selectPopups(id);
			if (null == amountType || "".equals(amountType)){
				if(afPopupsDo!=null && StringUtil.isNotBlank(afPopupsDo.getUrl())){
					afPopupsService.updatePopups(afPopupsDo);
				}else{
					logger.error("首页极光推送跳转失败，popupsId："+id);
				}
			}else if ("reachAmount".equals(amountType)){
				if(afPopupsDo!=null && StringUtil.isNotBlank(afPopupsDo.getUrl())){
					afPopupsService.updatePopupsReachAmount(afPopupsDo.getId());
				}else{
					logger.error("首页极光推送跳转失败，popupsId："+id);
				}
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
