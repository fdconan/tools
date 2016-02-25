package jm.tools.db.procedure;

import java.sql.DatabaseMetaData;

class ParamInfo {
	// 参数的输入输出模式
	public static final int UNKNOWN = DatabaseMetaData.procedureColumnUnknown;
	public static final int IN = DatabaseMetaData.procedureColumnIn; // 1
	public static final int INOUT = DatabaseMetaData.procedureColumnInOut; // 2
	public static final int OUT = DatabaseMetaData.procedureColumnOut; // 4
	public static final int RETURN = DatabaseMetaData.procedureColumnReturn; // 5
	//public static final int RESULT = DatabaseMetaData.procedureColumnResult;
	
	// oracle.jdbc.OracleTypes.CURSOR 是 -10, 对此进行硬编码, 
	// 避免引入oracle 包，导致访问非oracle数据库也必须带oracle jdbc的jar包
	public static final int TYPE_CURSOR = -10;
	
	public ParamInfo(int index, int mode, int sqlType) {
		this.index = index;	// 参数序号，从1开始
		this.mode = mode;
		this.sqlType = sqlType;
	}
	
	public ParamInfo(String paramName, int index, int mode, int sqlType) {
		this.paramName = paramName;
		this.index = index;	// 参数序号，从1开始
		this.mode = mode;
		this.sqlType = sqlType;
	}
	
	String paramName = "";
	int index;
	int mode;
	int sqlType;
}
