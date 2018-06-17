package conduit;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Form1
 */
public class Form1 extends HttpServlet {
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
        connect2db();
        delall();
        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/input.html");
	}
	
	private void delall(){
		try{
			String query = "DELETE FROM CONTACTS";
			PreparedStatement preparedStatement = connection.prepareStatement(query);
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
