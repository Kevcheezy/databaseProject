package starsrus;

import java.sql.*;
import java.util.*;
public class Manager {
	String username, password;
	
	// Default constructor
	public Manager(){
		
	}
	
	// 3.) Add interest
	
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
					System.out.println(" 	- " + type + " of " + amount + "was made.");

				}
				
				// Stock account
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
						System.out.println("buy detected");
					}
					if(type.equals("sell")){
						System.out.println("sell detected");
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
	// 6.) Generate government DTER  
	// 7.) Customer report 
	// 8.) Delete transactions 
	// 9.) Open market for the day
	// 10.)Close market for the day 
}
