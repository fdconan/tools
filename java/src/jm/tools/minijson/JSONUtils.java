package jm.tools.minijson;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;

public class JSONUtils {
	public static final Pattern FUNCTION_HEADER_PATTERN = Pattern
			.compile("^function[ ]?\\(.*\\)$");
	public static final Pattern FUNCTION_PARAMS_PATTERN = Pattern
			.compile("^function[ ]?\\((.*?)\\)$");
	public static final Pattern FUNCTION_PATTERN = Pattern
			.compile("^function[ ]?\\(.*\\)[ ]?\\{.*\\}$");

	public static String doubleToString(double d) {
		if ((Double.isInfinite(d)) || (Double.isNaN(d))) {
			return "null";
		}

		String s = Double.toString(d);
		if ((s.indexOf('.') > 0) && (s.indexOf('e') < 0)
				&& (s.indexOf('E') < 0)) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}

	public static Map getJSONProperties(JSONObject jsonObject) {
		Map properties = new HashMap();
		for (Iterator keys = jsonObject.keys(); keys.hasNext();) {
			String key = (String) keys.next();
			properties.put(key, getJSONType(jsonObject.get(key)));
		}
		return properties;
	}

	public static Object getJSONType(Object obj) throws JSONException {
		if (isNull(obj))
			return "object";
		if (isArray(obj))
			return "array";
		if (isFunction(obj))
			return "function";
		if (isBoolean(obj))
			return "boolean";
		if (isNumber(obj))
			return "number";
		if (isString(obj))
			return "string";
		if (isObject(obj)) {
			return "object";
		}
		throw new JSONException("Unsupported type");
	}

	public static Map getProperties(JSONObject jsonObject) {
		Map properties = new HashMap();
		for (Iterator keys = jsonObject.keys(); keys.hasNext();) {
			String key = (String) keys.next();
			properties.put(key, getTypeClass(jsonObject.get(key)));
		}
		return properties;
	}

	public static Class getTypeClass(Object obj) throws JSONException {
		if (isNull(obj))
			return Object.class;
		if (isArray(obj))
			return new Object[0].getClass();
		if (isFunction(obj))
			return JSONFunction.class;
		if (isBoolean(obj))
			return Boolean.class;
		if (isNumber(obj))
			return Double.class;
		if (isString(obj))
			return String.class;
		if (isObject(obj)) {
			return Object.class;
		}
		throw new JSONException("Unsupported type");
	}

	public static boolean isArray(Object obj) {
		if ((obj != null) && (obj.getClass().isArray())) {
			return true;
		}
		if ((obj instanceof Collection)) {
			return true;
		}
		return false;
	}

	public static boolean isBoolean(Object obj) {
		if ((obj instanceof Boolean)) {
			return true;
		}
		if ((obj != null) && (obj.getClass() == Boolean.TYPE)) {
			return true;
		}
		return false;
	}

	public static boolean isFunction(Object obj) {
		if (((obj instanceof String)) && (obj != null)) {
			String str = (String) obj;
			return FUNCTION_PATTERN.matcher(str).matches();
		}

		if (((obj instanceof JSONFunction)) && (obj != null)) {
			return true;
		}
		return false;
	}

	public static boolean isFunctionHeader(Object obj) {
		if (((obj instanceof String)) && (obj != null)) {
			String str = (String) obj;
			return FUNCTION_HEADER_PATTERN.matcher(str).matches();
		}

		return false;
	}

	public static boolean isNull(Object obj) {
		if ((obj instanceof JSONObject)) {
			return ((JSONObject) obj).isNullObject();
		}
		return JSONNull.getInstance().equals(obj);
	}

	public static boolean isNumber(Object obj) {
		if (((obj != null) && (obj.getClass() == Byte.TYPE))
				|| ((obj != null) && (obj.getClass() == Short.TYPE))
				|| ((obj != null) && (obj.getClass() == Integer.TYPE))
				|| ((obj != null) && (obj.getClass() == Long.TYPE))
				|| ((obj != null) && (obj.getClass() == Float.TYPE))
				|| ((obj != null) && (obj.getClass() == Double.TYPE))) {
			return true;
		}
		if (((obj instanceof Byte)) || ((obj instanceof Short))
				|| ((obj instanceof Integer)) || ((obj instanceof Long))
				|| ((obj instanceof Float)) || ((obj instanceof Double))) {
			return true;
		}
		return false;
	}

