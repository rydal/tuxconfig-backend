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
<title>Linuxconf</title>
</head>
<body>
<img src="./img/linuxconf.png" height="200" width="400"><br>
<div id="output" > </div>
	<%
	// Get an array of Cookies associated with this domain
	Cookie cookie = null;
		Cookie[] cookies = null;

		String myemail = null; 
		String hash = null; 
	
			cookies = request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
				if (cookie.getName().equals("email")) {
					myemail = cookie.getValue();
				}
				if (cookie.getName().equals("password")) {
					hash = cookie.getValue();
			} 
			}
			
			if (myemail == null ) {
				 out.write("Email address not received from cookie");
				 out.write("<A HREF='https://linuxconf.feedthepenguin.org/hehe/login.jsp'>Login?</A>");

				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				return;
			}
			if (hash == null) {
				 out.write("Password not received from cookie");
				 out.write("<A HREF='https://linuxconf.feedthepenguin.org/hehe/login.jsp'>Login?</A>");
				 return;
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
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
			PreparedStatement stmt = conn.prepareStatement("SELECT email,password,authorised FROM user where email = ?");
			stmt.setObject(1, myemail);
			ResultSet rs2 = stmt.executeQuery();
			if (!rs2.next()) {
				session.setAttribute("flag", "Can't find email address");
				response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/Register.jsp");
				return;

			} else {
				if (!hash.equals(rs2.getString("password"))) {
					session.setAttribute("flag", "not logged in");
					response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/login.jsp");
					return;
				}
			}
			
		//assume logged in.
		if (rs2.getInt("authorised") != 1) {
			out.write("not authorised to vet configurations");
			out.write("<A HREF='https://linuxconf.feedthepenguin.org/hehe/RequestAuthorization.jsp'> reapply for authorizaion </A> ");
					
		}
		
		
		out.write("<h1> Review proposed configurations</h1>");
		out.write("<input type='text' id='searchbox'  style='width:250px;'>"); 
		out.write("<img src='./img/search-by-id.png' id='idsearch' onclick='search(this.id)'>");
		out.write("<img src='./img/search-by-name.png' id='namesearch' onclick='search(this.id)'>");
		out.write("<img src='./img/search-by-contributor-name.png' id='contributorsearch' onclick='search(this.id)'>");
		
		
		out.write("<h2>Devices awaiting authorisation</h2>");
		String query_url = "SELECT * from contributor inner join devices on devices.owner_git_id = contributor.owner_git_id where devices.authorised = 0 order by devices.name";
		PreparedStatement get_devices = conn.prepareStatement(query_url);
		ResultSet got_devices = get_devices.executeQuery();
		int index = 0;
		while (got_devices.next()) {
			
			out.println("Device id: " + got_devices.getObject("devices.device_id"));
			out.println("Device name: " + got_devices.getObject("devices.name"));
			
			out.println("<A HREF='" + got_devices.getObject("devices.git_url") +"'> " + got_devices.getObject("git_url")  + "</A>");
			out.println("<input type='hidden'  value='" + got_devices.getObject("devices.commit_hash") + "' id='" +  "hash" + index  +"'>");
			out.println("<input type='hidden'  value='" + got_devices.getObject("devices.owner_git_id") + "' id='" +  "owner_git_id" + index  +"'><br>");
			out.println("<input type='hidden'  value='" + got_devices.getObject("devices.device_id") + "' id='" +  "device_id" + index  +"'><br>");
			out.println("<input type='hidden'  value='" + got_devices.getObject("devices.git_url") + "' id='" +  "url" + index  +"'><br>");

			out.println("Commit id " + got_devices.getObject("commit_hash") + "<br>");
			out.println("User email address " + got_devices.getObject("contributor.email"));
			
		
			out.println("<img src=\"./img/accept.png\" id=\""  +  "input" + index +  "\" onclick=\"send_post(this.id , 'authorise')\">");
			out.println("<img src=\"./img/delete.png\" id=\""  +  "input" + index +  "\" onclick=\"send_post(this.id , 'delete')\">");
			out.println("<hr>");
			index++;
		}

		out.write("<h2>Devices authorised</h2>");
		index = 0;
		query_url = "SELECT  * from contributor inner join devices on devices.owner_git_id = contributor.owner_git_id where devices.authorised = 1 order by devices.name";
		PreparedStatement get_authorised_devices = conn.prepareStatement(query_url);
		ResultSet got_authorised_devices = get_authorised_devices.executeQuery();
		while (got_authorised_devices.next()) {
			
			out.println("Device id: " + got_authorised_devices.getObject("devices.device_id"));
			out.println("Device name: " + got_authorised_devices.getObject("devices.name"));
			
			out.println("<A HREF='" + got_authorised_devices.getObject("devices.git_url") +"'> " + got_authorised_devices.getObject("git_url")  + "</A>");
			out.println("<input type='hidden'  value='" + got_authorised_devices.getObject("devices.commit_hash") + "' id='" +  "hash" + index  +"'>");
			out.println("<input type='hidden'  value='" + got_authorised_devices.getObject("devices.owner_git_id") + "' id='" +  "owner_git_id" + index  +"'><br>");
			out.println("<input type='hidden'  value='" + got_authorised_devices.getObject("devices.device_id") + "' id='" +  "device_id" + index  +"'><br>");
			out.println("<input type='hidden'  value='" + got_authorised_devices.getObject("devices.git_url") + "' id='" +  "url" + index  +"'><br>");

			out.println("Commit id " + got_authorised_devices.getObject("commit_hash") + "<br>");
			out.println("User email address " + got_authorised_devices.getObject("contributor.email"));
					
			out.println("<img src=\"./img/decline.png\" id=\"" +  "input" + index + "\" onclick=\"send_post(this.id , 'unauthorise')\">");
			out.println("<img src=\"./img/delete.png\" id=\"" +  "input" + index + "\" onclick=\"send_post(this.id , 'delete')\">");
			
			out.println("<hr>");
			index++;
		}
		}
		
		catch (Exception ex) {
			ex.printStackTrace(new PrintWriter(out));
		}
	%>
</body>
<script>
function send_post(input, command){
	
	var i = input.replace("input","");

	if (command === "delete") {
		var delete_string  = document.getElementById("url" + i).value;
		  var r = confirm("Really delete " + delete_string + " ? ");
		  if (r == false) {
		      return;
		  }  
	  }
	
	
	var dataString = "";
		
	dataString += "owner_git_id=" + document.getElementById("owner_git_id" + i).value;
	dataString += "&hash="	+ document.getElementById("hash" + i).value;
	dataString += "&device_id="	+ document.getElementById("device_id" + i).value;
	dataString += "&command=" + command;
	
    $.ajax({
        type: "GET",
        url: "https://linuxconf.feedthepenguin.org/hehe/vetconfigurations",
        data: dataString,
         
        dataType: "json",
        success: function(data, textStatus) {
            if (data.redirect) {
                // data.redirect contains the string URL to redirect to
            	window.location.href = data.redirect;
            }
            else {
                // data.form contains the HTML for the replacement form
                alert(data.form);
                location.reload();

            }
        }
        
    })
}


function search(command){
	
	var dataString  ="";
	dataString += "pattern=" + document.getElementById("searchbox").value;
	dataString += "&command=" + command;

	window.location.replace("https://linuxconf.feedthepenguin.org/hehe/SearchConfigurations.jsp?" + dataString);
}


</script>

</html>

