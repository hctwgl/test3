package com.ald.fanbei.api.web.vo;

import com.ald.fanbei.api.common.AbstractSerial;
import lombok.Getter;
import lombok.Setter;

/**
 * 拆红包参数Vo
 *
 * @author wangli
 * @date 2018/5/14 16:05
 */
@Getter
@Setter
public class OpenRedPacketParamVo extends AbstractSerial {

    private String code;

    private Long shareId;

    private String sourceType;

    private Long id;

    private String mobile;

    private String verifyCode;

    private String token;

    private String bsqToken;

    @Override
    public String toString() {
        return "OpenRedPacketParamVo{" +
                "code='" + code + '\'' +
                ", shareId=" + shareId +
                ", sourceType='" + sourceType + '\'' +
                ", id=" + id +
                ", mobile='" + mobile + '\'' +
                ", verifyCode='" + verifyCode + '\'' +
                ", token='" + token + '\'' +
                ", bsqToken='" + bsqToken + '\'' +
                '}';
    }
}
