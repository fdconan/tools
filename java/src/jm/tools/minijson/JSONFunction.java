package jm.tools.minijson;

import java.io.Serializable;
import java.util.regex.Matcher;

public class JSONFunction
implements Serializable
{
private static final String[] EMPTY_PARAM_ARRAY = new String[0];
private static final long serialVersionUID = 1L;
private String[] params;
private String text;

public static JSONFunction parse(JSONTokener x)
  throws JSONException
{
  Object v = x.nextValue();
  if (!JSONUtils.isFunctionHeader(v)) {
    throw new JSONException("String is not a function. " + v);
  }

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

  return new JSONFunction(params != null ? params.split(",") : null, text);
}

public static JSONFunction parse(String str)
  throws JSONException
{
  return parse(new JSONTokener(str));
}

public JSONFunction(String text)
{
  this(null, text);
}

public JSONFunction(String[] params, String text)
{
  this.text = (text != null ? text.trim() : "");
  if (params != null) {
    this.params = new String[params.length];
    System.arraycopy(params, 0, this.params, 0, params.length);
  } else {
    this.params = EMPTY_PARAM_ARRAY;
  }
}

public String[] getParams()
{
  return this.params;
}

public String getText()
{
  return this.text;
}

public String toString()
{
  StringBuffer b = new StringBuffer("function(");
  if (this.params.length > 0) {
    for (int i = 0; i < this.params.length - 1; i++) {
      b.append(this.params[i] + ",");
    }
    b.append(this.params[(this.params.length - 1)]);
  }
  b.append("){");
  if (this.text.length() > 0) {
    b.append(" " + this.text + " ");
  }
  b.append("}");
  return b.toString();
}
}