package jm.tools.minijson;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang.ArrayUtils;

public class JSONArray {
	private ArrayList myArrayList;

	public static JSONArray fromArray(Object[] array) throws JSONException {
		return new JSONArray(Arrays.asList(array));
	}

	public static JSONArray fromCollection(Collection collection)
			throws JSONException {
		return new JSONArray(collection);
	}

	public static JSONArray fromJSONTokener(JSONTokener tokener)
			throws JSONException {
		return new JSONArray(tokener);
	}

	public static JSONArray fromObject(Object object) throws JSONException {
		if ((object instanceof Collection))
			return fromCollection((Collection) object);
		if ((object instanceof JSONTokener))
			return fromJSONTokener((JSONTokener) object);
		if ((object instanceof String))
			return fromString((String) object);
		if ((object != null) && (object.getClass().isArray())) {
			Class type = object.getClass().getComponentType();

			if (!type.isPrimitive()) {
				return fromArray((Object[]) object);
			}
			if (type == Boolean.TYPE)
				return new JSONArray((boolean[]) object);
			if (type == Byte.TYPE)
				return new JSONArray((byte[]) object);
			if (type == Short.TYPE)
				return new JSONArray((short[]) object);
			if (type == Integer.TYPE)
				return new JSONArray((int[]) object);
			if (type == Long.TYPE)
				return new JSONArray((long[]) object);
			if (type == Float.TYPE)
				return new JSONArray((float[]) object);
			if (type == Double.TYPE)
				return new JSONArray((double[]) object);
			if (type == Character.TYPE) {
				return new JSONArray((char[]) object);
			}
			throw new IllegalArgumentException("Unsupported type");
		}

		throw new IllegalArgumentException("Unsupported type");
	}

	public static JSONArray fromString(String string) throws JSONException {
		return new JSONArray(string);
	}

	public JSONArray() {
		this.myArrayList = new ArrayList();
	}

	public JSONArray(boolean[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(ArrayUtils.toObject(array)));
	}

	public JSONArray(byte[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(ArrayUtils.toObject(array)));
	}

	public JSONArray(char[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(JSONUtils.toObject(array)));
	}

	public JSONArray(Collection collection) throws JSONException {
		this.myArrayList = new ArrayList();
		Iterator elements;
		if (collection != null)
			for (elements = collection.iterator(); elements.hasNext();) {
				Object element = elements.next();
				if (JSONUtils.isArray(element))
					this.myArrayList.add(fromObject(element));
				else if (JSONUtils.isFunction(element)) {
					if ((element instanceof String))
						this.myArrayList.add(JSONFunction
								.parse((String) element));
					else
						this.myArrayList.add(element);
				} else if (JSONUtils.isObject(element))
					this.myArrayList.add(JSONObject.fromObject(element));
				else
					this.myArrayList.add(element);
			}
	}

	public JSONArray(double[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(ArrayUtils.toObject(array)));
	}

	public JSONArray(float[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(ArrayUtils.toObject(array)));
	}

	public JSONArray(int[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(ArrayUtils.toObject(array)));
	}

	public JSONArray(JSONTokener x) throws JSONException {
		this();
		if (x.nextClean() != '[') {
			throw x.syntaxError("A JSONArray text must start with '['");
		}
		if (x.nextClean() == ']') {
			return;
		}
		x.back();
		while (true) {
			if (x.nextClean() == ',') {
				x.back();
				this.myArrayList.add(null);
			} else {
				x.back();
				Object v = x.nextValue();
				if (!JSONUtils.isFunctionHeader(v)) {
					this.myArrayList.add(v);
				} else {
					Matcher matcher = JSONUtils.FUNCTION_PARAMS_PATTERN
							.matcher((String) v);
					matcher.matches();
					String params = matcher.group(1);

					int i = 0;
					StringBuffer sb = new StringBuffer();
					while (true) {
						char ch = x.next();
						if (ch == 0) {
							break;
						}
						if (ch == '{') {
							i++;
						}
						if (ch == '}') {
							i--;
						}
						sb.append(ch);
						if (i == 0) {
							break;
						}
					}
					if (i != 0) {
						throw x.syntaxError("Unbalanced '{' or '}' on prop: "
								+ v);
					}

					String text = sb.toString();
					text = text.substring(1, text.length() - 1).trim();

					this.myArrayList.add(new JSONFunction(
							params != null ? params.split(",") : null, text));
				}
			}

			switch (x.nextClean()) {
			case ',':
			case ';':
				if (x.nextClean() == ']') {
					return;
				}
				x.back();
				break;
			case ']':
				return;
			default:
				throw x.syntaxError("Expected a ',' or ']'");
			}
		}
	}

