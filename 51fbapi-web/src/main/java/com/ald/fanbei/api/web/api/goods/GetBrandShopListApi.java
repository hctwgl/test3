/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfBrandShopVo;

/**
 * 
 * @类描述：品牌店铺列表
 * @author hexin 2017年3月1日下午16:30:20
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBrandShopListApi")
public class GetBrandShopListApi implements ApiHandle {

	@Resource
	private AfResourceService afResourceService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		List<AfResourceDo> list = afResourceService.getConfigByTypes(Constants.RES_BRAND_SHOP);
		resp.setResponseData(getBrandShopVo(list));
		return resp;
	}
	
	private Map<String,Object> getBrandShopVo(List<AfResourceDo> list){
		List<AfBrandShopVo> brandList = new ArrayList<AfBrandShopVo>();
		Map<String,Object> map = new HashMap<String,Object>();
		for (AfResourceDo brand : list) {
			if(StringUtil.isNotBlank(brand.getValue4())){
				AfBrandShopVo vo = new AfBrandShopVo();
				vo.setBrandName(brand.getName());
				vo.setBrandIcon(brand.getValue());
				vo.setBrandRate(brand.getValue3());
				vo.setBrandUrl(brand.getValue2());
				brandList.add(vo);
			}
		}
		map.put("brandList", brandList);
		return map;
	}
}
