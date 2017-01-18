package com.ald.fanbei.api.common.exception;

/**
 * 
 *@类AppException.java 的实现描述：异常类
 *@author 陈金虎 2017年1月16日 下午11:27:35
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class FanbeiException extends RuntimeException {
    
	private static final long serialVersionUID = 5540484171361000892L;
	
	private FanbeiExceptionCode errorCode;
    
    public FanbeiException() {
        super();
        errorCode = FanbeiExceptionCode.SYSTEM_ERROR;
    }

    public FanbeiException(String message) {
        super(message);
        errorCode = FanbeiExceptionCode.SYSTEM_ERROR;
    }

    public FanbeiException(String message, Throwable e) {
        super(message, e);
        errorCode = FanbeiExceptionCode.SYSTEM_ERROR;
    }

    public FanbeiException(Throwable e) {
        super(e);
        errorCode = FanbeiExceptionCode.SYSTEM_ERROR;
    }

    public FanbeiException(FanbeiExceptionCode CommonErrorCode) {
        super();
        errorCode = CommonErrorCode;
    }

    public FanbeiException(FanbeiExceptionCode CommonErrorCode, Throwable e) {
        super(e);
        errorCode = CommonErrorCode;
    }

    public FanbeiException(String message, FanbeiExceptionCode CommonErrorCode) {
        super(message);
        errorCode = CommonErrorCode;
    }

    public FanbeiException(String message, FanbeiExceptionCode CommonErrorCode, Throwable e) {
        super(message, e);
        errorCode = CommonErrorCode;
    }

    public FanbeiExceptionCode getErrorCode() {
        return errorCode;
    }
    

}
