package com.jumao.java.util;


import com.jumao.java.bean.Categories;
import com.jumao.java.bean.FuturesCodeBean;
import com.jumao.java.bean.IndustryBean;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlUtil {

	private static String url = "jdbc:mysql://172.18.203.131:3306/jsjcenter?useUnicode=true&characterEncoding=utf8&characterSetResults=utf8&zeroDateTimeBehavior=convertToNull";

	private static String username = "root";

	private static String pw = "rootROOT1.";

	private Connection conn = null;

	// 用户名字和密码是自己建立的。

	public MySqlUtil(String url, String username, String pw) {
		this.url = url;
		this.username = username;
		this.pw = pw;
	}

	public Connection OpenConn() {

		try {
			Class.forName("com.mysql.jdbc.Driver");
			try {
				conn = DriverManager.getConnection(url, username, pw);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return conn;
	}

	/*
	 * public static ResultSet executeQuery(String sql) { MySqlUtil db = new
	 * MySqlUtil(); ResultSet rs = null; Connection con = db.OpenConn(); try {
	 * Statement sm = con.createStatement(); rs = sm.executeQuery(sql); } catch
	 * (SQLException e) { e.printStackTrace(); } return rs; }
	 */

	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 获取数据库中所有表的表名，并添加到列表结构中。
	public static List getTableNameList(Connection conn, String itemName)
			throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		// 访问当前用户jm下的所有表
		ResultSet rs = dbmd.getTables("null", itemName, "%",
				new String[] { "TABLE" });
		List tableNameList = new ArrayList();
		while (rs.next()) {
			tableNameList.add(rs.getString("TABLE_NAME"));
		}
		return tableNameList;

	}

	// 获取数据表中所有列的列名，并添加到列表结构中。
	public static List getColumnNameList(Connection conn, String tableName)
			throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getColumns(null, "%", tableName, "%");
		List columnNameList = new ArrayList();
		while (rs.next()) {
			columnNameList.add(rs.getString("COLUMN_NAME"));
		}
		return columnNameList;

	}

	public static void exeSql(List tableList, String sql, String hdfsFileName,
			Connection conn) throws IOException, SQLException {

	}


	public static Map<String, IndustryBean> getIndustryMap() {

		MySqlUtil dbConn = new MySqlUtil(url, username, pw);

		Connection conn = dbConn.OpenConn();
		Map<String, IndustryBean> map = new HashMap<String, IndustryBean>();

		/*if (conn == null) {
			System.out.println("连接失败");
		} else {
			System.out.println("连接成功");
		}*/

		try {
			// String sql =
			// "select * from jm_dsz.jmuser_account a,djt_dsz.djtbill_sell b where a.id =b.id";
			String sql = "select * from jsj_industry";
			PreparedStatement stmt;
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			List<IndustryBean> list = new ArrayList<IndustryBean>();

			while (rs.next()) {
				// System.out.println(rs.getInt(1));
				String name = rs.getString(6).trim();
				String alias = rs.getString(8);
				IndustryBean industry = new IndustryBean(String.valueOf(rs
						.getInt(1)), String.valueOf(rs.getInt(2)),
						String.valueOf(rs.getInt(3)), rs.getString(4),
						rs.getString(5), name, String.valueOf(rs
								.getInt(7)), alias);
				list.add(industry);
				
				if(null != alias && !"".equals(alias)) {
					String[] aliasArray = alias.split(",");
					for (String str : aliasArray) {//别名也可以作为过滤条件
						map.put(str, industry);
					}
				}
				map.put(name, industry);

			}

			// System.out.println(list.size());

		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();

				} catch (SQLException e) {

					e.printStackTrace();

				}
			}
		}

		return map;

	}

	public static Map<String, Categories> getAllCategories() {

		MySqlUtil dbConn = new MySqlUtil(url, username, pw);

		Connection conn = dbConn.OpenConn();
		Map<String, Categories> map = new HashMap<String, Categories>();

		/*if (conn == null) {
			System.out.println("连接失败");
		} else {
			System.out.println("连接成功");
		}*/

		try {
			String sql = "select * from jsj_ie_categories";
			PreparedStatement stmt;
			Categories category;
			String cateId = null;
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				category = new Categories();
				cateId = rs.getString(1);
				category.setCateId(cateId);
				category.setCateName(rs.getString(2));
				category.setpId(rs.getString(3));
				category.setpName(rs.getString(4));
				category.setLevel(rs.getInt(5));

				map.put(cateId, category);
			}

		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();

				} catch (SQLException e) {

					e.printStackTrace();

				}
			}
		}

		return map;
	}

	public static Map<String, FuturesCodeBean> getFuturesCodeByAbbr() {

		MySqlUtil dbConn = new MySqlUtil(url, username, pw);

		Connection conn = dbConn.OpenConn();
		Map<String, FuturesCodeBean> map = new HashMap<String, FuturesCodeBean>();

		if (conn == null) {
//			System.out.println("连接失败");
		} else {
//			System.out.println("连接成功");
		}

		try {
			// String sql =
			// "select * from jm_dsz.jmuser_account a,djt_dsz.djtbill_sell b where a.id =b.id";
			String sql = "select * from jsj_futures_code";
			PreparedStatement stmt;
			stmt = conn.prepareStatement(sql);
			ResultSet rs = stmt.executeQuery(sql);
			List<FuturesCodeBean> list = new ArrayList<FuturesCodeBean>();

			while (rs.next()) {
				// System.out.println(rs.getInt(1));
				FuturesCodeBean fc = new FuturesCodeBean(String.valueOf(rs
						.getInt(1)), String.valueOf(rs.getString(2)),
						String.valueOf(rs.getString(3)), rs.getString(4),
						rs.getString(5));
				list.add(fc);
				map.put(rs.getString(3).trim(), fc);

			}

			// System.out.println(list.size());

		} catch (SQLException se) {
			se.printStackTrace();
		} finally {
			if (conn != null) {
				try {
					conn.close();

				} catch (SQLException e) {

					e.printStackTrace();

				}
			}
		}

		return map;

	}

	public static void main(String s[]) throws SQLException, IOException {

		Map<String, IndustryBean> data = getIndustryMap();
		if (data.containsKey("锌")) {
			System.out.println(data.get("锌").getIndustryCode());
		}
		Map<String, Categories> map = getAllCategories();
		if (map.containsKey("2514")) {
			System.out.println(map.get("2514").getCateName());
		}

		// for (Map.Entry<String, IndustryBean> entry : data.entrySet()) {
		// System.out.println(entry.getKey() + " : " + entry.getValue());
		// }

	}
}