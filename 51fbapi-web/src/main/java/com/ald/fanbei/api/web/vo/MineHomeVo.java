package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import com.ald.fanbei.api.common.enums.YesNoStatus;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 我的首页VO
 *
 * @author wangli
 * @date 2018/4/12 19:58
 */
@Getter
@Setter
public class MineHomeVo extends AbstractSerial {

    // 用户头像
    private String avata = "";

    // 用户昵称
    private String nick = "";

    // 用户名
    private String userName = "";

    // 用户真实姓名
    private String realName = "";

    // 身份证号
    private String idNumber = "";

    // 手机号
    private String mobile = "";

    // 是否登陆 N/Y
    private String isLogin = YesNoStatus.NO.getCode();

    // 是否设置支付密码 N/Y
    private String isPayPwd = YesNoStatus.NO.getCode();

    // 是否签到 N/Y
    private String isSign = YesNoStatus.NO.getCode();

    // 签到Url
    private String signUrl = "";

    //
    private String vipLevel = "";

    // 返现现金金额
    private String rebateAmount = "0.00";

    // 优惠券数量
    private Integer couponCount = 0;

    // 第三方优惠券数量
    private Integer brandCouponCount = 0;

    // 平台优惠券数量
    private Integer plantformCouponCount = 0;

    // 人脸识别状态 N/Y
    private String faceStatus = YesNoStatus.NO.getCode();

    // 银行卡绑定状态 N/Y
    private String bankcardStatus = YesNoStatus.NO.getCode();

    // 推荐码
    private String recommendCode = "";

    // 客服电话
    private String customerPhone = "";

    // 借款额度
    private String showAmount = "0.00";

    // 借款额度描述
    private String desc = "";

    // 购物额度
    private String onlineShowAmount = "0.00";

    // 购物额度描述
    private String onlineDesc = "";

    // 待还金额
    private String outMoney = "0.00";

    // 待付款订单数
    private Integer newOrderNum = 0;

    // finishedOrderNum
    private Integer paidOrderNum = 0;

    // 待收货订单数
    private Integer deliveredOrderNum = 0;

    // 待返利订单数
    private Integer finishedOrderNum = 0;

    // 售后处理订单数
    private Integer afterSaleOrderNum = 0;

    // 爱花跳转url
    private String loveShopSkipUrl = "";

    // 签到图爿
    private String signImgUrl = "";

    // banner列表
    private List<Map<String, Object>> bannerList = new ArrayList<>();

    // 快速导航列表
    private List<Map<String, Object>> navigationList = new ArrayList<>();

    /**
     * 添加导航
     *
     * @author wangli
     * @date 2018/4/12 20:19
     */
    public void addNavigation(Map<String, Object> navigation) {
        navigationList.add(navigation);
    }
}
