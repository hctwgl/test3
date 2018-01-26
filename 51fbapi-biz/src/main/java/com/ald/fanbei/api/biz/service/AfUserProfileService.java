package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserProfileDo;

import java.util.List;

/**
 * 用户关联账号Service
 * 
 * @author xieqiang
 * @version 1.0.0 初始化
 * @date 2018-01-24 16:04:53
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserProfileService extends ParentService<AfUserProfileDo, Long>{
    /**
     * 保存一条用户相关账户记录
     * @param  userProfileDo 用户关联账号
     * @return
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/25 13:33
     * @author: xieqiang
     */
    public void saveUserProfile(AfUserProfileDo userProfileDo);
    /**
     * 更新一条用户相关账户记录
     * @param   userProfileDo 用户关联账号
     * @return
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/25 13:34
     * @author: xieqiang
     */
    public void updateUserProfileById(AfUserProfileDo userProfileDo);
    /**
     * 查询一条用户相关账户记录
     * @param    userProfileDo 用户关联账号
     * @return
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/25 13:35
     * @author: xieqiang
     */
    public AfUserProfileDo getUserProfileById(AfUserProfileDo userProfileDo);
    /**
     * 查询一条用户相关账户记录
     * @param userProfileDo 用户关联账号
     * @return
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/25 13:35
     * @author: xieqiang
     */
    public AfUserProfileDo getUserProfileByCommonCondition(AfUserProfileDo userProfileDo);
    /**
     * 查询多条用户相关账户记录
     * @param userProfileDo 用户关联账号
     * @return
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/25 13:36
     * @author: xieqiang
     */
    public List<AfUserProfileDo> getUserProfileListByCommonCondition(AfUserProfileDo userProfileDo);
    /**
     * 删除用户关联账户
     * @param id 记录id
     * @return
     * 返回值
     * <code>
     *  {
     *      "code": 1000,           //1000正常,其他异常
     *      "msg": "",              //错误信息
     *      "data": ""     //返回数据
     *  }
     * </code>
     * @throws Exception 异常
     * @date: 2018/1/25 14:02
     * @author: xieqiang
     */
    public void updateDeleteUserProfileById(long id);
}
