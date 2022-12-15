package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.sqlite.SQLiteConfig;

public class DBConnection{

	private static Connection myConn;
	
	
	//to connect to DBMS server
	//in case of sqlite, the jdbc connection is performed to the file.db/file.sqlite stored in path DB
	public static void setConn(String DBMS, String sv, String user, String password, String DB) throws Exception {
		try {
			String server_URL;
			if(DBMS.equalsIgnoreCase("sqlserver")){//SQL Server
				server_URL = "jdbc:sqlserver://localhost\\";
				myConn = DriverManager.getConnection(server_URL + sv.toUpperCase() + ";user="+user+";password="+password+";");			
			}
			else if (DBMS.equalsIgnoreCase("mysql")){//MySQL
				server_URL = "jdbc:mysql://";
				Class.forName("com.mysql.cj.jdbc.Driver");
				//myConn = DriverManager.getConnection(server_URL + sv + "/?user="+user+"&password="+password);
				myConn = DriverManager.getConnection(server_URL + sv + "/?serverTimezone=UTC", user, password);
			}
			else if (DBMS.equalsIgnoreCase("sqlite")) {//SQLite
				//Class.forName("org.sqlite.JDBC");
				//myConn = DriverManager.getConnection("jdbc:sqlite:"+DB);//DB=path to file.db/file.sqlite
				SQLiteConfig config = new SQLiteConfig();
			  	config.enforceForeignKeys(true);  //this is needed to handle foreign keys
			    myConn = DriverManager.getConnection("jdbc:sqlite:"+DB, config.toProperties());  
			}
			else 
				throw new IllegalArgumentException("DBMS not supported");
		}catch (SQLException se) {
			se.printStackTrace();
			System.out.println("Error: " + se.getMessage());
		}
	}
	
	//to get current connection
	public static Connection getConn() {
		return myConn;
	}
	
	//to close statement
	public static void closeSt(Statement st) {
		try {
			if(st != null) {
				st.close();
			}
		}catch (SQLException se) {
			System.out.println("Error: " + se.getMessage());
		}
	}
	
	//to close ResultSet
	public static void closeRs(ResultSet rs) {
		try {
			if(rs != null) {
				rs.close();
			}
		}catch (SQLException se) {
			System.out.println("Error: " + se.getMessage());
		}
	}
	
	//to close connection
	public static void closeConn() {
		try {
			if(myConn != null){
				myConn.close();
				//System.out.println("Connection to server closed.");
			}
		}
		catch (SQLException se) {
			System.out.println("Error: " + se.getMessage());
		}
	}
}

	