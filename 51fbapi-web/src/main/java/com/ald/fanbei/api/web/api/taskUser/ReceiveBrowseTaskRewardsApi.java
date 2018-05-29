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
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

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
@Controller("receiveBrowseTaskRewardsApi")
public class ReceiveBrowseTaskRewardsApi  implements ApiHandle {

	@Resource
	AfTaskUserService afTaskUserService;


	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		try{
			Map<String, Object> data = Maps.newHashMap();
			String taskUserIds = request.getParameter("taskUserIds");
			if (StringUtils.isNotEmpty(taskUserIds)) {
				List<Long> taskUserIdList = Lists.newArrayList();
				String[] taskUserIdAray = taskUserIds.split(",");
				for (String id : taskUserIdAray) {
					taskUserIdList.add(Long.parseLong(id));
				}
				List<AfTaskUserDo> taskUserList = afTaskUserService.getTaskUserListByIds(taskUserIdList);
				int rewardType = afTaskUserService.isSameRewardType(taskUserList);
				if (-1 == rewardType) {
				} else if (-2 == rewardType) {
					data.put("message", "奖励已放入您的账户，继续逛逛能得到更多哦!");
					afTaskUserService.batchUpdateTaskUserStatus(taskUserIdList);
				} else {
					data.put("message", afTaskUserService.getRewardAmount(taskUserList, rewardType));
					afTaskUserService.batchUpdateTaskUserStatus(taskUserIdList);
				}
			}
			resp.addResponseData("data",data);
		}catch(Exception e){
			logger.error("receiveBrowseTaskRewards error, ", e);
		}
		return resp;
	}




}