	public static boolean isObject(Object obj) {
		return ((!isNumber(obj)) && (!isString(obj)) && (!isBoolean(obj)) && (!isArray(obj)))
				|| (isNull(obj));
	}

	public static boolean isString(Object obj) {
		if ((obj instanceof String)) {
			return true;
		}
		if ((obj instanceof Character)) {
			return true;
		}
		if ((obj != null) && (obj.getClass() == Character.TYPE)) {
			return true;
		}
		return false;
	}

	public static String numberToString(Number n) throws JSONException {
		if (n == null) {
			throw new JSONException("Null pointer");
		}
		testValidity(n);

		String s = n.toString();
		if ((s.indexOf('.') > 0) && (s.indexOf('e') < 0)
				&& (s.indexOf('E') < 0)) {
			while (s.endsWith("0")) {
				s = s.substring(0, s.length() - 1);
			}
			if (s.endsWith(".")) {
				s = s.substring(0, s.length() - 1);
			}
		}
		return s;
	}

	public static String quote(String string) {
		if (isFunction(string)) {
			return string;
		}
		if ((string == null) || (string.length() == 0)) {
			return "\"\"";
		}

		char c = '\000';

		int len = string.length();
		StringBuffer sb = new StringBuffer(len + 4);

		sb.append('"');
		for (int i = 0; i < len; i++) {
			char b = c;
			c = string.charAt(i);
			switch (c) {
			case '"':
			case '\\':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<') {
					sb.append('\\');
				}
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			default:
				if (c < ' ') {
					String t = "000" + Integer.toHexString(c);
					sb.append("\\u" + t.substring(t.length() - 4));
				} else {
					sb.append(c);
				}
				break;
			}
		}
		sb.append('"');
		return sb.toString();
	}

	public static void testValidity(Object o) throws JSONException {
		if (o != null)
			if ((o instanceof Double)) {
				if ((((Double) o).isInfinite()) || (((Double) o).isNaN()))
					throw new JSONException(
							"JSON does not allow non-finite numbers");
			} else if (((o instanceof Float))
					&& ((((Float) o).isInfinite()) || (((Float) o).isNaN())))
				throw new JSONException(
						"JSON does not allow non-finite numbers.");
	}

	public static Object[] toObject(char[] array) {
		if (array == null)
			return null;
		if (array.length == 0) {
			return ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY;
		}
		Character[] result = new Character[array.length];
		for (int i = 0; i < array.length; i++) {
			result[i] = new Character(array[i]);
		}
		return result;
	}

	public static String valueToString(Object value) throws JSONException {
		if ((value == null) || (value.equals(null))) {
			return "null";
		}
		if ((value instanceof JSONFunction)) {
			return ((JSONFunction) value).toString();
		}
		if ((value instanceof JSONString)) {
			Object o;
			try {
				o = ((JSONString) value).toJSONString();
			} catch (Exception e) {
				throw new JSONException(e);
			}
			if ((o instanceof String)) {
				return (String) o;
			}
			throw new JSONException("Bad value from toJSONString: " + o);
		}
		if ((value instanceof Number)) {
			return numberToString((Number) value);
		}
		if (((value instanceof Boolean)) || ((value instanceof JSONObject))
				|| ((value instanceof JSONArray))) {
			return value.toString();
		}
		return quote(value.toString());
	}

	public static String valueToString(Object value, int indentFactor,
			int indent) throws JSONException {
		if ((value == null) || (value.equals(null))) {
			return "null";
		}
		if ((value instanceof JSONFunction))
			return ((JSONFunction) value).toString();
		try {
			if ((value instanceof JSONString)) {
				Object o = ((JSONString) value).toJSONString();
				if ((o instanceof String)) {
					return (String) o;
				}
			}
		} catch (Exception e) {
		}
		if ((value instanceof Number)) {
			return numberToString((Number) value);
		}
		if ((value instanceof Boolean)) {
			return value.toString();
		}
		if ((value instanceof JSONObject)) {
			return ((JSONObject) value).toString(indentFactor, indent);
		}
		if ((value instanceof JSONArray)) {
			return ((JSONArray) value).toString(indentFactor, indent);
		}
		return quote(value.toString());
	}
}
