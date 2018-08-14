<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="org.json.JSONObject"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Configure Me</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
 <script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.3.1.min.js"></script>
<script>
function send_post(email, command){
    $.ajax({
        type: "POST",
        url: "https://linuxconf.feedthepenguin.org/hehe/adminconsole",
        data: {"email" : email, "action" : command },
        
        dataType: "json",
        success: function(data, textStatus) {
            if (data.redirect) {
                // data.redirect contains the string URL to redirect to
            	window.location.href = data.redirect;
            }
            else {
                // data.form contains the HTML for the replacement form
                
            }
        }
        
    })
}
</script>
</head>
<body>
<img src="./img/linuxconf.png" height="200" width="400"><br>
<h2> Pending users to be Authorized:</h2>



	<%
	// Get an array of Cookies associated with this domain
	Cookie cookie = null;
		Cookie[] cookies = null;

		String email = null; 
		String hash = null; 
	
			cookies = request.getCookies();
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
				if (cookie.getName().equals("email")) {
					email = cookie.getValue();
				}
				if (cookie.getName().equals("password")) {
					hash = cookie.getValue();
			} 
			}
			
			if (email == null ) {
				 out.write("Email address not received from cookie");
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				return;
			}
			if (hash == null) {
				 out.write("Password not received from cookie");
				 return;
				// Assuming your json object is **jsonObject**, perform the following, it will return your json object  
				}
	
		try {
			out.write("<h2> Pending users to be Authorized:</h2>");

		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

		PreparedStatement get_waiting_users = con.prepareStatement("select * from user where authorized = ? order by email");
		get_waiting_users.setObject(1, "0");
		ResultSet got_waiting_users = get_waiting_users.executeQuery();
		while (got_waiting_users.next()) {
			out.println(got_waiting_users.getObject("email"));
			out.println("<img src=\"./img/delete.png\" id=\"" +  got_waiting_users.getObject("email") + "\" onclick=\"send_post(this.id , 'delete')\">");
			out.println("<img src=\"./img/authorize.png\" id=\"" +  got_waiting_users.getObject("email") + "\" onclick=\"send_post(this.id , 'authorize')\">");
			out.println("<hr>");
		}
		
		out.print("<h2> Currenrly authroized users");

			PreparedStatement get_current_users = con
					.prepareStatement("select * from user where authorized = ? order by email");
			get_waiting_users.setObject(1, "1");
			ResultSet got_current_users = get_waiting_users.executeQuery();
			while (got_current_users.next()) {
				out.println(got_current_users.getObject("email"));
				out.println("<img src=\"./img./delete.png\" id=\"" + got_waiting_users.getObject("email")
						+ "\" onclick=\"send_post(this.id,'delete')>");

				out.println("<hr>");
			}
		} catch (Exception ex) {
			ex.printStackTrace(new PrintWriter(out));
		}
	%> 
</body>
</html>