package com.ald.jsd.admin.web.dto.resp;

public class Resp<T> {
	public RespHeader header = new RespHeader();
	public T body;
	
	/**
	 * @param body @nullable
	 * @param failMsg @nullable
	 * @return
	 */
	public static <T> Resp<T> fail(T body, String failMsg) {
		Resp<T> resp = new Resp<T>();
		resp.body = body;
		resp.header.succ = false;
		resp.header.msg = failMsg;
		
		return resp;
	}
	
	/**
	 * @param body @nullable
	 * @param succMsg @nullable
	 * @return
	 */
	public static <T> Resp<T> succ(T body, String succMsg) {
		Resp<T> resp = new Resp<T>();
		resp.body = body;
		resp.header.succ = true;
		resp.header.msg = succMsg;
		
		return resp;
	}
	
}