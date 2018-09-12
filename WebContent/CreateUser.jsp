<%@page import="org.eclipse.jgit.api.DeleteBranchCommand"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>

<%@ page import="org.apache.commons.dbutils.QueryRunner" %>
<%@ page import="org.apache.commons.dbutils.ResultSetHandler" %>
<%@ page import="org.apache.commons.dbutils.handlers.BeanHandler" %>
    <%@ page import="javax.sql.DataSource" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Linuxconf</title>
<script src="https://code.jquery.com/jquery-3.3.1.min.js"
	integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
	crossorigin="anonymous"></script>
<script>
	function submit_repositries() {

		var i = 0;
		var success = true;
		var result = "";
		var dataString = "url=" + document.getElementById("url").value
				+ "&description="
				+ document.getElementById("description").value + "&git_email="
				+ document.getElementById("email").value + "&";

			if (document.getElementById("device_checkbox" + i).checked) {
				if (document.getElementById("device_id" + i).value === "") {
					success = false;
					result += "Device ID not filled out\n";
				}
				if (document.getElementById("device_name" + i).value === "") {
					success = false;
					result += "Device Name not filled out\n";
				}

				if (document.getElementById("device_id" + i).value != "") {
					var usb_id = document.getElementById("device_id" + i).value;
					var res = usb_id.split(':', 2);

					if (res != undefined
							&& (res[0].length != 4 || res[1].length != 4)) {

						result += "Usb Id number not in the correct format, NNNN:NNNN\n";
						success = false;
					}
				}

				result += "For device number " + i + "\n\n";
			}

		
		if (document.getElementById("device_checkbox" + i).checked) {

				dataString += "git_url" + i + "="
						+ document.getElementById("git_url_hidden" + i).value;
				dataString += "&device_id" + i + "="
						+ document.getElementById("device_id" + i).value;
				dataString += "&device_name" + i + "="
						+ document.getElementById("device_name" + i).value;
			}
		i++;

	}

	if (success == false) {
		alert(result);
		return false;
	} else {
		$.ajax({
			type : "GET",
			url : "https://linuxconf.feedthepenguin.org/hehe/createuser",
			data : dataString,
			dataType : "json",
			success : function(data, textStatus) {
                $("#output").replaceWith(data.form);
                if(data.Error) {
                $("#output").replaceWith(data.Error);
                $("#output").elem.style.color = "Red";

                    	
                }

			}
		});

	}}
</script>
</head>
<body>
	<img src="./img/linuxconf.png" height="200" width="400">
	<br>
	<%
	DataSource dataSource = CustomDataSource.getInstance();
	QueryRunner run = new QueryRunner(dataSource);
  ResultSetHandler<DBcontributor> contributor_results= new BeanHandler<DBcontributor>(DBcontributor.class);
  ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
  
		try {

			Class.forName("com.mysql.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost/linuxconf", "arwen", "imleaving");

			if ((String) session.getAttribute("git_email") == null) {
				response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/GitAuth.html");
			}
			String url = "";
			String description = "";
			String owner_git_id = (String) session.getAttribute("git_id");

			DBcontributor details = run.query("select * from contributor where owner_git_id = ?",contributor_results,owner_git_id);
			if (details == null) 
				{
					out.write("Error");
					return;
				}

			ArrayList<String> clone_urls = null;
			if (session.getAttribute("repo_names") instanceof ArrayList) {
				clone_urls = (ArrayList<String>) session.getAttribute("repo_names");
			} else {
				out.println("Error retrieving projects form git");
				return;
			}
			out.println("Your Email:" + details.getEmail() + "<br>");
			out.println("Your Git Id:" + owner_git_id + "<br>");
			out.println("Your Description:" + details.getDescription() + "<br>");
			out.println("Your Location:" + details.getLocation() + "<br>");
			out.println("Your Name:" + details.getName() + "<br>");
			

			
			out.println("Your homepage / linkedin etc:");
			out.println("<input type='text' name='url' id='url' required maxlength='255' value='" + url + "'>");
			
			out.write("<table><tr>");
			out.write(
					"<th style='width:50%'>Git Repositry</th><th style='width:25%'>Device ID</th><th style='width:25%'>Device Name</th></tr>");
			int i = 0;
			

			while (i < clone_urls.size()) {

				out.write("<tr>");
				out.write("Submit repository: " + clone_urls.get(i) + " ");
				out.write("<input type='checkbox' name='git_url" + i + "' id='device_checkbox" + i + "' value='" + clone_urls.get(i) + "'>");
				out.write("</tr>");

				i++;
			}
			out.write("</table>");
			
			out.write(
					"<input type='image' src='./img/submit.jpg' onclick='submit_repositries()' alt='Submit Form' />");
			out.write("<div id='output'></div>");
		} catch (Exception ex) {
			ex.printStackTrace(new java.io.PrintWriter(out));
		}
	%>
</body>
</html>