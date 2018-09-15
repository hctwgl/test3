package com.ald.fanbei.api.common.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 陈金虎 2017年1月16日 下午11:27:35
 * 使用说明:
 * throw new FanbeiException("测试异常");//app提示:通用的流量过大系统开启小差
 * throw new FanbeiException("测试异常",true);//显示动态参数,app提示:测试异常
 * throw new FanbeiException("config_withdraw_limit","参数1","参数2");//由资源配置中读取,并会将资源中的如(从1开始):"测试&1替换&2符的使用",替换符替换成"参数1" "参数2"
 * @类AppException.java 的实现描述：异常类
 * @注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 5540484171361000892L;

    private BizExceptionCode errorCode;
    //动态的异常，可以根据资源文件中配置
    private Boolean dynamicMsg;
    private String resourceType;
    public Map<String,String> paramsMap = new HashMap<String,String>();
    public BizException() {
        super();
        errorCode = BizExceptionCode.SYSTEM_ERROR;
    }

    public BizException(String message) {
        super(message);
        errorCode = BizExceptionCode.SYSTEM_ERROR;
        dynamicMsg = true;
//        errorCode.setDesc(message);
    }

    /**
     * app显示传入参数，而不是通用的流量过大开启小差
     * @param message
     * @param dynamicMsg
     */
    public BizException(String message, Boolean dynamicMsg) {
        super(message);
        this.dynamicMsg = dynamicMsg;
        errorCode = BizExceptionCode.SYSTEM_ERROR;
        //errorCode.setDesc(message);
    }

//    /**
//     * 动态配置异常
//     * @param resourceType 异常类型
//     * @param args 多个动态参数
//     */
//    public FanbeiException( String resourceType, String... args) {
//        super("资源配置,动态参数");
//        this.resourceType = resourceType;
//        if (args != null) {
//            for (int i = 0; i < args.length; i++) {
//                paramsMap.put("&"+(i+1),args[i]);
//            }
//        }
//        errorCode = FanbeiExceptionCode.SYSTEM_ERROR;
//    }

    public BizException(String message, Throwable e) {
        super(message, e);
        errorCode = BizExceptionCode.SYSTEM_ERROR;
    }

    public BizException(Throwable e) {
        super(e);
        errorCode = BizExceptionCode.SYSTEM_ERROR;
    }

    public BizException(BizExceptionCode CommonErrorCode) {
        super(CommonErrorCode.getErrorMsg());
        errorCode = CommonErrorCode;
    }

    public BizException(BizExceptionCode CommonErrorCode, Throwable e) {
        super(e);
        errorCode = CommonErrorCode;
    }

    public BizException(String message, BizExceptionCode CommonErrorCode) {
        super(message);
        errorCode = CommonErrorCode;
    }

    public BizException(String message, BizExceptionCode CommonErrorCode, Throwable e) {
        super(message, e);
        errorCode = CommonErrorCode;
    }

    public BizExceptionCode getErrorCode() {
        return errorCode;
    }


    public Boolean getDynamicMsg() {
        return dynamicMsg;
    }

    public void setDynamicMsg(Boolean dynamicMsg) {
        this.dynamicMsg = dynamicMsg;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
