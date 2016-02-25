package jm.tools.minijson;

public class JSONException extends RuntimeException {
	private static final long serialVersionUID = 5932767352480986988L;

	public JSONException() {
	}

	public JSONException(String msg) {
		super(msg, null);
	}

	public JSONException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public JSONException(Throwable cause) {
		super(cause == null ? null : cause.toString(), cause);
	}
}
