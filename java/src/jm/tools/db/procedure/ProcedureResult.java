package jm.tools.db.procedure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 存储过程返回结果
 * @author yjm
 *
 */
public final class ProcedureResult {
	public static String RESULT_SET = "RESULT_SET";
	private static String RESULT_SET_0 = RESULT_SET+"_0";
	private static String RESULT_SET_1 = RESULT_SET+"_1";
	private static String RETURN_VALUE = "RETURN_VALUE";
	private Map results;
	ProcedureResult(){
		results = new LinkedHashMap();
	}
	
	void addResult(String paramName, Object result){
		if(RESULT_SET_0.equals(paramName)){
			results.put(RESULT_SET, result);
		}else if(RESULT_SET_1.equals(paramName)){
			results.put(RESULT_SET_0, results.get(RESULT_SET));
			results.put(paramName, result);
		}else {
			results.put(paramName, result);
		}
	}
	
	public Object get(String paramName){
		return this.results.get(paramName);
	}
	
	public String getString(String paramName){
		Object objVal = this.get(paramName);
		if(objVal != null){
			return String.valueOf(objVal);
		}
		return null;
	}
	
	public List<Map> getResultSet(){
		return (List<Map>)this.results.get(RESULT_SET);
	}
	
	public List<Map> getResultSet(String name){
		return (List<Map>)this.results.get(name);
	}
	
	public Object getReturnValue(){
		Object val = this.results.get(RETURN_VALUE);
		if(val != null){
			return val;
		}
		return this.results.get(null);
	}
	
	public List<Map> getResultSet(int index){
		if(index < 0){
			throw new RuntimeException("index must be bigger than zero");
		}
		List<Map> ret = (List<Map>)this.results.get(RESULT_SET + "_" + index);
		if(ret != null){
			return ret;
		}
		return getResultSet();
	}
	
	public int getInt(String paramName){
		String strVal = this.getString(paramName);
		if(strVal != null){
			try{
				return Integer.valueOf(strVal);
			}catch(Exception e){
				new NumberFormatException("format " + strVal + " value to Integer is not supported");
			}
		}
		throw new NumberFormatException("format null value to Integer is not supported");
	}
	
	public double getDouble(String paramName){
		String strVal = this.getString(paramName);
		if(strVal != null){
			try{
				return Double.valueOf(strVal);
			}catch(Exception e){
				new NumberFormatException("format " + strVal + " value to Double is not supported");
			}
		}
		throw new NumberFormatException("format null value to Double is not supported");
	}

	@Override
	public String toString() {
		return results.toString();
	}
	
	
}
