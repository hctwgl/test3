/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;

/**
 * @类描述：
 * 贷款超市列表
 * @author chengkang 2017年6月3日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLoanSupermarketListApi")
public class GetLoanSupermarketListApi implements ApiHandle {
	Integer pageNoCount = 20;
	@Resource
	AfLoanSupermarketService afLoanSupermarketService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 1);
		List<AfLoanSupermarketDo> list = afLoanSupermarketService.getLoanSupermarketListOrderNo((pageNo - 1) * pageNoCount);
		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> supermarketList = new ArrayList<Object>();
		for (AfLoanSupermarketDo afLoanSupermarketDo : list) {
			supermarketList.add(objectWithAfLoanSupermarketDo(afLoanSupermarketDo));
		}
		data.put("supermarketList", supermarketList);
		data.put("currPageNo", pageNo);
		resp.setResponseData(data);
		return resp;
	}

	public Map<String, Object> objectWithAfLoanSupermarketDo(AfLoanSupermarketDo afLoanSupermarketDo) {
		Map<String, Object> data = new HashMap<String, Object>();
		String linkUrl = afLoanSupermarketDo.getLinkUrl();
		if(StringUtil.isEmpty(linkUrl)){
			linkUrl = "";
		}else{
			linkUrl = linkUrl.replaceAll("\\*", "\\&");
		}
		data.put("lsmNo", afLoanSupermarketDo.getLsmNo());
		data.put("iconUrl", afLoanSupermarketDo.getIconUrl());
		data.put("lsmName", afLoanSupermarketDo.getLsmName());
		data.put("lsmIntro", afLoanSupermarketDo.getLsmIntro());
		data.put("linkUrl", linkUrl);
		data.put("label", afLoanSupermarketDo.getLabel());
		return data;

	}

}
