package com.ald.fanbei.api.biz.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.AfAssetSideOperaLogDao;
import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAssetPackageDetailDao;
import com.ald.fanbei.api.dal.domain.AfAssetPackageDetailDo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditReqBo;
import com.ald.fanbei.api.biz.bo.assetside.edspay.EdspayGetCreditRespBo;
import com.ald.fanbei.api.biz.service.AfAssetPackageDetailService;



/**
 * 资产包与债权记录关系ServiceImpl
 * @author chengkang
 * @version 1.0.0 初始化
 * @date 2017-11-27 15:47:49
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAssetPackageDetailService")
public class AfAssetPackageDetailServiceImpl extends ParentServiceImpl<AfAssetPackageDetailDo, Long> implements AfAssetPackageDetailService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAssetPackageDetailServiceImpl.class);
   
    @Resource
    private AfAssetPackageDetailDao afAssetPackageDetailDao;
    @Resource
    private AfAssetSideOperaLogDao afAssetSideOperaLogDao;


	@Override
	public BaseDao<AfAssetPackageDetailDo, Long> getDao() {
		return afAssetPackageDetailDao;
	}
		
	/**
	 * 批量债权包明细撤回操作
	 * @param orderNos
	 */
	@Override
	public void batchGiveBackCreditInfo(List<String> orderNos){
		//校验
		
		//begin事务,记录变更
		
		//end事务
		
		//重新生成资产包上传oss  begin
		
		//重新生成资产包上传oss  end
		
	}
	
	
	
	/**
	 * 根据资产方要求,获取资产方对应的债权信息
	 */
	@Override
	public List<EdspayGetCreditRespBo> getBatchCreditInfo(BigDecimal money,Date startTime,Date endTime,BigDecimal sevenMoney){
		List<EdspayGetCreditRespBo> creditInfos = new ArrayList<EdspayGetCreditRespBo>();
		//加锁Lock
		
		//校验现在金额是否满足
		
		
		//分配债权资产包
		
		
		//释放锁Lock
		
		
		//异步线程生成包并上传oss
		
		
		return creditInfos;
	}
}