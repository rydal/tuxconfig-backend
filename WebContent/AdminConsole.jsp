<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Configure Me</title>
</head>
<body>
<h2> Pending users to be Authorized:</h2>
<img src="./img/linuxconf.png" height="200" width="400">
	<%
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

			PreparedStatement get_waiting_users = con.prepareStatement("select * from user where authorized = ? order by email");
			get_waiting_users.setObject(1, "0");
			ResultSet got_waiting_users = get_waiting_users.executeQuery();
			while (got_waiting_users.next()) {
				out.println(got_waiting_users.getObject("email"));
				out.println("<img src='delete.png' id='" +  got_waiting_users.getObject("email") + "' onlcick='send_post(this.id,delete)'");
				out.println("<img src='authorize.png' id='" +  got_waiting_users.getObject("email") + "' onlcick='send_post(this.id,authorize)'");
				out.println("<hr>");
			}
			
			out.print("<h2> Currenrly authroized users");
			
			PreparedStatement get_current_users = con.prepareStatement("select * from user where authorized = ? order by email");
			get_waiting_users.setObject(1, "1");
			ResultSet got_current_users = get_waiting_users.executeQuery();
			while (got_waiting_users.next()) {
				out.println(got_waiting_users.getObject("email"));
				out.println("<img src='delete.png' id='" +  got_waiting_users.getObject("email") + "' onlcick='send_post(this.id,delete)'");
				
				out.println("<hr>");
			}
		}
			catch (Exception ex) { ex.printStackTrace(new PrintWriter (out)); }
			%> 
</body>
</html>