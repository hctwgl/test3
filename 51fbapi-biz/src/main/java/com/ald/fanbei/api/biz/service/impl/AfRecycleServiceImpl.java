/**
 * 
 */
package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.thirdpay.ThirdRecycleEnum;
import com.ald.fanbei.api.biz.service.AfRecycleService;
import com.ald.fanbei.api.biz.third.util.AppRecycleUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.dal.dao.AfRecycleDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.AfRecycleDo;
import com.ald.fanbei.api.dal.domain.AfRecycleRatioDo;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.dal.domain.query.AfRecycleRatioQuery;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @类描述： 有得卖  回收业务
 * @author weiqingeng 2018年2月27日上午9:55:29
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afRecycleService")
public class AfRecycleServiceImpl implements AfRecycleService {
	@Autowired
	private TransactionTemplate transactionTemplate;
	@Autowired
	private AfRecycleDao afRecycleDao;
	@Autowired
	private AfUserAccountDao afUserAccountDao;


	/**
	 * 订单添加事物
	 * @param afRecycleQuery
	 * @return
	 */
	@Override
	public Integer addRecycleOrder(final AfRecycleQuery afRecycleQuery) {
		transactionTemplate.execute(new TransactionCallback<Integer>() {
			@Override
			public Integer doInTransaction(TransactionStatus transactionStatus) {
				Integer result = afRecycleDao.addRecycleOrder(afRecycleQuery);
				//回调有得卖确认接口
				Map<String, String> params = new HashMap<String, String>();
				String sign = SignUtil.sign(AppRecycleUtil.createLinkString(params),AppRecycleUtil.PRIVATE_KEY);
				String postResult = HttpUtil.post(AppRecycleUtil.YDM_CALLBACK_URL, params);
				JSONObject jsonObject = JSONObject.parseObject(postResult);
				if(null != jsonObject && StringUtils.equals("1",jsonObject.getString("code"))){//返回成功
					//给用户账号添加订单金额
					AfRecycleRatioDo afRecycleRatioDo = afRecycleDao.getRecycleRatio(new AfRecycleRatioQuery(ThirdRecycleEnum.RETURN_MONEY.getType()));
					BigDecimal amount = afUserAccountDao.getAuAmountByUserId(afRecycleQuery.getUid());//查找用户账号信息
					if(null == amount){//用户账号信息不存在,则需要添加一条账号信息

					}else{//直接往账号上添加金额 金额 = 订单金额 *（1 + 返现比例）

					}
				}
				return result;
			}
		});
		return 1;
	}

	@Override
	public AfRecycleDo getRecycleOrder(AfRecycleQuery afRecycleQuery) {
		return afRecycleDao.getRecycleOrder(afRecycleQuery);
	}

	@Override
	public AfRecycleRatioDo getRecycleRatio(AfRecycleRatioQuery afRecycleRatioQuery) {
		return afRecycleDao.getRecycleRatio(afRecycleRatioQuery);
	}
}
