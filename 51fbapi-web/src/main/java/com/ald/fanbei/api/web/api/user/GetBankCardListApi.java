package com.ald.fanbei.api.web.api.user;

import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserAuthService;
import com.ald.fanbei.api.biz.service.AfUserBankcardService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.AesUtil;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.ConfigProperties;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthDo;
import com.ald.fanbei.api.dal.domain.dto.AfBankUserBankDto;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import org.dbunit.util.Base64;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @类描述：
 * @author suweili 2017年2月22日下午8:21:39
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component("getBankCardListApi")
public class GetBankCardListApi implements ApiHandle {

    @Resource
    AfUserAuthService afUserAuthService;
    @Resource
    AfUserBankcardService afUserBankcardService;
    @Resource
    AfUserAccountService afUserAccountService;

    @Override
    public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
	ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);

	Long userId = context.getUserId();
	if (userId == null) {
	    throw new FanbeiException("user id is invalid", FanbeiExceptionCode.PARAM_ERROR);
	}

	Object cardType = requestDataVo.getParams().get("cardType");

	List<AfBankUserBankDto> list = afUserBankcardService.getUserBankcardByUserId(userId, context.getAppVersion(), cardType==null?null:cardType.toString());

	resp.addResponseData("bankCardList", list);
	String bankcardStatus = "N";
	if (CollectionUtil.isNotEmpty(list)) {
	    for (AfBankUserBankDto item : list) {
		if (StringUtil.equals(item.getIsMain(), YesNoStatus.YES.getCode())) {
		    bankcardStatus = "Y";
		    break;
		}
	    }
	}

	AfUserAuthDo afUserAuthDo = afUserAuthService.getUserAuthInfoByUserId(userId);

	AfUserAccountDo userAccount = afUserAccountService.getUserAccountByUserId(userId);
	resp.addResponseData("realName", userAccount.getRealName());
	resp.addResponseData("bankcardStatus", bankcardStatus);
	if (StringUtil.equals(bankcardStatus, YesNoStatus.NO.getCode())) {
	    String publicKey = AesUtil.decrypt(ConfigProperties.get(Constants.CONFKEY_YOUDUN_PUBKEY), ConfigProperties.get(Constants.CONFKEY_AES_KEY));
	    resp.addResponseData("ydKey", publicKey);
	    resp.addResponseData("ydUrl", ConfigProperties.get(Constants.CONFKEY_YOUDUN_NOTIFY));
	    resp.addResponseData("idNumber", Base64.encodeString(userAccount.getIdNumber()));
	}
	resp.addResponseData("faceStatus", afUserAuthDo.getFacesStatus());
	return resp;
    }
}
