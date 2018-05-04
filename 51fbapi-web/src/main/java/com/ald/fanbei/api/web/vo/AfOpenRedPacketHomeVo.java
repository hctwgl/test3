package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * 拆红包主页Vo
 *
 * @author wangli
 * @date 2018/5/3 17:22
 */
@Getter
@Setter
public class AfOpenRedPacketHomeVo extends AbstractSerial {

    /**
     * 红包对象id的key
     *
     * @author wangli
     * @date 2018/5/3 20:42
     */
    public static final String KEY_REDPACKET_ID = "id";

    // 总红包
    private Map<String, String> redPacket;

    // 提现记录
    private List<Map<String, String>> withdrawList;

    // 提现总人数
    private int withdrawTotalNum;

    // 拆红包记录
    private List<Map<String, String>> openList;

    // 当前用户id（站外拆红包使用）
    private String userId;

    // 帮拆信息（站外拆红包使用）
    private Map<String, String> helpOpenInfo;

    // 是否可以帮拆红包（站外拆红包使用）
    private String isCanOpen;
}
