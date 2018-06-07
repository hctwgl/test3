package com.ald.fanbei.api.web.api.taskUser;

import com.ald.fanbei.api.biz.service.*;
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
import com.ald.fanbei.api.web.validator.constraints.NeedLogin;
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
@NeedLogin
@Component("addBrowseTaskUserApi")
public class AddBrowseTaskUserApi implements ApiHandle{
	

	@Resource
	AfTaskUserService afTaskUserService;
	@Resource
	AfTaskBrowseGoodsService afTaskBrowseGoodsService;
	
	
	@Override
	public ApiHandleResponse process(RequestDataVo requestDataVo, FanbeiContext context, HttpServletRequest request) {
		logger.info("addBrowseTaskUser start..");
		ApiHandleResponse resp = new ApiHandleResponse(requestDataVo.getId(), FanbeiExceptionCode.SUCCESS);
		try{
			Long userId = context.getUserId();
			if(null != userId){
				Map<String, Object> data = Maps.newHashMap();
				String goodsId = ObjectUtils.toString(requestDataVo.getParams().get("goodsId"));
				String taskContition = ObjectUtils.toString(requestDataVo.getParams().get("activityUrl"));
				logger.info("cfp addBrowseTaskUserApi cfp taskContition = " + taskContition);
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
						data.put("message","太棒了!"+"\r\n"+"您已完成每日任务");
					}

				} else if(StringUtils.isNotEmpty(taskContition)){
					// 浏览活动链接任务
					List<AfTaskUserDo> specifiedTaskUserList = afTaskUserService.taskHandler(userId, AfTaskType.BROWSE.getCode(),taskContition);
					logger.info("cfp addBrowseTaskUserApi cfp specifiedTaskUserList = " + specifiedTaskUserList);
					String taskUserIds = buildTaskUserIds(specifiedTaskUserList);
					if(StringUtils.isNotEmpty(taskUserIds)){
						data.put("taskUserIds", taskUserIds);
						data.put("message","太棒了!"+"\r\n"+"您已完成每日任务");
					}
				}
				data.put("taskUserIds", "1");
				data.put("message","太棒了!"+"\r\n"+"您已完成每日任务");
				resp.setResponseData(data);
				logger.info("cfp addBrowseTaskUserApi cfp " + data);
			}

		}catch(Exception e){
			logger.error("addBrowseTaskUser error, ", e);
		}

		return resp;
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
