<!-- https://developers.openshift.com/en/tomcat-ds.html -->
<%@page import="conduit.Contact"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import='java.sql.*' %>
<%@ page import='javax.sql.*' %>
<%@ page import='javax.naming.*' %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Пример работы из JSP с базой данных </title>
</head>
<body>
<h1>Пример работы с базой данных</h1>
 <img src="/images/database.jpg"> <br/>
 <pre>
<%
Connection result = null;
try {
    InitialContext ic = new InitialContext();
    Context initialContext = (Context) ic.lookup("java:comp/env");
    DataSource datasource = (DataSource) initialContext.lookup("jdbc/PostgreSQLDS");
    result = datasource.getConnection();
    Statement stmt = result.createStatement() ;
    String query = "select * from contacts;" ;
    ResultSet rs = stmt.executeQuery(query) ;
    while (rs.next()) {
        out.println(new Contact(rs.getString(2),rs.getDate(3),rs.getLong(1)));
    }
} catch (Exception ex) {
    out.println("Exception: " + ex + ex.getMessage());
}
%>
  </pre>

</body>
</html>