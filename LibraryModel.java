/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.*;

public class LibraryModel {
	Connection con;
    // For use in creating dialogs and making them modal
    private JFrame dialogParent;

    public LibraryModel(JFrame parent, String userid, String password)  {
	dialogParent = parent;
	try {






		Class.forName("org.postgresql.Driver");

	String url = "jdbc:postgresql://db.ecs.vuw.ac.nz/"+userid + "_jdbc";

		con = DriverManager.getConnection(url, userid, password);







	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}


    }

    public String bookLookup(int isbn) {
    	String title = "Isbn not found";
    	try {
			con.setAutoCommit(false);
			String lookup = "SELECT * FROM Book WHERE isbn = "+ isbn+";";

	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	while(rs.next()){
	    		title = rs.getString("Title");
	    	}
	    	con.setAutoCommit(true);
	    	stmt.close();
	    	con.close();


		} catch (SQLException e) {
			e.printStackTrace();
		}


	return title;
    }

    public String showCatalogue() {
	return "Show Catalogue Stub";
    }

    public String showLoanedBooks() {
	return "Show Loaned Books Stub";
    }

    public String showAuthor(int authorID) {
	return "Show Author Stub";
    }

    public String showAllAuthors() {
	return "Show All Authors Stub";
    }

    public String showCustomer(int customerID) {
	return "Show Customer Stub";
    }

    public String showAllCustomers() {
	return "Show All Customers Stub";
    }

    public String borrowBook(int isbn, int customerID, int day, int month, int year) {
	return "Borrow Book Stub";
    }

    public String returnBook(int isbn, int customerid) {
	return "Return Book Stub";
    }

    public void closeDBConnection() {
    }

    public String deleteCus(int customerID) {
    	return "Delete Customer";
    }

    public String deleteAuthor(int authorID) {
    	return "Delete Author";
    }

    public String deleteBook(int isbn) {
    	return "Delete Book";
    }
}