package naverAPI;

import java.sql.Connection;
import java.sql.DriverManager;

import javax.sql.DataSource;

public class DBAction {
	static private DBAction instance = new DBAction();
	private Connection conn;

	public DBAction() {
		String driver = "com.mysql.cj.jdbc.Driver";
		String url = "jdbc:mysql://localhost:3306/exercise?characterEncoding=UTF-8&serverTimezone=UTC";
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url,"root","java");
		}catch(Exception e) {
		e.printStackTrace();
		}
	}
	
	static public DBAction getInstance() {
		if(instance == null)
			instance = new DBAction();
		
		return instance;
	}
	
	public Connection getConnection() {
		return conn;
	}
	
	public void close() {
		try {
			if(conn != null) conn.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
