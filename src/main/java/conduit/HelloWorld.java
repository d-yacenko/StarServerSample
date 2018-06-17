package conduit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
@WebServlet("/conduit/HelloWorld")
public class HelloWorld extends HttpServlet {
	private static final long serialVersionUID = -3950937333898038206L;
	public static final String DATABASE_HOST = System.getenv("OPENSHIFT_POSTGRESQL_DB_HOST");
	public static final String DATABASE_PORT = System.getenv("OPENSHIFT_POSTGRESQL_DB_PORT");
	public static final String DATABASE_NAME = "conduit";
	private static final String USER = "admintyvjk9j";
	private static final String PASS = "tH139Jrvp6US";
	private Connection connection;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		String name = request.getParameter("name");
		PrintWriter out = response.getWriter();
		out.println("Hello " + name + "!");
		out.flush();
		connect2db();
		if (connection != null) {
			out.println("Connect to DB established!<br/>");
			out.flush();
		}
		java.util.List<Contact> list = getAll();
		if (list != null) {
			out.println("get from DB " + list.size() + " records<br/>");
			out.println("<pre>");
			for(Contact contact:list) out.println(contact+"");
			out.println("</pre>");
			out.flush();
		}
		out.close();
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 int length = request.getContentLength();
         ServletInputStream sin = request.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(
                         sin));
         connect2db();
         String command = reader.readLine();
         if (command.equals("put")) {
        	 	Serializer ser = new Persister();
        	 	Contact contact=null;
				try {
					contact = ser.read(Contact.class, reader);
					put(contact);
			         reader.close();
			         sin.close();
			         response.setStatus(HttpServletResponse.SC_OK);
			         OutputStreamWriter writer = new OutputStreamWriter(
			                         response.getOutputStream());
			         writer.write("I receive all OK!");
			         writer.flush();
			         writer.close();
				} catch (Exception e) {
					System.out.println("ошибка получения объекта "+e.getMessage());
				}
        	 	System.out.println(command+"\t"+contact);
         } else if(command.equals("get")){
            reader.close();
            sin.close();
            response.setStatus(HttpServletResponse.SC_OK);
     		Phonebook phonebook=new Phonebook();
     		phonebook.contacts= getAll();
    	 	Serializer ser = new Persister();
            OutputStreamWriter writer = new OutputStreamWriter(
                            response.getOutputStream());
            try {
				ser.write(phonebook, writer);
			} catch (Exception e) {
				System.out.println("ошибка сериализации списка клиенту - "+e.getMessage());
			}
            writer.flush();
            writer.write("I send all OK!");
            writer.flush();
            writer.close();
        	 
         } 
	}
	
	private List<Contact> getAll(){
		List<Contact> list;
		String query = "select * from contacts";
		try{
		Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        if(rs==null){
        	System.out.println("resultset is null");
        	return null;
        }
        list=new ArrayList<Contact>();
        while (rs.next()) {
            String name = rs.getString("CNAME");
            long number = rs.getLong("CNUMBER");
            Date date = rs.getDate("CDATE");
            list.add(new Contact(name,date,number));
        }
        }catch(Exception e){
        	System.out.println("DB error - "+e.getMessage());
        	System.out.println("Table not created yet? Try create");
        	createTable();
        	return null;
        }
        
        return list;
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
	
	private void createTable(){
		String query = "create table contacts ( cnumber int,cname varchar(255),cdate date); ";
		try{
		Statement stmt = connection.createStatement();
        stmt.executeUpdate(query);
		}catch(Exception e){
        	System.out.println("Table create errorr - "+e.getMessage());
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