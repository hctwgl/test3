/**
 * 
 */
package com.ald.fanbei.api.web.api.borrowCash;

import java.io.IOException;
import java.util.HashMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ald.fanbei.api.biz.service.AfLoanSupermarketService;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.domain.AfLoanSupermarketDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * @类描述：
 * 贷款超市访问
 * @author chengkang 2017年6月3日下午2:25:56
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Controller
@RequestMapping("/loanMarket")
public class AccessLoanSupermarketApi extends BaseController  {
	@Resource
	AfLoanSupermarketService afLoanSupermarketService;

	@RequestMapping(value = { "/accessLoanSupermarket" }, method = RequestMethod.POST,produces="application/json;charset=utf-8")
	public void downloadApp(@RequestBody String body, HttpServletRequest request,HttpServletResponse response) throws IOException {
		request.setCharacterEncoding(Constants.DEFAULT_ENCODE);
        response.setContentType("application/json;charset=utf-8");
        RequestDataVo reqVo = new RequestDataVo();
        JSONObject jsonObj = JSON.parseObject(body);
        reqVo.setParams((jsonObj == null || jsonObj.isEmpty()) ? new HashMap<String,Object>() : jsonObj);
        AfLoanSupermarketDo afLoanSupermarket  = null;
        String userName = "";
        try {
			userName = request.getHeader(Constants.REQ_SYS_NODE_USERNAME);
			String lsmNo = ObjectUtils.toString(reqVo.getParams().get("lsmNo"));
			afLoanSupermarket = afLoanSupermarketService.getLoanSupermarketByLsmNo(lsmNo);
			String accessUrl = afLoanSupermarket.getLinkUrl();
			accessUrl = accessUrl.replaceAll("\\*", "\\&");
			logger.info("贷款超市请求跳转发起:"+accessUrl+"-超市id:"+afLoanSupermarket.getId()+"-名称:"+afLoanSupermarket.getLsmName()+"-userName:"+userName);
			response.sendRedirect(accessUrl);
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("贷款超市请求跳转异常-超市id:"+afLoanSupermarket.getId()+"-userName:"+userName);
			throw new FanbeiException("参数格式错误"+e.getMessage(), FanbeiExceptionCode.REQUEST_PARAM_ERROR);
		}
	}

	@Override
	public String checkCommonParam(String reqData, HttpServletRequest request,
			boolean isForQQ) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RequestDataVo parseRequestData(String requestData,
			HttpServletRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String doProcess(RequestDataVo requestDataVo, FanbeiContext context,
			HttpServletRequest httpServletRequest) {
		// TODO Auto-generated method stub
		return null;
	}

}
