package com.ald.fanbei.api.biz.service.impl;

import java.util.List;

import javax.annotation.Resource;

import com.ald.fanbei.api.common.exception.FanbeiException;
import com.ald.fanbei.api.common.exception.FanbeiExceptionCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfGoodsPriceDao;
import com.ald.fanbei.api.dal.domain.AfGoodsPriceDo;
import com.ald.fanbei.api.biz.service.AfGoodsPriceService;

/**
 * '第三方-上树请求记录ServiceImpl
 * 
 * @author maqiaopan-template
 * @version 1.0.0 初始化
 * @date 2017-07-13 20:34:26 Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */

@Service("afGoodsPriceService")
public class AfGoodsPriceServiceImpl extends ParentServiceImpl<AfGoodsPriceDo, Long> implements AfGoodsPriceService {

	private static final Logger logger = LoggerFactory.getLogger(AfGoodsPriceServiceImpl.class);

	@Resource
	private AfGoodsPriceDao afGoodsPriceDao;

	@Override
	public BaseDao<AfGoodsPriceDo, Long> getDao() {
		return afGoodsPriceDao;
	}

	@Override
	public int updateStockAndSaleByPriceId(Long priceId, boolean isSold) {
		AfGoodsPriceDo priceDo = new AfGoodsPriceDo();
		priceDo = afGoodsPriceDao.getById(priceId);
		int result = 0;
		if (priceDo != null) {
			if (isSold) {// 出售
				try{
					result = afGoodsPriceDao.updateSell(priceId);
				}catch(Exception e){
					throw new FanbeiException(FanbeiExceptionCode.SOLD_OUT);
				}
			}else{
				try{
					result = afGoodsPriceDao.updateReturnGoods(priceId);
				}catch(Exception e){

				}
			}
		}
		return result;
	}

	@Override
	public List<AfGoodsPriceDo> getByGoodsId(Long goodsId) {
		// TODO Auto-generated method stub
		return afGoodsPriceDao.getByGoodsId(goodsId);
	}

	@Override
	public Integer selectSumStock(Long goodsId){
		return afGoodsPriceDao.selectSumStock(goodsId);
	}

	public Integer updateStockAndSaleCount(Long goodsId){
		return afGoodsPriceDao.selectSumStock(goodsId);
	}
}