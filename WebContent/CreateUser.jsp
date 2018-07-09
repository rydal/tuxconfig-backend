<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Linuxconf</title>
<script src="./js/createuser.js"></script>
</head>
<body>
<img src="./img/linuxconf.png" height="200" width="400">
<%
try {
	
	
	Class.forName("com.mysql.jdbc.Driver");  
	Connection con=DriverManager.getConnection(  
	"jdbc:mysql://localhost/linuxconf","arwen","imleaving");
	
	String email = (String) session.getAttribute("git_email");
	String url = "";
	String description= "";
	String owner_git_id = (String) session.getAttribute("git_id");
	
		
	
	PreparedStatement get_details = con.prepareStatement("select * from contributor where owner_git_id = ?");
	get_details.setObject(1, (String) session.getAttribute("git_id"));
	ResultSet details = get_details.executeQuery();
	if(details.next()) {
		url = details.getString("url");
		description = details.getString("description");
	}
	
	ArrayList<String> clone_urls = null;
	if (session.getAttribute("clone_urls") instanceof ArrayList ) {
	 clone_urls = (ArrayList<String>) session.getAttribute("clone_urls");
	} else {
		out.println("Error retrieving projects form git");
	}
	out.println("<form method='post' id='theform' action='http://linuxconf.feedthepenguin.org/hehe/createuser' onsubmit='subimit_reposities()'>");
	out.println("Your Email:" + email + "<br>");
	out.println("Your Git Id:" + owner_git_id + "<br>");
	
	out.println("Your homepage / linkedin etc:");
	out.println("<input type='text' name='url' id='url' required maxlength='255' value=" + url + ">");
	out.println("Your description:");
	out.println("<input type='text' name='description' id='description' required maxlength='768' value=" + description + ">");
	
	
	
	
	out.write("<table><tr>");
	out.write("<th style='width:50%'>Git Repositry</th><th style='width:25%'>Device ID</th><th style='width:25%'>Name of device</th></tr>");
	
	for(int i = 0; i < clone_urls.size(); i++) {
		out.write("<tr>");
	
		PreparedStatement get_device_by_id = con.prepareStatement("select device_id,name from devices where git_url = ?");
		get_device_by_id.setObject(1, clone_urls.get(i) );
		ResultSet got_device_by_id = get_device_by_id.executeQuery();
		if(got_device_by_id.next()) {
			out.write("<td style='width:50%'>" + i + ": "  + clone_urls.get(i) + ": <input type='checkbox' name='git_url" + i + "' value='" + clone_urls.get(i) + "'></td> ");
			out.write("<td style='width:20%'><input type='text' name='device_id" + i + "' id='device_id" + i +"' value=" + got_device_by_id.getString("device_id") + " > </td>");
			out.write("<td style='width:25%'><input type='text' name='device_name" + i + "' value=" + got_device_by_id.getString("name") +  "> </td>");
			out.write("</tr><br>");
			out.flush();
		} else {
		
		
		out.write("<td style='width:50%'>" + i + ": "  + clone_urls.get(i) + ": <input type='checkbox' name='git_url" + i + "' value='" + clone_urls.get(i) + "'></td> ");
		out.write("<td style='width:20%'><input type='text' name='device_id" + i + "' id='device_id" + i +"' > </td>");
		out.write("<td style='width:25%'><input type='text' name='device_name" + i + "' > </td>");
		out.write("</tr><br>");
		out.flush();
	}
	}
	out.write("</table>");
	out.write("<input type='image' src='./img/submit.png' alt='Submit Form' />");

	out.println("</form>");
	
	
} catch (Exception ex) { ex.printStackTrace(new java.io.PrintWriter(out)); }

%>
</body>
</html>