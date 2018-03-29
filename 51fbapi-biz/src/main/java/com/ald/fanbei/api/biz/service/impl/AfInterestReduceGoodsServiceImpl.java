package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.service.AfInterestReduceGoodsService;
import com.ald.fanbei.api.common.util.CollectionUtil;
import com.ald.fanbei.api.common.util.StringUtil;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.dao.AfInterestReduceGoodsDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceGoodsDo;
import com.ald.fanbei.api.dal.domain.AfInterestReduceRulesDo;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

import net.bytebuddy.implementation.bind.annotation.Super;

/**
 * 降息ServiceImpl
 * 
 * @author qiao
 * @version 1.0.0 初始化
 * @date 2018-03-29 13:41:22 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afInterestReduceGoodsService")
public class AfInterestReduceGoodsServiceImpl extends ParentServiceImpl<AfInterestReduceGoodsDo, Long>
		implements AfInterestReduceGoodsService {

	private static final Logger logger = LoggerFactory.getLogger(AfInterestReduceGoodsServiceImpl.class);

	@Resource
	private AfInterestReduceGoodsDao afInterestReduceGoodsDao;

	@Resource
	AfGoodsDao afGoodsDao;

	@Override
	public BaseDao<AfInterestReduceGoodsDo, Long> getDao() {
		return afInterestReduceGoodsDao;
	}

	@Override
	public JSONArray checkIfReduce(Long goodsId) {
		JSONArray result = null;

		AfGoodsDo goodsDo = afGoodsDao.getGoodsById(goodsId);
		if (goodsDo != null) {

			// check goodsId
			AfInterestReduceRulesDo resultDo = afInterestReduceGoodsDao.checkIfReduce(0, goodsId);
			if (resultDo != null) {
				return getJSONArrayFromDo(resultDo);

			}

			// get brandId due to goodsId
			Long brandId = goodsDo.getBrandId();
			resultDo = afInterestReduceGoodsDao.checkIfReduce(1, brandId);
			if (resultDo != null) {
				return getJSONArrayFromDo(resultDo);
			}

			// get categoryId due to goodsId
			Long categoryId = goodsDo.getCategoryId();
			resultDo = afInterestReduceGoodsDao.checkIfReduce(2, categoryId);
			if (resultDo != null) {
				return getJSONArrayFromDo(resultDo);
			}
		}
		return result;
	}

	private JSONArray getJSONArrayFromDo(AfInterestReduceRulesDo resultDo) {
		JSONArray result = null;
		if (resultDo != null) {
			
			List<Object> list = new ArrayList<>();
			Nper nper2 = new Nper(2,resultDo.getNper2());
			Nper nper3 = new Nper(3,resultDo.getNper3());
			Nper nper6 = new Nper(6,resultDo.getNper6());
			Nper nper9 = new Nper(9,resultDo.getNper9());
			Nper nper12 = new Nper(12, resultDo.getNper12());
			
			list.add(nper2);
			list.add(nper3);
			list.add(nper6);
			list.add(nper9);
			list.add(nper12);
			
			if (CollectionUtil.isNotEmpty(list)) {
				result =  new JSONArray(list);
			}
			
		}

		return result;

	}
	
	
	class Nper{
		
		private int nper;
		private BigDecimal rate;
		
		public Nper(){
			super();
		}
		
		public Nper(int nper,BigDecimal rate){
			this.nper = nper;
			this.rate = rate;
		}
		
		
		public int getNper() {
			return nper;
		}
		public void setNper(int nper) {
			this.nper = nper;
		}

		public BigDecimal getRate() {
			return rate;
		}

		public void setRate(BigDecimal rate) {
			this.rate = rate;
		}

		
	}
}