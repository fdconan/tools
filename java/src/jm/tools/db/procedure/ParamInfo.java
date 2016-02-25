package jm.tools.db.procedure;

import java.sql.DatabaseMetaData;

class ParamInfo {
	// �������������ģʽ
	public static final int UNKNOWN = DatabaseMetaData.procedureColumnUnknown;
	public static final int IN = DatabaseMetaData.procedureColumnIn; // 1
	public static final int INOUT = DatabaseMetaData.procedureColumnInOut; // 2
	public static final int OUT = DatabaseMetaData.procedureColumnOut; // 4
	public static final int RETURN = DatabaseMetaData.procedureColumnReturn; // 5
	//public static final int RESULT = DatabaseMetaData.procedureColumnResult;
	
	// oracle.jdbc.OracleTypes.CURSOR �� -10, �Դ˽���Ӳ����, 
	// ��������oracle �������·��ʷ�oracle���ݿ�Ҳ�����oracle jdbc��jar��
	public static final int TYPE_CURSOR = -10;
	
	public ParamInfo(int index, int mode, int sqlType) {
		this.index = index;	// ������ţ���1��ʼ
		this.mode = mode;
		this.sqlType = sqlType;
	}
	
	public ParamInfo(String paramName, int index, int mode, int sqlType) {
		this.paramName = paramName;
		this.index = index;	// ������ţ���1��ʼ
		this.mode = mode;
		this.sqlType = sqlType;
	}
	
	String paramName = "";
	int index;
	int mode;
	int sqlType;
}
