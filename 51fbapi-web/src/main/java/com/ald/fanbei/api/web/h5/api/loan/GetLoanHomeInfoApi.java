package com.ald.fanbei.api.web.h5.api.loan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfBorrowLegalService;
import com.ald.fanbei.api.biz.service.AfLoanService;
import com.ald.fanbei.api.biz.service.AfRecommendUserService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.context.Context;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.H5Handle;
import com.ald.fanbei.api.web.common.H5HandleResponse;
import com.google.common.collect.Maps;

/**
 * 获取借钱首页信息
 * @author ZJF
 * @类描述：申请贷款 参考{@link com.ald.fanbei.api.web.api.legalborrowV2.GetLegalBorrowCashHomeInfoV2Api}
 */
@Component("getLoanHomeInfoApi")
public class GetLoanHomeInfoApi implements H5Handle {

	@Resource
	private AfBorrowLegalService afBorrowLegalService;
	@Resource
	private AfLoanService afLoanService;
	@Resource
	private AfRecommendUserService afRecommendUserService;
	@Resource
	private AfResourceService afResourceService;
	
	@Override
	public H5HandleResponse process(Context context) {
		H5HandleResponse resp = new H5HandleResponse(context.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		
		resp.addResponseData("bannerList", getBannerList(context));
		resp.addResponseData("loanInfos", afLoanService.getHomeInfo(userId));
		resp.addResponseData("xdInfo", afBorrowLegalService.getHomeInfo(userId));
		
		return resp;
	}
	
	private List<Object> getBannerList(Context context) {
		List<Object> bannerList = new ArrayList<Object>();

		String type = ConfigProperties.get(Constants.CONFKEY_INVELOMENT_TYPE);
		if (Constants.INVELOMENT_TYPE_ONLINE.equals(type) || Constants.INVELOMENT_TYPE_TEST.equals(type)) {//生产和测试环境
			bannerList = getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowTopBanner.getCode()));
		} else if (Constants.INVELOMENT_TYPE_PRE_ENV.equals(type)) { 	//预发环境
			bannerList = getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderByOnPreEnv(AfResourceType.BorrowTopBanner.getCode()));
		}
		
		return bannerList;
	}
	
	private List<Object> getBannerObjectWithResourceDolist(List<AfResourceDo> bannerResclist) {
		List<Object> bannerList = new ArrayList<Object>();
		for (AfResourceDo afResourceDo : bannerResclist) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("imageUrl", afResourceDo.getValue());
			data.put("titleName", afResourceDo.getName());
			data.put("type", afResourceDo.getValue1());
			data.put("content", afResourceDo.getValue2());
			data.put("sort", afResourceDo.getSort());

			bannerList.add(data);
		}
		return bannerList;
	}

	
}