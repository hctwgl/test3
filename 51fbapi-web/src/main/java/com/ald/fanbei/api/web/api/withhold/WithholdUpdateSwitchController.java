package com.ald.fanbei.api.web.api.withhold;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.service.AfUserWithholdService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiWebContext;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.JsonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityUserItemsDo;
import com.ald.fanbei.api.dal.domain.AfUserBankcardDo;
import com.ald.fanbei.api.dal.domain.AfUserDo;
import com.ald.fanbei.api.dal.domain.AfUserWithholdDo;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;

/**
 * @类描述:
 * @author fanmanfu 创建时间：2017年11月6日 下午1:20:42
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller("updateWithholdSwitchApi")
public class WithholdUpdateSwitchController  implements ApiHandle {

	@Resource
	AfUserWithholdService afUserWithholdService;
	@Resource
	AfUserService afUserService;
	@Resource
	AfUserBankcardService afUserBankCardService;

	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		String userName = context.getUserName();
		String IsSwitch = ObjectUtils.toString(requestDataVo.getParams().get("isSwitch"));
		
		AfUserDo userDo = afUserService.getUserByUserName(userName);
		AfUserWithholdDo afUserWithholdDo = afUserWithholdService.getAfUserWithholdDtoByUserId(userDo.getRid());
		if (afUserWithholdDo == null) {
				AfUserBankcardDo mainBankcard = afUserBankCardService.getUserMainBankcardByUserId(userDo.getRid());
				
				AfUserWithholdDo userWithholdDo = new AfUserWithholdDo();
				try {
					userWithholdDo.setUserName(userDo.getUserName());
					userWithholdDo.setUserId(userDo.getRid());
					userWithholdDo.setCardNumber1(mainBankcard.getCardNumber());
					userWithholdDo.setCardId1(mainBankcard.getRid());
					userWithholdDo.setIsWithhold(Integer.parseInt(IsSwitch));
					afUserWithholdService.insertAfUserWithholdDto(userWithholdDo);
				} catch (Exception e){
					return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SYSTEM_ERROR);
				}
		} else {
			afUserWithholdService.updateAfUserWithholdDtoByUserId(userDo.getRid(), Integer.parseInt(IsSwitch));
		}
		AfUserWithholdDo withholdInfo = afUserWithholdService.getWithholdInfo(userDo.getRid());
		resp.setResponseData(withholdInfo.getIsWithhold());
		return resp;
	}
	
}
