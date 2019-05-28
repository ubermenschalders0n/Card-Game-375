package se375;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class gameRunning {
	public final static String DB_URL = "jdbc:mysql://localhost:3306/gameBase";
	public final static String USER = "root";
	public final static String PASSWORD = "1625";

	public static void updatePoints(String playerName, int points) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		int newPoints;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		String sql = "Select points from gameRunning where playerName=?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, playerName);
		ResultSet rs = stmt.executeQuery();
		rs.first();
		int oldPoints = rs.getInt(1);
		newPoints = oldPoints + points;
		String sql2 = "UPDATE gameRunning " + "SET points = ? WHERE playerName=?";
		stmt = conn.prepareStatement(sql2);
		stmt.setInt(1, newPoints);
		stmt.setString(2, playerName);
		stmt.executeUpdate();
		conn.close();
		stmt.close();
	}

	public static void showPointRs(String sessionName) throws ClassNotFoundException, SQLException {
		Connection conn = null;
		PreparedStatement stmt = null;
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
		String sql = "Select points from gameRunning where sessionName=?";
		stmt = conn.prepareStatement(sql);
		stmt.setString(1, sessionName);
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

	/*public static void deleteRunning(String sessionName) {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
			String sql = "Delete from gameRunning where sessionName=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, sessionName);
			stmt.executeUpdate();
			conn.close();
			stmt.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/
}
