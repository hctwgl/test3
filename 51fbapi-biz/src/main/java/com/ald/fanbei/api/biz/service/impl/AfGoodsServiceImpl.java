package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.biz.bo.TaobaoItemInfoBo;
import com.ald.fanbei.api.biz.bo.TaobaoResultBo;
import com.ald.fanbei.api.biz.service.AfGoodsService;
import com.ald.fanbei.api.biz.service.BaseService;
import com.ald.fanbei.api.dal.dao.AfGoodsDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDo;
import com.ald.fanbei.api.dal.domain.query.AfGoodsQuery;
import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @类描述：
 * @author hexin 2017年2月18日上午10:08:37
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service("afGoodsService")
public class AfGoodsServiceImpl extends BaseService implements AfGoodsService{

	@Resource
	AfGoodsDao afGoodsDao;
	@Override
	public List<AfGoodsDo> getCateGoodsList(AfGoodsQuery query) {
		return afGoodsDao.getCateGoodsList(query);
	}
	@Override
	public AfGoodsDo getGoodsById(Long rid) {
		return afGoodsDao.getGoodsById(rid);
	}
	@Override
	public int subscribeGoods(String messageContent) {
		logger.info("subscribeGoods begin, messageContent = {}", messageContent);
		TaobaoResultBo bo = JSONObject.parseObject(messageContent, TaobaoResultBo.class);
		return afGoodsDao.updateSubscribeStatus(bo.getItem_id(), "Y", null);
	}
	@Override
	public int unSubscribeGoods(String messageContent) {
		logger.info("unSubscribeGoods begin, messageContent = {}", messageContent);
		TaobaoResultBo bo = JSONObject.parseObject(messageContent, TaobaoResultBo.class);
		return afGoodsDao.updateSubscribeStatus(bo.getItem_id(), "N", "CANCEL");
	}
	@Override
	public int updateTaobaoInfo(String messageContent, String messageType) {
		logger.info(messageType+" updateTaobaoInfo begin, messageContent = {}", messageContent);
		TaobaoResultBo bo = JSONObject.parseObject(messageContent, TaobaoResultBo.class);
		AfGoodsDo goodsInfo = parseBoToDo(bo);
		return afGoodsDao.updateTaobaoGoodsInfo(goodsInfo);
	}
	
	private static AfGoodsDo parseBoToDo(TaobaoResultBo bo) {
		AfGoodsDo goodsInfo = new AfGoodsDo();
		goodsInfo.setNumId(bo.getItem_id());
		TaobaoItemInfoBo itemInfo = bo.getItem_info();
		if (itemInfo != null) {
			String priceArr = itemInfo.getPrice().split("-")[0];
			String saleAmount = priceArr;
			if(!StringUtils.isBlank(itemInfo.getPromotion_price())){
				saleAmount = itemInfo.getPromotion_price().split("-")[0];
			}
			goodsInfo.setPriceAmount(new BigDecimal(priceArr));
			goodsInfo.setSaleAmount(new BigDecimal(saleAmount));
			if(null != itemInfo.getImg_urls()){
				goodsInfo.setGoodsIcon(itemInfo.getImg_urls().getString(0));
			}
			goodsInfo.setName(itemInfo.getTitle());
		}
		return goodsInfo;
	}
	
	@Override
	public int cancelPublishGoods(String messageContent) {
		logger.info("cancelGoods begin, messageContent = {}", messageContent);
		TaobaoResultBo bo = JSONObject.parseObject(messageContent, TaobaoResultBo.class);
		return afGoodsDao.cancelPublishGoods(bo.getItem_id());
	}
	@Override
	public AfGoodsDo getGoodsByNumId(String numId) {
		return afGoodsDao.getGoodsByNumId(numId);
	}

	@Override
	public int updateSelfSupportGoods(Long rid, Integer addSaleCount) {
		int rowNums = 0;
		try {
			rowNums = afGoodsDao.updateSelfSupportGoods(rid, addSaleCount);
		} catch (Exception e) {
			logger.error("updateSelfSupportGoods storage error", e);
		}
		return rowNums;
	}
	@Override
	public AfGoodsDo checkIsSelfBuild(String numId) {
		return afGoodsDao.checkIsSelfBuild(numId);
	}
	@Override
	public List<AfGoodsDo> listGoodsListByParentIdAndFormerCategoryId(Long parentId) {
	    // TODO Auto-generated method stub
	    	return afGoodsDao.listGoodsListByParentIdAndFormerCategoryId(parentId);
	}

}
