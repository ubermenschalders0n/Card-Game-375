package se375;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class gameSession {
	public final static String DB_URL = "jdbc:mysql://localhost:3306/gameBase";
	public final static String USER = "root";
	public final static String PASSWORD = "1625";

	public static void printRs() throws SQLException, ClassNotFoundException {
		Connection conn = null;
		Statement stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		String sql = "Select sessionName from gameSession";
		stmt = conn.createStatement();
		ResultSet resultSet = stmt.executeQuery(sql);
		ResultSetMetaData rsmd = resultSet.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (resultSet.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1)
					System.out.print(",  ");
				String columnValue = resultSet.getString(i);
				System.out.print(columnValue + " " + rsmd.getColumnName(i));
			}
			System.out.println("");
		}
		conn.close();
		stmt.close();
		resultSet.close();

	}

	public static void insertSession(String sessionName, String password) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		String sql = "INSERT INTO gameSession " + "(sessionName,password)VALUES " + "(?,?)";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionName);
		stmt.setBytes(2, encryption.encryptKey(password));
		stmt.executeUpdate();
		conn.close();
		stmt.close();
	}

	public static void killSession(String sessionName) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		String sql = "DELETE from gameSession where sessionName=?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionName);
		stmt.executeUpdate();
		conn.close();
		stmt.close();
	}

	public static boolean connect(String sessionName, String password, String userName) throws Exception {
		Connection conn = null;
		PreparedStatement stmt = null;
		PreparedStatement stmt2 = null;
		PreparedStatement stmt3 = null;
		boolean found = false;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		String sql = "SELECT EXISTS(SELECT * from gameSession WHERE gameSession.sessionName=? and gameSession.password=?)";
		/*
		 * String sql2="Select password from gameSession where sessionName=?"; String
		 * sql3="SELECT EXISTS(SELECT * from gameSession WHERE gameSession.sessionName=?)"
		 * ; stmt=conn.prepareStatement(sql2); stmt.setString(1,sessionName);
		 * if(stmt.executeQuery().equals(null)) { stmt=conn.prepareStatement(sql3);
		 * stmt.setString(1,sessionName); ResultSet rs = stmt.executeQuery(); if
		 * (rs.next()) { return true; } else { return false; } stmt.close(); }else {
		 */
		String sql2 = "Select adminName from gameSession where sessionName=?";
		String sql3 = "UPDATE gameSession SET adminName=? WHERE sessionName=?";
		stmt2 = conn.prepareStatement(sql2);
		stmt2.setString(1, sessionName);
		ResultSet rs2 = stmt2.executeQuery();
		rs2.first();
		if (rs2.getString(1).equals("notFound")) {
			stmt3 = conn.prepareStatement(sql3);
			stmt3.setString(1, userName);
			stmt3.setString(2, sessionName);
			stmt3.executeUpdate();
		}
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionName);
		stmt.setBytes(2, encryption.encryptKey(password));
		ResultSet rs = stmt.executeQuery();
		rs.first();
		if (rs.getBoolean(1)) {
			found = true;
		}
		return found;
	}
}
