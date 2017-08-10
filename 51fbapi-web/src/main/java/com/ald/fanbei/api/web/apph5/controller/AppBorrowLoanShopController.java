package com.ald.fanbei.api.web.apph5.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketTabService;
import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.enums.AfResourceType;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfLoanSupermarketDao;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketTabDo;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.api.borrowCash.GetBorrowCashBase;
import com.ald.fanbei.api.web.api.borrowCash.GetBowCashLogInInfoApi;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfLoanShopVo;
import com.ald.fanbei.api.web.vo.AfLoanTapsAndShops;
import com.ald.fanbei.api.web.vo.AfScrollbarVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Controller
@RequestMapping("/borrow")
public class AppBorrowLoanShopController extends BaseController {

	@Resource
	AfResourceService afResourceService;
	
	@Resource
    private AfLoanSupermarketTabService afLoanSupermarketTabService;
	
    @Resource
    private AfLoanSupermarketDao afLoanSupermarketDao;
    
	/**
	 * @说明：显示借贷超市的页面，包括bannerList，scrollbar，tapList和shopList
	 * @param: @param
	 *             request
	 * @param: @param
	 *             response
	 * @param: @return
	 * @return: String
	 */
	@RequestMapping(value = "/loanShop", method = RequestMethod.POST, produces = "text/html;charset=UTF-8")
	@ResponseBody
	public String getInfoForBorrowLaonShop(HttpServletRequest request, HttpServletResponse response) {
		Calendar calStart = Calendar.getInstance();
		H5CommonResponse resp = H5CommonResponse.getNewInstance();
		FanbeiWebContext context = new FanbeiWebContext();
		try {
			AfResourceDo resourceDo = afResourceService.getScrollbarByType();
			AfScrollbarVo scrollbar = GetBowCashLogInInfoApi.getAfScrollbarVo(resourceDo);
			
			GetBorrowCashBase base = new GetBorrowCashBase();
			List<Object> bannerList =base.getBannerObjectWithResourceDolist(afResourceService.getResourceHomeListByTypeOrderBy(AfResourceType.BorrowShopBanner.getCode()));
			List<AfLoanTapsAndShops> list = new ArrayList<>();
			List<AfLoanSupermarketTabDo> tabList = afLoanSupermarketTabService.getTabList();
			if (tabList != null && tabList.size() > 0 ) {
				list =  convertList(tabList);
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			
			//给轮播拼接地址
			if(bannerList!=null){
				for(Object obj:bannerList){
					Map<String, Object> map = (Map<String, Object>) obj;
					String content = (String) map.get("content");
					if(StringUtils.isNotBlank(content)){
						if(content.contains("=")){
							map.put("content", content+"&linkType=h5LoanBanner");
						}else{
							map.put("content", content+"?linkType=h5LoanBanner");
						}
					}
				}
			}
			
			String contextPath = request.getScheme() +"://" + request.getServerName()  + ":" +request.getServerPort() +request.getContextPath();
			//重新设置linkUrl作为埋点用
			if(list!=null){
				for(AfLoanTapsAndShops loanTapsAndShop:list){
					List<AfLoanShopVo> shopVos = loanTapsAndShop.getLoanShopList();
					if(shopVos!=null){
						for(AfLoanShopVo vo:shopVos){
							vo.setLinkUrl(contextPath+"/fanbei-web/thirdPartyLink?linkType=h5LoanList&lsmNo="+vo.getLsmNo());
						}
					}
				}
			}
			
			data.put("bannerList", bannerList);
			data.put("scrollbar", scrollbar);
			data.put("tabList", list);

			resp = H5CommonResponse.getNewInstance(true, "初始化成功", "", data);
		} catch (FanbeiException e) {
			resp = H5CommonResponse.getNewInstance(false, "获取数据失败", "", e.getErrorCode().getDesc());
			logger.error("获取借贷超市数据失败", e);
		} catch (Exception e) {
			resp = H5CommonResponse.getNewInstance(false, "获取数据失败", "", e.getMessage());
			logger.error("获取借贷超市数据失败" , e);
		} finally {
			Calendar calEnd = Calendar.getInstance();
			doLog(request, resp, context.getAppInfo(), calEnd.getTimeInMillis() - calStart.getTimeInMillis(),
					context.getUserName());
		}
		return resp.toString();

	}
	
	public List<AfLoanTapsAndShops> convertList(List<AfLoanSupermarketTabDo> OldList){
		List<AfLoanTapsAndShops> list = new ArrayList<>();
		if (OldList != null && OldList.size() > 0 ) {
			for(AfLoanSupermarketTabDo old : OldList){
				AfLoanTapsAndShops tapsAndShops = new AfLoanTapsAndShops();
				tapsAndShops.setAlias(old.getAlias());
				tapsAndShops.setName(old.getName());
				tapsAndShops.setSort(old.getSort());
				
				List<AfLoanShopVo> shopList = new ArrayList<>();
				List<AfLoanSupermarketDo> sourceSupermarketList = afLoanSupermarketDao.getLoanSupermarketByLabel(old.getAlias());
				if (sourceSupermarketList != null && sourceSupermarketList.size() > 0 ) {
					for(AfLoanSupermarketDo oldShop :sourceSupermarketList){
						AfLoanShopVo newShop = new AfLoanShopVo();
						newShop.setIconUrl(oldShop.getIconUrl());
						newShop.setLabel(oldShop.getLabel());
						newShop.setLinkUrl(oldShop.getLinkUrl());
						newShop.setLsmIntro(oldShop.getLsmIntro());
						newShop.setLsmName(oldShop.getLsmName());
						newShop.setLsmNo(oldShop.getLsmNo());
						newShop.setMarketPoint(oldShop.getMarketPoint());
						
						shopList.add(newShop);
					}
				}
				tapsAndShops.setLoanShopList(shopList);
				
				list.add(tapsAndShops);
			}
		}
		
		return list;
	}
	
	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request, boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData, HttpServletRequest request) {
		try {
			RequestDataVo reqVo = new RequestDataVo();

			JSONObject jsonObj = JSON.parseObject(requestData);
			reqVo.setId(jsonObj.getString("id"));
			reqVo.setMethod(request.getRequestURI());
			reqVo.setSystem(jsonObj);

			return reqVo;
		} catch (Exception e) {
			throw new FanbeiException("参数格式错误" + e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public BaseResponse doProcess(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
