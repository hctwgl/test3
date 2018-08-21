package com.ald.fanbei.api.biz.third.cuishou;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.HashMap;

public class CuiShouBackMoney  extends AbstractSerial {
    private HashMap<Integer,String> errorCodeMap = new HashMap<Integer, String>();
    public CuiShouBackMoney(){
        errorCodeMap.put(200,"success");
        errorCodeMap.put(201,"sign error");
        errorCodeMap.put(202,"type error");
        errorCodeMap.put(500,"error");
        errorCodeMap.put(203,"backmoney  0");
        errorCodeMap.put(204,"bill is null");
        errorCodeMap.put(205,"bill error");
        errorCodeMap.put(206,"process");
        errorCodeMap.put(207,"type error");

        errorCodeMap.put(301,"borrow not exists");
        errorCodeMap.put(302,"third tradeNo exists");
        errorCodeMap.put(303,"params not exists");
        errorCodeMap.put(304,"balance error");
        errorCodeMap.put(305,"user error");

    }



    private Integer code;
    private String msg;
    private Object data;

    public CuiShouBackMoney(Integer code,String msg){
        this.code =code;
        this.msg=msg;
    }


    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
        this.msg = errorCodeMap.get(code);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }




}
