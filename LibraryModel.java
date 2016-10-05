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

    /**
     * Shows the book authors sorted according to AuthSeqNo
     * @param isbn
     * @return "Book Lookup:
     * 				ISBN: title,
     * 				Edition: 1, Number of copies: 10, copies left: 10
     * 				Authors: Sunames
     */
    public String bookLookup(int isbn) {
    	String title = "Isbn not found";
    	String edition = "";
    	String no_copies = "";
    	String copies_left = "";
    	String Author = "(No Authors)";
    	//ArrayList<String> Asequence = new ArrayList();
    	try {
			con.setAutoCommit(false);
			String lookup = "SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR "
							+ "WHERE isbn = "+ isbn
							+"ORDER BY AuthorSeqNo ASC;";

	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	while(rs.next()){
	    		title = rs.getString("Title");
	    		edition ="Edition: "+ rs.getInt("edition_no");
	    		no_copies ="Number of copies: "+ rs.getString("numofcop");
	    		copies_left = "copies left: "+rs.getString("numleft");
	    		Author += rs.getString("Name")+rs.getString("surname")+',';

	    	}

	    	con.setAutoCommit(true);
	    	stmt.close();
	    	//con.close();


		} catch (SQLException e) {
			e.printStackTrace();
		}

    	String result =isbn +": "+title +"\n " +edition + no_copies+ copies_left+"\n " + Author ;
	return result.replaceAll("\\s+", " ");
    }

    /***
     * This returns the book lookup for all books in the catalogue.
     * @return
     */
    public String showCatalogue() {
    	//get all the isbns
    	//for each one call lookupbook
    	//add all the strings together?


	return "Show Catalogue Stub";
    }

    public String showLoanedBooks() {
	    //get all the books where number of copies doesnt equal number left
	    //then call book lookup on it.
	    String result="No books are currently on loan";
	    try {
		con.setAutoCommit(false);
		String lookup = "SELECT * FROM Book";
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);
		int isbn;
		int no_copies;
		int copies_left;
	    	while(rs.next()){
			no_copies ="Number of copies: "+ rs.getString("numofcop");
	    		copies_left = "copies left: "+rs.getString("numleft");
			if(no_copies > copies_left){
				result += "\n \n "+ bookLookup(rs.getInt("isbn"));
			}
		}
			
		con.setAutoCommit(true);
	    	stmt.close();
	    	//con.close();


		} catch (SQLException e) {
			e.printStackTrace();
		}
	    
	return result;
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
