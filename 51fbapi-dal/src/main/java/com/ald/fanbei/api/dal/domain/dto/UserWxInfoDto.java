package com.ald.fanbei.api.dal.domain.dto;

import com.ald.fanbei.api.common.AbstractSerial;
import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户微信信息Dto
 *
 * @author wangli
 * @date 2018/5/4 14:58
 */
@Getter
@Setter
public class UserWxInfoDto extends AbstractSerial {

    public static final String KEY_OPEN_ID = "openid";

    public static final String KEY_AVATAR = "headimgurl";

    public static final String KEY_NICK = "nickname";

    private String openId;

    private String avatar;

    private String nick;

    private Long userId;

    private String userName;

    public UserWxInfoDto() {}

    public UserWxInfoDto(JSONObject jsonInfo) {
        openId = jsonInfo.getString(KEY_OPEN_ID);
        avatar = jsonInfo.getString(KEY_AVATAR);
        nick = jsonInfo.getString(KEY_NICK);
    }
}
