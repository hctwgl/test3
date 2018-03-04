package com.ald.fanbei.api.biz.third.util.cuishou;

import com.ald.fanbei.api.common.AbstractSerial;

import java.util.HashMap;

public class CuiShouBackMoney  extends AbstractSerial{
    private HashMap<Integer,String> errorCodeMap = new HashMap<Integer, String>();
    public CuiShouBackMoney(){
        errorCodeMap.put(200,"成功");
        errorCodeMap.put(201,"验签错误");
        errorCodeMap.put(202,"类型错误");
        errorCodeMap.put(500,"内部错误");
        errorCodeMap.put(203,"还款金额为 0");
        errorCodeMap.put(204,"帐单为空");
        errorCodeMap.put(205,"帐单 对不上");
        errorCodeMap.put(206,"处理中");
        errorCodeMap.put(207,"类型错误");

        errorCodeMap.put(301,"借款记录不存在");
        errorCodeMap.put(302,"第三方交易单号已被使用，不要进行重复操作");
        errorCodeMap.put(303,"请求参数缺失");
        errorCodeMap.put(304,"平帐失败");

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
