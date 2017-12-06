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
				managerInterface();
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
	            	
	        		break;
	        	case 4:
	            	
	        		break;
	        	case 5:
	            	user.showMarketAccountBalance();
	        		break;
	        	case 6:
	            	
	        		break;
	        	case 7:
	            	
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
	public static void managerInterface() throws SQLException{
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
	}
	
	// Login function for trader/manager
	public static String login(Scanner sc, Customer customer, String person) throws SQLException {
		String retVal = "";
		String HOST = "jdbc:mysql://cs174a.engr.ucsb.edu:3306/kevinchanDB";
		String USER = "kevinchan";
		String PWD = "651";
		
		// Get info from user
		System.out.print("Enter username:  ");
		sc.nextLine();
		String loginUsername = sc.nextLine();
		System.out.print("Enter password:  ");
		String loginPassword = sc.nextLine();
        
        // Update customer
        customer.username = loginUsername;
        customer.password = loginPassword;
        
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
            // Compare user name and password
            if (resultUsername.equals(customer.username) && resultPassword.equals(customer.password)){
            	System.out.println("Successful login!");
            	retVal = resultUsername;
            	return retVal;
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
			
			Integer numNewTaxID = Integer.valueOf(newTaxID);

			// Construct query and send to db
			String QUERY = "INSERT INTO customers " + 
							"(username, password, name, taxid, phone_number, email, state) VALUES "
							+ "(?,?,?,?,?,?,?)";
			
			Connection con = null;
			PreparedStatement stm = null;
			PreparedStatement stm1 = null;
			PreparedStatement stm2 = null;
			
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
				
				// Create new account
				String accountQuery = "INSERT INTO accounts (account_type, username) VALUES (?,?)";
				stm1 = con.prepareStatement(accountQuery);
				stm1.setString(1, "market");
				stm1.setString(2,newUsername);
				stm1.executeUpdate();
				
				// Create new market account by updating from accounts table
				String marketAccountQuery = "INSERT INTO market_accounts (aid,username) SELECT ac.aid, ac.username "
						+ "FROM accounts ac WHERE NOT EXISTS (SELECT aid FROM market_accounts a2 WHERE a2.aid = ac.aid) "
						+ "AND ac.account_type = 'market'";
				stm2 = con.prepareStatement(marketAccountQuery);
				stm2.executeUpdate();
				
				// Add new deposit transaction for user
				
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
