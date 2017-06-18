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

import com.ald.fanbei.api.biz.service.AfuserCollectionService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfGoodsPayTypeVo;
import com.alibaba.fastjson.JSONObject;

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
		List<AfGoodsPayTypeVo> afGoodsPayList = new ArrayList<AfGoodsPayTypeVo>();
    	afGoodsPayList.add(new AfGoodsPayTypeVo("FBFQ", "返呗分期", "Y"));
    	afGoodsPayList.add(new AfGoodsPayTypeVo("ALI", "支付宝", "N"));
    	afGoodsPayList.add(new AfGoodsPayTypeVo("WX", "微信", "N"));
    	afGoodsPayList.add(new AfGoodsPayTypeVo("BANK", "银行卡", "N"));
		String jsonStr = JSONObject.toJSONString(afGoodsPayList);
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("afGoodsPayList", jsonStr);
		resp.setResponseData(data);
		return resp;
	}
	
	

}
