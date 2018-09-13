<%@page import="org.eclipse.jgit.api.DeleteBranchCommand"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="jsplink.*"%>


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
	function send_post(myid) {
		var dataString = "myrepo=";
		 dataString += document.getElementById(myid).value;

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
	}

	
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

			
			String url = "";
			String description = "";
			int owner_git_id = (int) session.getAttribute("git_id");

			DBcontributor details = run.query("select * from contributor where git_id = ?",contributor_results,owner_git_id);
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
			out.println("Your Bio:" + details.getDescription() + "<br>");
			out.println("Your Location:" + details.getLocation() + "<br>");
			out.println("Your Name:" + details.getName() + "<br>");
			

			out.println("<form action='https://linuxconf.feedthepenguin.org/hehe/createuser' method='get'>");
			out.println("Your homepage / linkedin etc:<br>");
			out.println("<input type='text' name='url' id='url' required maxlength='255' value='" + url + "'><br>");
			
		
			int i = 0;
			while (i < clone_urls.size()) {

				out.write("Submit repository: " + clone_urls.get(i) + "<br>");
				out.write("<input type='button' name='git_url" + i + "' id='device_checkbox" + i + "' value='" + clone_urls.get(i) + "' onclick='send_post(this.id)'><br>");
		
				i++;
			}
				
			out.println("</form>");
			out.write("<div id='output'></div>");
		} catch (Exception ex) {
			ex.printStackTrace(new java.io.PrintWriter(out));
		}
	%>
</body>
</html>