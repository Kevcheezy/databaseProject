package starsrus;

import java.sql.*; 
import java.util.*;
public class Customer {
	
	// User information
	String username, password;
	
	// SQL info
	public static final String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/kevinchanDB";
	public static final String USER = "kevinchan";
	public static final String PWD = "651";

	
	// Default constructor
	public Customer(){
		
	}
	
	// 1) Deposit into market account
	public void deposit(Integer amt) throws SQLException {
		System.out.println("Depositing " + amt.toString() + " ...");
		
		// Send request query to database getting current market account balance of user
		String query = "";
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			double userBalance =  getMarketAccountBalance();
			
			// Increase balance by amount
			System.out.println("Current market account balance: " + String.format("%.2f", userBalance));
			userBalance = userBalance + (double)amt;
			
			// Update database using aid and new balance amount
			query = "UPDATE market_accounts SET balance = ? WHERE taxID = ?";
			int taxID = taxIDOfUsername(this.username);
			stm2 = con.prepareStatement(query);
			stm2.setDouble(1,userBalance);
			stm2.setInt(2, taxID);
			stm2.executeUpdate();
			System.out.println("New market account balance is: " + String.format("%.2f", userBalance));
			
			
			// Insert a new record into transaction table for this deposit
			query = "INSERT INTO transactions (aid, type, date, amount, balance) VALUES (?,?,?,?,?)";
			java.sql.Date sqlDate = db.getCurrentTime();
			int aid = getAIDofAccount("market");
			stm3 = con.prepareStatement(query);
			stm3.setInt(1, aid);
			stm3.setString(2, "deposit");
			stm3.setDate(3, sqlDate);
			stm3.setDouble(4, (double)amt);
			stm3.setDouble(5, userBalance);
			stm3.executeUpdate();
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm2 != null) try { stm2.close(); } catch (SQLException e) {}
            if (stm3 != null) try { stm3.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
		}
	}
	
	// 2) Withdraw from market account
	public void withdraw(Integer amt) throws SQLException{
		
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			
			// Send request query to database getting current market account balance of user
			double userBalance = getMarketAccountBalance();
			
			// Increase balance by amount
			System.out.println("Current market account balance: " + String.format("%.2f", userBalance));
			userBalance = userBalance - (float)amt;
			if (userBalance < 0){
				System.out.println("ERROR: BALANCE BELOW $0!");
				return;
			}
			
			// Update database using aid and new balance amount
			String query = "UPDATE market_accounts SET balance = ? WHERE aid = ?";
			int aid = getAIDofAccount("market");
			stm2 = con.prepareStatement(query);
			stm2.setDouble(1,userBalance);
			stm2.setInt(2, aid);
			stm2.executeUpdate();
			System.out.println("New market account balance: " + String.format("%.2f", userBalance));
			
			// Insert a new record into transaction table for this withdrawal
			query = "INSERT INTO transactions (aid, type, date, amount, balance) VALUES (?,?,?,?,?)";
			java.sql.Date sqlDate = db.getCurrentTime();
			stm3 = con.prepareStatement(query);
			stm3.setInt(1, aid);
			stm3.setString(2, "withdrawal");
			stm3.setDate(3, sqlDate);
			stm3.setDouble(4, (double)amt);
			stm3.setDouble(5, userBalance);
			stm3.executeUpdate();
			
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm2 != null) try { stm2.close(); } catch (SQLException e) {}
            if (stm3 != null) try { stm3.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
		}
	}
	
	// 3) Buy stock
	public void buyStock(Scanner sc) throws SQLException{
		
		// Display all stocks for purchase
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		PreparedStatement stm4 = null;
		PreparedStatement stm5 = null;
		PreparedStatement stm6 = null;
		PreparedStatement stm7 = null;
		PreparedStatement stm8 = null;
		PreparedStatement stm9 = null;
		ResultSet rs = null;
		String query = "SELECT stock_symbol,stock_price FROM actor_directors";
		
		try {
			MySQLDB db = new MySQLDB();
			int isMarketOpen = db.isMarketOpen();
			
			// Only if market is open
			if(isMarketOpen == 1){
				con = db.getDBConnection();
				stm = con.prepareStatement(query);
				rs = stm.executeQuery();
				ArrayList<String> allStocks = new ArrayList<String>();
				ArrayList<Double> allStockPrices = new ArrayList<Double>();
				
				String stock_symbol = "";
				double stock_price = 0.0d;
				
				while (rs.next()){
					stock_symbol = rs.getString("stock_symbol");
					allStocks.add(stock_symbol);
					stock_price = rs.getFloat("stock_price");
					allStockPrices.add(stock_price);
				}
				
				for(int i=1; i<= allStocks.size(); i++){
					System.out.print(i + ") " + allStocks.get(i-1) + " | Price: ");
					System.out.printf("%.3f", allStockPrices.get(i-1));
					System.out.println();
				}
				// Display current balance
				double currentBalance = this.getMarketAccountBalance();
				double stock_account_balance = this.getStockAccountBalance();
				System.out.println("Current market account balance: " + currentBalance);
				System.out.println("Current stock account balance: " + stock_account_balance);
				
				// User selects stock and how many
				System.out.print("Enter index of stock to purchase: ");
				int choice = sc.nextInt();
				System.out.print("Number of stocks to purchase: ");
				int numStock = sc.nextInt();
				String chosenStockSymbol = allStocks.get(choice-1);
				double chosenStockPrice = allStockPrices.get(choice-1);
				
				// Deduct money from market account (make sure balance >= 0)
				double totalPrice = (double)numStock * allStockPrices.get(choice-1) + 20.0d;
				double newBalance = (double)currentBalance - totalPrice;
				
				// If user does not have enough money to make this purchase, do not allow
				if (newBalance < 0){
					System.out.println("ERROR! Market account has insufficient funds to make this purchase!");
				}
				else {
					// Otherwise, update new market account balance
					int aid = getAIDofAccount("market");
					query = "UPDATE market_accounts SET balance = ? WHERE aid = ?";
					stm2 = con.prepareStatement(query);
					stm2.setDouble(1,newBalance);
					stm2.setInt(2, aid);
					stm2.executeUpdate();
					System.out.println();
					System.out.println("New market account balance: " + String.format("%.2f", newBalance));	
					
					
					// Check if user has existing stock account
					query = "SELECT aid FROM accounts WHERE username = ? && account_type = ?";
					stm4 = con.prepareStatement(query);
					stm4.setString(1, this.username);
					stm4.setString(2, "stock");
					rs = stm4.executeQuery();
					int useraid = -1;
					while(rs.next()){
						useraid = rs.getInt("aid");
					}
					
					// If user has an existing stock account: add purchased stock info into account
					if (useraid != -1){
						query = "INSERT INTO stock_accounts (aid,stock_symbol,amount_bought,bought_at,date) VALUES (?,?,?,?,?)";
						stm5 = con.prepareStatement(query);
						stm5.setInt(1, useraid);
						stm5.setString(2, chosenStockSymbol);
						stm5.setInt(3, numStock);
						stm5.setDouble(4, chosenStockPrice);
						stm5.setDate(5, db.getCurrentTime());
						stm5.executeUpdate();
						
						// Query - Update stock_account_balance with new balance
						query = "UPDATE stock_accounts_balance SET balance = ? WHERE aid = ?";
						stock_account_balance = stock_account_balance + (numStock * chosenStockPrice);
						stm9 = con.prepareStatement(query);
						stm9.setDouble(1,stock_account_balance);
						stm9.setInt(2, useraid);
						System.out.println("New stock account balance: " + String.format("%.3f", stock_account_balance));
						
					}
					
					// If user does NOT have stock account, create one and add purchased stock info into account
					else{
						// Create new account
						int taxID = taxIDOfUsername(this.username);
						query = "INSERT INTO accounts (account_type,username, taxID) VALUES (?,?,?)";
						stm6 = con.prepareStatement(query);
						stm6.setString(1, "stock");
						stm6.setString(2, this.username);
						stm6.setInt(3, taxID);
						stm6.executeUpdate();
						
						// Get newly made aid of stock account
						int newStockaid = getAIDofAccount("stock");
						
						// Create new stock account entry
						query = "INSERT INTO stock_accounts (aid,stock_symbol,amount_bought,bought_at,date) VALUES (?,?,?,?,?)";
						stm7 = con.prepareStatement(query);
						stm7.setInt(1, newStockaid);
						stm7.setString(2, chosenStockSymbol);
						stm7.setInt(3, numStock);
						stm7.setDouble(4, chosenStockPrice);
						stm7.setDate(5, db.getCurrentTime());
						stm7.executeUpdate();
						
						// Query - Insert stock_account_balance with new balance
						query = "INSERT INTO stock_accounts_balance (balance,aid) VALUES (?,?)";
						stock_account_balance = getStockAccountBalance() + (numStock * chosenStockPrice);
						stm9 = con.prepareStatement(query);
						stm9.setDouble(1,stock_account_balance);
						stm9.setInt(2, newStockaid);
						System.out.println("New stock account balance: " + String.format("%.3f", stock_account_balance));
					}
					
					// Insert new buy transaction
					query = "INSERT INTO transactions (aid,type,date,amount,stock_symbol,num_shares,buy_price, balance)"
							+ "VALUES (?,?,?,?,?,?,?,?)";
					aid = getAIDofAccount("stock");
					stm3 = con.prepareStatement(query);
					stm3.setInt(1, aid);
					stm3.setString(2, "buy");
					stm3.setDate(3, db.getCurrentTime());
					stm3.setDouble(4,totalPrice);
					stm3.setString(5, chosenStockSymbol);
					stm3.setInt(6, numStock);
					stm3.setDouble(7, chosenStockPrice);
					stm3.setDouble(8, stock_account_balance);
					stm3.executeUpdate();
				}
			}
			else{
				System.out.println("ERROR! Market is not open!");
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
            if (stm6 != null) try { stm6.close(); } catch (SQLException e) {}
            if (stm7 != null) try { stm7.close(); } catch (SQLException e) {}
            if (stm8 != null) try { stm8.close(); } catch (SQLException e) {}    
            if (stm9 != null) try { stm9.close(); } catch (SQLException e) {}    
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
	}
	
	// 4) Sell stock
	public void sellStock(Scanner sc) throws SQLException{

		System.out.println();
		// Show user which stocks are in account
		String query = "SELECT stock_symbol,amount_bought,bought_at,sa_id FROM stock_accounts WHERE aid = ?";
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		PreparedStatement stm2 = null;
		PreparedStatement stm3 = null;
		PreparedStatement stm4 = null;
		PreparedStatement stm5 = null;
		ResultSet rs = null;
		
		try {
			int stockAID = getAIDofAccount("stock");
			MySQLDB db = new MySQLDB();
			int isMarketOpen = db.isMarketOpen();
			if(isMarketOpen == 1){
				con = db.getDBConnection();
				stm = con.prepareStatement(query);
				stm.setInt(1,stockAID);
				rs = stm.executeQuery();
				
				String stock_symbol = "";
				int amount_bought = 0;
				double bought_at = 0.0d;
				int sa_id = -1;
				
				ArrayList<String> stock_symbolList = new ArrayList<String>();
				ArrayList<Integer> amount_boughtList = new ArrayList<Integer>();
				ArrayList<Double> bought_atList = new ArrayList<Double>();
				ArrayList<Integer> sa_idList = new ArrayList<Integer>();
				
				while (rs.next()){
					stock_symbol = rs.getString("stock_symbol");
					amount_bought = rs.getInt("amount_bought");
					bought_at = rs.getDouble("bought_at");
					sa_id = rs.getInt("sa_id");
					stock_symbolList.add(stock_symbol);
					amount_boughtList.add(amount_bought);
					bought_atList.add(bought_at);
					sa_idList.add(sa_id);
				}
				
				for(int i=1; i<=stock_symbolList.size(); i++){
					System.out.println(i+") " + amount_boughtList.get(i-1) + " " + stock_symbolList.get(i-1) + " bought at " + bought_atList.get(i-1));
				}
				
				// Display current stock account balance
				double stock_account_balance = this.getStockAccountBalance();
				System.out.println("Current stock account balance: " + String.format("%.3f", stock_account_balance));
				
				// User chooses which stock to sell: list all in stock account
				System.out.print("Enter index of stock to sell: ");
				int choice = sc.nextInt();
				
				// User picks how many of stocks to sell
				System.out.print("Quantity: ");
				int numStocks = sc.nextInt();
				
				String chosenStockSymbol = stock_symbolList.get(choice-1);
				Double chosenBoughtAt = bought_atList.get(choice-1);
				int chosenAmountBought = amount_boughtList.get(choice-1);
				int chosensaID = sa_idList.get(choice-1);
				
				// Make sure user has enough of the stocks to sell
				// Calculate stocks remaining = amount_bought - numStocks
				int stocksLeft = chosenAmountBought - numStocks;
				if (stocksLeft < 0){
					System.out.println("You do not have enough stocks.");
				}
				else{
					// Get current price of stock
					query = "SELECT stock_price FROM actor_directors WHERE stock_symbol = ?";
					stm1 = con.prepareStatement(query);
					stm1.setString(1,chosenStockSymbol);
					rs = stm1.executeQuery();
					float stock_price = 0.0f;
					while(rs.next()){
						stock_price = rs.getFloat("stock_price");
					}
					
					// Calculate and update earnings in transactions = (current price - bought_at) * numStocks - commission
					double earnings = (stock_price - chosenBoughtAt) * numStocks - 20;
					double marketBalance = getMarketAccountBalance();
					
					// Check if stock account balance is < 0, if so, do not proceed
					stock_account_balance = stock_account_balance + (numStocks * stock_price) - 20;
					if (stock_account_balance >= 0){
						
						// If the transaction will make marketBalance < 0, don't let
						if(marketBalance >= 20){
							query = "INSERT INTO transactions (aid,type,date,amount,stock_symbol,num_shares,sell_price, balance) VALUES (?,?,?,?,?,?,?,?)";
							stm3 = con.prepareStatement(query);
							stm3.setInt(1, stockAID);
							stm3.setString(2, "sell");
							stm3.setDate(3, db.getCurrentTime());
							stm3.setDouble(4, earnings);
							stm3.setString(5, chosenStockSymbol);
							stm3.setInt(6, numStocks);
							stm3.setDouble(7, stock_price);
							stm3.setDouble(8, stock_account_balance);
							stm3.executeUpdate();
							
							// Query - Update stock_account_balance with new balance
							query = "UPDATE stock_accounts_balance SET balance = ? WHERE aid = ?";
							stm5 = con.prepareStatement(query);
							stm5.setDouble(1,stock_account_balance);
							stm5.setInt(2, stockAID);
							stm5.executeUpdate();
							System.out.println("New stock account balance: " + String.format("%.3f", stock_account_balance));
							
							// Update or delete stock_account depending on stocks left
							if (stocksLeft == 0){
								// Delete records with no stocks left in stock account
								System.out.println("Sold all of that stock!");
								query = "DELETE FROM stock_accounts WHERE sa_id = ?";
								stm4 = con.prepareStatement(query);
								stm4.setInt(1, chosensaID);
								stm4.executeUpdate();
							}
							
							else{
								// Update stocks remaining in stock account
								query = "UPDATE stock_accounts SET amount_bought = ? WHERE sa_id = ?";
								stm2 = con.prepareStatement(query);
								stm2.setInt(1, stocksLeft);
								stm2.setInt(2,chosensaID);
								stm2.executeUpdate();
							}
							System.out.println("You earned: " + earnings + "!");
						}
						else{
							System.out.println("ERROR! Insufficient funds in market account!");
						}
					}
					
					// Error: stock account balance will go below 0
					else{
						System.out.println("ERROR! Stock account balance will go below 0!");
					}
				}
			}
			else{
				System.out.println("ERROR! Market is not open!");
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
	
	// 5) Show market account balance
	public double getMarketAccountBalance() throws SQLException{
		
		System.out.println();
		// Send request query to database getting current market account balance of user
		int taxID = taxIDOfUsername(this.username);
		String query = "SELECT balance FROM market_accounts WHERE taxID = ?";
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		double userBalance = 0;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setInt(1,taxID);
			rs = stm.executeQuery();
			while (rs.next()){
				userBalance = rs.getFloat("balance");
			}
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return userBalance;
	}
	
	// 6) Show stock account transaction history
	public void showStockTransactionHistory() throws SQLException{
		String query = "SELECT type,date,amount,stock_symbol,num_shares,buy_price,sell_price FROM transactions WHERE aid = ?";
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		ResultSet rs = null;
		
		try {
			int stockAID = getAIDofAccount("stock");
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setInt(1,stockAID);
			rs = stm.executeQuery();
			
			// Query - Show recent stock transaction history
			String type = "";
			java.sql.Date date = null;
			int amount = 0;
			String stock_symbol = "";
			int num_shares = 0;
			double buy_price = 0.0d;
			double sell_price = 0.0d;
			
			while (rs.next()){
				System.out.println();
				type = rs.getString("type");
				date = rs.getDate("date");
				amount = rs.getInt("amount");
				stock_symbol = rs.getString("stock_symbol");
				num_shares = rs.getInt("num_shares");
				buy_price = rs.getDouble("buy_price");
				sell_price = rs.getDouble("sell_price");
				if(type.equals("buy")) {
					System.out.println(num_shares + " shares of " + stock_symbol + " was/were bought at " + buy_price + ", on " + date);
				}
				if(type.equals("sell")) {
					System.out.println(num_shares + " shares of " + stock_symbol + " was/were sold at " + sell_price + ", on " + date);
				}
			}
			
			// Query - Show stock account balance (in terms of stock)
			query = "SELECT balance FROM stock_accounts_balance WHERE aid = ?";
			stm1 = con.prepareStatement(query);
			stm1.setInt(1, stockAID);
			rs = stm1.executeQuery();
			double balance = 0.0d;
			
			while(rs.next()){
				balance = rs.getDouble("balance");
			}

			System.out.println();
			System.out.println("Current stock account balance (in terms of stock): " + String.format("%.3f", balance));
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
	
	// 7) List current price of stock and display actor/director profile
	public void showStockAndProfile(Scanner sc) throws SQLException{
		
		// Display all stock to pick from
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		ResultSet rs = null; 
		String query = "SELECT stock_symbol,name FROM actor_directors";
		
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			rs = stm.executeQuery();
			ArrayList<String> allStocks = new ArrayList<String>();
			
			String stock_symbol = "";
			while (rs.next()){
				stock_symbol = rs.getString("stock_symbol");
				allStocks.add(stock_symbol);
			}
			
			// Display movies to choose from
			for(int i=1; i<= allStocks.size(); i++){
				System.out.println(i + ") " + allStocks.get(i-1));
			}
			
			// User selects stock
			System.out.print("Enter index of stock to display details of: ");
			int choice = sc.nextInt();			
			String chosenStock = allStocks.get(choice-1);
			
			// Display actor/director's details from selected stock
			// Get basic info
			query = "SELECT * FROM actor_directors WHERE stock_symbol = ?";
			stm1 = con.prepareStatement(query);
			stm1.setString(1,chosenStock);
			rs = stm1.executeQuery();
			
			String name = "";
			String dob = "";
			double stock_price = 0.0d;
			String movie_title = "";
			String role = "";
			int year = 0;
			int contract = 0;
			System.out.println();
			
			while (rs.next()){
				name = rs.getString("name");
				dob = rs.getString("dob");
				stock_price = rs.getDouble("stock_price");
				movie_title = rs.getString("movie_title");
				role = rs.getString("role");
				year = rs.getInt("year");
				contract = rs.getInt("contract");
				
				System.out.println("------------------ Stock: "+ chosenStock + " ------------------");
				System.out.printf("Stock price: %.3f", stock_price);
				System.out.println();
				System.out.println("Name: " + name);
				System.out.println("Date of birth: " + dob);
				System.out.println("Movie title: " + movie_title);
				System.out.println("Role: " + role);
				System.out.println("Year: " + year);
				System.out.println("Contract value: " + contract);
				System.out.println();
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
	
	// 8) Movie info router
	public void movieInfo(Scanner sc) throws SQLException{
        System.out.println("|-------------------------------------------------------|");
        System.out.println("|                   Movie Menu:                         |");
        System.out.println("|                                                       |");
        System.out.println("| 1.) Display movie information                         |");
        System.out.println("| 2.) Top movies between certain time                   |");
        System.out.println("| 3.) Display all reviews for a movie                   |");
        System.out.println("--------------------------------------------------------");
        System.out.print("Enter option: ");
        
        int choice = sc.nextInt();
        System.out.println();
        switch (choice){
        	case 1:
        		showMovies(sc);
        		break;
        	case 2:
        		System.out.print("Enter begin year: ");
        		int beginYear = sc.nextInt();
        		System.out.print("Enter end year: ");
        		int endYear = sc.nextInt();
        		moviesBtwnTime(sc,beginYear,endYear);
        		break;
        	case 3:
        		displayReviews(sc);
        		break;
        	default:
        		System.out.println("Invalid option!");
        }
	}
	
	// 8a) List all movie information
	public void showMovies(Scanner sc) throws SQLException{
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		String query = "SELECT id,title FROM Movies";
		ResultSet rs = null;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getMoviesDBConnection();
			stm = con.prepareStatement(query);
			rs = stm.executeQuery();
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
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (stm1 != null) try { stm1.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}		
	}
	
	// 8b) Display top movies within time interval
	public void moviesBtwnTime(Scanner sc, int beginYear, int endYear) throws SQLException {
		// Send request query to database getting current market account balance of user
		String query = "SELECT title FROM Movies WHERE rating = 5 && production_year >= ? && production_year <= ?";
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		System.out.println();
		try {
			MySQLDB db = new MySQLDB();
			con = db.getMoviesDBConnection();
			stm = con.prepareStatement(query);
			stm.setInt(1,beginYear);
			stm.setInt(2, endYear);
			rs = stm.executeQuery();
			String title = "";
			while (rs.next()){
				title = rs.getString("title");
				System.out.println("Movie title: " + title);
			}
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}		
	}
	
	// 8c) Display all reviews for a given movie
	public void displayReviews(Scanner sc) throws SQLException{
		Connection con = null;
		PreparedStatement stm = null;
		PreparedStatement stm1 = null;
		ResultSet rs = null;
		// Give menu of movies to choose from
		String query = "SELECT id,title FROM Movies";
		try {
			MySQLDB db = new MySQLDB();
			con = db.getMoviesDBConnection();
			stm = con.prepareStatement(query);
			rs = stm.executeQuery();
			Map<Integer, String> moviesMap = new HashMap<Integer,String>();
			
			String title = "";
			int movieID = -1;
			
			while (rs.next()){
				movieID = rs.getInt("id");
				title = rs.getString("title");
				moviesMap.put(movieID, title);
			}
			
			for(int i=1; i<= moviesMap.size(); i++){
				System.out.println(i + ") " + moviesMap.get(i));
			}
			
			// Pick movie to display
			System.out.println();
			System.out.print("Enter index of movie to display details : ");
			int choice = sc.nextInt();
			
			// Get reviews of that movie
			query = "SELECT author,review FROM Reviews WHERE movie_id = ?";
			stm1 = con.prepareStatement(query);
			stm1.setInt(1,choice);
			rs = stm1.executeQuery();
			String author = "";
			String review = "";
			
			System.out.println();
			System.out.println("For movie title: " + moviesMap.get(choice));
			while (rs.next()){
				author = rs.getString("author");
				review = rs.getString("review");
				System.out.println(author + " gave the review: " + review);
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
	
	public int taxIDOfUsername(String username) throws SQLException {
		// Send request query to database getting aid of user
		String query = "SELECT taxID FROM customers WHERE username = ?";
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		int taxID = -1;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setString(1,username);
			rs = stm.executeQuery();
			while (rs.next()){
				taxID = rs.getInt("taxID");
			}
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return taxID;
	}
	
	public double getStockAccountBalance() throws SQLException{
		
		System.out.println();
		// Send request query to database getting current stock account balance of user
		int stockAID = getAIDofAccount("stock");
		String query = "SELECT balance FROM stock_accounts_balance WHERE aid = ?";
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		
		double userBalance = 0;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setInt(1,stockAID);
			rs = stm.executeQuery();
			while (rs.next()){
				userBalance = rs.getDouble("balance");
			}
		} catch (SQLException e){
			
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return userBalance;
		
	}
	
	public int getAIDofAccount(String accountType) throws SQLException{
		// Send request query to database getting aid of user
		String query = "SELECT aid FROM accounts WHERE username = ? && account_type = ?";
		Connection con = null;
		PreparedStatement stm = null;
		ResultSet rs = null;
		int aid = -1;
		try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setString(1,this.username);
			stm.setString(2, accountType);
			rs = stm.executeQuery();
			while (rs.next()){
				aid = rs.getInt("aid");
			}
		} catch (SQLException e){
			System.out.println(e.getMessage());
			
		} finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
		}
		return aid;	
	}
}
