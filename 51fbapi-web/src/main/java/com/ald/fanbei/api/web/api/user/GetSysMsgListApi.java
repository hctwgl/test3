package com.ald.fanbei.api.web.api.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import com.ald.fanbei.api.biz.service.AfSysMsgService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.dal.domain.AfSysMsgDo;
import com.ald.fanbei.api.dal.domain.query.AfSysMsgQuery;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.ald.fanbei.api.web.vo.AfSysMsgVo;

/**
 * 
 *@类描述：getSysMsgListApi
 *@author 何鑫 2017年1月19日  20:28:32
 *@注意：本内容仅限于浙江阿拉丁电子商务股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getSysMsgListApi")
public class GetSysMsgListApi implements ApiHandle{

	@Resource
	private AfSysMsgService afSysMsgService;
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo,FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(),FanbeiExceptionCode.SUCCESS);

        Long userId = context.getUserId();
        Integer pageNo = NumberUtil.objToIntDefault(ObjectUtils.toString(requestDataVo.getParams().get("pageNo")), 1);
		
        logger.info("userId=" + userId + ",pageNo=" + pageNo);
        
        AfSysMsgQuery query = new AfSysMsgQuery();
        query.setUserId(userId);
        query.setPageNo(pageNo);
        List<AfSysMsgDo> sysMsgList = afSysMsgService.getSysMsgList(query);
        List<AfSysMsgVo> sysMsgVoList = new ArrayList<AfSysMsgVo>();
        for (AfSysMsgDo sysMsg : sysMsgList) {
        	AfSysMsgVo sysMsgVo = getAfSysMsgVo(sysMsg);
        	sysMsgVoList.add(sysMsgVo);
		}
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("pageNo", pageNo);
		data.put("messageList", sysMsgVoList);
		resp.setResponseData(data);
		return resp;
	}
	
	private AfSysMsgVo getAfSysMsgVo(AfSysMsgDo sysMsg){
		
		AfSysMsgVo sysMsgVo = new AfSysMsgVo();
		sysMsgVo.setContent(sysMsg.getContent());
		sysMsgVo.setNoticeTime(sysMsg.getGmtCreate());
		sysMsgVo.setRid(sysMsg.getRid());
		sysMsgVo.setTitle(sysMsg.getTitle());
		sysMsgVo.setUserId(sysMsg.getUserId());
		
		return sysMsgVo;
	}

}
