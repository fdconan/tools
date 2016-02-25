package jm.tools.db.procedure;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jm.tools.cache.LRUCache;
import jm.tools.db.jdbc.JDBCHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



/**
 * 
 * @author yjm
 *
 */
public final class ProcedureHelper {
	public static LRUCache procCache = new LRUCache(10, 3600*1000);
	private Connection conn = null;
	private boolean autoCloseConn = true;
	private static final Log LOG = LogFactory.getLog(ProcedureHelper.class);
	
	private ProcedureHelper(Connection conn){
		this(conn, true);
	}
	
	private ProcedureHelper(Connection conn, boolean autoCloseConn){
		this.conn = conn;
		this.autoCloseConn = autoCloseConn;
	}
	
	public static ProcedureHelper getInstantce(Connection conn){
		return new ProcedureHelper(conn);
	}
	
	public static ProcedureHelper getInstantce(Connection conn, boolean autoCloseConn){
		return new ProcedureHelper(conn, autoCloseConn);
	}
	/*
	public Object[] execute(String procName, Object... inParams) throws Exception{
		StoredProcInfo procInfo = (StoredProcInfo)procCache.get(procName.toUpperCase());
		if(procInfo == null){
			procInfo = this.prepareProcedure(conn, procName);
		}
		CallableStatement statement = null;
		ParamInfo[] paramsInfo = procInfo.paramsInfo;
		try{
			statement = conn.prepareCall(procInfo.callSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			int vIndex = 0;
			for (ParamInfo pi : paramsInfo) {
				switch (pi.mode) {
					case ParamInfo.IN:
					case ParamInfo.INOUT:
						setParamValue(statement, pi.index, pi.sqlType, inParams[vIndex++]);
						if (pi.mode == ParamInfo.IN)
							break;
					case ParamInfo.OUT:
					case ParamInfo.RETURN:
						statement.registerOutParameter(pi.index, pi.sqlType);
				}
			}
			
			List<List<RowModel>> dataSetList = new ArrayList<List<RowModel>>();
			
			boolean result = statement.execute ();
			int rowsAffected = 0;

			do {
				if(result){
					ResultSet rs = statement.getResultSet();
					dataSetList.add(JDBCHelper.resultSetToList(rs));
					rs.close();
					result = false;
				}
				else {
					rowsAffected = statement.getUpdateCount();
					result = statement.getMoreResults();
				}
			}while (result || rowsAffected != -1); // equals to !((statement.getMoreResults() == false) && (statement.getUpdateCount() == -1))
			
			List<ParamInfo> cursorParamsInfo = procInfo.getCursorParamsInfo();
			for (ParamInfo pi : cursorParamsInfo) {
				System.out.println("cursor param:" + pi.paramName);
				ResultSet rts = (ResultSet)statement.getObject(pi.index);
				dataSetList.add(JDBCHelper.resultSetToList(rts));
			}
			
			//statement.getUpdateCount();
			
			List<Object> resultList = new LinkedList<Object>();
			List<ParamInfo> outParamsInfo = procInfo.getOutParamsInfo();
			
			for (ParamInfo pi : outParamsInfo) {
				System.out.println("out param:" + pi.paramName);
				Object outVal = getOutParamValue(statement, pi.index, pi.sqlType);
				resultList.add(outVal);
			}
			
			resultList.add(dataSetList);
			
			return resultList.toArray();
		}catch(Exception e){
			throw e;
		} finally {
			JDBCHelper.closeStatement(statement);
			if(this.autoCloseConn){
				JDBCHelper.closeConnection(conn);
			}
		}
	}
	*/
	
	public ProcedureResult execute(String procName, Object... inParams) throws Exception{
		return this.execute(procName, null, null, inParams);
	}
	
