<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head><meta charset="utf-8">
<script src="./js/register.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.3.1/jquery.min.js"></script>
 <script src="https://ajax.aspnetcdn.com/ajax/jQuery/jquery-3.3.1.min.js"></script>
<link rel="stylesheet" href="./css/styles.css">
</head>
<body>
<meta name="viewport" content="width=device-width, initial-scale=1">

<div style="text-align:center" id="portrait">

<img src="./img/linuxconf.png">
<div id="output">
</div>
<div id="server_response">
</div>

<br>
<%  String flag = (String) session.getAttribute("flag");
if( flag != null) {
	if(flag == "invalid_email") {
	 out.write("invalid email"); 
	} 
	else if (flag == "not_verified") {
	    out.write("Verification email not replied to, <A HREF='https://linuxconf.feedthepenguin.org/hehe/resend_email'> send another?</A>"); 
		
	} else { out.write(flag);}
	}

%>
<script>


</script>
<h2>
Register
</h1>
<form action="https://linuxconf.feedthepenguin.org/hehe/doregister" method="post" id="myform" onsubmit="status_check()">
Email:
<br>
<input type=text name="email" id="email" class=center size="35">
<br>
Password:
<br>
<input type=password name="password" id="pword" class=center size="35">
<br>
Check Password:

<br>
<input type=password name="chkpassword" id="chkpword"  class=center size="35">
</form>
<br>
<img src="./img/submit.jpg" border="0" onclick="status_check()" />

    
</div>	

</body>
</html>
