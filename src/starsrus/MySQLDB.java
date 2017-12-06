package starsrus;
import java.sql.*;

public class MySQLDB {
	
	// DB info
	public static final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/kevinchanDB";
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
		
	
	public Statement getDBStatement() throws SQLException{
	      Connection connection = null;
	      Statement statement = null;
	        try {
	            Class.forName("com.mysql.jdbc.Driver");
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }

	        try {
	            connection = DriverManager.getConnection(HOST, USER, PWD);
	            statement = connection.createStatement();
	            //ResultSet resultSet = statement.executeQuery(QUERY);
	            return statement;

	        } catch (SQLException e) {
	            e.printStackTrace();
	            
	        } finally {
	            if (statement != null) {
	                statement.close();
	            }

	            if (connection != null) {
	                connection.close();
	            }
	        }
	       return statement;
	}
}
