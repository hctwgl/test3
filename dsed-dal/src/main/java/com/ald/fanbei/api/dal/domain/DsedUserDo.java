package com.ald.fanbei.api.dal.domain;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.math.BigDecimal;

/**
 * 都市易贷用户信息实体
 * 
 * @author guoshuaiqiang
 * @version 1.0.0 初始化
 * @date 2018-06-19 10:51:34
 * Copyright 本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Setter
@Getter
 public class DsedUserDo extends AbstractSerial {

    private static final long serialVersionUID = 1L;

    /**
     * 主键Rid
     */
    private Long rid;
    

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 最后修改时间
     */
    private Date gmtModified;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码盐值
     */
    private String salt;

    /**
     * 性别 【F：女 ，M：男， U:未知】
     */
    private String gender;

    /**
     * 昵称
     */
    private String nick;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 绑定手机号
     */
    private String mobile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 出生年月日，格式yyyy-MM-dd
     */
    private String birthday;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区/县
     */
    private String county;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 邀请人id
     */
    private Long recommendId;

    /**
     * 登录失败次数
     */
    private Integer failCount;

    /**
     * 邀请码
     */
    private String recommendCode;

    /**
     * NORMAL:正常使用，FROZEN：表示冻结
     */
    private String status;

    /**
     * 注册来源渠道ID
     */
    private String registerChannelId;

    /**
     * 注册来源渠道位置ID
     */
    private String registerChannelPointId;

    /**
     * 马甲包名称,www为app,其余的为马甲包名称
     */
    private String majiabaoName;

    /**
     * 微信小程序用户对应的openid
     */
    private String openId;

    private String idNumber;



}