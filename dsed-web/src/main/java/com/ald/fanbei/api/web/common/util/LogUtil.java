package com.ald.fanbei.api.web.common.util;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.common.FanbeiContext;
import com.ald.fanbei.api.common.util.CommonUtil;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.dal.domain.AfBorrowCashDo;
import com.ald.fanbei.api.web.common.RequestDataVo;

public class LogUtil {
	
	
	private static final Logger maidianLog = LoggerFactory.getLogger("FBMD_BI");// 埋点日志
	
	private static final Logger logger = LoggerFactory.getLogger(LogUtil.class);
	/**
	 * 记录埋点相关日志日志
	 *
	 * @param request
	 * @param cashDo
	 * @param requestDataVo
	 * @param context
	 */
	public static void doMaidianLog(HttpServletRequest request, AfBorrowCashDo cashDo, RequestDataVo requestDataVo,
			FanbeiContext context,String ... exts) {
		try {
			String ext1 = StringUtils.EMPTY;
			String ext2 = StringUtils.EMPTY;
			String ext3 = StringUtils.EMPTY;
			String ext4 = StringUtils.EMPTY;
			try{
				// 获取可变参数
				ext1 = exts[0];
				ext2 = exts[1];
				ext3 = exts[2];
				ext4 = exts[3];
			} catch (Exception e){
				// ignore error
			}
			
			maidianLog.info(com.ald.fanbei.api.common.util.StringUtil.appendStrs("	",
					DateUtil.formatDate(new Date(), DateUtil.DATE_TIME_SHORT), "	",
					com.ald.fanbei.api.common.util.StringUtil.judgeClientDeviceFlag(requestDataVo.getId()), "	rmtIP=",
					CommonUtil.getIpAddr(request), "	userName=", context.getUserName(), "	", 0, "	",
					request.getRequestURI(), "	", cashDo.getRid() + "", "	",
					DateUtil.formatDate(new Date(), DateUtil.MONTH_SHOT_PATTERN), "	", "userBorrowCashApply", "	", ext1,
					"	", ext2, "	", ext3, "	", ext4,
					"	reqD=", "appFlag:" + requestDataVo.getId() + ",appVersion:" + context.getAppVersion()
							+ ",userId=" + cashDo.getUserId() + ",cashAmount:" + cashDo.getAmount(),
					"	resD=", "null"));
		} catch (Exception e) {
			logger.error("userBorrowCashApply maidian logger error", e);
		}
	}
}
