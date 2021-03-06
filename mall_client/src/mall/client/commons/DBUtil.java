package mall.client.commons;

import java.sql.*;

public class DBUtil {
	// 1. DB 연결
	public Connection getConnection() {
		Connection conn = null;
		try {	
			conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/mall","root","java1004");
		} catch(Exception e) {
			e.printStackTrace();
		}
			return conn;
	}
	// 2. DB 자원(conn, stmt, rs) 해제
	public void close(Connection conn, PreparedStatement stmt, ResultSet rs) {
		// rs, stmt, conn이 null이 아닐 경우에만 close 시켜서 에러가 안뜨게 함 (유효성 검사)
		if(rs != null) {
			try {	
				rs.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(stmt != null) {
			try {	
				stmt.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(conn != null) {
			try {	
				conn.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}