package com.ald.fanbei.api.biz.service.impl;

import com.ald.fanbei.api.biz.bo.thirdpay.ThirdRecycleEnum;
import com.ald.fanbei.api.biz.service.AfRecycleService;
import com.ald.fanbei.api.biz.service.AfRecycleTradeService;
import com.ald.fanbei.api.biz.service.AfUserService;
import com.ald.fanbei.api.biz.third.util.AppRecycleUtil;
import com.ald.fanbei.api.common.util.HttpUtil;
import com.ald.fanbei.api.common.util.SignUtil;
import com.ald.fanbei.api.dal.dao.AfRecycleDao;
import com.ald.fanbei.api.dal.dao.AfRecycleViewDao;
import com.ald.fanbei.api.dal.dao.AfUserAccountDao;
import com.ald.fanbei.api.dal.domain.*;
import com.ald.fanbei.api.dal.domain.query.AfRecycleQuery;
import com.ald.fanbei.api.dal.domain.query.AfRecycleRatioQuery;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import java.math.BigDecimal;
import java.util.Date;
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
	private AfRecycleViewDao afRecycleViewDao;
	@Autowired
	private AfUserAccountDao afUserAccountDao;
	@Autowired
	private AfUserService afUserService;
	@Autowired
	private AfRecycleTradeService afRecycleTradeService;


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
					Long userId = afRecycleQuery.getUid();
					BigDecimal settlePrice = afRecycleQuery.getSettlePrice();
					BigDecimal amount = afUserAccountDao.getAuAmountByUserId(userId);//查找用户账号信息
					if(null == amount){//用户账号信息不存在,则需要添加一条账号信息
						//根据用户Id查找用户名
						AfUserDo afUserDo = afUserService.getUserById(userId);
						//给用户的返现金额
						BigDecimal rebateAmount = BigDecimal.ONE.add(afRecycleRatioDo.getRatio()).multiply(settlePrice);						
						AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
						afUserAccountDo.setUserId(userId);
						afUserAccountDo.setUserName(afUserDo.getUserName());
						afUserAccountDo.setRebateAmount(rebateAmount);
						afUserAccountDao.addUserAccount(afUserAccountDo);
						//有得卖账户减钱操作
						recycleTradeSave(afRecycleQuery, afRecycleRatioDo,settlePrice, rebateAmount);
					}else{//直接往账号上添加金额 金额 = 订单金额 *（1 + 返现比例）
						BigDecimal rebateAmount = BigDecimal.ONE.add(afRecycleRatioDo.getRatio()).multiply(settlePrice);
						AfUserAccountDo afUserAccountDo = new AfUserAccountDo();
						afUserAccountDo.setRebateAmount(rebateAmount);
						afUserAccountDao.updateUserAccount(afUserAccountDo);
						//有得卖账户减钱操作
						recycleTradeSave(afRecycleQuery, afRecycleRatioDo,settlePrice, rebateAmount);
					}
				}
				return result;
			}

			private void recycleTradeSave(final AfRecycleQuery afRecycleQuery,
					AfRecycleRatioDo afRecycleRatioDo, BigDecimal settlePrice,
					BigDecimal rebateAmount) {
				AfRecycleTradeDo afRecycleTradeDo = afRecycleTradeService.getLastRecord();
				AfRecycleTradeDo newAfRecycleTradeDo = new AfRecycleTradeDo();
				newAfRecycleTradeDo.setGmtCreate(new Date());
				newAfRecycleTradeDo.setGmtModified(new Date());
				newAfRecycleTradeDo.setRatio(afRecycleRatioDo.getRatio());
				newAfRecycleTradeDo.setRefId(afRecycleQuery.getRid());
				newAfRecycleTradeDo.setRemainAmount(afRecycleTradeDo.getRemainAmount().subtract(settlePrice));
				newAfRecycleTradeDo.setReturnAmount(rebateAmount);
				newAfRecycleTradeDo.setTradeAmount(settlePrice.add(rebateAmount));
				newAfRecycleTradeDo.setType(1);
				afRecycleTradeService.saveRecord(newAfRecycleTradeDo);
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
