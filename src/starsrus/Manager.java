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
		PreparedStatement stm1 = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		PreparedStatement stm4 = null;
		ResultSet rs = null;
		
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			
			// Query - Update running_balance = running_balance/30 * ( 0.03 / 12); monthly interest rate= annual rate /12
			String query = "UPDATE market_accounts SET running_balance = (running_balance/30) * (0.03/12) ";
			stm = con.prepareStatement(query);
			stm.executeUpdate();
			
			// Query - Get current date
			query = "SELECT time_value FROM general";
			java.sql.Date date = null;
			stm1 = con.prepareStatement(query);
			rs = stm1.executeQuery();
			while(rs.next()){
				date = rs.getDate("time_value");
			}
			
			// Query - Add transaction entry into transactions table
			query = "INSERT INTO transactions (aid,type,date,amount) SELECT aid,'interest',?,running_balance FROM market_accounts";
			stm2 = con.prepareStatement(query);
			stm2.setDate(1, date);
			stm2.executeUpdate();
			
			// Query - Add interest into all market accounts: Divide running_balance by 30 and 
			// add that amount to balance
			query = "UPDATE market_accounts SET balance = balance + running_balance";
			stm3 = con.prepareStatement(query);
			stm3.executeUpdate();
			
			// Query - Reset running_balance to balance because it is the end of the month
			query = "UPDATE market_accounts SET running_balance = balance";
			stm4 = con.prepareStatement(query);
			stm4.executeUpdate();
			
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
	// 4.) Generate monthly statement 
	
	// 4.) Generate monthly statement for user
	public void monthlyStatement(Scanner sc) throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		PreparedStatement stm4 = null;
		PreparedStatement stm5 = null;
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
			
			System.out.println();
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
			System.out.println();
			
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
			double amount = 0.0d;
			
			
			ArrayList<Double> balances = new ArrayList<Double>();
			// Only market account
			if(aidList.size() == 1){
				System.out.println("In market account: ");
				stm3 = con.prepareStatement(query);
				stm3.setInt(1, aidList.get(0));
				stm3.setInt(2, month);
				stm3.setInt(3, year);
				rs = stm3.executeQuery();
				
				// Query - Get initial and final balance for market account
				balances = getBalances(aidList.get(0));
				System.out.println("   - Initial balance: " + balances.get(0));
				while(rs.next()){
					type = rs.getString("type");
					amount = rs.getDouble("amount");
					System.out.println(" 	- " + type + " of " + amount + "was made.");
				}
				System.out.println("   - Final balance: " + balances.get(1));
				
				// Query - Get total earnings
				double earnings = getEarnings(aidList.get(0), -1);
				System.out.println("Total earnings: " + earnings);

				// Query - Get total amount of commissions paid
				double commissions = getCommissions(aidList.get(0), -1);
				System.out.println("Total amount of commissions paid: " + commissions);
				
			}
			// Market AND stock accounts
			else{

				// Market account first
				balances = getBalances(aidList.get(0));
				System.out.println("In market account: ");
				System.out.println("   - Initial balance: " + balances.get(0));
				stm3 = con.prepareStatement(query);
				stm3.setInt(1, aidList.get(0));
				stm3.setInt(2, month);
				stm3.setInt(3, year);
				rs = stm3.executeQuery();
				while(rs.next()){
					type = rs.getString("type");
					amount = rs.getDouble("amount");
					System.out.println(" 	- " + type + " of " + amount + " was made");

				}
				System.out.println("   - Final balance: " + balances.get(1));
				System.out.println();
				
				// Stock account
				balances = getBalances(aidList.get(1));
				System.out.println("In stock account: ");
				System.out.println("   - Initial balance: " + balances.get(0));
				String stock_symbol = "";
				int num_shares = 0;
				double buy_price = 0.0f;
				double sell_price = 0.0f;
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
					buy_price = rs.getDouble("buy_price");
					sell_price = rs.getDouble("sell_price");
					if(type.equals("buy")){
						System.out.println(" 	- " + type + " of " + num_shares + " of " + stock_symbol + " at " + buy_price + " was made");
					}
					if(type.equals("sell")){
						System.out.println(" 	- " + type + " of " + num_shares + " of " + stock_symbol + " at " + sell_price + " was made");
					}
				}
				System.out.println("   - Final balance: " + balances.get(1));
				System.out.println();
				
				// Query - Get total earnings
				double earnings = getEarnings(aidList.get(0),aidList.get(1));
				System.out.println("Total earnings/loss: " + earnings);
				
				// Query - Get total amount of commissions paid
				double commissions = getCommissions(aidList.get(0), aidList.get(1));
				System.out.println("Total amount of commissions paid: " + commissions);
			}
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (stm2 != null) try { stm2.close(); } catch (SQLException e) {}
            if (stm3 != null) try { stm3.close(); } catch (SQLException e) {}
            if (stm4 != null) try { stm4.close(); } catch (SQLException e) {}
            if (stm5 != null) try { stm5.close(); } catch (SQLException e) {}
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
			
			System.out.println("This month's active users: ");
			while(rs.next()){
				username = rs.getString("username");
				System.out.println("	- " + username);
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
				System.out.println("Name: " + name + " , State: " + state );
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
		PreparedStatement stm2 = null;
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
			double balance = 0.0d;
			while(rs.next()){
				balance = rs.getDouble("balance");
			}
			System.out.println();
			System.out.println("For username: " + username);
			System.out.println();
			System.out.println("	- Market account balance: " + balance);
			System.out.println();
			
			// Query - Returns stock_account balance
			query = "SELECT sa.balance,sa.aid FROM stock_accounts_balance sa INNER JOIN (select aid,username FROM accounts WHERE username = ?) a ON sa.aid = a.aid";
			stm2 = con.prepareStatement(query);
			stm2.setString(1, username);
			rs = stm2.executeQuery();
			double stock_balance = 0.0d;
			while(rs.next()){
				stock_balance = rs.getDouble("balance");
			}
			System.out.println("	- Stock account balance: " + stock_balance);
			
			// Query - Returns stocks in stock account according to user name
			query = "SELECT stock_symbol,amount_bought,bought_at FROM stock_accounts INNER JOIN (select aid,account_type FROM accounts WHERE username = ?) a ON stock_accounts.aid = a.aid";
			stm1 = con.prepareStatement(query);
			stm1.setString(1, username);
			rs = stm1.executeQuery();
			String stock_symbol = "";
			int amount_bought = 0;
			double bought_at = 0.0d;
			
			while(rs.next()){
				stock_symbol = rs.getString("stock_symbol");
				amount_bought = rs.getInt("amount_bought");
				bought_at = rs.getDouble("bought_at");
				System.out.println("		- " + amount_bought + " stocks of " + stock_symbol + " bought at " + bought_at);
			}
		
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (stm2 != null) try { stm2.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
	}
	
	// 8.) Delete Transactions
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
			
			// Query - Update running balances to 0
			query = "UPDATE market_accounts SET running_balance = 0";
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
	
	// 9.) Open Market
	public void openMarket() throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();		

			// Query - Update in general: isMarketOpen to 1 (open)
			String query = "UPDATE general SET isMarketOpen = 1 WHERE id = 1";
			stm = con.prepareStatement(query);
			stm.executeUpdate();
		
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
		}	
		System.out.println("Market is now open!");
		System.out.println();
	}
	
	// 10.)Close market for the day 
	
	// 10.) Close Market
	public void closeMarket() throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		ResultSet rs = null;
		
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - In market_accounts, update running_balance = running_balance + balance
			String query = "UPDATE market_accounts SET running_balance = balance + running_balance";
			stm = con.prepareStatement(query);
			stm.executeUpdate();
			
			// Query - Update daily closing price of stock
			query = "UPDATE actor_directors SET daily_closing_price = stock_price";
			stm3 = con.prepareStatement(query);
			stm3.executeUpdate();
		
			// Query - Update current day to next day
			query = "UPDATE general SET time_value = DATE_ADD(time_value, INTERVAL 1 DAY) WHERE id = 0";
			stm1 = con.prepareStatement(query);
			stm1.executeUpdate();

			// Query - Update in general: isMarketOpen to 0 (closed)
			query = "UPDATE general SET isMarketOpen = 0 WHERE id = 1";
			stm2 = con.prepareStatement(query);
			stm2.executeUpdate();
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (stm2 != null) try { stm2.close(); } catch (SQLException e) {}
            if (stm3 != null) try { stm3.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}	
		System.out.println("Market is now closed for the day!");
		System.out.println();
	}
	
	// Returns total commissions paid for a given account aid
	public double getCommissions(int marketAID, int stockAID) throws SQLException{
		double commissions = 0;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - Get 
			String query = "select COUNT(aid) as commissions FROM transactions WHERE (aid = ?)";
			stm = con.prepareStatement(query);
			stm.setInt(1, stockAID);
			rs = stm.executeQuery();
			if(rs.next()){
				commissions = rs.getDouble("commissions");
			}
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}	
		
		return commissions;
	}
	
	// Returns a month's earnings for a given account aid
	public double getEarnings(int marketAID, int stockAID) throws SQLException{
		
		double earnings = 0;
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - Get 
			String query = "SELECT SUM(amount) as earnings FROM transactions WHERE (aid = ? AND type = 'interest') OR ( aid = ? AND type ='sell')";
			stm = con.prepareStatement(query);
			stm.setInt(1, marketAID);
			stm.setInt(2, stockAID);
			rs = stm.executeQuery();
			if(rs.next()){
				earnings = rs.getDouble("earnings");
			}
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}	
		
		return earnings;
	}
	
	// Returns initial and final balances for an account aid
	public ArrayList<Double> getBalances(int aid) throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		ArrayList<Double> balances = new ArrayList<Double>();
		
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
		
			// Query - In market_accounts, update running_balance = running_balance + balance
			String query = "SELECT tid,aid,balance,date FROM transactions WHERE aid = ? ORDER BY date DESC";
			stm = con.prepareStatement(query);
			stm.setInt(1, aid);
			rs = stm.executeQuery();
			double balance = 0.0f;
			if(rs.next()){
				balance = rs.getDouble("balance");
				balances.add(balance);
			}
			rs.afterLast();
			if(rs.previous()){
				balance = rs.getDouble("balance");
				balances.add(balance);
			}
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}	
		return balances;
	}
}
