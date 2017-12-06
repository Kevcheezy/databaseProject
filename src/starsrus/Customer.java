package starsrus;

import java.sql.*; 
import java.util.*;
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
	
	// 1) Deposit into market account
	public void deposit(Integer amt) throws SQLException {
		System.out.println("Depositing " + amt.toString() + "...");
		
		// Send request query to database getting current market account balance of user
		String query = "SELECT balance,aid FROM market_accounts WHERE username = ?";
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm2 = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setString(1,this.username);
			ResultSet rs = stm.executeQuery();
			int userBalance = 0;
			int aid = -1;
			while (rs.next()){
				userBalance = rs.getInt("balance");
				aid = rs.getInt("aid");
			}
			// Increase balance by amt
			System.out.println("Current market account balance: " + userBalance);
			userBalance = userBalance + amt;
			// Update database using aid and new balance amount
			query = "UPDATE market_accounts SET balance = ? WHERE aid = ?";
			stm2 = con.prepareStatement(query);
			stm2.setInt(1,userBalance);
			stm2.setInt(2, aid);
			stm2.executeUpdate();
			System.out.println("New market account balance is: " + userBalance);
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
			if (stm != null){
				stm.close();
			}
			if (stm2 != null){
				stm2.close();
			}
			if (con != null){
				con.close();
			}
		}
	}
	
	// 2) Withdraw from market account
	public void withdraw(Integer amt) throws SQLException{
		System.out.println("Withdrawing " + amt.toString() + "...");
		// Send request query to database getting current market account balance of user
		String query = "SELECT balance,aid FROM market_accounts WHERE username = ?";
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm2 = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setString(1,this.username);
			ResultSet rs = stm.executeQuery();
			int userBalance = 0;
			int aid = -1;
			while (rs.next()){
				userBalance = rs.getInt("balance");
				aid = rs.getInt("aid");
			}
			// Increase balance by amount
			System.out.println("Current market account balance: " + userBalance);
			userBalance = userBalance - amt;
			if (userBalance < 0){
				System.out.println("ERROR: BALANCE BELOW $0!");
				return;
			}
			// Update database using aid and new balance amount
			query = "UPDATE market_accounts SET balance = ? WHERE aid = ?";
			stm2 = con.prepareStatement(query);
			stm2.setInt(1,userBalance);
			stm2.setInt(2, aid);
			stm2.executeUpdate();
			System.out.println("New market account balance: " + userBalance);
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
			if (stm != null){
				stm.close();
			}
			if (stm2 != null){
				stm2.close();
			}
			if (con != null){
				con.close();
			}
		}		
	}
	
	// 5) Show market account balance
	public void showMarketAccountBalance() throws SQLException{
		// Send request query to database getting current market account balance of user
		String query = "SELECT balance,aid FROM market_accounts WHERE username = ?";
		Connection con = null;
		PreparedStatement stm = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setString(1,this.username);
			ResultSet rs = stm.executeQuery();
			int userBalance = 0;
			while (rs.next()){
				userBalance = rs.getInt("balance");
			}
			System.out.println("Current market account balance: " + userBalance);
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
	
	// 8a) List all movie information
	public void showMovies(Scanner sc) throws SQLException{
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		String query = "SELECT id,title FROM Movies";
		try {
			MySQLDB db = new MySQLDB();
			con = db.getMoviesDBConnection();
			stm = con.prepareStatement(query);
			ResultSet rs = stm.executeQuery();
			Map<Integer, String> moviesMap = new HashMap<Integer,String>();
			
			String title = "";
			int movieID = -1;
			float rating = 0.0f;
			int prodYear = 0;
			
			while (rs.next()){
				movieID = rs.getInt("id");
				title = rs.getString("title");
				moviesMap.put(movieID, title);
			}
			
			// Display movies to choose from
			for(int i=1; i<= moviesMap.size(); i++){
				System.out.println(i + ") " + moviesMap.get(i));
			}
			
			// Pick movie to display
			System.out.print("Enter index of movie to display details : ");
			int choice = sc.nextInt();
			query = "SELECT * FROM Movies WHERE id = ?";
			stm1 = con.prepareStatement(query);
			stm1.setInt(1,choice);
			rs = stm1.executeQuery();
			while (rs.next()){
				movieID = rs.getInt("id");
				title = rs.getString("title");
				rating = rs.getFloat("rating");
				prodYear = rs.getInt("production_year");
			}
			
			// Display movie results
			System.out.println();
			System.out.println("Movie ID: " + movieID);
			System.out.println("Movie title: " + title);
			System.out.println("Movie rating: " + rating);
			System.out.println("Production Year: " + prodYear);
			System.out.println();
			
		} catch (SQLException e){
			System.out.println(e.getMessage());
		} finally {
			if (stm != null){
				stm.close();
			}
			if (stm1 != null){
				stm1.close();
			}
			if (con != null){
				con.close();
			}
		}		
	}
}
