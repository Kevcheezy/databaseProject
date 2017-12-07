package starsrus;

import java.sql.*;
import java.util.Scanner;

public class starsrus {
	
	// Top-level execution
	public static void runProgram() throws SQLException{
		Boolean keepRunning = true;
		Scanner sc = new Scanner(System.in);
		int userInput;
		
		while(keepRunning){
			// Welcome! Pick trader or manager interface
			System.out.println("Welcome to StarsRUS!");
			System.out.print("Please enter 1 for trader interface, 2 for manager interface, or 3 to exit: ");
			userInput = sc.nextInt();
			// Trader Interface
			if (userInput == 1){
				traderInterface(sc);
			}
			// Manager Interface
			if (userInput == 2){
				managerInterface(sc);
			}
			if (userInput == 3){
				keepRunning = false;
			}
		}
		sc.close();
	}
	
	// Trader Interface
	public static void traderInterface(Scanner sc) throws SQLException{
		Boolean keepRunning = true;
		Customer user = new Customer();
		
		while(keepRunning){
			// Login or register
			System.out.println("Welcome to the trader interface!");
			System.out.print("Enter 1 for register, 2 for user login, or 3 to quit: ");
			int userInput = sc.nextInt();
			
			// Register
			if (userInput == 1){
				register(sc, "trader");
			}
				// Login
			if (userInput == 2){
				// Successful login
				if(login(sc, user, "trader") != ""){
					keepRunning = false;
					traderMenu(sc,user);
				}
				// Unsuccessful login, loop back
			}
			if (userInput == 3){
				keepRunning = false;
			}
		}
		
	}
	
	// Trader Menu
	public static void traderMenu(Scanner sc, Customer user) throws SQLException{
		Boolean keepRunning = true;
		while (keepRunning){
			
	        System.out.println("|-------------------------------------------------------|");
	        System.out.println("|                   Trader Menu:                        |");
	        System.out.println("|                                                       |");
	        System.out.println("| 1.) Deposit into market account                       |");
	        System.out.println("| 2.) Withdraw from market account                      |");
	        System.out.println("| 3.) Buy stock                                         |");
	        System.out.println("| 4.) Sell stock                                        |");
	        System.out.println("| 5.) Show market account balance                       |");
	        System.out.println("| 6.) Show stock account transaction history            |");
	        System.out.println("| 7.) List current price of a stock and actor profile   |");
	        System.out.println("| 8.) List movie information                            |");
	        System.out.println("| 9.) Exit                                              |");
	        System.out.println("--------------------------------------------------------");
   			System.out.println("Welcome " + user.username + "!");
	        System.out.print("Select action: ");
	        int choice = sc.nextInt();
	        switch (choice){
	        	case 1:
        			System.out.print("Enter amount to deposit: ");
        			Integer depositAmt = sc.nextInt();
        			user.deposit(depositAmt);
	        		break;
	        	case 2:
            		System.out.print("Enter amount to withdraw: ");
            		Integer withdrawAmt = sc.nextInt();
            		user.withdraw(withdrawAmt);
	        		break;
	        	case 3:
	            	user.buyStock(sc);
	        		break;
	        	case 4:
	            	
	        		break;
	        	case 5:
	            	float balance = user.getMarketAccountBalance();
	    			System.out.println("Current market account balance: " + balance);
	        		break;
	        	case 6:
	        		user.showStockTransactionHistory();
	        		break;
	        	case 7:
	        		user.showStockAndProfile(sc);
	        		break;
	        	case 8:
	        		user.movieInfo(sc);
	        		break;
	        	case 9:
	            	keepRunning = false;
	        		break;    		
	        	default:
	        		System.out.println("Incorrect choice, choose again");
	        }
		}
	}
	
	// Manager Interface
	public static void managerInterface(Scanner sc) throws SQLException{
        System.out.println("|-------------------------------------------------------|");
        System.out.println("|                   Manager Menu:                       |");
        System.out.println("|                                                       |");
        System.out.println("| 1.) Set a new date to be today's date                 |");
        System.out.println("| 2.) Withdraw from market account                      |");
        System.out.println("| 3.) Buy stock                                         |");
        System.out.println("| 4.) Sell stock                                        |");
        System.out.println("| 5.) Show market account balance                       |");
        System.out.println("| 6.) Show stock account transaction history            |");
        System.out.println("| 7.) List current price of a stock and actor profile   |");
        System.out.println("| 8.) List movie information                            |");
        System.out.println("| 9.) Exit                                              |");
        System.out.println("--------------------------------------------------------");
        
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        
        switch(choice){
        	case 1:
    			MySQLDB db = new MySQLDB();
    			db.setCurrentDate(sc);
        		break;
        
        }
	}
	
