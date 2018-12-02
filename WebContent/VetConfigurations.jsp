<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.sql.*"%>
    <%@ page import="jsplink.*" %>
    <%@ page import="javax.sql.DataSource" %>

<%@ page import="org.apache.commons.dbutils.QueryRunner" %>
<%@ page import="org.apache.commons.dbutils.ResultSetHandler" %>
<%@ page import="org.apache.commons.dbutils.handlers.BeanHandler" %>
<%@ page import="org.apache.commons.dbutils.handlers.BeanListHandler" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
 <script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.3.1.min.js"></script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Linuxconf</title>
</head>
<body>
<img src="./img/tux_banner.png">
<div id="output" > </div>
	<%
	try {
	DataSource dataSource = CustomDataSource.getInstance();
	QueryRunner run = new QueryRunner(dataSource);
  ResultSetHandler<DBcontributor> contributor_results= new BeanHandler<DBcontributor>(DBcontributor.class);
  ResultSetHandler<DBDevice> device_results = new BeanHandler<DBDevice>(DBDevice.class);
		
		out.write("<h1> Review proposed configurations</h1>");
	
		
		out.write("<h2>Repositories awaiting authorisation</h2>");
		
		
		String query_url = "SELECT * from contributor c inner join git_url g on c.git_id = g.owner_git_id where g.authorised = 0";
		
		ResultSetHandler<List<DBDevice>> rsh = new BeanListHandler<DBDevice>(DBDevice.class);
		List<DBDevice> rows = run.query(query_url, rsh);

		Iterator<DBDevice> it = rows.iterator();
	
		
		
		int index = 0;
		while (it.hasNext()) {
			DBDevice bean = it.next();
			out.println("<A HREF='" + bean.getGit_url() +"'> " + bean.getGit_url()  + "</A>");
			out.println("<input type='hidden'  value='" + bean.getCommit_hash() + "' id='" +  "hash" + index  +"'>");
			out.println("<input type='hidden'  value='" + bean.getGit_url() + "' id='" +  "git_url" + index  +"'><br>");
			out.println("<input type='hidden'  value=device_id,'" + bean.getDevice_id()+ "' id='" +  "device_id" + index  +"'><br>");
			out.println("<input type='hidden'  value='" + bean.getGit_url() + "' id='" +  "url" + index  +"'><br>");

			out.println("Commit id " + bean.getCommit_hash() + "<br>");
			out.println("User email address " + bean.getEmail() + "<br>");
			
			
		
			out.println("<img src=\"./img/accept.png\" id=\""  +  "input" + index +  "\" onclick=\"send_post(this.id , 'authorise')\">");
			out.println("<img src=\"./img/delete.png\" id=\""  +  "input" + index +  "\" onclick=\"send_post(this.id , 'delete')\">");
			out.println("<hr>");
			index++;
		}

		out.write("<h2>Repositories authorised</h2>");
		index = 0;
		String query_url2 = "SELECT * from contributor c inner join git_url g on c.git_id = g.owner_git_id  where g.authorised = 1";
		
		ResultSetHandler<List<DBDevice>> rsh2 = new BeanListHandler<DBDevice>(DBDevice.class);
		List<DBDevice> rows2 = run.query(query_url2, rsh2);

		Iterator<DBDevice> it2 = rows2.iterator();
		while (it2.hasNext()) {
			DBDevice bean2 = it2.next();

			
			
			out.println("<A HREF='" + bean2.getGit_url() +"'> " + bean2.getGit_url()  + "</A>");
			out.println("<input type='hidden'  value='" + bean2.getCommit_hash() + "' id='" +  "hash" + index  +"'>");
			out.println("<input type='hidden'  value='" + bean2.getGit_url() + "' id='" +  "git_url" + index  +"'><br>");
			out.println("<input type='hidden'  value='" + bean2.getDevice_id()+ "' id='" +  "device_id" + index  +"'><br>");
			out.println("<input type='hidden'  value='" + bean2.getGit_url() + "' id='" +  "url" + index  +"'><br>");

			out.println("Commit id " + bean2.getCommit_hash() + "<br>");
			out.println("User email address " + bean2.getEmail() + "<br>");
			
					
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
		
	dataString += "git_url=" + document.getElementById("git_url" + i).value;
	dataString += "&hash="	+ document.getElementById("hash" + i).value;
	dataString += "&command=" + command;
	
    $.ajax({
        type: "GET",
        url: "http://localhost:8080/hehe/vetconfigurations",
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

	window.location.replace("http://localhost:8080/hehe/SearchConfigurations.jsp?" + dataString);
}


</script>

</html>

