package starsrus;
import java.sql.*;

public class MySQLDB {
	
	// DB info
	public static String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/kevinchanDB";
	public static final String USER = "kevinchan";
	public static final String PWD = "651";
	
	
	public Connection getDBConnection() throws SQLException{
	      Connection connection = null;
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }

	        try {
	            connection = DriverManager.getConnection(HOST, USER, PWD);
	            return connection;

	        } catch (SQLException e) {
	            e.printStackTrace();
	            
	        } 
	       return connection;
	}		

	
	public Connection getMoviesDBConnection() throws SQLException{
		  String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/moviesDB";
	      Connection connection = null;
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }

	        try {
	            connection = DriverManager.getConnection(HOST, USER, PWD);
	            return connection;

	        } catch (SQLException e) {
	            e.printStackTrace();
	            
	        } 
	       return connection;
	}		

}
