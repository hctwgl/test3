package com.ald.fanbei.api.biz.bo.common;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ald.fanbei.api.biz.util.BizCacheUtil;
import com.ald.fanbei.api.biz.util.TokenCacheUtil;
import com.ald.fanbei.api.common.util.CommonUtil;



/**
 * 
 *@类描述：健康检测
 *@author 陈金虎 2017年1月18日 上午12:50:34
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class HealthCheck {
    private static Logger logger = LoggerFactory.getLogger(HealthCheck.class);
    
    @Resource
    BizCacheUtil bizCacheUtil;
    @Resource
    TokenCacheUtil tokenCacheUtil;
    
    public void checkBizCache() throws InterruptedException{
    	this.checkBiz();
    	this.checkToken();
    }
    
    private void checkBiz() throws InterruptedException{
        Thread.sleep(CommonUtil.getRandomNum(20)+1);
        Long startTime = System.currentTimeMillis();
        boolean putResult = bizCacheUtil.putObjectForCheckHeath("biz_cache_check", "hello", 5);
        String result = String.valueOf(bizCacheUtil.getObjectForCheckHeath("biz_cache_check"));
        Long time = System.currentTimeMillis() - startTime.longValue();
        
        Thread.sleep(CommonUtil.getRandomNum(1000)+1);
        startTime = System.currentTimeMillis();
        String result1 = String.valueOf(bizCacheUtil.getObjectForCheckHeath("biz_cache_check"));
        Long time1 = System.currentTimeMillis() - startTime;
        if (("hello".equals(result) && time < 200) || ("hello".equals(result1) && time1 < 200)) {
            logger.info(putResult + ",cache server is avaliable");
            BizCacheUtil.BIZ_CACHE_SWITCH = true;
        } else {
        	BizCacheUtil.BIZ_CACHE_SWITCH = false;
            logger.info(putResult + ",cache server is inavailable,time="+time+",result=" + result + ",time1=" + time1 + ",result1=" + result1);
        }
    }
    
    private void checkToken() throws InterruptedException{
        Thread.sleep(CommonUtil.getRandomNum(20)+1);
        Long startTime = System.currentTimeMillis();
        boolean putResult = tokenCacheUtil.putObjectForCheckHeath("token_cache_check", "hellotoken", 5);
        String result = String.valueOf(tokenCacheUtil.getObjectForCheckHeath("token_cache_check"));
        Long time = System.currentTimeMillis() - startTime.longValue();
        
        Thread.sleep(CommonUtil.getRandomNum(1000)+1);
        startTime = System.currentTimeMillis();
        String result1 = String.valueOf(tokenCacheUtil.getObjectForCheckHeath("token_cache_check"));
        Long time1 = System.currentTimeMillis() - startTime;
        if (("hellotoken".equals(result) && time < 200) || ("hellotoken".equals(result1) && time1 < 200)) {
            logger.info(putResult + ",token cache server is avaliable");
            BizCacheUtil.BIZ_CACHE_SWITCH = true;
        } else {
        	BizCacheUtil.BIZ_CACHE_SWITCH = false;
            logger.info(putResult + ",token cache server is inavailable,time="+time+",result=" + result + ",time1=" + time1 + ",result1=" + result1);
        }
    }
}
