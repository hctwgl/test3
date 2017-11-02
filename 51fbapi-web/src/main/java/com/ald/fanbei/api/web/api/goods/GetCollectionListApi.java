/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import com.ald.fanbei.api.biz.service.AfResourceService;
import com.ald.fanbei.api.biz.service.AfuserCollectionService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfResourceDo;
import com.ald.fanbei.api.dal.domain.AfUserCollectionDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @类描述：收藏列表
 * @author suweili 2017年2月25日下午2:04:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getCollectionListApi")
public class GetCollectionListApi implements ApiHandle {
	@Resource
	AfuserCollectionService afuserCollectionService;

	@Resource
	AfResourceService afResourceService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> data = new HashMap<String, Object>();

		//爬取商品开关
		AfResourceDo isWorm = afResourceService.getConfigByTypesAndSecType(Constants.THIRD_GOODS_TYPE,Constants.THIRD_GOODS_IS_WORM_SECTYPE);
		String value = "";
		if(null != isWorm){
			value = isWorm.getValue();
		}else{
			value = "0";
		}

		if (context.getAppVersion() < 350) {
			List<AfUserCollectionDo> list= afuserCollectionService.getUserGoodsIdCollectionListByUserId(userId);
			List<AfUserCollectionDo> newList = new ArrayList<>();
			for(AfUserCollectionDo afUserCollectionDo : list){
				afUserCollectionDo.setIsWorm(value);
				newList.add(afUserCollectionDo);
			}
			data.put("collectionList", JSON.toJSON(list));

		}else{
			List<AfUserCollectionDo> list= afuserCollectionService.getUserCollectionListByUserId(userId);
			List<AfUserCollectionDo> newList = new ArrayList<>();
			for(AfUserCollectionDo afUserCollectionDo : list){
				afUserCollectionDo.setIsWorm(value);
				newList.add(afUserCollectionDo);
			}

			data.put("collectionList", JSON.toJSON(newList));
		}
		
		resp.setResponseData(data);

		return resp;
	}
	
	

}
