<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
 <script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.3.1.min.js"></script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
</head>
<body>
	<%
		Cookie cookie = null;
		Cookie[] cookies = null;

		String email = "";
		String password = "";
		String id = "";

		// Get an array of Cookies associated with this.id domain
		cookies = request.getCookies();
		boolean gotcookies = false;
		for (int i = 0; i < cookies.length; i++) {
			cookie = cookies[i];
			if (cookie.getName().equals("email")) {
				email = cookie.getValue();
				gotcookies = true;
			}
			if (cookie.getName().equals("password")) {
				password = cookie.getValue();
				gotcookies = true;
			}

		}
		try {
			Class.forName("com.mysql.jdbc.Driver");

			final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
			final String DB_URL = "jdbc:mysql://localhost/linuxconf";

			//  Database credentials
			final String USER = "arwen";
			final String PASS = "imleaving";

			// Register JDBC driver
			// Open a connection
			Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

			String user_type = (String) session.getAttribute("user_type");
			PreparedStatement stmt = conn.prepareStatement("SELECT email,password,authorized FROM user where email = ?");
			stmt.setObject(1, email);
			ResultSet rs2 = stmt.executeQuery();
			if (!rs2.next()) {
				session.setAttribute("flag", "Can't find email address");
				response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/Register.jsp");
				return;

			} else {
				if (!password.equals(rs2.getString("password"))) {
					session.setAttribute("flag", "not logged in");
					response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/login.jsp");
					return;
				}
			}
			
		//assume logged in.
		if (rs2.getInt("authorized") == 0) {
			out.write("not authorized to vet configurations");
			out.write("<A HREF='https://linuxconf.feedthepenguin.org/hehe/RequestAuthorization.jsp'> reapply for authorizaion </A> ");
					
		}
		
		
		out.write("<h1> Review proposed configurations</h1>");
		out.write("<input type='text' id='searchbox'  style='width:50px;'>"); 
		out.write("<img src='./img/search-by-id.png' id='idsearch' onclick='send_post(this.id)");
		out.write("<img src='./img/search-by-name.png' id='namesearch' onclick='send_post(this.id)");
		out.write("<img src='./img/search-by-contributor-name.png' id='contributorsearch' onclick='send_post(this.id)");
		
		PreparedStatement get_devices = conn.prepareStatement("select * from devices where authorized = 0 order by name");
		ResultSet got_devices = get_devices.executeQuery();
		while (got_devices.next()) {
			
			out.println(got_devices.getObject("device_id"));
			out.println(got_devices.getObject("name"));
			out.println("A HREF='" + got_devices.getObject("git_url") +"> " + got_devices.getObject("git_url")  + "</A>");
			out.println("<img src=\"./img/accept.png\" id=\"" +  got_devices.getObject("device_id") + "\" onclick=\"send_post(this.id , 'accept')\">");
			out.println("<img src=\"./img/decline.png\" id=\"" +  got_devices.getObject("device_id") + "\" onclick=\"send_post(this.id , 'decline')\">");
			out.println("<hr>");
		}
		
		PreparedStatement get_authorized_devices = conn.prepareStatement("select * from devices where authorized = 1 order by name");
		ResultSet got_authorized_devices = get_authorized_devices.executeQuery();
		while (got_authorized_devices.next()) {
			
			out.println(got_devices.getObject("device_id"));
			out.println(got_devices.getObject("name"));
			out.println("A HREF='" + got_devices.getObject("git_url") +"> " + got_devices.getObject("git_url")  + "</A>");
			out.println("<img src=\"./img/decline.png\" id=\"" +  got_devices.getObject("device_id") + "\" onclick=\"send_post(this.id , 'decline')\">");
			out.println("<hr>");
		}
		}
		
		catch (Exception ex) {
			ex.printStackTrace(response.getWriter());
		}
	%>
</body>
</html>