	// Login function for trader/manager
	public static String login(Scanner sc, Customer customer, String person) throws SQLException {
		String retVal = "";
		
		// Get info from user
		System.out.print("Enter username:  ");
		sc.nextLine();
		String loginUsername = sc.nextLine();
		System.out.print("Enter password:  ");
		String loginPassword = sc.nextLine();
        
        // Update customer
        customer.username = loginUsername;
        customer.password = loginPassword;
        
        String query = "SELECT * from customers WHERE username = ? && password = ?";
		Connection con = null;
        PreparedStatement stm = null;
        ResultSet rs = null;
        
        try {
			MySQLDB db = new MySQLDB();
			con = db.getDBConnection();
			stm = con.prepareStatement(query);
			stm.setString(1,loginUsername);
			stm.setString(2, loginPassword);
            
			rs = stm.executeQuery();
            String resultUsername = "";
            String resultPassword = "";
            while (rs.next()) {
                resultUsername = rs.getString("username");
                resultPassword = rs.getString("password");

            }
            
            // Compare user name and password
            if (resultUsername.equals(customer.username) && resultPassword.equals(customer.password)){
            	System.out.println("Successful login!");
            	retVal = resultUsername;
            }
            
        } catch (SQLException e) { e.printStackTrace();} 
        finally {
            if (stm != null) try { stm.close(); } catch (SQLException e) {}
            if (con != null) try { con.close(); } catch (SQLException e) {}
            if (rs != null) try { rs.close(); } catch (SQLException e) {}
        }
        return retVal;
	}
	
	// Register function for trader/manager
	public static void register(Scanner sc, String person) throws SQLException{
			
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
			System.out.print("Enter ssn: ");
			String newSSN = sc.nextLine();
			System.out.print("Enter address: ");
			String newAddress = sc.nextLine();
			
			Integer numNewTaxID = Integer.valueOf(newTaxID);

			// Construct query and send to database
			String QUERY = "INSERT INTO customers " + 
							"(username, password, name, taxid, phone_number, email, state, ssn, address) VALUES "
							+ "(?,?,?,?,?,?,?,?,?)";
			
			Connection con = null;
			PreparedStatement stm = null;
			PreparedStatement stm1 = null;
			PreparedStatement stm2 = null;
			PreparedStatement stm3 = null;
			PreparedStatement stm4 = null;
			
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
				stm.setString(8, newSSN);
				stm.setString(9, newAddress);
				stm.executeUpdate();
				
				
				// Create new account
				String accountQuery = "INSERT INTO accounts (account_type, username, taxID) VALUES (?,?,?)";
				stm1 = con.prepareStatement(accountQuery);
				stm1.setString(1, "market");
				stm1.setString(2,newUsername);
				stm1.setInt(3, numNewTaxID);
				stm1.executeUpdate();
				
				
				// Create new market account by updating from accounts table
				String marketAccountQuery = "INSERT INTO market_accounts (aid,taxID) SELECT ac.aid, ac.taxID "
						+ "FROM accounts ac WHERE NOT EXISTS (SELECT aid FROM market_accounts a2 WHERE a2.taxID = ac.taxID) "
						+ "AND ac.account_type = 'market'";
				stm2 = con.prepareStatement(marketAccountQuery);
				stm2.executeUpdate();
				
				// Get aid of newly generated account
				String query = "SELECT aid FROM accounts WHERE username = ?";
				stm4 = con.prepareStatement(query);
				stm4.setString(1, newUsername);
				ResultSet rs = stm4.executeQuery();
				int aid = -1;
				while(rs.next()){
					aid = rs.getInt("aid");
				}
				
				
				// Insert a new record into transaction table for this deposit
				query = "INSERT INTO transactions (aid, type, date, amount) VALUES (?,?,?,?)";
				java.sql.Date sqlDate = db.getCurrentTime();
				stm3 = con.prepareStatement(query);
				stm3.setInt(1, aid);
				stm3.setString(2, "deposit");
				stm3.setDate(3, sqlDate);
				stm3.setInt(4, 1000);
				stm3.executeUpdate();
				
				
			} catch (SQLException e){
				
				System.out.println(e.getMessage());
				
			} finally {
				if (stm != null){
					stm.close();
				}
				if (stm1 != null){
					stm1.close();
				}
				if (stm2 != null){
					stm2.close();
				}
				if (stm3 != null){
					stm3.close();
				}
				if (stm4 != null){
					stm4.close();
				}
				if (con != null){
					con.close();
				}
			}
		}
	
	
	// Main function
	public static void main(String[] args) throws SQLException{
		runProgram();
	}

}
