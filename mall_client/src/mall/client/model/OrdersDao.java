package mall.client.model;

import java.sql.*;
import java.util.*;

import mall.client.commons.DBUtil;
import mall.client.vo.Orders;

public class OrdersDao {
	private DBUtil dbUtil;
	
	// 베스트 셀러 메소드
	public List<Map<String, Object>> selectBestOrdersList () {
		// 변수 및 객체 초기화
		List<Map<String, Object>> list = new ArrayList<>();
		this.dbUtil = new DBUtil();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		// DB 처리
		try {
			conn = this.dbUtil.getConnection();
			// 쿼리 띄어쓰기 잘 하기!, 표현 익히기
			String sql = "SELECT t.ebook_no ebookNo, t.cnt cnt, e.ebook_title ebookTitle, e.ebook_price ebookPrice"
							+ " FROM"
								// 쿼리 안에 있는 쿼리 (서브쿼리)
								+ "	(SELECT ebook_no, COUNT(ebook_no) cnt"
								+ "	FROM orders"
								+ "	WHERE orders_state = '주문완료'"
								+ "	GROUP BY ebook_no" // group by 키워드로 동일한 값들을 집계할 수 있음
								+ "	HAVING COUNT(ebook_no) > 0"
								+ "	LIMIT 5) t INNER JOIN ebook e" // 5개만 출력함 (LIMIT 절)
						+ " ON t.ebook_no = e.ebook_no"
						+ " ORDER BY cnt DESC"; // 많이 담긴 순서대로 출력
			stmt = conn.prepareStatement(sql);
			System.out.println(stmt+"<-- OrdersDao.selectBestOrdersList의 stmt");
			rs = stmt.executeQuery();
			while(rs.next()) {
				Map<String, Object> map = new HashMap<>();
				map.put("ebookNo", rs.getInt("ebookNo"));
				map.put("cnt", rs.getInt("cnt"));
				map.put("ebookTitle", rs.getString("ebookTitle"));
				map.put("ebookPrice", rs.getInt("ebookPrice"));
				list.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			this.dbUtil.close(conn, stmt, rs);
		}
		return list;
	}
	
	// 장바구니 목록 메소드
	public List<Map<String, Object>> selectOrderListByClient(int clientNo) {
		// 변수 및 객체 초기화
		List<Map<String, Object>> list = new ArrayList<>();
		this.dbUtil = new DBUtil();
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		
		// DB 처리
		try {
			conn = this.dbUtil.getConnection();
			String sql = "SELECT o.orders_no ordersNo, o.ebook_no ebookNo, o.orders_date ordersDate, o.orders_state ordersState, e.ebook_title ebookTitle, e.ebook_price ebookPrice FROM orders o INNER JOIN ebook e ON o.ebook_no = e.ebook_no WHERE o.client_no = ? ";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, clientNo);
			System.out.println(stmt+"<-- OrdersDao.selectOrderListByClient의 stmt");
			rs = stmt.executeQuery();
			while(rs.next()) {
				Map<String, Object> map = new HashMap<>();
				map.put("ordersNo", rs.getInt("ordersNo"));
				map.put("ebookNo", rs.getInt("ebookNo"));
				map.put("ordersDate", rs.getString("ordersDate"));
				map.put("ordersState", rs.getString("ordersState"));
				map.put("ebookTitle", rs.getString("ebookTitle"));
				map.put("ebookPrice", rs.getInt("ebookPrice"));
				list.add(map);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			this.dbUtil.close(conn, stmt, rs);
		}
		return list;
	}
	
	// 주문 추가 메소드
	public int insertOrders(Orders orders) {
		// 변수 및 객체 초기화
		int rowCnt = 0;
		this.dbUtil = new DBUtil();
		Connection conn = null;
		PreparedStatement stmt = null;
		
		// DB 처리
		try {
			conn = this.dbUtil.getConnection();
			String sql = "INSERT INTO orders(ebook_no, client_no, orders_date, orders_state) VALUES (?,?, NOW(), '주문완료')";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, orders.getEbookNo());
			stmt.setInt(2, orders.getClientNo());
			System.out.println(stmt+"<-- OrdersDao.insertOrders의 stmt");
			rowCnt = stmt.executeUpdate();
		} catch(Exception e) {
			// 예외 발생 시 시스템을 멈추고, 함수(메소드)호출스택구조를 그대로 콘솔에 출력함
			e.printStackTrace();
		} finally {
			this.dbUtil.close(conn, stmt, null);
		}
		return rowCnt;
	}
}
