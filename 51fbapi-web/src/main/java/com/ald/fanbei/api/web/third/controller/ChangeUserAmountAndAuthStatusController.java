package com.ald.fanbei.api.web.third.controller;

import java.math.BigDecimal;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyReqBo;
import com.ald.fanbei.api.biz.bo.CollectionOperatorNotifyRespBo;
import com.ald.fanbei.api.biz.service.AfUserAccountSenceService;
import com.ald.fanbei.api.biz.service.AfUserAuthStatusService;
import com.ald.fanbei.api.biz.third.AbstractThird;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.util.BigDecimalUtil;
import com.ald.fanbei.api.common.util.DigestUtil;
import com.ald.fanbei.api.common.util.NumberUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfUserAccountSenceDo;
import com.ald.fanbei.api.dal.domain.AfUserAuthStatusDo;
import com.ald.fanbei.api.web.common.BaseController;
import com.ald.fanbei.api.web.common.BaseResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/** 
 * @类描述:风控系统人工修改状态和额度后推送给51
 * @author fanmanfu 创建时间：2018年03月08日 下午10:50:42 
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Controller
@RequestMapping("/third/risk")
public class ChangeUserAmountAndAuthStatusController extends AbstractThird {

	@Resource
	AfUserAccountSenceService afUserAccountSenceService;
	@Resource
	AfUserAuthStatusService afUserAuthStatusService;
	
	
	 /**
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = {"/ChangeUserAmountAndAuthStatus"}, method = RequestMethod.POST)
    @ResponseBody
	public CollectionOperatorNotifyRespBo ChangeUserAmountAndAuthStatus(HttpServletRequest request, HttpServletResponse response) {
    	  Long userId = Long.parseLong(request.getParameter("userId").toString());
          String status = ObjectUtils.toString(request.getParameter("status"));
          BigDecimal auAmount = NumberUtil.objToBigDecimalDefault(request.getParameter("auAmount").toString(),BigDecimal.ZERO);
          String scene = ObjectUtils.toString(request.getParameter("scene"));
          String isAuth = ObjectUtils.toString(request.getParameter("isAuth"));
          logger.info("ChangeUserAmountAndAuthStatus begin,userId=" + userId + ",status=" + status + ",auAmount=" + auAmount+ ",scene=" + scene+ ",isAuth=" + isAuth);
          CollectionOperatorNotifyRespBo notifyRespBo = new CollectionOperatorNotifyRespBo();
         try{
	          if ("1".equals(isAuth)) {  //"1"  认证已成功  直接提额
		          AfUserAccountSenceDo userAccountSenceDo = afUserAccountSenceService.getByUserIdAndScene(scene, userId);
		          if(userAccountSenceDo == null) {
		        	  notifyRespBo.setCode("1001");
		        	  notifyRespBo.setMsg("用户额度信息不存在！");
		        	  return notifyRespBo;
		          }
		          afUserAccountSenceService.updateUserSceneAuAmountByScene(scene, userId, auAmount);
	          } else if ("0".equals(isAuth)) {   //"0" 认证失败  修改认证状态  插入额度信息
	        	  AfUserAuthStatusDo afUserAuthStatusDo = afUserAuthStatusService.getAfUserAuthStatusByUserIdAndScene(userId, scene);
	        	  if(afUserAuthStatusDo == null) {
	        		  notifyRespBo.setCode("1002");
		        	  notifyRespBo.setMsg("用户认证信息不存在！");
	        	  }
	        	  if (afUserAuthStatusService.updateAfUserAuthStatusByUserId(userId, scene, status) > 0) {
	        		  afUserAccountSenceService.updateUserSceneAuAmount(scene, userId, auAmount);
	        	  }	        		  
	        	  
	          } else {
	        	  notifyRespBo.setCode("1003");
	        	  notifyRespBo.setMsg("isAuth is Undefined");
	        	  return notifyRespBo;
	          }
	          notifyRespBo.setCode("1000");
	    	  notifyRespBo.setMsg("ChangeUserAmountAndAuthStatus is SUCCESS");
	          return notifyRespBo;
	    }catch(Exception e) {
	    	logger.info(userId+" ChangeUserAmountAndAuthStatus has Exception,e  = "+e);
	    	notifyRespBo.setCode("1004");
	    	notifyRespBo.setMsg("ChangeUserAmountAndAuthStatus is FAIL");
	        return notifyRespBo;
	    }
    }
}
