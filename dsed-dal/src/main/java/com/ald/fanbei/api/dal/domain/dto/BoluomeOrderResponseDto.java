package com.ald.fanbei.api.dal.domain.dto;

public class BoluomeOrderResponseDto {
    private Integer code;
    private String success;
    private String msg;
    private String data;

    public Integer getCode() {
	return code;
    }

    public void setCode(Integer code) {
	this.code = code;
    }

    public String getSuccess() {
	return success;
    }

    public void setSuccess(String success) {
	this.success = success;
    }

    public String getMsg() {
	return msg;
    }

    public void setMsg(String msg) {
	this.msg = msg;
    }

    public String getData() {
	return data;
    }

    public void setData(String data) {
	this.data = data;
    }

    @Override
    public String toString() {
	return "BoluomeOrderResponse [code=" + code + ", success=" + success + ", msg=" + msg + ", data=" + data + "]";
    }

}