	public JSONArray(long[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(ArrayUtils.toObject(array)));
	}

	public JSONArray(Object[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		for (int i = 0; i < array.length; i++) {
			Object element = array[i];
			if (JSONUtils.isArray(element))
				this.myArrayList.add(fromObject(element));
			else if (JSONUtils.isObject(element))
				this.myArrayList.add(JSONObject.fromObject(element));
			else
				this.myArrayList.add(element);
		}
	}

	public JSONArray(short[] array) throws JSONException {
		this.myArrayList = new ArrayList();
		this.myArrayList.addAll(Arrays.asList(ArrayUtils.toObject(array)));
	}

	public JSONArray(String string) throws JSONException {
		this(new JSONTokener(string));
	}

	public Object get(int index) throws JSONException {
		Object o = opt(index);
		if (o == null) {
			throw new JSONException("JSONArray[" + index + "] not found.");
		}
		return o;
	}

	public boolean getBoolean(int index) throws JSONException {
		Object o = get(index);
		if ((o.equals(Boolean.FALSE))
				|| (((o instanceof String)) && (((String) o)
						.equalsIgnoreCase("false")))) {
			return false;
		}
		if ((o.equals(Boolean.TRUE))
				|| (((o instanceof String)) && (((String) o)
						.equalsIgnoreCase("true")))) {
			return true;
		}
		throw new JSONException("JSONArray[" + index + "] is not a Boolean.");
	}

	public double getDouble(int index) throws JSONException {
		Object o = get(index);
		try {
			return (o instanceof Number) ? ((Number) o).doubleValue() : Double
					.parseDouble((String) o);
		} catch (Exception e) {
		}
		throw new JSONException("JSONArray[" + index + "] is not a number.");
	}

	public int getInt(int index) throws JSONException {
		Object o = get(index);
		return (o instanceof Number) ? ((Number) o).intValue()
				: (int) getDouble(index);
	}

	public JSONArray getJSONArray(int index) throws JSONException {
		Object o = get(index);
		if ((o instanceof JSONArray)) {
			return (JSONArray) o;
		}
		throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
	}

	public JSONObject getJSONObject(int index) throws JSONException {
		Object o = get(index);
		if ((o instanceof JSONObject)) {
			return (JSONObject) o;
		}
		throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
	}

	public long getLong(int index) throws JSONException {
		Object o = get(index);
		return (o instanceof Number) ? ((Number) o).longValue()
				: (long) getDouble(index);
	}

	public String getString(int index) throws JSONException {
		return get(index).toString();
	}

	public String join(String separator) throws JSONException {
		int len = length();
		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < len; i++) {
			if (i > 0) {
				sb.append(separator);
			}
			sb.append(JSONUtils.valueToString(this.myArrayList.get(i)));
		}
		return sb.toString();
	}

	public int length() {
		return this.myArrayList.size();
	}

	public Object opt(int index) {
		return (index < 0) || (index >= length()) ? null : this.myArrayList
				.get(index);
	}

	public boolean optBoolean(int index) {
		return optBoolean(index, false);
	}

