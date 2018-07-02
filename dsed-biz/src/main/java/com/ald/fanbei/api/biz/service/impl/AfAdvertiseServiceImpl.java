package com.ald.fanbei.api.biz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ald.fanbei.api.dal.dao.BaseDao;
import com.ald.fanbei.api.dal.dao.AfAdvertiseDao;
import com.ald.fanbei.api.dal.domain.AfAdvertiseDo;
import com.ald.fanbei.api.dal.domain.AfAssetSideInfoDo;
import com.ald.fanbei.api.dal.domain.AfBoluomeActivityMsgIndexDo;
import com.ald.fanbei.api.dal.domain.dto.AfAdvertiseDto;
import com.ald.fanbei.api.biz.service.AfAdvertiseService;
import com.aliyun.odps.Column;
import com.aliyun.odps.Instance;
import com.aliyun.odps.Instance.Result;
import com.aliyun.odps.Odps;
import com.aliyun.odps.OdpsException;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.data.Record;
import com.aliyun.odps.task.SQLTask;



/**
 * 定向广告规则ServiceImpl
 * 
 * @author chenqiwei
 * @version 1.0.0 初始化
 * @date 2018-05-17 22:38:06
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
 
@Service("afAdvertiseService")
public class AfAdvertiseServiceImpl extends ParentServiceImpl<AfAdvertiseDo, Long> implements AfAdvertiseService {
	
    private static final Logger logger = LoggerFactory.getLogger(AfAdvertiseServiceImpl.class);
   
    @Resource
    private AfAdvertiseDao afAdvertiseDao;

		@Override
	public BaseDao<AfAdvertiseDo, Long> getDao() {
		return afAdvertiseDao;
	}

		private int getDirectionalRecommendCount(Long userId,String queryConditions) {
		    // TODO Auto-generated method stub
		return afAdvertiseDao.getDirectionalRecommendCount(userId,queryConditions);
	}
	
		public AfAdvertiseDto getDirectionalRecommendInfo(String positionCode,Long userId) {
			
			try{
				List<AfAdvertiseDto>  advertiseList = afAdvertiseDao.getDirectionalRecommendInfo(positionCode);
				if(advertiseList != null && advertiseList.size() > 0){
					//循环匹配规则
				
					for(AfAdvertiseDto afAdvertiseDto:advertiseList){
				     int count =  	getDirectionalRecommendCount(userId,afAdvertiseDto.getQueryConditions());
					     if(count > 0){
					    	 //更新次数+1；
					       int clickCount = afAdvertiseDao.updateClickCountById(afAdvertiseDto.getRid());
					       logger.info("getDirectionalRecommendInfo clickCount++ by userId"+userId+"clickCount = "+clickCount+"count = "+count);
					        if(clickCount >0) {
								return afAdvertiseDto;
							}
					     }
					}
				}
			}catch(Exception e){
				logger.error("getDirectionalRecommendInfo error = "+e);
			}
			return null;
		}
		
		
//      数据分析时间过长，废弃		
//		public AfAdvertiseDto getDirectionalRecommendInfo(String positionCode,Long userId) {
//			
//			List<AfAdvertiseDto>  advertiseList = afAdvertiseDao.getDirectionalRecommendInfo(positionCode);
//			if(advertiseList != null && advertiseList.size() > 0){
//				//循环匹配规则
//			
//				for(AfAdvertiseDto afAdvertiseDto:advertiseList){
//			     int count =  	0;
//			     try{
//			    	 //String sql = "";
//			    	 if(afAdvertiseDto.getQueryConditions() != null && userId!= null){
//			    		 String queryConditons = afAdvertiseDto.getQueryConditions();
//			    		 StringBuffer buffer = new StringBuffer();
//			    		 buffer.append("select count(0) num from fb_user_portrait_hujm_reslut b where user_id = ").append(userId).append(" ").append(queryConditons).append(";");
//			    		 long startTime = System.currentTimeMillis();  
//			    		 System.out.println("otps查询开始时间：" + startTime); 
//			    		 count = getDirectionalRecommendCounts(buffer.toString());
//			    		 long endTime = System.currentTimeMillis();
//			    		 System.out.println("otps查询结束时间：" + endTime); 
//			    		 System.out.println("程序运行时间：" + (endTime - startTime) + "ms"); 
//			    		 long time = (endTime - startTime);
//			    		 logger.info("getDirectionalRecommendCounts time = "+time);
//			    	 }
//			        }catch(Exception e){
//			        	logger.error("getDirectionalRecommendCounts error e="+e);
//			     }
//			     
//				     if(count > 0){
//				    	 //更新次数+1；
//				       int clickCount = afAdvertiseDao.updateClickCountById(afAdvertiseDto.getRid());
//				       logger.info("getDirectionalRecommendInfo clickCount++ by userId"+userId+"clickCount = "+clickCount);
//				       return	 afAdvertiseDto;
//				     }
//				}
//			}
//			return null;
//		}
//		
//		private int getDirectionalRecommendCounts(String sql) throws OdpsException {
//			int num = 0;
//			
//			AliyunAccount account = new AliyunAccount("LTAIzczhcplYorV4", "REKfiRyRyKyuRL0VF6lnK8Zupcj4WE");
//		    Odps odps = new Odps(account);
//		    odps.setDefaultProject("fanbei");
//		    long startTime = System.currentTimeMillis();  
//		    Instance instance = SQLTask.run(odps, sql);
//		    long endTime = System.currentTimeMillis();
//			 System.out.println("otps SQLTask.run时间：" + (endTime - startTime) + "ms"); 
//			  long startTime1 = System.currentTimeMillis();
//			 if(!instance.isSync()){
//		      instance.waitForSuccess();//不可删除？？
//		      
//		    }
//			 long endTime1 = System.currentTimeMillis();
//			 System.out.println("otps waitForSuccess时间：" + (endTime1 - startTime1) + "ms"); 
//			    
//		    if(instance.isSuccessful()){
//		      System.out.println("成功");
//		      List<Record> recordsss = SQLTask.getResult(instance);
//		      if(recordsss != null && recordsss.size()>0){
//		    	       num =     Integer.parseInt(recordsss.get(0).getString("num").toString()) ;
//		    		  logger.info("getDirectionalRecommendCounts recordsssnum = "+num);
//		      }
//		    }else{
//		      System.out.println("失败");
//		    }
//			return num;
//		 }	
}