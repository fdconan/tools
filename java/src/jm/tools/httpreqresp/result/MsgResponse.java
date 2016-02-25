package jm.tools.httpreqresp.result;

import java.util.Iterator;

import jm.tools.minijson.JSONObject;

public abstract class MsgResponse {
	/**
	 * 处理结果
	 */
	private String result;
	/**
	 * 处理结果描述
	 */
	private String desc;
	/**
	 * 请求流水号
	 */
	private String serialId;
	/**
	 * 响应流水号
	 */
	private String respSerialId = null;
	
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getSerialId() {
		return serialId;
	}
	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}
	public String getRespSerialId() {
		return respSerialId;
	}
	public void setRespSerialId(String respSerialId) {
		this.respSerialId = respSerialId;
	}
	
	public String getResponseData(){
		JSONObject respData = JSONObject.fromString("{}");
		respData.put("result", result);
		respData.put("desc", desc);
		respData.put("serialId", serialId);
		respData.put("respSerialId", respSerialId);
		/*
		JSONObject extRespData = getExtRespData();
		if(extRespData != null){
			respData.put("extRespData", extRespData.get("extRespData"));
		}
		*/
		
		JSONObject jsonObj = this.getCustomRespData();
		if(jsonObj != null){
			Iterator iter = jsonObj.keys();
			while(iter.hasNext()){
				String key = (String)iter.next();
				respData.put(key, jsonObj.get(key));
			}
		}
		
		return respData.toString();
	}
	
	/**
	 * 
	 * @return json 格式字符串
	 */
	protected abstract JSONObject getCustomRespData();
	
	/*
	protected JSONObject getExtRespData(){
		return null;
	}
	*/
}
