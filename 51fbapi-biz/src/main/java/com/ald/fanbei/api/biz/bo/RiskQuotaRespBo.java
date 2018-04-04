package com.ald.fanbei.api.biz.bo;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class RiskQuotaRespBo  implements Serializable{

	private static final long serialVersionUID = 1L;

	private String code;

	private Data data;

	private String msg;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public static class Data implements Serializable{
		
        	private static final long serialVersionUID = 1L;
        	private String consumerNo;
        	private Result[] results;
        	private String amount;
        	private Result[] bldResults;
        	private String bldAmount;
        	private String totalAmount;
        	private Result[] fqResults;
        	private String fqAmount;
        
        	public String getConsumerNo() {
        	    return consumerNo;
        	}
        
        	public void setConsumerNo(String consumerNo) {
        	    this.consumerNo = consumerNo;
        	}
        
        	public Result[] getResults() {
        	    return results;
        	}
        
        	public void setResults(Result[] results) {
        	    this.results = results;
        	}
        
        	public String getAmount() {
        	    return amount;
        	}
        
        	public void setAmount(String amount) {
        	    this.amount = amount;
        	}
        
        	public Result[] getBldResults() {
        	    return bldResults;
        	}
        
        	public void setBldResults(Result[] bldResults) {
        	    this.bldResults = bldResults;
        	}
        
        	public String getBldAmount() {
        	    return bldAmount;
        	}
        
        	public void setBldAmount(String bldAmount) {
        	    this.bldAmount = bldAmount;
        	}
        
        	public String getTotalAmount() {
        	    return totalAmount;
        	}
        
        	public void setTotalAmount(String totalAmount) {
        	    this.totalAmount = totalAmount;
        	}
        
        	public Result[] getFqResults() {
        	    return fqResults;
        	}
        
        	public void setFqResults(Result[] fqResults) {
        	    this.fqResults = fqResults;
        	}
        
        	public String getFqAmount() {
        	    return fqAmount;
        	}
        
        	public void setFqAmount(String fqAmount) {
        	    this.fqAmount = fqAmount;
        	}
        
        	@Override
        	public String toString() {
        	    return "Data [consumerNo=" + consumerNo + ", results=" + Arrays.toString(results) + ", amount=" + amount + ", bldResults=" + Arrays.toString(bldResults) + ", bldAmount=" + bldAmount + ", totalAmount=" + totalAmount + ", fqResults=" + Arrays.toString(fqResults) + ", fqAmount=" + fqAmount + "]";
        	}
	}
	
	public static class Result implements Serializable{
		
		private static final long serialVersionUID = 1L;
		private String scene ;
		private String result;
		public String getScene() {
			return scene;
		}
		public void setScene(String scene) {
			this.scene = scene;
		}
		public String getResult() {
			return result;
		}
		public void setResult(String result) {
			this.result = result;
		}
	}
	
	public boolean isSuccess() {
		return StringUtils.equals("0000", code);
	}
}
