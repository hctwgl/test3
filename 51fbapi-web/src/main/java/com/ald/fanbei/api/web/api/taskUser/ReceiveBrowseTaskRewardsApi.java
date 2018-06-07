package com.ald.fanbei.api.web.api.taskUser;

import com.ald.fanbei.api.biz.service.AfBoluomeActivityUserItemsService;
import com.ald.fanbei.api.biz.service.AfFacescoreShareCountService;
import com.ald.fanbei.api.biz.service.AfGameChanceService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.web.common.*;
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 *@类现描述：浏览商品领取奖励
 *@author cfp 2017年6月7日 下午4:08:18
 *@version
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@NeedLogin
@Controller("receiveBrowseTaskRewardsApi")
public class ReceiveBrowseTaskRewardsApi  implements ApiHandle {

	@Resource
	AfTaskUserService afTaskUserService;

	@Resource
	TransactionTemplate transactionTemplate;


	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.FAILED);
		Map<String, Object> data = Maps.newHashMap();
		final String taskUserIds = ObjectUtils.toString(requestDataVo.getParams().get("taskUserIds"));
		if(StringUtils.isEmpty(taskUserIds)){
			data.put("message", "没有奖励信息");
			resp.setResponseData(data);
			return resp;
		}

		String resultMessage = transactionTemplate.execute(new TransactionCallback<String>() {
			@Override
			public String doInTransaction(TransactionStatus status) {
				String resultMessage;
				try {
					List<Long> taskUserIdList = Lists.newArrayList();
					String[] taskUserIdAray = taskUserIds.split(",");
					for (String id : taskUserIdAray) {
						taskUserIdList.add(Long.parseLong(id));
					}
					List<AfTaskUserDo> taskUserList = afTaskUserService.getTaskUserListByIds(taskUserIdList);
					int rewardType = afTaskUserService.isSameRewardType(taskUserList);
					if (-1 == rewardType) {
						resultMessage = "任务已经过期啦" + "\r\n" +"快去完成新任务吧";
					} else if (-2 == rewardType) {
						afTaskUserService.batchUpdateTaskUserStatus(taskUserIdList);
						afTaskUserService.saveCouponRewardAndUpdateAccount(taskUserList);
						resultMessage = "奖励已放入您的账户" + "\r\n" +"继续逛逛能得到更多哦!";
					} else {
						afTaskUserService.batchUpdateTaskUserStatus(taskUserIdList);
						afTaskUserService.saveCouponRewardAndUpdateAccount(taskUserList);
						resultMessage = afTaskUserService.getRewardAmount(taskUserList, rewardType);
					}

				}catch(Exception e){
					resultMessage = "系统异常";
					logger.error("receiveBrowseTaskRewards error, ", e);
					status.setRollbackOnly();
				}

				return resultMessage;
			}
		});

		if(StringUtils.isEmpty(resultMessage) || StringUtils.equals("系统异常", resultMessage) || StringUtils.equals("任务已经过期啦，快去完成新任务吧", resultMessage)){
			data.put("message", resultMessage);
			resp.setResponseData(data);
		}
		else{
			resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
			data.put("message", resultMessage);
			resp.setResponseData(data);
		}
		return resp;
	}




}
