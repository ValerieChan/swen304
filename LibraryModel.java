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
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

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
    		//con.setReadOnly(true);
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
	    		Author += rs.getString("surname")+',';

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
	return isbn +": "+title +"\n    Edition: "+ edition + " - Number of copies: "+no_copies+" - Copies Left: "+ copies_left+"\n    Authors: " + Author.replaceAll("\\s+","") ;//result.replaceAll("\\s+","");
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
			String lookup = "SELECT isbn FROM book ORDER BY isbn ASC;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	int isbn;

	    	while(rs.next()){
	    		isbn = rs.getInt("isbn");

	    		res += "\n \n "+bookLookup(isbn);
	    	}


		} catch (SQLException e) {
			return "error retrieving books";
		}



	return res;
    }

    private String showNumBorrowers(int isbn){
    	int result = 0;
    	try {
    		//con.setReadOnly(true);
	    	String query = "SELECT count(*) as count FROM cust_book WHERE isbn = "+isbn+";";
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(query);

	    	while(rs.next()){
	    		result = rs.getInt("count");
	    	}

    	con.setAutoCommit(true);
    	stmt.close();
    	//con.close();


	} catch (SQLException e) {
		e.printStackTrace();
	}
	return "("+result+ "current borrowers)";
    }

    public String showLoanedBooks() {
    	//get all the books where number of copies doesnt equal number left
	    //then call book lookup on it.

    	String result = "Loaned Books: \n";
    	try {
    		con.setReadOnly(true);
	    	String query = "SELECT * FROM Book WHERE numofcop > numLeft ORDER BY isbn ASC;";
	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(query);
	    	boolean some = false;
	    	while(rs.next()){
	    		int isbn = rs.getInt("isbn");
	    		result +=  bookLookup(isbn) +"\n    "+showNumBorrowers(isbn)+" \n";
	    		some = true;
	    	}
	    	con.setAutoCommit(true);
	    	stmt.close();
	    	if(some == false){return result + "\n   No books on loan";}

		} catch (SQLException e) {
			return "Error retrieving books.";
		}
    	return result;
    }

    public String showAuthor(int authorID) {
    	String result = "" ;
    	String books="";
    	String bookTitle="";
    	try {
    		//con.setReadOnly(true);
			String query = "SELECT * FROM Book NATURAL JOIN Book_Author NATURAL JOIN AUTHOR "
					+ "WHERE AuthorId = "+ authorID
					+"ORDER BY AuthorSeqNo ASC;";

	    	Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(query);
	    	int count=0;
	    	while(rs.next()){
	    		result ="   "+ authorID +"-"+ rs.getString("name").replaceAll("\\s+","") + rs.getString("surname").replaceAll("\\s+","")
	    				+"\n   ";
	    		books +="\n        "+rs.getInt("isbn")+"-"+ rs.getString("title");
	    		count++;

	    	}
	    	if(count == 0){
	    		bookTitle="No books written: ";
	    	} else if(count == 1){
	    		bookTitle = "Book written: ";
	    	} else{
	    		bookTitle= "Books written: ";
	    	}
	    	con.setAutoCommit(true);
	    	stmt.close();
	    	//con.close();


		} catch (SQLException e) {
			return "No such authorid: "+authorID;
		}

    	return "Show Author: \n"+ result+bookTitle+ books+" \n";
    }



    public String showAllAuthors() {
    	String res = "Show all authors: \n";
    	//get all the isbns
    	try {
    		//con.setReadOnly(true);
			String lookup = "SELECT * FROM author;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	int AuthorId;

	    	while(rs.next()){

	    		res += "   "+rs.getInt("AuthorId")+":"+ rs.getString("name").replaceAll("\\s+","")+","+rs.getString("surname")+" \n ";
	    	}


		} catch (SQLException e) {
			return"An error selecting all authors has occured.";
		}



	return res;
    }


    public String showCustomer(int customerID) {

    	String result = "Show Customer: \n" ;
    	String books="";
    	String bookBorrowed="";
    	try{
    		con.setReadOnly(true);
    		//con.setAutoCommit(false);
    		Statement stmt = con.createStatement();
    		String query = "SELECT * FROM  Customer WHERE customerId = "+ customerID
    				+";";

    		ResultSet rs = stmt.executeQuery(query);
    		int count =0;
    		while(rs.next()){
    			count ++;
    		}
    		if(count ==0){
    			con.commit();
				//con.setAutoCommit(true);
    			return "No such CustomerId: "+customerID;
    		}
    		try {
    			//con.setReadOnly(true);
    			query = "SELECT * FROM Customer "
    					+ "WHERE customerId = "+ customerID
    					+";";
    			rs = stmt.executeQuery(query);
    			while(rs.next()){
    				result +="   "+ customerID +"-"+ rs.getString("l_name").replaceAll("\\s+","") + rs.getString("f_name").replaceAll("\\s+","")+"- "+rs.getString("city");
    			}

    			query = "SELECT * FROM Cust_book NATURAL JOIN book "
    					+ "WHERE customerId = "+ customerID
    					+";";
    			rs = stmt.executeQuery(query);
    			count =0;
    			while(rs.next()){
        			books +="\n        "+rs.getInt("isbn")+"-"+ rs.getString("title");
    				count++;    			}

    			if(count == 0){
    				bookBorrowed="\n     (No books borrowed) ";
    			} else if(count == 1){
    				bookBorrowed = "\n Book borrowed: ";
    			} else{
    				bookBorrowed= "\n Books borrowed: ";
    			}

    			//con.setAutoCommit(true);
    			stmt.close();
    			//con.close();

    		} catch (SQLException e) {
    			return "Error finding books our for that customer: "+customerID;
    		}
    	} catch (SQLException e) {
    		return "No such CustomerId: "+customerID;
    	}

    	return result+bookBorrowed + books+" \n";
    }

    public String showAllCustomers() {
    	String res = "Show all customers: \n";
    	//get all the isbns
    	try {
    		con.setReadOnly(true);
			String lookup = "SELECT *FROM customer;";
			Statement stmt = con.createStatement();
	    	ResultSet rs = stmt.executeQuery(lookup);

	    	int customerId;

	    	while(rs.next()){

	    		res += "   "+rs.getInt("customerid")+":"+ rs.getString("l_name").replaceAll("\\s+","")+","+rs.getString("f_name").replaceAll("\\s+","")+
	    				"-"+rs.getString("city")+" \n ";
	    	}


		} catch (SQLException e) {
			return "An error retireving all customers has occured.";
		}



    	return res;
    }

    public String borrowBook(int isbn, int customerID, int day, int month, int year) {
    	String res = "Borrow Book: \n";
    	Boolean success = false;
    	//check the date here...
    	Calendar c =Calendar.getInstance(TimeZone.getTimeZone("UTC+12:00"));
    	c.clear();
    	c.set(year, month, day);
    	Date dateDue = new Date(c.getTimeInMillis());
    	Date dateNow = new Date(System.currentTimeMillis());
    	if(dateNow.after(dateDue)){
    		return "due date is before current date";
    	}

    	try {
    		con.setReadOnly(false);
    		con.setAutoCommit(false);
    		con.setTransactionIsolation(con.TRANSACTION_SERIALIZABLE);
    		//check if customer exists
    		Statement stmt = con.createStatement();
    		String customerValid = "SELECT * FROM Customer WHERE customerid ="+customerID+" FOR UPDATE;";// lock the customer
    		ResultSet customer = stmt.executeQuery(customerValid);
    		String name="";
    		while(customer.next()){
    			name = customer.getString("l_name").replaceAll("\\s+","")+","+customer.getString("f_name").replaceAll("\\s+","");
    		}
    		if(name == ""){
    			con.commit();
				con.setAutoCommit(false);
    			return "invalid customerid: "+ customerID;
    		}
    		try {
    			//check that a book with that isbn exists
    			String bookValid = "SELECT * FROM book WHERE isbn ="+isbn+";";// split this up into exists and is enough
    			ResultSet book = stmt.executeQuery(bookValid);
    			String title="";
				while(book.next()){title = book.getString("title").replaceAll("\\s+","");}
				if( title.equals("")){
					con.commit();
					con.setAutoCommit(false);
					return "Invalid Isbn: "+ isbn;
					}
    			try {
    				//check they arent already borrowing the book
    				bookValid = "SELECT * FROM cust_book WHERE isbn ="+isbn+" AND customerid = "+customerID+";";
    				book = stmt.executeQuery(bookValid);
    				while(book.next()){
    					con.commit();
						con.setAutoCommit(false);
    					return "This customer already has the book out.";
    				}
    				try {
    					con.setAutoCommit(false);
    					//check that there is a copy left to borrow
    					bookValid = "SELECT * FROM book WHERE isbn ="+isbn+" AND numLeft >0 FOR UPDATE;";// lock the book
    					book = stmt.executeQuery(bookValid);

    					title="";
    					while(book.next()){title = book.getString("title").replaceAll("\\s+","");}
    					if( title.equals("")){
    						con.commit();
    						con.setAutoCommit(false);
    						return "Not enough copies left";
    						}


    					JOptionPane.showMessageDialog(dialogParent, "Locked the tuples(s), ready to update. Click Ok to continue");

    					//update the book
    					try {
    						//con.setAutoCommit(true);
    						String borrowBookQuery = "UPDATE book SET numleft = (SELECT numleft FROM book WHERE isbn ="+isbn+")-1 WHERE isbn="+isbn+";";
    						stmt.executeUpdate(borrowBookQuery);

    						//Add a tuple to customer_books
    						String customerBookQuery = "INSERT INTO Cust_book VALUES("+isbn+","+"'"+year+"-"+month+"-"+day+"'"+","+customerID+");";
    						stmt.executeUpdate(customerBookQuery);
    						res +="    Book: "+isbn+"("+title+")\n    Loaned to: "+ customerID +"("+title+")\n    Due Date: "+day+" "+month+" "+year;
    						con.commit();
    						con.setAutoCommit(false);
    						success = true;

    					} catch (SQLException e) {
    						res = "Unable to borrow book.";
    					}
    				} catch (SQLException e) {
    					res= "Not enough copies available";
    				}
    			} catch (SQLException e) {
    				res=  "This book is already on loan to this customer";
    			}
    		} catch (SQLException e) {
    			res=  "Error retrieving book with isbn :"+isbn;
    		}
    	} catch (SQLException e) {
    		res=  "Error retrieving customer: "+ customerID;
    	}

    	if(success == false){
    		try {
    			System.out.println("rollback");
    			con.rollback();
    		} catch (SQLException e) {
    			res= "roll back failed";
    		}
    	}


    	return res;
    }

    public String returnBook(int isbn, int customerID) {
    	String res = "Return Book: \n";
    	Boolean success = false;

    	try {
    		con.setReadOnly(false);
    		con.setAutoCommit(false);
    		con.setTransactionIsolation(con.TRANSACTION_SERIALIZABLE);
    		//check if customer exists
    		Statement stmt = con.createStatement();
    		String customerValid = "SELECT * FROM Customer WHERE customerid ="+customerID+" FOR UPDATE;";// lock the customer
    		ResultSet customer = stmt.executeQuery(customerValid);
    		String name="";
    		while(customer.next()){
    			name = customer.getString("l_name").replaceAll("\\s+","")+","+customer.getString("f_name").replaceAll("\\s+","");
    		}
    		if(name == ""){
    			con.commit();
				con.setAutoCommit(false);
    			return "invalid customerid: "+ customerID;
    		}
    		try {
    			//check that a book with that isbn exists
    			String bookValid = "SELECT * FROM book WHERE isbn ="+isbn+";";// split this up into exists and is enough
    			ResultSet book = stmt.executeQuery(bookValid);
    			String title="";
				while(book.next()){title = book.getString("title").replaceAll("\\s+","");}
				if( title.equals("")){
					con.commit();
					con.setAutoCommit(false);
					return "Invalid Isbn: "+ isbn;
					}
    			try {
    				//check they are already borrowing the book
    				bookValid = "SELECT * FROM cust_book WHERE isbn ="+isbn+" AND customerid = "+customerID+";";
    				book = stmt.executeQuery(bookValid);
    				boolean out = false;
    				while(book.next()){
    					out = true;
    				}
    				if(out == false){
    					con.commit();
    					con.setAutoCommit(false);
    					return "This customer does not have the book out.";
    				}
    					JOptionPane.showMessageDialog(dialogParent, "Locked the tuples(s), ready to update. Click Ok to continue");

    					//update the book
    					try {
    						String borrowBookQuery = "UPDATE book SET numleft = (SELECT numleft FROM book WHERE isbn ="+isbn+")+1 WHERE isbn="+isbn+";";
    						stmt.executeUpdate(borrowBookQuery);

    						//Add a tuple to customer_books
    						String customerBookQuery = "DELETE FROM Cust_book WHERE isbn="+isbn+"AND customerid = "+customerID+";";
    	    				stmt.executeUpdate(customerBookQuery);
    	    				res +="    Book: "+isbn+"("+title+")\n    Returned by: "+ customerID +"("+title+")\n ";
    						con.commit();
    						con.setAutoCommit(false);
    						success = true;

    					} catch (SQLException e) {
    						res = "Unable to return book.";
    					}
    			} catch (SQLException e) {
    				res=  "This book was not on loan to this customer";
    			}
    		} catch (SQLException e) {
    			res=  "Error retrieving book with isbn :"+isbn;
    		}
    	} catch (SQLException e) {
    		res=  "Error retrieving customer: "+ customerID;
    	}

    	if(success == false){
    		try {
    			con.rollback();
    		} catch (SQLException e) {
    			res= "roll back failed";
    		}
    	}


    	return res;
    }

    public void closeDBConnection() {
    	try {
			con.close();
    	} catch (SQLException e) {
    		System.out.println("Could not close the connection");
    	}
    }

    public String deleteCus(int customerID) {//deletes but you cant do anythign after... not sure why
    	boolean success=false;
    	String res = "";
    	try{
     		con.setReadOnly(false);
    		con.setAutoCommit(false);
    		con.setTransactionIsolation(con.TRANSACTION_SERIALIZABLE);

    		//check they are real
    		Statement stmt = con.createStatement();
    		String customerValid = "SELECT * FROM Customer WHERE customerid ="+customerID+" FOR UPDATE;";
    		ResultSet customer = stmt.executeQuery(customerValid);
    		String name="";
    		while(customer.next()){
    			name = customer.getString("l_name").replaceAll("\\s+","")+","+customer.getString("f_name").replaceAll("\\s+","");
    		}
    		if(name == ""){
    			con.commit();
				con.setAutoCommit(false);
    			return "invalid customerid: "+ customerID;
    		}

    		try{
    			//dont let them if they have books out
    			String customerOwes = "SELECT * FROM cust_book WHERE customerid ="+customerID+";";
        		ResultSet onloan = stmt.executeQuery(customerOwes);
        		String exit = "";
        		int count =0;
        		while(onloan.next()){
        			exit += bookLookup(onloan.getInt("isbn"));
        			count ++;
        		}
        		if(count ==1){
        			con.commit();
					con.setAutoCommit(false);
        			return"This Customer still has a book on loan, it must be returned before deletion."+ exit;
        		} else if (count > 1){
        			con.commit();
					con.setAutoCommit(false);
        			return"This Customer still has books on loan, they must be returned before deletion."+ exit;
        		}

    			try{

    				con.setAutoCommit(false);
    				String customerDelete = "DELETE FROM Customer WHERE customerid = "+customerID+";";
    				stmt.executeUpdate(customerDelete);
					con.commit();
					con.setAutoCommit(true);
    				success = true;
    				res ="Customer deleted:"+  customerID +"\n ";

    			}catch (SQLException e) {
    				res="Error deleting customer: "+ customerID;
    			}
    		}catch (SQLException e) {
    			res="Customer has books owing";
    		}
    	}catch (SQLException e) {
    		res="Error retireving customer: "+ customerID;
    	}
    	if(success == false){
    		try {
    			con.rollback();
				con.setAutoCommit(true);
    		} catch (SQLException e) {
    			res= "roll back failed";
    		}
    	}
    	return res;
    }

    public String deleteAuthor(int authorID) {
    	boolean success=false;
    	String res = "";
    	try{
     		con.setReadOnly(false);
    		con.setAutoCommit(false);
    		con.setTransactionIsolation(con.TRANSACTION_SERIALIZABLE);
    		//before you delete check that they do not have any books on loan.
    		Statement stmt = con.createStatement();
    		String authorValid = "SELECT * FROM author WHERE authorid ="+authorID+" FOR UPDATE;";
    		ResultSet author = stmt.executeQuery(authorValid);


    		try{
    			//do we delete all books that they have written?
    			//what about the ones where they are a co- author?
    			//what about the ones on loan to customers?
    			String authorWrite = "SELECT * FROM book NATURAL JOIN book_author WHERE authorid ="+authorID+" FOR UPDATE;";
    			ResultSet onloan = stmt.executeQuery(authorWrite);
    			String Books ="";
    			while(onloan.next()){
    				Books += onloan.getString("isbn")+", ";
    			}

    			if(Books != ""){
    				//set the entries in book author to default
    				con.setAutoCommit(false);
					String updateAuthor = "UPDATE book_author SET authorid =0 WHERE authorid ="+authorID+";";
    				stmt.executeUpdate(updateAuthor);
    			}
    			try{

    				con.setAutoCommit(false);
    				String authorDelete = "DELETE FROM author WHERE authorid = "+authorID+";";
    				stmt.executeUpdate(authorDelete);
    				con.commit();
					con.setAutoCommit(true);
    				success = true;
    				res ="Author deleted:"+  authorID +"\n ";

    			}catch (SQLException e) {
    				res = "Error deleting: "+ authorID;
    			}
    		}catch (SQLException e) {
    			res = "books still in lib: "+ authorID;
    		}
    	}catch (SQLException e) {
    		res = "Error retireving author: "+ authorID;
    	}
    	if(success == false){
    		try {
    			con.rollback();
				con.setAutoCommit(true);
    		} catch (SQLException e) {
    			res= "roll back failed";
    		}
    	}

    	return res;
    }

    public String deleteBook(int isbn) {

    	boolean success=false;
    	String res = "";


    	try{
    		con.setReadOnly(false);
    		//con.setAutoCommit(false);
    		con.setTransactionIsolation(con.TRANSACTION_SERIALIZABLE);

    		Statement stmt = con.createStatement();
    		//check its a real book
    		String bookValid= "SELECT * FROM book WHERE isbn="+isbn+"FOR UPDATE;";
    		ResultSet there = stmt.executeQuery(bookValid);
    		int count =0;
    		while(there.next()){
    			count ++;
    		}
    		if(count ==0){
    			con.commit();
    			con.setAutoCommit(false);
    			return "This is not a valid isbn"+ isbn;
    		}

    		try{
    			//before you delete check that there are no books on loan.
    			String booksLoan = "SELECT * FROM cust_book WHERE isbn="+isbn+"FOR UPDATE;";
    			there = stmt.executeQuery(booksLoan);
    			while(there.next()){
    				con.setAutoCommit(false);

    				con.commit();
    				return "This books still has issues on loan, please return them before deleting the book.";

    			}
    			try{
    				con.setAutoCommit(false);

    				try{
    				//check that the author of this book has written other books, if not then delete them too.
    				//save the author id
    				String findAuthor = "SELECT * FROM  book_author WHERE isbn ="+isbn+";";
    				there = stmt.executeQuery(findAuthor);
    				ArrayList<Integer> authorList = new ArrayList<Integer>();
    				int i=0;
    				while(there.next()){
    					authorList.add(there.getInt("authorid"));
    					i++;
    				}


    				for(int j = 0; j< authorList.size(); j++){

    					//count how many other books they have
    					String findotherbooks ="SELECT * FROM book_author WHERE authorid ="+authorList.get(j)+";";
    					there = stmt.executeQuery(findotherbooks);
    					int authorb=0;
    					while(there.next()){
    						authorb ++;
    					}
    					if(authorb == 1){
    						String bookauthordelete = "DELETE FROM author WHERE authorid="+authorList.get(j)+";";
    						stmt.executeUpdate(bookauthordelete);
    					}
    				}
    				} catch (SQLException e) {
    	    			return "Error finding author: "+ isbn;
    	    		}

    				String bookDelete = "DELETE FROM book WHERE isbn="+isbn+";";
    				stmt.executeUpdate(bookDelete);

    				String bookauthor = "SELECT FROM book_author WHERE isbn="+isbn+" FOR UPDATE;";
    				stmt.executeQuery(bookauthor);

    				String bookauthordelete = "DELETE FROM book_author WHERE isbn="+isbn+";";
    				stmt.executeUpdate(bookauthordelete);



    				try{//deletes any isbns set to 0 so no null pointer exceptions.
    					String Deletecheck = "SELECT * FROM book_author WHERE isbn ="+0+";";
        				ResultSet check = stmt.executeQuery(Deletecheck);

        				String delete = "DELETE FROM book_author WHERE isbn="+0+";";
        				stmt.executeUpdate(delete);

    				}catch (SQLException e) {
        				res = "check delete did not work: "+ isbn;
        			}

    				con.commit();
    				con.setAutoCommit(false);
    				//con.setAutoCommit(true);
    				success = true;
    				res = "Book deleted: "+ isbn;

    			}catch (SQLException e) {
    				res = "Error deleting: "+ isbn;
    			}
    		}catch (SQLException e) {
    			res = "book author check: "+ isbn;
    		}
    	}catch (SQLException e) {
    		res = "books still in lib: "+ isbn;
    	}
    	if(success == false){
    		try {
    			System.out.println("here");
    			con.rollback();
    			con.setAutoCommit(false);
    		} catch (SQLException e) {
    			res= "roll back failed";
    		}
    	}
    	System.out.println(res);
    	return res;
    }
}