package starsrus;

import java.sql.*;
import java.util.*;
public class Manager {
	String username, password;
	
	// Default constructor
	public Manager(){
		
	}

	
	// 3.) Add interest
	public void addInterest() throws SQLException{
	
		Connection con = null;
		PreparedStatement stm = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - Add interest into all market accounts: Divide running_balance by 30 and 
			// add that amount to balance

			String query = "UPDATE market_accounts SET running_balance = balance";
			stm = con.prepareStatement(query);
			stm.executeUpdate();
			
			// Query - Add transaction entry into transactions table
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
		}	
				
	}
	// 4.) Generate monthly statement 
	public void monthlyStatement(Scanner sc) throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		PreparedStatement stm4 = null;
		ResultSet rs = null;

		try {
			
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Get current month from 'general'
			String query = "SELECT time_value FROM general WHERE id = 0";
			stm = con.prepareStatement(query);
			rs = stm.executeQuery();
			java.sql.Date currentDate = null;
			while(rs.next()){
				currentDate = rs.getDate("time_value");
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			int month = cal.get(Calendar.MONTH)+1;
			int year = cal.get(Calendar.YEAR);
			
			// Get customer user name from input
			System.out.print("Enter customer username: ");
			sc.nextLine();
			String username = sc.nextLine();
			
			System.out.println("---------- Monthly Statement for " + month + "/" + year + "----------");
			
			// Get email and name of user name
			query = "SELECT name,email FROM customers WHERE username = ?";
			stm1 = con.prepareStatement(query);
			stm1.setString(1, username);
			rs = stm1.executeQuery();
			String name = "";
			String email = "";
			while(rs.next()){
				name = rs.getString("name");
				email = rs.getString("email");
				System.out.println("Name: " + name);
				System.out.println("Email: " + email);
			}
			
			// Get all account id's that this customer owns
			query = "SELECT aid FROM accounts WHERE username = ?";
			stm2 = con.prepareStatement(query);
			stm2.setString(1, username);
			rs = stm2.executeQuery();
			ArrayList<Integer> aidList = new ArrayList<Integer>();
			while(rs.next()){
				aidList.add(rs.getInt("aid"));
			}
			
			
			// FROM EACH account, Get all transactions from this year and month
			query = "SELECT type,amount,stock_symbol,num_shares,buy_price,sell_price FROM transactions "
					+ "WHERE aid = ? AND MONTH(date) = ? AND YEAR(date) = ?";
			
			String type = "";
			float amount = 0.0f;

			// Only market account
			if(aidList.size() == 1){
				System.out.println("In market account: ");
				stm3 = con.prepareStatement(query);
				stm3.setInt(1, aidList.get(0));
				stm3.setInt(2, month);
				stm3.setInt(3, year);
				rs = stm3.executeQuery();
				while(rs.next()){
					type = rs.getString("type");
					amount = rs.getFloat("amount");
					System.out.println(" 	- " + type + " of " + amount + "was made.");
				}
			}
			// Market AND stock accounts
			else{

				// Market account first
				System.out.println("In market account: ");
				stm3 = con.prepareStatement(query);
				stm3.setInt(1, aidList.get(0));
				stm3.setInt(2, month);
				stm3.setInt(3, year);
				rs = stm3.executeQuery();
				while(rs.next()){
					type = rs.getString("type");
					amount = rs.getFloat("amount");
					System.out.println(" 	- " + type + " of " + amount + " was made");

				}
				
				// Stock account
				System.out.println("In stock account: ");
				String stock_symbol = "";
				int num_shares = 0;
				float buy_price = 0.0f;
				float sell_price = 0.0f;
				stm4 = con.prepareStatement(query);
				stm4.setInt(1, aidList.get(1));
				stm4.setInt(2, month);
				stm4.setInt(3, year);
				rs = stm4.executeQuery();
				while(rs.next()){
					type = rs.getString("type");
					amount = rs.getFloat("amount");
					stock_symbol = rs.getString("stock_symbol");
					num_shares = rs.getInt("num_shares");
					buy_price = rs.getFloat("buy_price");
					sell_price = rs.getFloat("sell_price");
					if(type.equals("buy")){
						System.out.println(" 	- " + type + " of " + num_shares + " of " + stock_symbol + " at " + buy_price + " was made");
					}
					if(type.equals("sell")){
						System.out.println(" 	- " + type + " of " + num_shares + " of " + stock_symbol + " at " + sell_price + " was made");
					}
				}
				
				
			}
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (stm2 != null) try { stm2.close(); } catch (SQLException e) {}
            if (stm3 != null) try { stm3.close(); } catch (SQLException e) {}
            if (stm4 != null) try { stm4.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
	}
	
	// 5.) List active customers
	public void activeCustomers() throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		ResultSet rs = null;

		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Get current month from 'general'
			String query = "SELECT time_value FROM general WHERE id = 0";
			stm = con.prepareStatement(query);
			rs = stm.executeQuery();
			java.sql.Date currentDate = null;
			while(rs.next()){
				currentDate = rs.getDate("time_value");
			}
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			int month = cal.get(Calendar.MONTH)+1;
			int year = cal.get(Calendar.YEAR);
			
			// Execute target query - returns user names satisfying requirement
			query = "SELECT DISTINCT accounts.username FROM accounts INNER JOIN (SELECT transactions.aid, SUM(num_shares),date FROM transactions GROUP BY aid HAVING MONTH(date) = ? AND YEAR(date) = ? AND SUM(num_shares) >= 1000) t on t.aid = accounts.aid;";
			stm1 = con.prepareStatement(query);
			stm1.setInt(1,month);
			stm1.setInt(2,year);
			rs = stm1.executeQuery();
			ArrayList<String> usernameList = new ArrayList<String>();
			String name = "";
			while(rs.next()){
				username = rs.getString("username");
				usernameList.add(username);
			}
			
			System.out.println();
			System.out.println("This month's active users");
			for(int i = 0; i < usernameList.size(); i++){
				System.out.println("Username: " + usernameList.get(i));
			}
			System.out.println();
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
	}
	
	// 6.) Generate government DTER  
	public void generateDTER() throws SQLException{
		String query = "SELECT name,state FROM customers INNER JOIN (SELECT accounts.username FROM accounts INNER JOIN (SELECT aid,SUM(amount),type FROM transactions GROUP BY aid,type HAVING SUM(amount) >= 10000 AND (type = 'sell' OR type = 'interest')) t ON t.aid = accounts.aid)a ON customers.username = a.username";
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			//Query
			stm = con.prepareStatement(query);
			rs = stm.executeQuery();
			String name = "";
			String state = "";

			System.out.println();
			System.out.println("------- Government Drug & Tax Evasion Report (DTER) -------");
			System.out.println();
			
			while(rs.next()){
				name = rs.getString("name");
				state = rs.getString("state");
				System.out.println("Name: " + name + " from " + state );
			}

		
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}

		}	
	}
	
	// 7.) Customer report 
	public void customerReport(Scanner sc) throws SQLException{
		
		// Get customer user name from input
		System.out.print("Enter customer's username: ");
		sc.nextLine();
		String username = sc.nextLine();
		
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		ResultSet rs = null;

		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - Returns market account balance according to user name
			// Execute target query - returns balance of user name
			String query = "SELECT balance FROM market_accounts INNER JOIN (select aid,account_type FROM accounts WHERE username = ?) a ON market_accounts.aid = a.aid";
			stm = con.prepareStatement(query);
			stm.setString(1,username);
			rs = stm.executeQuery();
			int balance = 0;
			while(rs.next()){
				balance = rs.getInt("balance");
			}
			System.out.println("For username: " + username);
			System.out.println("	- Market account balance: " + balance);
			System.out.println();
			System.out.println("	- Stock account balance: ");
			
			// Query - Returns stock account balance according to user name
			query = "SELECT stock_symbol,amount_bought,bought_at FROM stock_accounts INNER JOIN (select aid,account_type FROM accounts WHERE username = ?) a ON stock_accounts.aid = a.aid";
			stm1 = con.prepareStatement(query);
			stm1.setString(1, username);
			rs = stm1.executeQuery();
			String stock_symbol = "";
			int amount_bought = 0;
			float bought_at = 0.0f;
			
			while(rs.next()){
				stock_symbol = rs.getString("stock_symbol");
				amount_bought = rs.getInt("amount_bought");
				bought_at = rs.getFloat("bought_at");
				System.out.println("		- " + amount_bought + " stocks of " + stock_symbol + " bought at " + bought_at);
			}
		
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
	}
	
	// 8.) Delete transactions 
	public void deleteTransactions() throws SQLException{
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - Delete the list of transactions from each of the accounts
			String query = "DELETE FROM transactions";
			stm = con.prepareStatement(query);
			stm.executeUpdate();

			query = "DELETE FROM daily_market_balance";
			stm1 = con.prepareStatement(query);
			stm1.executeUpdate();
		
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
		}	
	}
	
	// 9.) Open market for the day
	public void openMarket() throws SQLException{
		// Update current date to next day
		
	}
	// 10.)Close market for the day 
	public void closeMarket() throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - In market_accounts, update running_balance = balance
			String query = "UPDATE market_accounts SET running_balance = balance";
			stm = con.prepareStatement(query);
			stm.executeUpdate();
			
			// Query - Store daily closing price of stock?
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
		}	
		
	}
}
