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
				user.register(sc);
			}
				// Login
			if (userInput == 2){
				// Successful login
				if(user.login(sc) == true){
					keepRunning = false;
					traderMenu(sc);
				}
				// Unsuccessful login, loop back
			}
			if (userInput == 3){
				keepRunning = false;
			}
		}
		
	}
	
	// Trader Menu
	public static void traderMenu(Scanner sc) throws SQLException{
		System.out.println("\n\n-------------------------------------");
        System.out.println("|    Welcome to the Trader Menu!    |");
        System.out.println("|-----------------------------------|");
        System.out.println("| Main Menu:                        |");
        System.out.println("| ----------                        |");
        System.out.println("|                                   |");
        System.out.println("| 1.) Choose a filename!            |");
        System.out.println("| 2.) Make a Database!              |");
        System.out.println("| 3.) Display Entire Database!      |");
        System.out.println("| 4.) Search The Database!          |");
        System.out.println("| 5.) About This Program!           |");
        System.out.println("| 6.) Exit!                  		|");
        System.out.println("-------------------------------------");
	}
	
	// Manager Interface
	public static void managerInterface() throws SQLException{
		System.out.println("Welcome to the manager interface!");
	}
	
	
	
	// Main function
	public static void main(String[] args) throws SQLException{
		runProgram();
	}

}
