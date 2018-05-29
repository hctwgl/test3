package com.ald.fanbei.api.web.api.taskUser;

import com.ald.fanbei.api.biz.service.AfTaskBrowseGoodsService;
import com.ald.fanbei.api.biz.service.AfTaskUserService;
import com.ald.fanbei.api.biz.service.AfUserAccountService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.FanbeiH5Context;
import com.ald.fanbei.api.common.enums.AfTaskType;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import com.ald.fanbei.api.dal.dao.AfUserAppealLogDao;
import com.ald.fanbei.api.dal.domain.AfTaskUserDo;
import com.ald.fanbei.api.dal.domain.AfUserAccountDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo;
import com.ald.fanbei.api.dal.domain.AfUserAppealLogDo.AfUserAppealLogStatusEnum;
import com.ald.fanbei.api.web.common.ApiHandle;
import com.ald.fanbei.api.web.common.ApiHandleResponse;
import com.ald.fanbei.api.web.common.H5CommonResponse;
import com.ald.fanbei.api.web.common.RequestDataVo;
import com.google.common.collect.Maps;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 浏览商品
 * @author cfp
 */
@Component("addBrowseTaskUserApi")
public class AddBrowseTaskUserApi implements ApiHandle{
	

	@Resource
	AfTaskUserService afTaskUserService;
	@Resource
	AfTaskBrowseGoodsService afTaskBrowseGoodsService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		try{
			Long userId = context.getUserId();
			if(null != userId){
				Map<String, Object> data = Maps.newHashMap();
				String goodsId = ObjectUtils.toString(requestDataVo.getParams().get("goodsId"));
				String taskContition = ObjectUtils.toString(requestDataVo.getParams().get("activityUrl"));

				if(StringUtils.isNotEmpty(goodsId)) {
					// 指定浏览商品、品牌、分类任务等
					List<AfTaskUserDo> specifiedTaskUserList = afTaskUserService.browerAndShoppingHandler(userId, Long.parseLong(goodsId), AfTaskType.BROWSE.getCode());

					// 每日浏览任务
					AfTaskUserDo taskUserDo = afTaskBrowseGoodsService.addBrowseGoodsTaskUserRecord(userId, Long.parseLong(goodsId));
					if (null != taskUserDo) {
						specifiedTaskUserList.add(taskUserDo);
					}

					String taskUserIds = buildTaskUserIds(specifiedTaskUserList);
					if(StringUtils.isNotEmpty(taskUserIds)){
						data.put("taskUserIds", taskUserIds);
					}
				} else if(StringUtils.isNotEmpty(taskContition)){
					// 浏览活动链接任务
					List<AfTaskUserDo> specifiedTaskUserList = afTaskUserService.taskHandler(userId, taskContition, AfTaskType.BROWSE.getCode());
					String taskUserIds = buildTaskUserIds(specifiedTaskUserList);
					if(StringUtils.isNotEmpty(taskUserIds)){
						data.put("taskUserIds", taskUserIds);
					}
				}
				resp.addResponseData("taskUserIds",data.get("taskUserIds"));
			}

		}catch(Exception e){
			logger.error("addBrowseTaskUser error, ", e);
		}
		
		return new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
	}

		/**
		 * 生成完成任务IDs
		 * @param taskUserDoList
		 * @return
		 */
		private static String buildTaskUserIds(List<AfTaskUserDo> taskUserDoList) {
			if(null == taskUserDoList || taskUserDoList.isEmpty()){
				return null;
			}
			StringBuffer idStringBuffer = new StringBuffer();
			for (AfTaskUserDo tempTaskUser : taskUserDoList) {
				idStringBuffer.append(tempTaskUser.getRid()).append(",");
			}
			idStringBuffer.deleteCharAt(idStringBuffer.length() - 1);
			return idStringBuffer.toString();
		}
	



}
