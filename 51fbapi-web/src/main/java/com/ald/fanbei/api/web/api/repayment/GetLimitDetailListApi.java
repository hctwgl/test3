package com.ald.fanbei.api.web.api.repayment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.UserAccountLogType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.dto.AfLimitDetailDto;
import com.ald.fanbei.api.dal.domain.query.AfLimitDetailQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfLimitDetailVo;

/**
 * 
 *@类描述：获取明细列表Api
 *@author 何鑫 2017年2月23日  14:24:37
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getLimitDetailListApi")
public class GetLimitDetailListApi implements ApiHandle{

	@Resource
	private AfUserAccountService afUserAccountService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,
			FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);
		Long userId = context.getUserId();
		int pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
		AfLimitDetailQuery query = new AfLimitDetailQuery();
		query.setUserId(userId);
		query.setPageNo(pageNo);
		List<AfLimitDetailDto> detailList = afUserAccountService.getLimitDetailList(query);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("limitList", getLimitDetailVoList(detailList));
		map.put("pageNo", pageNo);
		resp.setResponseData(map);
		return resp;
	}
	
	private List<AfLimitDetailVo> getLimitDetailVoList(List<AfLimitDetailDto> detailList){
		List<AfLimitDetailVo> detailVoList = new ArrayList<AfLimitDetailVo>();
		for (AfLimitDetailDto afLimitDetailDto : detailList) {
			AfLimitDetailVo vo= new AfLimitDetailVo();
			vo.setGmtCreate(afLimitDetailDto.getGmtCreate());
			vo.setRefId(afLimitDetailDto.getRefId());
			vo.setType(afLimitDetailDto.getType());
			if(afLimitDetailDto.getType().equals(UserAccountLogType.REPAYMENT.getCode())){
				vo.setAmount(afLimitDetailDto.getRepaymentAmount());
				vo.setLimitNo(afLimitDetailDto.getRepaymentNo());
				vo.setName(afLimitDetailDto.getRepaymentName());
			}else{
				vo.setAmount(afLimitDetailDto.getBorrowAmount());
				vo.setLimitNo(afLimitDetailDto.getBorrowNo());
				vo.setName(afLimitDetailDto.getBorrowName());
			}
			detailVoList.add(vo);
		}
		return detailVoList;
	}
	
}
