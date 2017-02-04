package com.ald.fanbei.api.web.api.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserFootmarkService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfUserFootmarkDo;
import com.ald.fanbei.api.dal.domain.query.AfUserFootmarkQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfUserFootmarkVo;

/**
 * 
 *@类描述：GetFootMarkListApi
 *@author 何鑫 2017年1月19日  16:20:21
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getFootMarkListApi")
public class GetFootMarkListApi implements ApiHandle{

	@Resource
	private AfUserFootmarkService afUserFootmarkService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);

        Long userId = context.getUserId();
        Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
		
        logger.info("userId=" + userId + ",pageNo=" + pageNo);
        AfUserFootmarkQuery query = new AfUserFootmarkQuery();
        query.setUserId(userId);
        query.setPageNo(pageNo);
        List<AfUserFootmarkDo> footmarkList = afUserFootmarkService.getUserFootmarkList(query);
        List<AfUserFootmarkVo> footmarkVoList = new ArrayList<AfUserFootmarkVo>();
        for (AfUserFootmarkDo afUserFootmark : footmarkList) {
        	AfUserFootmarkVo afUserFootmarkVo = getUserFootmarkVo(afUserFootmark);
        	footmarkVoList.add(afUserFootmarkVo);
		}
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("pageNo", pageNo);
		data.put("footMarkList", footmarkVoList);
		resp.setResponseData(data);
		return resp;
	}

	private AfUserFootmarkVo getUserFootmarkVo(AfUserFootmarkDo afUserFootmark){
		AfUserFootmarkVo afUserFootmarkVo = new AfUserFootmarkVo();
		afUserFootmarkVo.setGoodsId(afUserFootmark.getGoodsId());
		afUserFootmarkVo.setGoodsName(afUserFootmark.getGoodsName());
		afUserFootmarkVo.setGoodsSource(afUserFootmark.getSource());
		afUserFootmarkVo.setGoodsIcon(afUserFootmark.getGoodsIcon());
		afUserFootmarkVo.setPriceAmount(afUserFootmark.getPriceAmount());
		afUserFootmarkVo.setCommissionAmount(afUserFootmark.getCommissionAmount());
		afUserFootmarkVo.setCommissionRate(afUserFootmark.getCommissionRate());
		afUserFootmarkVo.setGmtStart(afUserFootmark.getGmtCreate());
		afUserFootmarkVo.setGmtEnd(afUserFootmark.getGmtModified());
		return afUserFootmarkVo;
	}
}
