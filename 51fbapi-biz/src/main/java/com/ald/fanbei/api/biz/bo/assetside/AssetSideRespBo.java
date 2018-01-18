package com.ald.fanbei.api.biz.bo.assetside;

import java.io.Serializable;

import com.ald.fanbei.api.common.exception.FanbeiAssetSideRespCode;

/**
 *@类现描述：资产方平台响应实体
 *@author chengkang 2017年11月29日 14:29:12
 *@version 
 *@注意：本内容仅限于杭州阿拉丁信息科技股份有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
public class AssetSideRespBo implements Serializable{

	private static final long serialVersionUID = -7413851396139275642L;
	private Integer code;
	private String message;
	private String data;
	
	public AssetSideRespBo() {
		super();
		this.code = FanbeiAssetSideRespCode.SUCCESS.getCode();
		this.message = FanbeiAssetSideRespCode.SUCCESS.getMsg();
	}
	
	public AssetSideRespBo(Integer code, String message,String data) {
		super();
		this.code = code;
		this.message = message;
		this.data = data;
	}

	public AssetSideRespBo(FanbeiAssetSideRespCode respCode,String data) {
		super();
		this.code = respCode.getCode();
		this.message = respCode.getDesc();
		this.data = data;
	}
	
	public void resetRespInfo(FanbeiAssetSideRespCode respCode) {
		this.code = respCode.getCode();
		this.message = respCode.getDesc();
	}
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "AssetSideRespBo [code=" + code + ", message=" + message
				+ ", data=" + data + "]";
	}
	
	
}
