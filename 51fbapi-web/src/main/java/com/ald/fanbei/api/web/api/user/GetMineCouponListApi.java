package com.ald.fanbei.api.web.api.user;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfCouponCategoryService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfUserCouponService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.ResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfCouponCategoryDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.dto.AfUserCouponDto;
import com.ald.fanbei.api.dal.domain.query.AfUserCouponQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserCouponVo;

/**
 * 
 *@类描述：getMineCouponListApi
 *@author 何鑫 2017年1月20日  14:40:45
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getMineCouponListApi")
public class GetMineCouponListApi implements ApiHandle{

	@Resource
	private AfUserCouponService afUserCouponService;
	@Resource
	private AfResourceService afResourceService;
	@Resource
	private AfCouponCategoryService afCouponCategoryService;
	
	private static int EXPIRE_DAY = 2;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);

        Long userId = context.getUserId();
        Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
        String status = ObjectUtils.toString(requestDataVo.getParams().get("status"));
        Map<String, Object> data = new HashMap<String, Object>();
        // 获取领券中心URL add by jrb
        List<AfResourceDo>  resourceList = afResourceService.getConfigByTypes(ResourceType.COUPON_CENTER_URL.getCode());
        if(resourceList != null && !resourceList.isEmpty()) {
        	AfResourceDo resourceDo = resourceList.get(0);
        	String couponCenterUrl = resourceDo.getValue();
        	String isShow = resourceDo.getValue1();
        	if("Y".equals(isShow)) {
        		data.put("couponCenterUrl", couponCenterUrl);
        	}
        }
        logger.info("userId=" + userId + ",pageNo=" + pageNo + ",status=" + status);
        
        AfUserCouponQuery query = new AfUserCouponQuery();
        query.setPageNo(pageNo);
        query.setUserId(userId);
        query.setStatus(status);
        List<AfUserCouponDto> couponList = afUserCouponService.getUserCouponByUser(query);
        List<AfUserCouponVo> couponVoList = new ArrayList<AfUserCouponVo>();
        for (AfUserCouponDto afUserCouponDto : couponList) {
        	AfUserCouponVo couponVo = getUserCouponVo(afUserCouponDto);
        	Date gmtEnd = couponVo.getGmtEnd();
        	// 如果当前时间离到期时间小于48小时,则显示即将过期
        	Calendar cal = Calendar.getInstance();
        	cal.add(Calendar.DAY_OF_YEAR, EXPIRE_DAY);
        	Date twoDay = cal.getTime();
        	if(gmtEnd != null){
        		if(twoDay.after(gmtEnd)) {
            		couponVo.setWillExpireStatus("Y");
            	} else {
            		couponVo.setWillExpireStatus("N");
            	}
        	} else {
        		couponVo.setWillExpireStatus("N");
        	}
        	// 查询优惠券所在分类
        	List <AfCouponCategoryDo> couponCategoryList = afCouponCategoryService.getCouponCategoryByCouponId(couponVo.getRid());
        	if(couponCategoryList != null && !couponCategoryList.isEmpty()) {
        		AfCouponCategoryDo afCouponCategoryDo = couponCategoryList.get(0);
        		String shopUrl = afCouponCategoryDo.getUrl();
        		couponVo.setShopUrl(shopUrl);
        	}
        	couponVoList.add(couponVo);
		}
        
        data.put("pageNo", pageNo);
		data.put("couponList", couponVoList);
		resp.setResponseData(data);
		return resp;
	}
	
	private AfUserCouponVo getUserCouponVo(AfUserCouponDto afUserCouponDto){
		AfUserCouponVo couponVo = new AfUserCouponVo();
		couponVo.setAmount(afUserCouponDto.getAmount());
		couponVo.setGmtEnd(afUserCouponDto.getGmtEnd());
		couponVo.setGmtStart(afUserCouponDto.getGmtStart());
		couponVo.setLimitAmount(afUserCouponDto.getLimitAmount());
		couponVo.setName(afUserCouponDto.getName());
		couponVo.setStatus(afUserCouponDto.getStatus());
		couponVo.setUseRule(afUserCouponDto.getUseRule());
		couponVo.setType(afUserCouponDto.getType());
		return couponVo;
	}
	
}
