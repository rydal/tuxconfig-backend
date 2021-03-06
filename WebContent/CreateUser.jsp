<%@page import="org.apache.commons.dbutils.DbUtils"%>
<%@page import="org.eclipse.jgit.api.DeleteBranchCommand"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="jsplink.*"%>


<%@ page import="org.apache.commons.dbutils.QueryRunner"%>
<%@ page import="org.apache.commons.dbutils.ResultSetHandler"%>
<%@ page import="org.apache.commons.dbutils.handlers.BeanHandler"%>
<%@ page import="org.apache.commons.dbutils.handlers.BeanListHandler"%>
<%@ page import="javax.sql.DataSource"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Linuxconf</title>
<script src="https://code.jquery.com/jquery-3.3.1.min.js"
	integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
	crossorigin="anonymous"></script>
<script>
var chosen_repository = false;
function set_repository(name) {
	var url = document.getElementById(name).value;

	document.getElementById("content").innerHTML = url;
	document.getElementById("git_url").value = url;
	chosen_repository = true;

}
</script>
</head>
<body>
<div align="center">
	<img src="./img/tux_banner.png" height="200" width="400">
	<br>
	<%
		DataSource dataSource = CustomDataSource.getInstance();
		QueryRunner run = new QueryRunner(dataSource);
		ResultSetHandler<DBcontributor> contributor_results = new BeanHandler<DBcontributor>(DBcontributor.class);
		ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);

		try {


			String url = "";
			String description = "";
			String git_id_string = (String) session.getAttribute("git_id");
			if (git_id_string == null) {
				response.sendRedirect("https://linuxconf.feedthepenguin.org/hehe/GitAuth.html");
				return;
			}
			int owner_git_id = Integer.parseInt(git_id_string);
			
			DBcontributor details = run.query("select * from contributor where git_id = ?", contributor_results,
					owner_git_id);
			if (details == null) {
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

			out.println("<form action='https://linuxconf.feedthepenguin.org/hehe/createuser' method='get' id='create'  >");
			out.println("Your homepage / linkedin etc:<br>");
		    out.println("<input type='text' name='website' id='website' required maxlength='255' ><br>");

		    
		    out.println("<BR>");
			ResultSetHandler<List<DBDevice>> rsh = new BeanListHandler<DBDevice>(DBDevice.class);
			List<DBDevice> rows = run.query("select * from git_url where owner_git_id = ? ", rsh, owner_git_id);

			Iterator<DBDevice> it = rows.iterator();
			if (rows.size() > 0) {
				out.println("<h3> Submitted repository details: </h3><br>");
				while (it.hasNext()) {
				DBDevice bean = it.next();
				

			    out.println("<b> Submitted repository details: </b>");
			    float vote_difference = bean.getUpvotes()  - bean.getDownvotes();
			    if (vote_difference < 0) {
			    	out.println("<font color='red'>");
			    }
			    if (vote_difference < 0) {
			    	out.println("<font color='green'>");
			    }
				out.print("Repository:\t" + bean.getGit_url() + "\t" + "<br>Commit hash:" + bean.getCommit_hash()  + "Vote difference:\t");
						if (vote_difference < 0) {
					    	out.println("<font color='red'>");
					    }
					    if (vote_difference > 0) {
					    	out.println("<font color='green'>");
					    }
						
						out.print( vote_difference + "</font><br>");
				}
			}
			int i = 0;
			while (i < clone_urls.size()) {

				out.write("Submit repository: " + clone_urls.get(i) + "<br>");
				
				out.write("<input type='button' id='device_checkbox" + i + "' value='"	+ clone_urls.get(i) + "' onclick='set_repository(this.id)'><br>");
				out.println("<input type='hidden' name='git_url' id='git_url' ><br>");
			    
				i++;
			}
				
			out.println("<hr>");
			out.println("Select a repository:");
		    out.println("<div id=\"content\"> </div>");
			out.println("Distribution name");
		   out.println("<input type='submit' value='submit repository' >");

			out.println("</form>");
			out.write("<div id='output'></div>");
		} catch (Exception ex) {
			ex.printStackTrace(new java.io.PrintWriter(out));
		}
	%>
	</div>
</body>
<script>

$('#create').submit(function() { // catch the form's submit event
	if (chosen_repository == false ) {
		alert("Must choose one repository");
		return false;
	}
    $.ajax({ // create an AJAX call...
        data: $(this).serialize(), // get the form data
        type: $(this).attr('method'), // GET or POST
        url: $(this).attr('action'), // the file to call
        success: function(response) { // on success..
            if (response.Form) {
            	$("#output").css('color', 'green');                  
            	
            	$('#output').append(response.Form + "<br>"); // update the DIV	
            } 
            else  {
                $("#output").css('color', 'red');
                $('#output').append(response.Error + "<br>"); // update the DIV
            }
        	
        }
    });
    return false; // cancel original event to prevent form submitting
});
</script>
</html>
