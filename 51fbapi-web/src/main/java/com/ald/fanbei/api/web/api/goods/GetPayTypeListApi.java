/**
 * 
 */
package com.ald.fanbei.api.web.api.goods;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfuserCollectionService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfUserCollectionDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述：获取支付方式列表
 * @author chengkang 2017年6月16日下午21:27:31
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getPayTypeListApi")
public class GetPayTypeListApi implements ApiHandle {
	@Resource
	AfuserCollectionService afuserCollectionService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		if (userId == null) {
			throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
		}
		Map<String, Object> data = new HashMap<String, Object>();

		if (context.getAppVersion() < 350) {
			List<AfUserCollectionDo> list= afuserCollectionService.getUserGoodsIdCollectionListByUserId(userId);
			data.put("collectionList", JSON.toJSON(list));

		}else{
			List<AfUserCollectionDo> list= afuserCollectionService.getUserCollectionListByUserId(userId);
			
			data.put("collectionList", JSON.toJSON(list));
		}
		
		resp.setResponseData(data);

		return resp;
	}
	
	

}
