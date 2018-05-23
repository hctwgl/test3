package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.dal.domain.AfRedPacketSelfOpenDo;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户自拆红包Dto
 *
 * @author wangli
 * @date 2018/5/3 20:48
 */
@Getter
@Setter
public class AfRedPacketSelfOpenDto extends AfRedPacketSelfOpenDo {

    // 用户id
    private Long userId;

    // 用户头像
    private String userAvatar;

    // 用户昵称
    private String userNick;

}
