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
import java.util.ArrayList;

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
    	String Author = "";
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
	    		edition = rs.getString("edition_no");
	    		no_copies =rs.getString("numofcop");
	    		copies_left = "copies left: "+rs.getString("numleft");
	    		Author += rs.getString("Name")+rs.getString("surname")+',';

	    	}

	    	if(Author.equals("")){Author = "(No Authors)";}
	    	if(edition.equals("")){edition = "unspecified";}
	    	if(no_copies.equals("")){edition = "unspecified";}
	    	con.setAutoCommit(true);
	    	stmt.close();
	    	//con.close();


		} catch (SQLException e) {
			e.printStackTrace();
		}

    	String result =isbn +": "+title +"\n Edition: "+ edition + "Number of copies: "+no_copies+ copies_left+"\n Authors: " + Author ;
	return isbn +": "+title +"\n Edition: "+ edition + "Number of copies: "+no_copies+" Copies Left: "+ copies_left+"\n Authors: " + Author.replaceAll("\\s+", " ") ;//result.replaceAll("\\s+", " ");
    }

    /***
     * This returns the book lookup for all books in the catalogue.
     * @return
     */
    public String showCatalogue() {
    	String res = "";
    	//get all the isbns
    	try {
			con.setAutoCommit(false);
			String lookup = "SELECT isbn FROM book;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	int isbn;

	    	while(rs.next()){
	    		isbn = rs.getInt("isbn");

	    		res += "\n \n "+bookLookup(isbn);
	    	}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	return res;
    }

    public String showLoanedBooks() {
    	//get all the books where number of copies doesnt equal number left
	    //then call book lookup on it.

    	String result = "Loaned Books: \n";
    	try {
			con.setAutoCommit(false);
	    	String query = "SELECT * FROM Book WHERE numofcop > numLeft ORDER BY isbn ASC;";
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(query);

	    	while(rs.next()){
	    		result = result + bookLookup(rs.getInt("isbn")) + " \n";
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
    	String result = "Show Author: \n" ;
    	try {
			con.setAutoCommit(false);
			String query = "SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR "
					+ "WHERE AuthorId = "+ authorID
					+"ORDER BY AuthorSeqNo ASC;";
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(query);

	    	while(rs.next()){
	    		result +="   "+ authorID +"-"+ rs.getString("name").replaceAll("\\s+", " ") + rs.getString("surname").replaceAll("\\s+", " ")
	    				+"\n   Book Written:\n"+ "        "+rs.getInt("isbn")+"-"+ rs.getString("title");

	    	}

	    	con.setAutoCommit(true);
	    	stmt.close();
	    	//con.close();


		} catch (SQLException e) {
			e.printStackTrace();
		}

    	return result+" \n";
    }

    public String showAllAuthors() {
    	String res = "Show all authors: \n";
    	//get all the isbns
    	try {
			con.setAutoCommit(false);
			String lookup = "SELECT *FROM author;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	int AuthorId;

	    	while(rs.next()){

	    		res += "   "+rs.getInt("AuthorId")+":"+ rs.getString("name").replaceAll("\\s+", " ")+","+rs.getString("surname")+" \n ";
	    	}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	return res;
    }

    public String showCustomer(int customerID) {
	return "Show Customer Stub";
    }

    public String showAllCustomers() {
    	String res = "";
    	//get all the isbns
    	try {
			con.setAutoCommit(false);
			String lookup = "SELECT AuthorId FROM author;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	int AuthorId;

	    	while(rs.next()){
	    		AuthorId= rs.getInt("AuthorId");

	    		res += " \n "+showAuthor(AuthorId);
	    	}


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	return res;
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