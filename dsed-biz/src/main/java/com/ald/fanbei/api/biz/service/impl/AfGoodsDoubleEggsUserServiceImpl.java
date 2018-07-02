package com.ald.fanbei.api.biz.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfGoodsDoubleEggsUserDao;
import com.ald.fanbei.api.dal.domain.AfGoodsDoubleEggsUserDo;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.ald.fanbei.api.biz.service.AfGoodsDoubleEggsUserService;



/**
 * 双蛋活动ServiceImpl
 * 
 * @author maqiaopan_temple
 * @version 1.0.0 初始化
 * @date 2017-12-07 14:47:43
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afGoodsDoubleEggsUserService")
public class AfGoodsDoubleEggsUserServiceImpl extends ParentServiceImpl<AfGoodsDoubleEggsUserDo, Long> implements AfGoodsDoubleEggsUserService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfGoodsDoubleEggsUserServiceImpl.class);
   
    @Resource
    private AfGoodsDoubleEggsUserDao afGoodsDoubleEggsUserDao;

		@Override
	public BaseDao<AfGoodsDoubleEggsUserDo, Long> getDao() {
		return afGoodsDoubleEggsUserDao;
	}

		@Override
		public boolean isExist(Long goodsId, Long userId) {
			boolean result = false;
			if(afGoodsDoubleEggsUserDao.isExist(goodsId,userId) > 0){
				result = true;
			}
			return result; 
		}

		@Override
		public int isSubscribed(Long doubleGoodsId,Long userId ) {
			
			return afGoodsDoubleEggsUserDao.isSubscribed(doubleGoodsId,userId);
		}

		@Override
		public int getSpringFestivalNumber(Long goodsId) {
			
			return afGoodsDoubleEggsUserDao.getSpringFestivalNumber(goodsId);
		}
}