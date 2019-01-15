package com.ald.fanbei.api.web.task;

import com.ald.fanbei.api.biz.service.*;
import com.ald.fanbei.api.biz.util.GetHostIpUtil;
import com.ald.fanbei.api.common.ConfigProperties;
import com.ald.fanbei.api.common.Constants;
import com.ald.fanbei.api.common.enums.*;
import com.ald.fanbei.api.common.util.DateUtil;
import com.ald.fanbei.api.common.util.timeUtil;
import com.ald.fanbei.api.common.util.dingding.DingdingUtil;
import com.ald.fanbei.api.dal.domain.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @类描述： 每日1点执行一次
 *
 * @author cfp
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Component
public class BusinessTotalInfoJob {
	Logger logger = LoggerFactory.getLogger(BusinessTotalInfoJob.class);

	@Resource
	JsdResourceService jsdResourceService;
	@Resource
	JsdTotalInfoService jsdTotalInfoService;

    private static String NOTICE_HOST = ConfigProperties.get(Constants.CONFKEY_TASK_ACTIVE_HOST);
    private static String HOST = "0.0.0.0";


	@Scheduled(cron = "0/10 * * * * ?")
	public void laonDueJob() {
		try {
			String curHostIp = GetHostIpUtil.getIpAddress();
			logger.info("curHostIp=" + curHostIp + ", configNoticeHost=" + NOTICE_HOST);
			if (StringUtils.equals(GetHostIpUtil.getIpAddress(), NOTICE_HOST) || StringUtils.equals(GetHostIpUtil.getIpAddress(), HOST)) {
				try {
					JsdResourceDo resourceDo = jsdResourceService.getByTypeAngSecType(ResourceType.JSD_CONFIG.name(),
							ResourceSecType.JSD_RATE_INFO.name());
					if (StringUtils.isNotBlank(resourceDo.getTypeDesc())) {
						// 获取数据库中最新数据
						JsdTotalInfoDo query = new JsdTotalInfoDo();
						JsdTotalInfoDo JsdTotalInfoDo = jsdTotalInfoService.getByCommonCondition(query);

						while (null != JsdTotalInfoDo && !timeUtil.isYesterday(JsdTotalInfoDo.getCountDate())) {
							Calendar calendar = Calendar.getInstance();
							calendar.setTime(JsdTotalInfoDo.getCountDate());
							calendar.add(Calendar.DAY_OF_MONTH, 1);
							Date tdate = calendar.getTime();
							String date = DateUtil.formatDate(tdate,
									DateUtil.DEFAULT_PATTERN_WITH_HYPHEN);
							try{
								
								jsdTotalInfoService.updateTotalInfo(tdate, date, resourceDo);
								JsdTotalInfoDo = jsdTotalInfoService.getByCommonCondition(query);
							}catch (Exception e) {
								//设置时间加一天，跳过当前时间
								calendar.add(Calendar.DAY_OF_MONTH, 1);
								JsdTotalInfoDo.setCountDate(calendar.getTime());
								
								// 执行失败，发送短信提醒
					            DingdingUtil.sendMessageByJob(NOTICE_HOST +"，日期为"+date+"，每日现金统计出现异常！",true);
								logger.info("error = ", e);
								e.getMessage();
							}
							
						}

					}
				}
				catch (Exception e) {
					// 执行失败，发送短信提醒
		            DingdingUtil.sendMessageByJob(NOTICE_HOST +"，逾期定时器执行失败！",true);
					logger.info("error = ", e);
					e.getMessage();
				}

			}
		}
		catch (Exception e) {
			logger.error("borrowCashDueJob  error, case=", e);
		}
	}

}
