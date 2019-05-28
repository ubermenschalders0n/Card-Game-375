package se375;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class gameUser {
	public final static String DB_URL = "jdbc:mysql://localhost:3306/gameBase?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
	public final static String USER = "root";
	public final static String PASSWORD = "1625";

	public static void insertData(String Name, String password) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			String sql = "INSERT INTO gameUser " + "(userName,password)" + "VALUES(?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, Name);
			stmt.setBytes(2, encryption.encryptKey(password));
			stmt.executeUpdate();

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.close();
		stmt.close();
	}

	public static boolean checkData(String name, String password) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		boolean found = false;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		String sql = "SELECT EXISTS(SELECT * from gameUser WHERE gameUser.userName=? and gameUser.password=?)";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, name);
		stmt.setBytes(2, encryption.encryptKey(password));
		ResultSet rs = stmt.executeQuery();
		String sql2="SELECT isLogin from gameUser where userName=?";
		stmt=conn.prepareStatement(sql2);
		stmt.setString(1,name);
		ResultSet rs2=stmt.executeQuery();
		rs.first();
		rs2.first();
		if (rs.getBoolean(1) && !rs2.getBoolean(1)) {
			found = true;
		}
		return found;
	}
	public static void isLogin(String Name) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			String sql = "INSERT INTO gameUser " + "(isLogin)" + "VALUES(?) where userName=?";
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, true);
			stmt.setString(2, Name);
			stmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.close();
		stmt.close();
	}
	public static void isLogout(String Name) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			String sql = "INSERT INTO gameUser " + "(isLogin)" + "VALUES(?) where userName=?";
			stmt = conn.prepareStatement(sql);
			stmt.setBoolean(1, false);
			stmt.setString(2, Name);
			stmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		conn.close();
		stmt.close();
	}
}