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
</script>
</head>
<body>
<img src="./img/linuxconf.png" height="200" width="400"><br>
<a href="https://linuxconf.feedthepenguin.org/hehe/VetConfigurations.jsp"><img src="./img/vet-configurations.png"></a> 
<div id="output" > </div>
	<%
	try {
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
		
		
		out.write("<input type='text' id='searchbox'  style='width:250px;'>"); 
		out.write("<img src='./img/search-by-id.png' id='idsearch' onclick='search(this.id)'>");
		out.write("<img src='./img/search-by-name.png' id='namesearch' onclick='search(this.id)'>");
		out.write("<img src='./img/search-by-email.png' id='contributorsearch' onclick='search(this.id)'><br>");
			
			
		
		String search_type = request.getParameter("command");
		String search_key = request.getParameter("pattern");
		
		
		PreparedStatement get_devices = null;
			
		switch (search_type) {
		case "idsearch": {
			get_devices = conn.prepareStatement("SELECT * from contributor inner join devices on devices.owner_git_id = contributor.owner_git_id where devices.device_id = ? order by name");
			get_devices.setObject(1, search_key);
			break;
		}
		case "namesearch": {
			get_devices = conn.prepareStatement("SELECT * from contributor inner join devices on devices.owner_git_id = contributor.owner_git_id where devices.name = ? order by name");get_devices.setObject(1, search_key);
			break;
		}
		case "contributorsearch": {
			get_devices = conn.prepareStatement("SELECT * from contributor inner join devices on devices.owner_git_id = contributor.owner_git_id where contributor.email  = ? order by name");get_devices.setObject(1, search_key);
			break;
		}
		default: {
			out.write("Cannot find search type");
			return;
			
		}
		}
		
		ResultSet got_devices = get_devices.executeQuery();
		int index = 0;
		while (got_devices.next()) {
			
			 out.println("<A HREF='" + got_devices.getObject("git_url") +"'> " + got_devices.getObject("git_url")  + "</A>");
			
			out.println("Git ID: '" + got_devices.getObject("device_id") +"' <br>" );
			out.println("Device Name: '" + got_devices.getObject("name") +"' <br>" );

			out.println("Git Owner ID: '" + got_devices.getObject("owner_git_id") +"' <br>" );
			out.println("Commit hash: '" + got_devices.getObject("commit_hash") +"' <br>" );
			out.println("Git Owner email: '" + got_devices.getObject("email") +"' <br>" );
			
			out.println("<input type='hidden'  value='" + got_devices.getObject("commit_hash") + "' id='" +  "hash" + index  +"'>");
			out.println("<input type='hidden'  value='" + got_devices.getObject("owner_git_id") + "' id='" +  "owner_git_id" + index  +"'>");
			out.println("<input type='hidden'  value='" + got_devices.getObject("device_id") + "' id='" +  "device_id" + index  +"'>");
			out.println("<input type='hidden'  value='" + got_devices.getObject("git_url") + "' id='" +  "url" + index  +"'>");

		
			out.println("<img src=\"./img/accept.png\" id=\""  +  "input" + index +  "\" onclick=\"send_post(this.id , 'authorise')\">");
			out.println("<img src=\"./img/decline.png\" id=\"" +  "input" + index + "\" onclick=\"send_post(this.id , 'unauthorise')\">");
			out.println("<img src=\"./img/delete.png\" id=\""  +  "input" + index +  "\" onclick=\"send_post(this.id , 'delete')\">");
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
        url: "https://linuxconf.feedthepenguin.org/hehe/SearchConfigurations.jsp",
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

