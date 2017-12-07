package starsrus;
import java.sql.*;
import java.util.*;
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

	        } catch (SQLException e) {
	            e.printStackTrace();
	            
	        } 
	       return connection;
	}		

	// Returns DB connection to Movies database
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

	        } catch (SQLException e) {
	            e.printStackTrace();
	            
	        } 
	       return connection;
	}		
	
	// Returns current time from 'general' database, id = 0, with type, java.sql.Date
	public java.sql.Date getCurrentTime() throws SQLException{
        Connection connection = null;
        Statement statement = null;
        java.sql.Date sqlDate = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
        	String QUERY = "SELECT time_value FROM general WHERE id = 0";
            connection = DriverManager.getConnection(HOST, USER, PWD);
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(QUERY);
            while (resultSet.next()) {
            	sqlDate = resultSet.getDate("time_value");
            	//System.out.println("Current SQL Date is: "+ sqlDate);
            }
            
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
        return sqlDate;
	}
	
	// Set a new date to be today's date
	public void setCurrentDate(Scanner sc) throws SQLException{
		// Get desired date from user
		System.out.println("Enter new date with format (YYYY-MM-DD): ");
		sc.nextLine();
		String newDate = sc.nextLine();
		
		// Prepare query and send to DB, updating the new date
       try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        PreparedStatement statement = null;
		String QUERY = "UPDATE general SET time_value = ? WHERE id = 0";
        try {
            connection = DriverManager.getConnection(HOST, USER, PWD);
            statement = connection.prepareStatement(QUERY);
            statement.setDate(1,java.sql.Date.valueOf(newDate));
            statement.executeUpdate();
            
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
	}

}
