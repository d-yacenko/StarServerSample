package conduit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.Request;

public class Form extends HttpServlet {
	public static final String DATABASE_HOST = System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST");
	public static final String DATABASE_PORT = System.getenv("OPENSHIFT_POSTGRESQL_DB_PORT");
	public static final String DATABASE_NAME = "conduit";
	private static final String USER = "admintyvjk9j";
	private static final String PASS = "tH139Jrvp6US";
	private Connection connection;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String name = request.getParameter("name");
        String number = request.getParameter("number");
        String birthday = request.getParameter("birthday");
        Date in_date= null;
        try {
        	System.out.println(name+"--"+birthday);
            in_date = new SimpleDateFormat("dd.MM.yyyy",new Locale("ru")).parse(birthday);
        } catch (ParseException e) {
        	System.out.println("Неверный формат даты "+e.getMessage());
        }
        Contact contact=new Contact(name, in_date,Long.parseLong(number));
        connect2db();
        put(contact);
        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/input.html");
	}
	
	private void put(Contact contact){
		contact.name=contact.name.replaceAll("['\"\\\\]", "\\\\$0");
		if(contact.name.length()>15 || contact.name.toUpperCase().contains("SCRIPT") ||contact.name.contains("<")||contact.name.contains(">") )return;
		try{
			String query = "INSERT INTO CONTACTS (CNUMBER, CNAME, CDATE) VALUES"
					+ " (?,?,?)";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setLong(1, contact.number);
			preparedStatement.setString(2, contact.name);
			preparedStatement.setDate(3, new java.sql.Date(contact.birthday.getTime()));
			preparedStatement .executeUpdate();		
			}catch(Exception e){
				e.printStackTrace();
        	System.out.println("Table insert error - "+e.getMessage());
		}
		
	} 
	private void connect2db() {
		if(connection!=null) return;
		String url = "jdbc:postgresql://" + DATABASE_HOST + ":" + DATABASE_PORT + "/" + DATABASE_NAME;
		try {
			Class.forName("org.postgresql.Driver");
			connection = DriverManager.getConnection(url, USER, PASS);
			System.out.println("Got Connection");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
