package com.ald.fanbei.api.web.api.legalborrowV2;

import com.ald.fanbei.api.biz.service.AfRenewalDetailService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfRenewalDetailDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**  
 * @Description: 续期记录
 * @Copyright (c) 浙江阿拉丁电子商务股份有限公司 All Rights Reserved.
 * @author chefeipeng
 * @date 2017年12月20日
 */
@Component("getLegalRenewalListV2Api")
public class GetLegalRenewalListV2Api implements ApiHandle {
	Integer pageNoCount = 20;
	@Resource
	AfRenewalDetailService afRenewalDetailService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		Long borrowId = NumberUtil.objToLongDefault(requestDataVo.getParams().get("borrowId"), 0l);
		Integer pageNo = NumberUtil.objToIntDefault(requestDataVo.getParams().get("pageNo"), 1);

		List<AfRenewalDetailDo> list = afRenewalDetailService.getRenewalListByBorrowId(borrowId, (pageNo - 1) * pageNoCount);

		Map<String, Object> data = new HashMap<String, Object>();
		List<Object> renewalList = new ArrayList<Object>();
		for (AfRenewalDetailDo afRenewalDetailDo : list) {
			renewalList.add(objectWithAfRenewalDetailDo(afRenewalDetailDo));
		}
		data.put("renewalList", renewalList);
		resp.setResponseData(data);
		return resp;
	}

	public Map<String, Object> objectWithAfRenewalDetailDo(AfRenewalDetailDo afRenewalDetailDo) {
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("rid", afRenewalDetailDo.getRid());// 续借id
		data.put("renewalAmount", afRenewalDetailDo.getRenewalAmount());// 续借金额
		data.put("renewalDay", afRenewalDetailDo.getRenewalDay());// 续期天数
		data.put("status", afRenewalDetailDo.getStatus());// 续期状态【A:新建状态，P:处理中, Y:续期成功 , N:续期失败】
		data.put("renewalPayAmount", afRenewalDetailDo.getActualAmount());// 续期应缴费用(续期实付金额)
		data.put("gmtCreate", afRenewalDetailDo.getGmtCreate().getTime());// 申请时间

		return data;

	}

}