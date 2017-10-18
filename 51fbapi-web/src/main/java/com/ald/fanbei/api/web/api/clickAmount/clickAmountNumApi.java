/**
 * 
 */
package com.ald.fanbei.api.web.api.clickAmount;

import com.ald.fanbei.api.biz.bo.BrandCouponResponseBo;
import com.ald.fanbei.api.biz.bo.BrandUserCouponRequestBo;
import com.ald.fanbei.api.biz.service.AfBusinessAccessRecordsService;
import com.ald.fanbei.api.biz.service.AfPopupsService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfBusinessAccessRecordsRefType;
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
import com.ald.fanbei.api.web.vo.AfBrandCouponVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 
 * @类描述：统计点击量
 * @author chefeipeng 2017年10月28日下午3:40:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("clickAmountNumApi")
public class clickAmountNumApi implements ApiHandle{


	@Resource
	AfBusinessAccessRecordsService afBusinessAccessRecordsService;
	@Resource
	AfResourceService afResourceService;
	@Resource
	AfUserDao afUserDao;
	@Resource
	AfPopupsService afPopupsService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		AfUserDo afUserDo = afUserDao.getUserByUserName(context.getUserName());
		String id = request.getParameter("popupsId");
		AfPopupsDo afPopupsDo = afPopupsService.selectPopups(Long.valueOf(id).longValue());
		if(afPopupsDo!=null && StringUtil.isNotBlank(afPopupsDo.getUrl())){
			String sysModeId = "";
			String channel = getChannel(sysModeId);
			String extraInfo = "sysModeId="+sysModeId+",appVersion="+context.getAppVersion()+",Name="+afPopupsDo.getName()+",accessUrl="+afPopupsDo.getUrl();
			AfBusinessAccessRecordsDo afBusinessAccessRecordsDo = new AfBusinessAccessRecordsDo();
			afBusinessAccessRecordsDo.setUserId(afUserDo.getRid());
			afBusinessAccessRecordsDo.setSourceIp(CommonUtil.getIpAddr(request));
			afBusinessAccessRecordsDo.setRefType(AfBusinessAccessRecordsRefType.LOANSUPERMARKET_BANNER.getCode());
			afBusinessAccessRecordsDo.setRefId(afPopupsDo.getId());
			afBusinessAccessRecordsDo.setExtraInfo(extraInfo);
			afBusinessAccessRecordsDo.setRemark(ThirdPartyLinkType.HOME_POPUP_WND.getCode());
			afBusinessAccessRecordsDo.setChannel(channel);
			afBusinessAccessRecordsDo.setRedirectUrl(afPopupsDo.getUrl());
			afBusinessAccessRecordsService.saveRecord(afBusinessAccessRecordsDo);
			int count = afPopupsDo.getClickAmount()+1;
			afPopupsDo.setClickAmount(count);
			afPopupsService.updatePopups(afPopupsDo);
		}else{
			logger.error("首页极光推送跳转失败，popupsId："+id+"-userId:"+afUserDo.getRid());
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
