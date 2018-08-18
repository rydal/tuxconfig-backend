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
	
	if (command === "delete") {
		  var r = confirm("Really delete " + command + " ? ");
		  if (r == false) {
		      return;
		  }  
	  }
	
    $.ajax({
        type: "GET",
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
                $("#output").replaceWith(data.form);

            }
        }
        
    })
}
</script>
</head>
<body>
<img src="./img/linuxconf.png" height="200" width="400"><br>

<div id="output" > </div>

	<%
	if (! request.getLocalAddr().equals(request.getRemoteAddr())) {
		out.write("Access only via localhost using ssh port forwarding");
		return;
	};
	
		try {
			out.write("<div id='server_response'></div>");

				out.write("<h2> Pending users to be Authorized:</h2>");

		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

		
		PreparedStatement get_waiting_users = con.prepareStatement("select * from user where authorised = '0' order by email");
		ResultSet got_waiting_users = get_waiting_users.executeQuery();
		while (got_waiting_users.next()) {
			out.println(got_waiting_users.getObject("email"));
			out.println("<img src=\"./img/delete.png\" id=\"" +  got_waiting_users.getObject("email") + "\" onclick=\"send_post(this.id , 'block')\">");
			out.println("<img src=\"./img/authorize.png\" id=\"" +  got_waiting_users.getObject("email") + "\" onclick=\"send_post(this.id , 'authorize')\">");
			out.println("<hr>");
		}
		
		out.print("<h2> Currently authorised users</h2><br>");

			PreparedStatement get_current_users = con
					.prepareStatement("select * from user where authorised = '1' order by email");
			ResultSet got_current_users = get_current_users.executeQuery();
			while (got_current_users.next()) {
				out.println(got_current_users.getObject("email"));
				out.println("<img src=\"./img./delete.png\" id=\"" + got_current_users.getObject("email")
				+ "\" onclick=\"send_post(this.id,'block')\">");

				out.println("<hr>");
			}
			out.print("<h2> Currenrly blocked users</h2><br>");

			PreparedStatement get_blocked_users = con
					.prepareStatement("select * from user where authorised = '2' order by email");
			ResultSet got_blocked_users = get_blocked_users.executeQuery();
			while (got_blocked_users.next()) {
				out.println(got_blocked_users.getObject("email"));
				out.println("<img src=\"./img./delete.png\" id=\"" + got_blocked_users.getObject("email")
						+ "\" onclick=\"send_post(this.id,'unblock')\">");

				out.println("<hr>");
			}
			
		} catch (Exception ex) {
			ex.printStackTrace(new PrintWriter(out));
		}
	%> 
</body>
</html>