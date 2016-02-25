package jm.tools.httpreqresp.result;

import jm.tools.minijson.JSONObject;

/**
 * 
 * @author yjm
 *
 */
public class BaseMsgResponse extends MsgResponse {

	@Override
	protected JSONObject getCustomRespData() {
		return null;
	}

}
