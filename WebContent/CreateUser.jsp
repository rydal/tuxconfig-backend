<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Linuxconf</title>
<script src="./js/createuser.js"></script>
</head>
<body>
<%
try {
	ArrayList<String> clone_urls = null;
	if (session.getAttribute("clone_urls") instanceof ArrayList ) {
	 clone_urls = (ArrayList<String>) session.getAttribute("clone_urls");
	} else {
		out.println("Error retrieving projects form git");
	}
	out.println("<form method='post' id='theform' action='http://linuxconf.feedthepenguin.org/hehe/createuser' onsubmit='subimit_reposities()'>");
	
	out.println("Your homepage / linkedin etc:");
	out.println("<input type='text' name='url' id='url' required maxlength='255'>");
	out.println("Your description:");
	out.println("<input type='text' name='description' id='description' required maxlength='768' >");
	
	
	
	
	out.write("<table><tr>");
	out.write("<th style='width:50%'>Git Repositry</th><th style='width:25%'>Device ID</th><th style='width:25%'>Name of device</th></tr>");
	for(int i = 0; i < clone_urls.size(); i++) {
		out.write("<tr>");
		out.write("<td style='width:50%'>" + i + ": "  + clone_urls.get(i) + ": <input type='checkbox' name='git_url" + i + "' value='" + clone_urls.get(i) + "'></td> ");
		out.write("<td style='width:20%'><input type='text' name='device_id" + i + "' id='device_id" + i +"' > </td>");
		out.write("<td style='width:25%'><input type='text' name='device_name" + i + "' > </td>");
		out.write("</tr><br>");
		out.flush();
	}
	out.write("</table>");
	out.write("<input type='image' src='./img/submit.png' alt='Submit Form' />");

	out.println("</form>");
	
	
} catch (Exception ex) { ex.printStackTrace(new java.io.PrintWriter(out)); }

%>
</body>
</html>