	public boolean optBoolean(int index, boolean defaultValue) {
		try {
			return getBoolean(index);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public double optDouble(int index) {
		return optDouble(index, (0.0D / 0.0D));
	}

	public double optDouble(int index, double defaultValue) {
		try {
			return getDouble(index);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public int optInt(int index) {
		return optInt(index, 0);
	}

	public int optInt(int index, int defaultValue) {
		try {
			return getInt(index);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public JSONArray optJSONArray(int index) {
		Object o = opt(index);
		return (o instanceof JSONArray) ? (JSONArray) o : null;
	}

	public JSONObject optJSONObject(int index) {
		Object o = opt(index);
		return (o instanceof JSONObject) ? (JSONObject) o : null;
	}

	public long optLong(int index) {
		return optLong(index, 0L);
	}

	public long optLong(int index, long defaultValue) {
		try {
			return getLong(index);
		} catch (Exception e) {
		}
		return defaultValue;
	}

	public String optString(int index) {
		return optString(index, "");
	}

	public String optString(int index, String defaultValue) {
		Object o = opt(index);
		return o != null ? o.toString() : defaultValue;
	}

	public JSONArray put(boolean value) {
		put(value ? Boolean.TRUE : Boolean.FALSE);
		return this;
	}

	public JSONArray put(Collection value) throws JSONException {
		put(fromCollection(value));
		return this;
	}

	public JSONArray put(double value) throws JSONException {
		Double d = new Double(value);
		JSONUtils.testValidity(d);
		put(d);
		return this;
	}

	public JSONArray put(int value) {
		put(new Integer(value));
		return this;
	}

	public JSONArray put(int index, boolean value) throws JSONException {
		put(index, value ? Boolean.TRUE : Boolean.FALSE);
		return this;
	}

	public JSONArray put(int index, Collection value) throws JSONException {
		put(index, new JSONArray(value));
		return this;
	}

	public JSONArray put(int index, double value) throws JSONException {
		put(index, new Double(value));
		return this;
	}

	public JSONArray put(int index, int value) throws JSONException {
		put(index, new Integer(value));
		return this;
	}

	public JSONArray put(int index, long value) throws JSONException {
		put(index, new Long(value));
		return this;
	}

	public JSONArray put(int index, Map value) throws JSONException {
		put(index, new JSONObject(value));
		return this;
	}

	public JSONArray put(int index, Object value) throws JSONException {
		JSONUtils.testValidity(value);
		if (index < 0) {
			throw new JSONException("JSONArray[" + index + "] not found.");
		}
		if (index < length()) {
			this.myArrayList.set(index, value);
		} else {
			while (index != length()) {
				put(JSONNull.getInstance());
			}
			put(value);
		}
		return this;
	}

	public JSONArray put(long value) {
		put(new Long(value));
		return this;
	}

	public JSONArray put(Map value) throws JSONException {
		put(new JSONObject(value));
		return this;
	}

	public JSONArray put(Object value) {
		this.myArrayList.add(value);
		return this;
	}

	public Object[] toArray() {
		return this.myArrayList.toArray();
	}

	public JSONObject toJSONObject(JSONArray names) throws JSONException {
		if ((names == null) || (names.length() == 0) || (length() == 0)) {
			return null;
		}
		JSONObject jo = new JSONObject();
		for (int i = 0; i < names.length(); i++) {
			jo.put(names.getString(i), opt(i));
		}
		return jo;
	}

	public String toString() {
		try {
			return '[' + join(",") + ']';
		} catch (Exception e) {
		}
		return null;
	}

	public String toString(int indentFactor) throws JSONException {
		return toString(indentFactor, 0);
	}

	public Writer write(Writer writer) throws JSONException {
		try {
			boolean b = false;
			int len = length();

			writer.write(91);

			for (int i = 0; i < len; i++) {
				if (b) {
					writer.write(44);
				}
				Object v = this.myArrayList.get(i);
				if ((v instanceof JSONObject))
					((JSONObject) v).write(writer);
				else if ((v instanceof JSONArray))
					((JSONArray) v).write(writer);
				else {
					writer.write(JSONUtils.valueToString(v));
				}
				b = true;
			}
			writer.write(93);
			return writer;
		} catch (IOException e) {
			throw new JSONException(e);
		}
	}

	String toString(int indentFactor, int indent) throws JSONException {
		int len = length();
		if (len == 0) {
			return "[]";
		}

		StringBuffer sb = new StringBuffer("[");
		if (len == 1) {
			sb.append(JSONUtils.valueToString(this.myArrayList.get(0),
					indentFactor, indent));
		} else {
			int newindent = indent + indentFactor;
			sb.append('\n');
			for (int i = 0; i < len; i++) {
				if (i > 0) {
					sb.append(",\n");
				}
				for (int j = 0; j < newindent; j++) {
					sb.append(' ');
				}
				sb.append(JSONUtils.valueToString(this.myArrayList.get(i),
						indentFactor, newindent));
			}
			sb.append('\n');
			for (int i = 0; i < indent; i++) {
				sb.append(' ');
			}
		}
		sb.append(']');
		return sb.toString();
	}
}