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
<script
  src="https://code.jquery.com/jquery-3.3.1.min.js"
  integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
  crossorigin="anonymous"></script>
<script>
function submit_repositries() {

	var i = 0;
	var success = true;
	var result = "";
	var dataString = "url=" + document.getElementById("url").value + "&description=" + document.getElementById("description").value + "&" ;
	
	while (document.getElementById("device_checkbox" + i) != undefined) {
		if (document.getElementById("device_checkbox" + i).checked) {
		
		dataString += "git_url" + i + "=" + document.getElementById("git_url_hidden" + i).value ; 
		dataString += "&commit_id" + i + "=" + document.getElementById("commitid" + i).value ; 
		
		}
				i++;
	}

		    $.ajax({
		        type: "GET",
		        url: "https://linuxconf.feedthepenguin.org/hehe/createuser",
		        data: dataString,
		        dataType: "json",
		        success: function(data, textStatus) {
		        	for(var i = 0; i < data.length; i++) {
					document.getElementById("output").innerHTML += data[i];
		        		
		                	
		        
		            		            }
		        }
		    });
		    
	
}
</script>
</head>
<body>
<img src="./img/linuxconf.png" height="200" width="400">
<%
try {
	
	
	Class.forName("com.mysql.jdbc.Driver");  
	Connection con=DriverManager.getConnection(  
	"jdbc:mysql://localhost/linuxconf","arwen","imleaving");
	
	if((String) session.getAttribute("git_email") == null ) {
		response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/GitAuth.html");
	}
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
	out.println("Your Email:" + email + "<br>");
	out.println("Your Git Id:" + owner_git_id + "<br>");
	
	out.println("Your homepage / linkedin etc:");
	out.println("<input type='text' id='url' required maxlength='255' value='" + url + "'>");
	
	out.println("Your description:");
	out.println("<input type='text' name='description' id='description' required maxlength='768' value=" + description + ">");
	
	
	
	
	out.write("<table><tr>");
	out.write("<th style='width:50%'>Git Repositry</th><th style='width:50%'>commit id</th></tr>");
	int i =  0;
	while ( i < clone_urls.size()) {
		
		out.write("<tr>");
	
		PreparedStatement get_device_by_id = con.prepareStatement("select device_id,name,commit_id from devices where git_url = ?");
		get_device_by_id.setObject(1, clone_urls.get(i) );
		ResultSet got_device_by_id = get_device_by_id.executeQuery();
		if(got_device_by_id.next()) {
			out.write("<td style='width:50%'>" + i + ": <A HREF='"  + clone_urls.get(i) + "'>" + clone_urls.get(i) + "</a> : <input type='checkbox' name='git_url" + i + "' id='device_checkbox" + i + "' value='" + clone_urls.get(i) + "'></td> ");
			out.write("<td>" +  "<input type='hidden' name='git_url_hidden" + i + "' value='" + clone_urls.get(i) + "' id='git_url_hidden" + i + "' ></td>" );
			out.write("<td>" + "<input type='text' name ='commitd" + i + "' id='commitid" + i + "' > </td>");
		out.write("</tr><br>");
			out.flush();
		} else {
		
		
		out.write("<td style='width:50%'>" + i + ": <A HREF='"  + clone_urls.get(i) + "'>" + clone_urls.get(i) + "</A> : <input type='checkbox' name='git_url" + i + "' id='device_checkbox" + i + "' value='" + clone_urls.get(i) + "'></td> ");
		out.write("<td>" +  "<input type='hidden' name='git_url_hidden" + i + "' value='" + clone_urls.get(i) + "' id='git_url_hidden" + i + "' ></td>" );
		out.write("<td>" + "<input type='text' name ='commitd" + i + "' id='commitid" + i + "' > </td>");

		
		out.write("</tr><br>");
		out.flush();
	}
		i++;
	}
	out.write("<script> var i = " + i + ";</script>");
	out.write("</table>");
	out.write("<input type='image' src='./img/submit.jpg' onclick='submit_repositries()' alt='Submit Form' />");
	out.write("<div id='output'></div>");

	
	
} 
catch (Exception ex) { ex.printStackTrace(new java.io.PrintWriter(out)); }

%>
</body>
</html>