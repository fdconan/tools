package jm.tools.httpreqresp.result;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import jm.tools.minijson.JSONObject;

/**
 * 
 * @author yjm
 *
 */
public abstract class MsgResponseUtil {
	public static void msgResp(MsgResponse msgResp, JSONObject jsonObject, String resultCode, String desc, HttpServletResponse resp){
		String serialId = "00000000";
		if(jsonObject != null){
			serialId = jsonObject.optString("serialId", "");
			if(serialId.length() == 0){
				serialId = "00000000";
			}
		}
		msgResp.setSerialId(serialId);
		msgResp.setResult(resultCode);
		msgResp.setDesc(desc);
		if(msgResp.getRespSerialId() == null) msgResp.setRespSerialId(Timestamper.next()+"");
		
		OutputStream output = null;
		try{
			String respData = msgResp.getResponseData();
			//CommonLogger.logger.debug("return data->" +respData );

			resp.setContentType("text/json;charset=utf-8");
			output = resp.getOutputStream();
			output.write(respData.getBytes("utf-8"));
			output.flush();
		}catch(Exception e){
			//CommonLogger.logger.error("[MsgResponseUtil]ÏûÏ¢·µ»Ø´íÎó:"+e.getMessage(), e);
		}finally {
			if(output!=null) {
				try {
					output.flush();
				} catch (Exception e1) {
					//CommonLogger.logger.error(e1.getMessage(), e1);
				}
				try {
					output.close();
				} catch (Exception e2) {
					//CommonLogger.logger.error(e2.getMessage(), e2);
				}
			}
		}
		
	}
}
