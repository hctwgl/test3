package com.ald.fanbei.api.biz.bo;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 拆红包主页Bo
 *
 * @author wangli
 * @date 2018/5/3 17:22
 */
@Getter
@Setter
public class OpenRedPacketHomeBo extends AbstractSerial {

    // 总红包
    private Map<String, String> redPacket;

    // 提现记录
    private List<Map<String, String>> withdrawList = new ArrayList<>();

    // 提现总人数
    private int withdrawTotalNum;

    // 拆红包记录
    private List<Map<String, String>> openList = new ArrayList<>();

    // 是否绑定手机号（站外拆红包使用）
    private String isBindMobile;

    // 帮拆信息（站外拆红包使用）
    private Map<String, String> helpOpenInfo;

    // 是否可以帮拆红包（站外拆红包使用）
    private String isCanHelpOpen;
}
