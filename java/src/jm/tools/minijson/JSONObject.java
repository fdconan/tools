package jm.tools.minijson;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class JSONObject
{
  private static final Log log = LogFactory.getLog(JSONObject.class);
  private HashMap myHashMap;
  private boolean nullObject;

  public static JSONObject fromBean(Object bean)
    throws JSONException
  {
    if (bean == null) {
      return new JSONObject(true);
    }
    JSONObject jsonObject = new JSONObject();
    if (bean != null) {
      if (JSONUtils.isArray(bean)) {
        throw new IllegalArgumentException("'bean' is an array. Use JSONArray instead");
      }
      if ((bean instanceof Map))
        jsonObject = fromMap((Map)bean);
      else {
        try {
          PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(bean);
          for (int i = 0; i < pds.length; i++) {
            String key = pds[i].getName();
            if (!"class".equals(key))
            {
              Class type = pds[i].getPropertyType();
              Object value = PropertyUtils.getProperty(bean, key);
              if (String.class.isAssignableFrom(type))
                jsonObject.put(key, value == null ? "" : value);
              else if (JSONUtils.isArray(value))
                jsonObject.put(key, JSONArray.fromObject(value));
              else if (JSONUtils.isFunction(value))
                jsonObject.put(key, value);
              else if (JSONUtils.isObject(value))
                jsonObject.put(key, fromObject(value));
              else
                jsonObject.put(key, value);
            }
          }
        }
        catch (Exception e) {
          log.warn(e);
        }
      }
    }
    return jsonObject;
  }

  public static JSONObject fromJSONTokener(JSONTokener tokener)
    throws JSONException
  {
    return new JSONObject(tokener);
  }

  public static JSONObject fromMap(Map map)
    throws JSONException
  {
    return new JSONObject(map);
  }

  public static JSONObject fromObject(Object object)
    throws JSONException
  {
    if (JSONUtils.isNull(object)) {
      return new JSONObject(true);
    }
    if ((object instanceof JSONTokener))
      return fromJSONTokener((JSONTokener)object);
    if ((object instanceof Map))
      return fromMap((Map)object);
    if ((object instanceof String)) {
      return fromString((String)object);
    }
    return fromBean(object);
  }

  public static JSONObject fromString(String str)
    throws JSONException
  {
    if ((str == null) || ("null".compareToIgnoreCase(str) == 0)) {
      return new JSONObject(true);
    }
    return new JSONObject(str);
  }

  public JSONObject()
  {
    this.myHashMap = new HashMap();
  }

  public JSONObject(boolean isNull)
  {
    this();
    this.nullObject = isNull;
  }

  public JSONObject(JSONObject jo, String[] sa)
    throws JSONException
  {
    this();
    for (int i = 0; i < sa.length; i++)
      putOpt(sa[i], jo.opt(sa[i]));
  }

  public JSONObject(JSONTokener x)
    throws JSONException
  {
    this();

    if (x.matches("null.*")) {
      this.nullObject = true;
      return;
    }

    if (x.nextClean() != '{')
      throw x.syntaxError("A JSONObject text must begin with '{'");
    while (true)
    {
      char c = x.nextClean();
      switch (c)
      {
      case '\000':
        throw x.syntaxError("A JSONObject text must end with '}'");
      case '}':
        return;
      }
      x.back();
      String key = x.nextValue().toString();

      c = x.nextClean();
      if (c == '=') {
        if (x.next() != '>')
          x.back();
      }
      else if (c != ':') {
        throw x.syntaxError("Expected a ':' after a key");
      }
      Object v = x.nextValue();
      if (!JSONUtils.isFunctionHeader(v)) {
        this.myHashMap.put(key, v);
      }
      else {
        Matcher matcher = JSONUtils.FUNCTION_PARAMS_PATTERN.matcher((String)v);
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
          throw x.syntaxError("Unbalanced '{' or '}' on prop: " + v);
        }

        String text = sb.toString();
        text = text.substring(1, text.length() - 1).trim();

        this.myHashMap.put(key, new JSONFunction(params != null ? params.split(",") : null, text));
      }

      switch (x.nextClean())
      {
      case ',':
      case ';':
        if (x.nextClean() == '}') {
          return;
        }
        x.back();
        break;
      case '}':
        return;
      default:
        throw x.syntaxError("Expected a ',' or '}'");
      }
    }
  }

  public JSONObject(Map map)
    throws JSONException
  {
    if (map == null) {
      this.nullObject = true;
      return;
    }

    this.myHashMap = new HashMap();
    if (map != null) {
      Iterator entries = map.entrySet().iterator();
      while (entries.hasNext()) {
        Map.Entry entry = (Map.Entry)entries.next();
        Object k = entry.getKey();
        String key = (k instanceof String) ? (String)k : String.valueOf(k);
        Object value = entry.getValue();

        if (JSONUtils.isArray(value))
          put(key, JSONArray.fromObject(value));
        else if (JSONUtils.isFunction(value))
          put(key, value);
        else if (JSONUtils.isObject(value))
          put(key, fromObject(value));
        else if (JSONUtils.isString(value))
          put(key, value == null ? "" : value);
        else
          put(key, value);
      }
    }
  }

  public JSONObject(Object object, String[] names)
    throws JSONException
  {
    this();
    if (object == null) {
      this.nullObject = true;
      return;
    }
    try
    {
      PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(object);
      for (int i = 0; i < pds.length; i++) {
        String key = pds[i].getName();
        if ((!"class".equals(key)) && (ArrayUtils.contains(names, key)))
        {
          Class type = pds[i].getPropertyType();
          Object value = PropertyUtils.getProperty(object, key);
          if (String.class.isAssignableFrom(type))
            put(key, value == null ? "" : value);
          else if (JSONUtils.isFunction(value))
            put(key, value);
          else if (JSONUtils.isArray(value))
            put(key, JSONArray.fromObject(value));
          else if (JSONUtils.isObject(value))
            put(key, fromObject(value));
          else
            put(key, value);
        }
      }
    }
    catch (Exception e) {
      log.warn(e);
    }
  }

  public JSONObject(String string)
    throws JSONException
  {
    this(new JSONTokener(string));
  }

  public JSONObject accumulate(String key, Object value)
    throws JSONException
  {
    if (isNullObject()) {
      throw new JSONException("Can't accumulate on null object");
    }

    JSONUtils.testValidity(value);
    Object o = opt(key);
    if (o == null)
      put(key, value);
    else if ((o instanceof JSONArray))
      ((JSONArray)o).put(value);
    else {
      put(key, new JSONArray().put(o).put(value));
    }

    return this;
  }

  public JSONObject append(String key, Object value)
    throws JSONException
  {
    if (isNullObject()) {
      throw new JSONException("Can't append on null object");
    }
    JSONUtils.testValidity(value);
    Object o = opt(key);
    if (o == null) {
      put(key, new JSONArray().put(value)); } else {
      if ((o instanceof JSONArray)) {
        throw new JSONException("JSONObject[" + key + "] is not a JSONArray.");
      }
      put(key, new JSONArray().put(o).put(value));
    }

    return this;
  }

  public Object get(String key)
    throws JSONException
  {
    verifyIsNull();
    Object o = opt(key);
    if (o == null) {
      throw new JSONException("JSONObject[" + JSONUtils.quote(key) + "] not found.");
    }
    return o;
  }

  public boolean getBoolean(String key)
    throws JSONException
  {
    verifyIsNull();
    Object o = get(key);
    if ((o.equals(Boolean.FALSE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("false"))))
    {
      return false;
    }if ((o.equals(Boolean.TRUE)) || (((o instanceof String)) && (((String)o).equalsIgnoreCase("true"))))
    {
      return true;
    }
    throw new JSONException("JSONObject[" + JSONUtils.quote(key) + "] is not a Boolean.");
  }

  public double getDouble(String key)
    throws JSONException
  {
    verifyIsNull();
    Object o = get(key);
    try {
      return (o instanceof Number) ? ((Number)o).doubleValue() : Double.parseDouble((String)o);
    } catch (Exception e) {
    }
    throw new JSONException("JSONObject[" + JSONUtils.quote(key) + "] is not a number.");
  }

  public int getInt(String key)
    throws JSONException
  {
    verifyIsNull();
    Object o = get(key);
    return (o instanceof Number) ? ((Number)o).intValue() : (int)getDouble(key);
  }

  public JSONArray getJSONArray(String key)
    throws JSONException
  {
    verifyIsNull();
    Object o = get(key);
    if ((o instanceof JSONArray)) {
      return (JSONArray)o;
    }
    throw new JSONException("JSONObject[" + JSONUtils.quote(key) + "] is not a JSONArray.");
  }

  public JSONObject getJSONObject(String key)
    throws JSONException
  {
    verifyIsNull();
    Object o = get(key);
    if ((o instanceof JSONObject)) {
      return (JSONObject)o;
    }
    throw new JSONException("JSONObject[" + JSONUtils.quote(key) + "] is not a JSONObject.");
  }

  public long getLong(String key)
    throws JSONException
  {
    verifyIsNull();
    Object o = get(key);
    return (o instanceof Number) ? ((Number)o).longValue() : (long)getDouble(key);
  }

  public String getString(String key)
    throws JSONException
  {
    verifyIsNull();
    return get(key).toString();
  }

  public boolean has(String key)
  {
    verifyIsNull();
    return this.myHashMap.containsKey(key);
  }

  public boolean isNullObject()
  {
    return this.nullObject;
  }

  public Iterator keys()
  {
    verifyIsNull();
    return this.myHashMap.keySet().iterator();
  }

  public int length()
  {
    verifyIsNull();
    return this.myHashMap.size();
  }

  public JSONArray names()
  {
    verifyIsNull();
    JSONArray ja = new JSONArray();
    Iterator keys = keys();
    while (keys.hasNext()) {
      ja.put(keys.next());
    }
    return ja.length() == 0 ? null : ja;
  }

  public Object opt(String key)
  {
    verifyIsNull();
    return key == null ? null : this.myHashMap.get(key);
  }

  public boolean optBoolean(String key)
  {
    verifyIsNull();
    return optBoolean(key, false);
  }

  public boolean optBoolean(String key, boolean defaultValue)
  {
    verifyIsNull();
    try {
      return getBoolean(key);
    } catch (Exception e) {
    }
    return defaultValue;
  }

  public double optDouble(String key)
  {
    verifyIsNull();
    return optDouble(key, (0.0D / 0.0D));
  }

  public double optDouble(String key, double defaultValue)
  {
    verifyIsNull();
    try {
      Object o = opt(key);
      return (o instanceof Number) ? ((Number)o).doubleValue() : new Double((String)o).doubleValue();
    }
    catch (Exception e) {
    }
    return defaultValue;
  }

  public int optInt(String key)
  {
    verifyIsNull();
    return optInt(key, 0);
  }

  public int optInt(String key, int defaultValue)
  {
    verifyIsNull();
    try {
      return getInt(key);
    } catch (Exception e) {
    }
    return defaultValue;
  }

  public JSONArray optJSONArray(String key)
  {
    verifyIsNull();
    Object o = opt(key);
    return (o instanceof JSONArray) ? (JSONArray)o : null;
  }

  public JSONObject optJSONObject(String key)
  {
    verifyIsNull();
    Object o = opt(key);
    return (o instanceof JSONObject) ? (JSONObject)o : null;
  }

  public long optLong(String key)
  {
    verifyIsNull();
    return optLong(key, 0L);
  }

  public long optLong(String key, long defaultValue)
  {
    verifyIsNull();
    try {
      return getLong(key);
    } catch (Exception e) {
    }
    return defaultValue;
  }

  public String optString(String key)
  {
    verifyIsNull();
    return optString(key, "");
  }

  public String optString(String key, String defaultValue)
  {
    verifyIsNull();
    Object o = opt(key);
    return o != null ? o.toString() : defaultValue;
  }

  public JSONObject put(String key, boolean value)
    throws JSONException
  {
    verifyIsNull();
    put(key, value ? Boolean.TRUE : Boolean.FALSE);
    return this;
  }

  public JSONObject put(String key, Collection value)
    throws JSONException
  {
    verifyIsNull();
    put(key, new JSONArray(value));
    return this;
  }

  public JSONObject put(String key, double value)
    throws JSONException
  {
    verifyIsNull();
    put(key, new Double(value));
    return this;
  }

  public JSONObject put(String key, int value)
    throws JSONException
  {
    verifyIsNull();
    put(key, new Integer(value));
    return this;
  }

  public JSONObject put(String key, long value)
    throws JSONException
  {
    verifyIsNull();
    put(key, new Long(value));
    return this;
  }

  public JSONObject put(String key, Map value)
    throws JSONException
  {
    verifyIsNull();
    put(key, new JSONObject(value));
    return this;
  }

  public JSONObject put(String key, Object value)
    throws JSONException
  {
    verifyIsNull();
    if (key == null) {
      throw new JSONException("Null key.");
    }
    if (value != null) {
      JSONUtils.testValidity(value);
      this.myHashMap.put(key, value);
    } else {
      remove(key);
    }
    return this;
  }

  public JSONObject putOpt(String key, Object value)
    throws JSONException
  {
    verifyIsNull();
    if ((key != null) && (value != null)) {
      put(key, value);
    }
    return this;
  }

  public Object remove(String key)
  {
    verifyIsNull();
    return this.myHashMap.remove(key);
  }

  public JSONArray toJSONArray(JSONArray names)
    throws JSONException
  {
    verifyIsNull();
    if ((names == null) || (names.length() == 0)) {
      return null;
    }
    JSONArray ja = new JSONArray();
    for (int i = 0; i < names.length(); i++) {
      ja.put(opt(names.getString(i)));
    }
    return ja;
  }

  public String toString()
  {
    if (isNullObject()) {
      return JSONNull.getInstance().toString();
    }
    try
    {
      Iterator keys = keys();
      StringBuffer sb = new StringBuffer("{");

      while (keys.hasNext()) {
        if (sb.length() > 1) {
          sb.append(',');
        }
        Object o = keys.next();
        sb.append(JSONUtils.quote(o.toString()));
        sb.append(':');
        sb.append(JSONUtils.valueToString(this.myHashMap.get(o)));
      }
      sb.append('}');
      return sb.toString();
    } catch (Exception e) {
    }
    return null;
  }

  public String toString(int indentFactor)
    throws JSONException
  {
    if (isNullObject()) {
      return JSONNull.getInstance().toString();
    }

    return toString(indentFactor, 0);
  }

  public String toString(int indentFactor, int indent)
    throws JSONException
  {
    if (isNullObject()) {
      return JSONNull.getInstance().toString();
    }

    int n = length();
    if (n == 0) {
      return "{}";
    }
    Iterator keys = keys();
    StringBuffer sb = new StringBuffer("{");
    int newindent = indent + indentFactor;

    if (n == 1) {
      Object o = keys.next();
      sb.append(JSONUtils.quote(o.toString()));
      sb.append(": ");
      sb.append(JSONUtils.valueToString(this.myHashMap.get(o), indentFactor, indent));
    } else {
      while (keys.hasNext()) {
        Object o = keys.next();
        if (sb.length() > 1)
          sb.append(",\n");
        else {
          sb.append('\n');
        }
        for (int i = 0; i < newindent; i++) {
          sb.append(' ');
        }
        sb.append(JSONUtils.quote(o.toString()));
        sb.append(": ");
        sb.append(JSONUtils.valueToString(this.myHashMap.get(o), indentFactor, newindent));
      }
      if (sb.length() > 1) {
        sb.append('\n');
        for (int i = 0; i < indent; i++) {
          sb.append(' ');
        }
      }
    }
    sb.append('}');
    return sb.toString();
  }

  public Writer write(Writer writer)
    throws JSONException
  {
    try
    {
      if (isNullObject()) {
        writer.write(JSONNull.getInstance().toString());

        return writer;
      }

      boolean b = false;
      Iterator keys = keys();
      writer.write(123);

      while (keys.hasNext()) {
        if (b) {
          writer.write(44);
        }
        Object k = keys.next();
        writer.write(JSONUtils.quote(k.toString()));
        writer.write(58);
        Object v = this.myHashMap.get(k);
        if ((v instanceof JSONObject))
          ((JSONObject)v).write(writer);
        else if ((v instanceof JSONArray))
          ((JSONArray)v).write(writer);
        else {
          writer.write(JSONUtils.valueToString(v));
        }
        b = true;
      }
      writer.write(125);
      return writer;
    }
    catch (IOException e) {
      throw new JSONException(e);
    }
  }

  private void verifyIsNull() throws JSONException
  {
    if (isNullObject())
      throw new JSONException("null object");
  }
}