	public ProcedureResult execute(String procName, String[][] resultSetColumnNames, String[][] cursorColumnNames, Object... inParams) throws Exception{
		ProcedureInfo procInfo = (ProcedureInfo)procCache.get(procName.toUpperCase());
		if(procInfo == null){
			//System.out.println("first execute proc " + procName + ", so anaylize it");
			procInfo = this.prepareProcedure(conn, procName);
		}
		CallableStatement statement = null;
		ParamInfo[] paramsInfo = procInfo.paramsInfo;
		try{
			statement = conn.prepareCall(procInfo.callSql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			
			int vIndex = 0;
			for (ParamInfo pi : paramsInfo) {
				switch (pi.mode) {
					case ParamInfo.IN:
					case ParamInfo.INOUT:
						setParamValue(statement, pi.index, pi.sqlType, inParams[vIndex++]);
						if (pi.mode == ParamInfo.IN)
							break;
					case ParamInfo.OUT:
					case ParamInfo.RETURN:
						statement.registerOutParameter(pi.index, pi.sqlType);
				}
			}
			
			ProcedureResult pr = new ProcedureResult();
			
			boolean result = statement.execute();
			int rowsAffected = 0;
			int rsCount = 0;
			do {
				if(result){
					ResultSet rs = statement.getResultSet();
					pr.addResult(ProcedureResult.RESULT_SET + "_" + rsCount, resultSetToList(rs, resultSetColumnNames != null ? resultSetColumnNames[rsCount] : null));
					rsCount++;
					rs.close();
					result = false;
				} else {
					rowsAffected = statement.getUpdateCount();
					result = statement.getMoreResults();
				}
			}while (result || rowsAffected != -1); // equals to !((statement.getMoreResults() == false) && (statement.getUpdateCount() == -1))

			int cursorCount = 0;
			List<ParamInfo> cursorParamsInfo = procInfo.getCursorParamsInfo();
			for (ParamInfo pi : cursorParamsInfo) {
				//System.out.println("cursor param:" + pi.paramName);
				ResultSet rts = (ResultSet)statement.getObject(pi.index);
				pr.addResult(pi.paramName, resultSetToList(rts, cursorColumnNames != null ? cursorColumnNames[cursorCount++] : null));
			}
			
			//statement.getUpdateCount();
			List<ParamInfo> outParamsInfo = procInfo.getOutParamsInfo();
			for (ParamInfo pi : outParamsInfo) {
				Object outVal = getOutParamValue(statement, pi.index, pi.sqlType);
				//System.out.println("out param:" + pi.paramName + ", value:" + outVal);
				pr.addResult(pi.paramName, outVal);
			}
			
			return pr;
		}catch(Exception e){
			throw e;
		} finally {
			JDBCHelper.closeStatement(statement);
			if(this.autoCloseConn){
				JDBCHelper.closeConnection(conn);
			}
		}
	}
	
	public void close(){
		JDBCHelper.closeConnection(conn);
		autoCloseConn = true;
	}
	
	public void setAutoCloseConnnection(boolean autoCloseConnnection) {
		this.autoCloseConn = autoCloseConnnection;
	}

	private ProcedureInfo prepareProcedure(Connection conn, String procName){
		try {
			ProcedureInfo procInfo = new ProcedureInfo(conn, procName);
			procCache.put(procName.toUpperCase(), procInfo);
			return procInfo;
		} catch (SQLException e) {
			LOG.error("prepare procedure " + procName + " error->", e);
		}
		return null;
	}
	
	private void setParamValue(PreparedStatement statement, int paramIndex, int targetSqlType, Object value) throws SQLException {
		switch (targetSqlType) {
		case java.sql.Types.DATE:
			value = DataType.toType(value, DataType.DT_Date);
			break;
		case java.sql.Types.TIME:
			value = DataType.toType(value, DataType.DT_Time);
			break;
		case java.sql.Types.TIMESTAMP:
			value = DataType.toType(value, DataType.DT_DateTime);
			break;
		}
		statement.setObject(paramIndex, value, targetSqlType);
		//statement.setObject(paramIndex, value);
	}
	
	private Object getOutParamValue(CallableStatement statement, int parameterIndex, int sqlType) throws Exception {
		switch(sqlType) {
		case java.sql.Types.TINYINT:
			return statement.getByte(parameterIndex);
		case java.sql.Types.SMALLINT:
			return statement.getShort(parameterIndex);
		case java.sql.Types.INTEGER:
		case java.sql.Types.DECIMAL:
		case java.sql.Types.NUMERIC:
			return statement.getInt(parameterIndex);
		case java.sql.Types.BIGINT:
			return statement.getLong(parameterIndex);
		case java.sql.Types.REAL:
			return statement.getFloat(parameterIndex);
		case java.sql.Types.FLOAT:
			return statement.getFloat(parameterIndex);
		case java.sql.Types.DOUBLE:
			return statement.getDouble(parameterIndex);
		/*case java.sql.Types.DECIMAL:
		case java.sql.Types.NUMERIC:
			return statement.getBigDecimal(parameterIndex);*/
		case java.sql.Types.BIT:
		case java.sql.Types.BOOLEAN:
			return statement.getBoolean(parameterIndex);
		case java.sql.Types.CHAR:
			return statement.getString(parameterIndex).charAt(0);
		case java.sql.Types.VARCHAR:
		case java.sql.Types.LONGVARCHAR:
			return statement.getString(parameterIndex);
		case java.sql.Types.BINARY:
		case java.sql.Types.VARBINARY:
		case java.sql.Types.LONGVARBINARY:
			return statement.getBytes(parameterIndex);
		default:
			throw new RuntimeException("type is not supported! sqltype=" + sqlType + ", parameterIndex=" + parameterIndex);
		}
	}	
	
	private List<Map> resultSetToList(ResultSet rs, String[] columnNames) throws SQLException{
		List<Map> result = new ArrayList<Map>();
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		while(rs.next()){
			Map map = new LinkedHashMap();
			for(int columnIndex=1; columnIndex<= columnCount; columnIndex++){
				String columnLabel = rsmd.getColumnLabel(columnIndex);
				if(columnLabel == null || columnLabel.length() == 0){
					if(columnNames != null){
						columnLabel = columnNames[columnIndex-1];
					}else {
						columnLabel = "__column__" + columnIndex;
					}
				}
				map.put(columnLabel, rs.getObject(columnIndex));
			}
			result.add(map);
		}
		return result;
	}
}
