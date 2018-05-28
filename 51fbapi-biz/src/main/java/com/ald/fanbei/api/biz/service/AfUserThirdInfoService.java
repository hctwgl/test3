package com.ald.fanbei.api.biz.service;

import com.ald.fanbei.api.dal.domain.AfUserThirdInfoDo;
import com.ald.fanbei.api.dal.domain.dto.UserWxInfoDto;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 用户第三方信息Service
 *
 * @author wangli
 * @version 1.0.0 初始化
 * @date 2018-05-04 09:20:23
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public interface AfUserThirdInfoService extends ParentService<AfUserThirdInfoDo, Long>{

    /**
     * 获取用户微信信息
     *
     * @author wangli
     * @date 2018/5/4 9:24
     */
    UserWxInfoDto getUserWxInfo(Long userId);

    /**
     * 获取用户头像、昵称等信息
     * 如果用户存在微信信息，则返回微信信息，否则返回本地用户信息
     *
     * @author wangli
     * @date 2018/5/4 17:03
     */
    UserWxInfoDto getWxOrLocalUserInfo(Long userId);

    /**
     * 根据微信openId获取用户信息
     *
     * @author wangli
     * @date 2018/5/6 12:17
     */
    UserWxInfoDto getLocalUserInfoByWxOpenId(String openId);

    /**
     * 根据微信openId获取用户id
     *
     * @author wangli
     * @date 2018/5/4 14:33
     */
    Long getUserIdByWxOpenId(String openId);

    /**
     * 绑定用户微信信息
     *
     * @author wangli
     * @date 2018/5/7 16:53
     */
    AfUserThirdInfoDo bindUserWxInfo(JSONObject userWxInfo, Long userId, String modifier);

    int saveRecord(AfUserThirdInfoDo afUserThirdInfoDo);

    int selectUserThirdInfoByUserName(String userName);

    int updateByUserName(AfUserThirdInfoDo afUserThirdInfoDo);

    List<AfUserThirdInfoDo> getListByCommonCondition(AfUserThirdInfoDo afUserThirdInfoDo);

}
