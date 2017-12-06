package starsrus;

import java.sql.*;
import java.util.Scanner;
import java.lang.StringBuilder;
public class Customer {
	
	// User information
	String username, password, name, taxid, phone_number, email, state;
	
	// SQL info
	public static final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/kevinchanDB";
	public static final String USER = "kevinchan";
	public static final String PWD = "651";

	
	// Default constructor
	public Customer(){
		
	}
	
	// Constructor for login
	public Customer(String u, String p){
		username = u;
		password = p;
	}
	
	// Helper func that wraps values with '' for SQL queries
	public String wrapValue(String s){
		StringBuilder _sb = new StringBuilder(s);
		_sb.insert(0,  "'");
		_sb.append("'");
		return _sb.toString();
	}
	
	// Send query to DB
	public ResultSet queryDB(String query) throws SQLException{
		ResultSet resultSet = null;
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
            resultSet = statement.executeQuery(query);
            return resultSet;
            
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
        return resultSet;
	}
	
	// Register function
	public void register(Scanner sc) throws SQLException{
		
		// Gather information from user
		System.out.print("Enter username: ");
		sc.nextLine();
		String newUsername = sc.nextLine();
		System.out.print("Enter password: ");
		String newPassword = sc.nextLine();
		System.out.print("Enter name: ");
		String newName = sc.nextLine();
		System.out.print("Enter tax ID number: ");
		String newTaxID = sc.nextLine();
		System.out.print("Enter phone number: ");
		String newPhone = sc.nextLine();
		System.out.print("Enter email: ");
		String newEmail = sc.nextLine();
		System.out.print("Enter state: ");
		String newState = sc.nextLine();
		
		Integer numNewTaxID = Integer.valueOf(newTaxID);
		// Prepare query data
		/*
		newUsername = wrapValue(newUsername);
		newPassword = wrapValue(newPassword);
		newName = wrapValue(newName);
		newTaxID = wrapValue(newTaxID);
		newPhone = wrapValue(newPhone);
		newEmail = wrapValue(newEmail);
		newState = wrapValue(newState);
		
		
		// Create query and send to db
		String QUERY = "INSERT INTO customers VALUES (" + newUsername + ","
													+ newPassword + ","
													+ newName + ","
													+ newTaxID + ","
													+ newPhone + ","
													+ newEmail + ","
													+ newState + ")";
		ResultSet resultSet = queryDB(QUERY);
		*/
		
		String QUERY = "INSERT INTO customers " + 
						"(username, password, name, taxid, phone_number, email, state) VALUES "
						+ "(?,?,?,?,?,?,?)";
		
		Connection con = null;
		PreparedStatement stm = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(QUERY);
			stm.setString(1,newUsername);
			stm.setString(2, newPassword);
			stm.setString(3,newName);
			stm.setInt(4,numNewTaxID);
			stm.setString(5,newPhone);
			stm.setString(6, newEmail);
			stm.setString(7, newState);
			
			stm.executeUpdate();
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
			if (stm != null){
				stm.close();
			}
			if (con != null){
				con.close();
			}
		}
	}
	
	// Login function
	public boolean login(Scanner sc) throws SQLException {
		
		// Get info from user
		System.out.print("Enter username:  ");
		sc.nextLine();
		String loginUsername = sc.nextLine();
		System.out.print("Enter password:  ");
		String loginPassword = sc.nextLine();
        
        // Update customer
        this.username = loginUsername;
        this.password = loginPassword;
        
		String QUERY = "SELECT * from customers WHERE username='" + loginUsername + "' && password='" + loginPassword + "'";
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
            ResultSet resultSet = statement.executeQuery(QUERY);
            
            String resultUsername = "";
            String resultPassword = "";
            while (resultSet.next()) {
                resultUsername = resultSet.getString("username");
                resultPassword = resultSet.getString("password");

            }
            // Compare username and password
            if (resultUsername.equals(username) && resultPassword.equals(password)){
            	System.out.println("Successful login!");
            	return true;
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
        System.out.println("Unsuccessful login, try again!");
        return false;
	}
	
	

}
