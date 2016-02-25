package jm.tools.db.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


/**
 * JDBC数据库操作助手
 * @author yjm
 *
 */
public class JDBCHelper {

	/**
	 * 关闭该数据库连接
	 * @param con Connection对象
	 */
	public static void closeConnection(Connection con) {
		if (con != null) {
			try {
				con.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 关闭一个连接对象statement/preparedstatement
	 * @param stat
	 */
	public static void closeStatement(Statement stat) {
		if (stat != null) {
			try {
				stat.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 关闭一个记录集对象statement/preparedstatement
	 * @param stat
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 同时关闭数据连接对象connection和statement
	 * @param con
	 * @param stat
	 */
	public static void close(Connection con, Statement stat) {
		JDBCHelper.closeStatement(stat);
		JDBCHelper.closeConnection(con);
	}

	/**
	 * 同时关闭数据连接对象connection,statement,resultset
	 * @param con
	 * @param stat
	 * @param rs
	 */
	public static void close(Connection con, Statement stat, ResultSet rs) {
		JDBCHelper.closeResultSet(rs);
		JDBCHelper.close(con,stat);
	}
	
	/**
	 * 开始事务
	 * @param conn
	 */
	public static void beginTransaction(Connection conn){
		if(conn != null){
			try {
				conn.setAutoCommit(false);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 提交事务
	 * @param conn
	 */
	public static void commitTransaction(Connection conn){
		if(conn != null){
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 回滚事务
	 * @param conn
	 */
	public static void rollbackTransaction(Connection conn){
		if(conn != null){
			try {
				conn.rollback();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取记录条数
	 * @param conn
	 * @param countSql
	 * @return
	 * @throws SQLException
	 */
	public static int getRecordCount(Connection conn, String countSql) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			pstmt = conn.prepareStatement(countSql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		}catch(SQLException e){
			throw e;
		} finally{
			JDBCHelper.close(null, pstmt, rs);
		}
		return 0;
	}
	
}

